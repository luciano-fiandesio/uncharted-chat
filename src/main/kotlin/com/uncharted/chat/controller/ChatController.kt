package com.uncharted.chat.controller

import com.uncharted.chat.model.Command
import com.uncharted.chat.model.CommandType
import com.uncharted.chat.model.Message
import com.uncharted.chat.service.MessageService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class ChatController(val messageService: MessageService) {

  @MessageMapping("/chat.send")
  fun sendMessage(@Payload message: Message, principal: Principal) {

    when {
      MessageUtils.isCommand(message) -> messageService.handleCommand(MessageUtils.asCommand(message), principal)
      else -> messageService.handleMessage(message)
    }
  }

  @MessageMapping("/chat.newUser")
  @SendTo("/topic/public")
  fun newUser(@Payload message: Message, headerAccessor: SimpMessageHeaderAccessor): Message {

    headerAccessor.sessionAttributes?.set("username", message.sender)
    return message
  }
}

class MessageUtils private constructor() {

  companion object Utils {

    fun isCommand(msg: Message): Boolean {
      return msg.content.startsWith("/")
    }

    fun asCommand(msg: Message): Command {
      return Command(
        CommandUtils.getCommandType(extractCommand(msg.content)), extractCommandArg(msg.content),
        msg.sender, msg.timestamp
      )
    }

    /**
     * Very simple extraction logic, a parser (like antlr) should be more appropriate
     */
    private fun extractCommand(content: String): String {
      return content.substringAfter("/").substringBefore(" ")
    }

    /**
     * Very simple extraction logic, a parser (like antlr) should be more appropriate
     */
    private fun extractCommandArg(content: String): String {
      return content.substringAfter(" ").trim()
    }
  }
}

class CommandUtils private constructor() {

  companion object Utils {
    fun getCommandType(command: String): CommandType {
      return when (command) {
        "history" -> CommandType.GET_LATEST_MSG
        else -> CommandType.UNKNOWN
      }
    }
  }
}
