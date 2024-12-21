package com.traders.exchange.vendor.dhan;

import com.traders.exchange.domain.InstrumentInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class DhanWebSocketHandler extends AbstractWebSocketHandler {

    private static final int MAX_INSTRUMENTS_PER_REQUEST = 100;
    private static final int REQUEST_CODE = 21;
    private static final int UNSUBSCRIBE_REQUEST_CODE = 22;
    private final int connectionId;
    private final Consumer<String> retryFunction;
    private final DhanConnectionMetadata metadata;
    public DhanWebSocketHandler(int connectionId,  Consumer<String> retryFunction, DhanConnectionMetadata metadata) {
        this.connectionId = connectionId;
        this.retryFunction = retryFunction;
        this.metadata = metadata;
        metadata.setSubscribeFunction(this::sendSubscriptionMessages);
        metadata.setUnSubscribeFunction(this::sendUnSubscriptionMessages);

    }


    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        metadata.addSession(session);
        log.info("connection established sending subscription");
        sendSubscriptionMessages(session,new ArrayList<>(metadata.getInstrumentDetails().entrySet()));

    }

    private void sendSubscriptionMessages(WebSocketSession session,  List<Map.Entry<Long, InstrumentInfo>> instrumentList)  {
        AtomicInteger batchNo = new AtomicInteger();
        IntStream.range(0, (instrumentList.size() + MAX_INSTRUMENTS_PER_REQUEST - 1) / MAX_INSTRUMENTS_PER_REQUEST) // Calculate number of batches
                .mapToObj(i -> instrumentList.subList(i * MAX_INSTRUMENTS_PER_REQUEST, Math.min((i + 1) * MAX_INSTRUMENTS_PER_REQUEST, instrumentList.size())))
                .forEach(batch -> {
                    metadata.addSubscription(batch.size());
                    metadata.getConnectionTracker().accept(true,batch.stream().map(Map.Entry::getKey).toList());
                    subscribeMessage(REQUEST_CODE,session,batch);
                    log.info("Sent subscription request for batch: {}, total subscription for connection id : {} is {}",
                            batchNo.incrementAndGet(), connectionId, (batchNo.get() * MAX_INSTRUMENTS_PER_REQUEST));
                });

    }

    private void sendUnSubscriptionMessages(WebSocketSession session,List<Map.Entry<Long, InstrumentInfo>> instrumentList)  {
        AtomicInteger batchNo = new AtomicInteger();
        IntStream.range(0, (instrumentList.size() + MAX_INSTRUMENTS_PER_REQUEST - 1) / MAX_INSTRUMENTS_PER_REQUEST) // Calculate number of batches
                .mapToObj(i -> instrumentList.subList(i * MAX_INSTRUMENTS_PER_REQUEST, Math.min((i + 1) * MAX_INSTRUMENTS_PER_REQUEST, instrumentList.size())))
                .forEach(batch -> {
                    metadata.addSubscription(-batch.size());
                    metadata.getConnectionTracker().accept(false,batch.stream().map(Map.Entry::getKey).toList());
                    subscribeMessage(UNSUBSCRIBE_REQUEST_CODE,session,batch);
                    log.info("Sent subscription request for batch: {}, total subscription for connection id : {} is {}",
                            batchNo.incrementAndGet(), connectionId, (batchNo.get() * MAX_INSTRUMENTS_PER_REQUEST));
                });

    }

    private String createSubscriptionMessage(List<Map.Entry<Long, InstrumentInfo>> instruments,Integer requestCode) {
        String instrumentListJson = instruments.stream()
                .map(Map.Entry::getValue)
                .map(instrument -> String.format("{\"ExchangeSegment\": \"%s\", \"SecurityId\": \"%s\"}",
                        instrument.getExchange(),
                        instrument.getInstrumentToken()))
                .collect(Collectors.joining(","));

        return """
                {"RequestCode": %d,"InstrumentCount": %d,"InstrumentList": [%s]}"""
                .formatted(requestCode, instruments.size(), instrumentListJson);
    }
    @SneakyThrows
    private void subscribeMessage(Integer requestCode,WebSocketSession session, List<Map.Entry<Long, InstrumentInfo>> batch){
        String subscriptionMessage = createSubscriptionMessage(batch, requestCode);
        //String subscriptionMessage ="{\"RequestCode\": 21,\"InstrumentCount\": 5,\"InstrumentList\": [{\"ExchangeSegment\": \"NSE_EQ\", \"SecurityId\": \"1333\"},{\"ExchangeSegment\": \"BSE_EQ\", \"SecurityId\": \"532540\"},{\"ExchangeSegment\": \"NSE_EQ\", \"SecurityId\": \"19237\"},{\"ExchangeSegment\": \"IDX_I\", \"SecurityId\": \"25\"},{\"ExchangeSegment\": \"NSE_FNO\", \"SecurityId\": \"43972\"}]}";
        session.sendMessage(new TextMessage(subscriptionMessage));
    }
//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
//        ByteBuffer byteBuffer = message.getPayload();
//        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // Set byte order if required by API
//
//        byte[] responseHeader = new byte[8];
//        byteBuffer.get(responseHeader);
//        byte feedResponseCode = responseHeader[0];
//
//        if (feedResponseCode == 50) {
//            short disconnectCode = byteBuffer.getShort();
//            System.out.println("Disconnection Code: " + disconnectCode);
//        } else if (feedResponseCode == 2) {
//            String securityId = extractSecurityId(responseHeader);
//            String instrumentName = INSTRUMENTS.get(securityId).getInstrumentName();
//            PacketParser.parseTickerPacket(byteBuffer, instrumentName);
//        }
//    }

    @Override
    protected void handleBinaryMessage(@NotNull WebSocketSession session, BinaryMessage message)  {
        ByteBuffer byteBuffer = message.getPayload();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // Ensure correct byte order

        byte[] responseHeader = new byte[8];
        byteBuffer.get(responseHeader);
        byte feedResponseCode = responseHeader[0];

        if (feedResponseCode == 50) {
            short disconnectCode = byteBuffer.getShort();
            log.info("Disconnection Code: {}" , disconnectCode);
        } else if (feedResponseCode == 8) {
            String securityId = extractSecurityId(responseHeader);
            DhanResponseHandler.parseTickerPacket(byteBuffer, securityId);
        }
    }

    private String extractSecurityId(byte[] responseHeader) {
        return String.valueOf(ByteBuffer.wrap(responseHeader, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, Throwable exception) {
       log.info("Error: {}" , exception.getMessage());
        retryFunction.accept(Objects.requireNonNull(session.getUri()).toString());
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status)  {
        log.info("Connection closed. {}",status);
        metadata.removeSession();
    }
}
