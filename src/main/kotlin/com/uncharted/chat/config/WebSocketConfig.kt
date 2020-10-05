package com.uncharted.chat.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

const val WEBSOCKET_PREFIX = "/topic"
const val WEBSOCKET_CHAT_ROOM = "$WEBSOCKET_PREFIX/public"

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(val customHandshakeHandler: CustomHandshakeHandler) : WebSocketMessageBrokerConfigurer {

  override fun registerStompEndpoints(registry: StompEndpointRegistry) {

    registry.addEndpoint("/chat-example")
      .setHandshakeHandler(customHandshakeHandler)
      .setAllowedOrigins("*")
      .withSockJS()
  }

  override fun configureMessageBroker(registry: MessageBrokerRegistry) {
    registry.setApplicationDestinationPrefixes("/app")
    registry.enableSimpleBroker(WEBSOCKET_PREFIX)
    registry.setPreservePublishOrder(true)
  }
}
