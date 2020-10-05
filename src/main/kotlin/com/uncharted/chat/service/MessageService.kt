package com.uncharted.chat.service

import com.uncharted.chat.config.MESSAGE_ROUTING_KEY
import com.uncharted.chat.config.RABBITMQ_EXCHANGE
import com.uncharted.chat.config.WEBSOCKET_CHAT_ROOM
import com.uncharted.chat.isNumeric
import com.uncharted.chat.model.Command
import com.uncharted.chat.model.CommandType
import com.uncharted.chat.model.Message
import com.uncharted.chat.model.MessageType
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class MessageService(
  private val rabbitTemplate: RabbitTemplate,
  private val messageRepository: MessageRepository,
  private val ops: SimpMessageSendingOperations
) {

  fun handleCommand(command: Command, principal: Principal) {

    when (command.type) {

      CommandType.GET_LATEST_MSG ->

        if (isValid(command, principal)) {
          messageRepository.getLatestMessages(command.commandArg.toInt())
            .forEach { handleSelfMessage(it, principal) }
        }

      CommandType.UNKNOWN ->
        sendWarning("Invalid command", principal)
    }
  }

  fun handleMessage(message: Message, queue: String = MESSAGE_ROUTING_KEY) {
    rabbitTemplate.convertAndSend(RABBITMQ_EXCHANGE, queue, message)
  }

  fun handleSelfMessage(message: Message, principal: Principal) {
    ops.convertAndSendToUser(principal.name, WEBSOCKET_CHAT_ROOM, message)
  }

  fun sendWarning(msg: String, principal: Principal) {
    handleSelfMessage(Message(MessageType.WARN, msg, "system", ""), principal)
  }

  /**
   * Move to more specialized validation component, if gets more complex
   */
  private fun isValid(command: Command, principal: Principal): Boolean {

    command.commandArg.let {
      if (it.isEmpty() || !it.isNumeric() || it.toInt() !in 5..100) {
        sendWarning("Please provide a value between 5 and 100 when using the /history command", principal)
        return false
      }
    }
    return true
  }
}
