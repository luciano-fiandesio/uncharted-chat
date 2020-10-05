package com.uncharted.chat.model

enum class MessageType {

  CHAT, CONNECT, DISCONNECT, WARN
}

data class Message(val type: MessageType, val content: String = "", val sender: String, val timestamp: String = "")
