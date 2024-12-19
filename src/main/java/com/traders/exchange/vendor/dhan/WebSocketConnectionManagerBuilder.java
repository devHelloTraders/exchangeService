package com.traders.exchange.vendor.dhan;

import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

public class WebSocketConnectionManagerBuilder {
        private StandardWebSocketClient client;
        private DhanWebSocketHandler handler;
        private String url;

        private WebSocketConnectionManagerBuilder() {}

        public static WebSocketConnectionManagerBuilder builder() {
            return new WebSocketConnectionManagerBuilder();
        }

        public WebSocketConnectionManagerBuilder withClient(StandardWebSocketClient client) {
            this.client = client;
            return this;
        }

        public WebSocketConnectionManagerBuilder withHandler(DhanWebSocketHandler handler) {
            this.handler = handler;
            return this;
        }

        public WebSocketConnectionManagerBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public WebSocketConnectionManager build() {
            return new WebSocketConnectionManager(client, handler, url);
        }
    }