package com.tradingtrends.market.infrastructure.config;

import com.tradingtrends.market.infrastructure.handler.CustomWebSocketHandler;
import java.util.Map;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CustomWebSocketHandler customWebSocketHandler;

    public WebSocketConfig(CustomWebSocketHandler customWebSocketHandler) {
        this.customWebSocketHandler = customWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler, "/ws/connect/coin-data-per-user")
            .setAllowedOrigins("*")
            .addInterceptors(new HttpSessionHandshakeInterceptor() {
                @Override
                public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                    org.springframework.http.server.ServerHttpResponse response,
                    org.springframework.web.socket.WebSocketHandler wsHandler,
                    Map<String, Object> attributes) throws Exception {
                    boolean result = super.beforeHandshake(request, response, wsHandler, attributes);
                    if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
                        var servletRequest = ((org.springframework.http.server.ServletServerHttpRequest) request).getServletRequest();
                        String userId = servletRequest.getParameter("userId");
                        if (userId != null) {
                            attributes.put("userId", userId);
                        } else {
                            throw new IllegalArgumentException("UserId is required to establish WebSocket connection");
                        }
                    }
                    return result;
                }
            });
    }
}
