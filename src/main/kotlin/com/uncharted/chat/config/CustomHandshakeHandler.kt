package com.uncharted.chat.config

import org.springframework.http.server.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.UUID

/**
 * Assigns a UID to a newly connected user.
 * This allows to uniquely identify a websocket user and send private messages
 */
@Component
class CustomHandshakeHandler : DefaultHandshakeHandler() {

  override fun determineUser(
    request: ServerHttpRequest,
    wsHandler: org.springframework.web.socket.WebSocketHandler,
    attributes: MutableMap<String, Any>
  ): Principal? {
    return StompPrincipal(UUID.randomUUID().toString())
  }
}

class StompPrincipal(private val name: String) : Principal {
  override fun getName(): String {
    return name
  }
}
