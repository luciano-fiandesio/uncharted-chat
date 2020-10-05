package com.uncharted.chat.service

import com.uncharted.chat.model.Message
import mu.KotlinLogging
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MessageRepository {
  companion object {
    val messageStorage = CircularFifoQueue<Message>(100)
  }

  fun addMessage(msg: Message) {

    messageStorage.add(msg)
    logger.debug { "current message storage size: ${messageStorage.size}" }
  }

  fun getLatestMessages(size: Int): List<Message> {

    return messageStorage.toList().subList(0, (if (messageStorage.size < size) messageStorage.size else size))
  }
}
