package com.uncharted.chat.model

enum class CommandType {

  GET_LATEST_MSG, UNKNOWN
}

data class Command(val type: CommandType, val commandArg: String, val sender: String, val timestamp: String = "")
