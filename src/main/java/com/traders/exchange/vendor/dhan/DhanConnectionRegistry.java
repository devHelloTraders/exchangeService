package com.traders.exchange.vendor.dhan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traders.exchange.domain.InstrumentInfo;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.properties.ConfigProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class DhanConnectionRegistry {

    private final Map<Integer, WebSocketConnectionManager> connectionPool = new HashMap<>();
    private final List<String> urlList = new ArrayList<>();
    private final Predicate<WebSocketConnectionManager> isConnectionActive = connection -> connection.isConnected() || connection.isRunning();
    private final ConfigProperties configProperties;
    private final BiFunction<Integer, List<InstrumentInfo>, Map<Long, List<InstrumentInfo>>> instrumentBatcher;
    private final ApiCredentials apiCredentials;
    private final List<DhanConnectionMetadata> poolSelector = new ArrayList<>();
    private final Set<Long> allSubscriptions = new HashSet<>();

    public DhanConnectionRegistry(ConfigProperties configProperties, ApiCredentials apiCredentials) {
        this.configProperties = configProperties;
        this.apiCredentials = apiCredentials;
        this.instrumentBatcher = ((totalAccounts, instrumentInfo) -> {
            int factor = Math.max(1, instrumentInfo.size() / (configProperties.getDhanConfig().getAllowedConnection() * totalAccounts));
            return IntStream.range(0, instrumentInfo.size())
                    .boxed()
                    .collect(Collectors.groupingBy(
                            index -> (long) index / factor,
                            Collectors.mapping(instrumentInfo::get, Collectors.toList())
                    ));
        });


//        this.instrumentBatcher = (totalAccounts, instrumentInfo) -> {
//            int batchSize = configProperties.getDhanConfig().getAllowedConnection() * totalAccounts;
//
//            return IntStream.range(0, (int) Math.ceil((double) instrumentInfo.size() / batchSize)).boxed()
//                    .collect(Collectors.toMap(i -> (long) i,
//                    i -> instrumentInfo.subList(i * batchSize, Math.min((i + 1) * batchSize, instrumentInfo.size()))));
//        };


    }

    private void startConnection(int mapIndex, WebSocketConnectionManager manager) {
        int index = mapIndex / configProperties.getDhanConfig().getAllowedConnection();
        if (manager == null) {
            log.info("Invalid connection Manager for id {}", urlList.get(index));
            return;
        }

        try {
            manager.start();
            log.info("Connection Started for id {}", urlList.get(index));
        } catch (Exception e) {
            throw new AttentionAlertException("Not able to start connection for id " + urlList.get(index), " SocketConnectionRegistry", " Please contact admin");
        }
    }

    private void stopConnection(int mapIndex, WebSocketConnectionManager manager) {
        int index = mapIndex / configProperties.getDhanConfig().getAllowedConnection();
        if (manager == null) {
            log.info("Stopped connection Manager for id {}", urlList.get(index));
            return;
        }

        manager.stop();
        log.info("Connection stopped for id {}", urlList.get(index));
    }

    private Consumer<String> retryConnectionConsumer() {
        return (String) -> connectionPool.values().stream()
                .filter(isConnectionActive.negate())
                .forEach(ConnectionManagerSupport::start);
    }

    public void startAllConnection() {
        connectionPool.forEach(this::startConnection);
    }

    public void stopAllConnection() {
        connectionPool.forEach(this::stopConnection);
    }

    private final Function<List<InstrumentInfo>, Map<Long, InstrumentInfo>> instrumentDetailsCreator = (instrumentList) ->
            instrumentList.stream()
                    .collect(Collectors.toMap(
                            InstrumentInfo::getInstrumentToken,
                            obj -> obj,
                            (existing, replacement) -> replacement
                    ));


    private String getWsUrl(String accessToken, String clientId) {
        return Routes._wsuri.replace(":token", accessToken).replace(":clientId", clientId);
    }


    public void addConnections(List<InstrumentInfo> instrumentInfoList) {
        int maxAllowedSubscriptions = configProperties.getDhanConfig().getReservedConnection() * configProperties.getDhanConfig().getTickerBatch();
        var instrumentInfo = instrumentInfoList.stream().limit( maxAllowedSubscriptions).toList();
        var instrumentBatch = instrumentBatcher.apply(apiCredentials.getCredentials().size(), instrumentInfo);
        IntStream.range(0, apiCredentials.getCredentials().size()).forEach(credentialIndex -> {
            var credentials = apiCredentials.getCredentials().get(credentialIndex);
            String url = getWsUrl(credentials.getApiKey(), credentials.getClientId());

            if (urlList.contains(url)) {
                log.info("Connection already present for URL: {}", url);
                return;
            }

            createConnectionsForCredential(url, instrumentBatch);
            urlList.add(url);
        });
    }

    private void createConnectionsForCredential(String url, Map<Long, List<InstrumentInfo>> instrumentBatch) {
        IntStream.range(0, configProperties.getDhanConfig().getAllowedConnection()).forEach(connectionIndex -> {
            int connectionId = connectionPool.size();
            var connectionMetadata =  DhanConnectionMetadata.of(connectionId,addForSubscriptionTracking);

            instrumentBatch.putIfAbsent((long) connectionId, new ArrayList<>());
            connectionMetadata.getInstrumentDetails().putAll(instrumentDetailsCreator.apply(instrumentBatch.get((long) connectionId)));
            var handler = new DhanWebSocketHandler(
                    connectionId,
                    retryConnectionConsumer(),
                    connectionMetadata
            );

            var connectionManager = WebSocketConnectionManagerBuilder.builder()
                    .withClient(new StandardWebSocketClient())
                    .withHandler(handler)
                    .withUrl(url)
                    .build();
            poolSelector.add(connectionMetadata);
            connectionMetadata.addConnectionManager(connectionManager);
            connectionPool.put(connectionId, connectionManager);
        });
    }

    public Request getDhanAPIRestRequest(List<InstrumentInfo> instrumentInfos) {


        ApiCredentials.Credentials credentials = apiCredentials.getRandomConnection();
        return (new Request.Builder()).url(Routes.restUrl)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("access-token", credentials.getApiKey())
                .header("client-id", credentials.getClientId())
                .post(getRequestBody(instrumentInfos))
                .build();

    }

    @SneakyThrows
    private RequestBody getRequestBody(List<InstrumentInfo> instrumentInfos) {
        Map<String, List<Long>> groupedByExchange = instrumentInfos.stream()
                .collect(Collectors.groupingBy(
                        InstrumentInfo::getExchange,
                        Collectors.mapping(InstrumentInfo::getInstrumentToken, Collectors.toList())
                ));

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        groupedByExchange.forEach(rootNode::putPOJO);

        return RequestBody.create(
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode), MediaType.parse("application/json"));
    }

    private DhanConnectionMetadata getMostUnderutilizedConnection(List<DhanConnectionMetadata> connections) {
        return connections.stream()
                .peek(DhanConnectionMetadata::keepAlive)
                .min(Comparator.comparingDouble(DhanConnectionMetadata::calculateWeight))
                .orElse(null);
    }

//    public void updateSubscription(List<InstrumentInfo> subscribeList,List<InstrumentInfo> unsubscribeList){
//        var metadata =getMostUnderutilizedConnection(poolSelector);
//        if(metadata==null)
//            return;
//        Map<Long, InstrumentInfo> subscribeInstrumentBatch =null;
//        Map<Long, InstrumentInfo> unsubscribeInstrumentBatch= null;
//        if(subscribeList!=null && !subscribeList.isEmpty()) {
//            subscribeInstrumentBatch = instrumentDetailsCreator.apply(subscribeList);
//            allSubscriptions.forEach(subscribeInstrumentBatch.keySet()::remove);
//            metadata.getInstrumentDetails().putAll(subscribeInstrumentBatch);
//        }
//        if(unsubscribeList!=null && !unsubscribeList.isEmpty()) {
//            unsubscribeInstrumentBatch = instrumentDetailsCreator.apply(unsubscribeList);
//            unsubscribeInstrumentBatch.keySet().retainAll(allSubscriptions);
//            metadata.getInstrumentDetails().keySet().removeAll(unsubscribeInstrumentBatch.keySet());
//        }
//
//        metadata.doAction(subscribeInstrumentBatch,unsubscribeInstrumentBatch);
//    }

    private final BiConsumer<Boolean,List<Long>> addForSubscriptionTracking =(isSubscribe, instruments)->{
        if(isSubscribe)
            allSubscriptions.addAll(instruments);
        else
            allSubscriptions.removeAll(instruments);
    };

    public void updateSubscription(List<InstrumentInfo> subscribeList, List<InstrumentInfo> unsubscribeList) {
        var metadata = getMostUnderutilizedConnection(poolSelector);
        if (metadata == null) return;

        Set<Long> allSubscriptionIds = new HashSet<>(allSubscriptions);
        Map<Long, InstrumentInfo> subscribeInstrumentBatch =null;
        if (subscribeList != null && !subscribeList.isEmpty()) {
            subscribeInstrumentBatch = instrumentDetailsCreator.apply(subscribeList);
            subscribeInstrumentBatch.keySet().removeIf(allSubscriptionIds::contains);
            metadata.getInstrumentDetails().putAll(subscribeInstrumentBatch);
        }
        Map<Long, InstrumentInfo> unsubscribeInstrumentBatch =null;
        if (unsubscribeList != null && !unsubscribeList.isEmpty()) {
            unsubscribeInstrumentBatch = instrumentDetailsCreator.apply(unsubscribeList);
            unsubscribeInstrumentBatch.keySet().retainAll(allSubscriptionIds);
            metadata.getInstrumentDetails().keySet().removeAll(unsubscribeInstrumentBatch.keySet());
        }

        metadata.doAction(subscribeInstrumentBatch, unsubscribeInstrumentBatch);
    }


}
