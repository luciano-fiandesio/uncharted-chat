package com.uncharted.chat.service

import com.uncharted.chat.config.StompPrincipal
import com.uncharted.chat.model.Command
import com.uncharted.chat.model.CommandType
import com.uncharted.chat.model.Message
import com.uncharted.chat.model.MessageType
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageServiceTest {

  private val rabbitTemplate: RabbitTemplate = mockk()
  private val websocketSendOps: SimpMessageSendingOperations = mockk()

  private val messageService = MessageService(rabbitTemplate, MessageRepository(), websocketSendOps)

  @BeforeEach
  fun init() {
    clearAllMocks()
  }

  @Test
  fun `verify message is sent to rabbitmq`() {
    every { rabbitTemplate.convertAndSend(any(), any(), any<Message>()) } answers { nothing }

    val msg = Message(MessageType.CHAT, "hello world", "luke", "")
    messageService.handleMessage(msg)

    verify { rabbitTemplate.convertAndSend("chatExchange", "chat.websocket.persistence", msg) }
  }

  @ParameterizedTest
  @ValueSource(strings = ["", "a", "3", "101"])
  fun `verify warning message is sent on invalid history command value`(commandArg: String) {
    every { websocketSendOps.convertAndSendToUser(any(), any(), any<Message>()) } answers { nothing }
    val principal = StompPrincipal("aaaaaa")
    val cmd = Command(CommandType.GET_LATEST_MSG, commandArg, "", "")

    messageService.handleCommand(cmd, principal)

    verify {
      websocketSendOps.convertAndSendToUser(
        principal.name, "/topic/public",
        Message(
          MessageType.WARN,
          "Please provide a value between 5 and 100 when using the /history command", "system", ""
        )
      )
    }
  }
}
