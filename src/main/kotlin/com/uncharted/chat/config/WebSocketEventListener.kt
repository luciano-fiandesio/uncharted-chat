package com.uncharted.chat.config

import com.uncharted.chat.model.Message
import com.uncharted.chat.model.MessageType
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

private val logger = KotlinLogging.logger {}
@Component
class WebSocketEventListener(val ops: SimpMessageSendingOperations) {

  @EventListener
  fun onConnect(event: SessionConnectEvent) {
    logger.debug { "client connected" }
  }

  @EventListener
  fun onDisconnect(event: SessionDisconnectEvent) {

    val headerAccessor = StompHeaderAccessor.wrap(event.message)

    val username = headerAccessor.sessionAttributes?.get("username")

    ops.convertAndSend(WEBSOCKET_CHAT_ROOM, Message(type = MessageType.DISCONNECT, sender = username as String))
  }
}
