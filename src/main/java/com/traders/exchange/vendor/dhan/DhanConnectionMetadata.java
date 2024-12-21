package com.traders.exchange.vendor.dhan;

import com.traders.exchange.domain.InstrumentInfo;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class DhanConnectionMetadata {
    private Integer connectionId;
    private Integer subscriptions =0;
    private WebSocketSession session;
    private WebSocketConnectionManager manager;
    private BiConsumer<WebSocketSession, List<Map.Entry<Long, InstrumentInfo>>> subscribeFunction;
    private BiConsumer<WebSocketSession, List<Map.Entry<Long, InstrumentInfo>>> unSubscribeFunction;
    private final Map<Long, InstrumentInfo> instrumentDetails = new HashMap<>();
    public final BiConsumer<Boolean,List<Long>> connectionTracker;

    public DhanConnectionMetadata(Integer connectionId, BiConsumer<Boolean,List<Long>> connectionTracker) {
        this.connectionId = connectionId;
        this.connectionTracker = connectionTracker;
    }

    public static DhanConnectionMetadata of(Integer connectionId,BiConsumer<Boolean,List<Long>> connectionTracker){
        return new DhanConnectionMetadata(connectionId,connectionTracker);
    }

    public void addSubscription(Integer subscriptions){
        this.subscriptions += subscriptions;
    }

    public void addSession(WebSocketSession session){
        this.session = session;
    }
    public void removeSession(){
        this.session =null;
    }

    public void addConnectionManager(WebSocketConnectionManager manager){
        this.manager = manager;
    }
    public double calculateWeight() {
        return subscriptions == 0 ? Double.MAX_VALUE : 1.0 / subscriptions;
    }
    public void keepAlive(){
        if(session==null || !manager.isConnected() || !manager.isRunning())
            manager.start();
    }

    public void setSubscribeFunction(BiConsumer<WebSocketSession, List<Map.Entry<Long, InstrumentInfo>>> subscribeFunction) {
        this.subscribeFunction = subscribeFunction;
    }

    public void setUnSubscribeFunction(BiConsumer<WebSocketSession, List<Map.Entry<Long, InstrumentInfo>>> unSubscribeFunction) {
        this.unSubscribeFunction = unSubscribeFunction;
    }

    private BiConsumer<WebSocketSession, List<Map.Entry<Long, InstrumentInfo>>> getSubscribeFunction() {
        return subscribeFunction;
    }

    private BiConsumer<WebSocketSession, List<Map.Entry<Long, InstrumentInfo>>> getUnSubscribeFunction() {
        return unSubscribeFunction;
    }

    public void doAction(Map<Long, InstrumentInfo> instrumentsToSubscribe,Map<Long, InstrumentInfo> instrumentToUnsubscribe){
        if(instrumentsToSubscribe!=null && !instrumentsToSubscribe.isEmpty()){
            this.subscribeFunction.accept(session,new ArrayList<>(instrumentsToSubscribe.entrySet()));

        }if(instrumentToUnsubscribe!=null && !instrumentToUnsubscribe.isEmpty()){
            this.unSubscribeFunction.accept(session,new ArrayList<>(instrumentToUnsubscribe.entrySet()));
        }
    }
}
