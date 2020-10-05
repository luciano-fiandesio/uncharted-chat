package com.uncharted.chat.listener

import com.uncharted.chat.model.Message
import com.uncharted.chat.service.MessageRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
class ChatListener(val ops: SimpMessageSendingOperations, val messageRepo: MessageRepository) {

  @RabbitListener(queues = ["chatMessageQueue"])
  fun listen1(msg: Message) {
    ops.convertAndSend("/topic/public", msg)
  }

  @RabbitListener(queues = ["chatPersistenceMessageQueue"])
  fun listen2(msg: Message) {
    messageRepo.addMessage(msg)
  }
}
