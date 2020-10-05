package com.uncharted.chat.controller

import com.uncharted.chat.model.CommandType
import com.uncharted.chat.model.Message
import com.uncharted.chat.model.MessageType
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageUtilsTest {

  @Test
  fun `verify a command is detected`() {
    var isCommand = MessageUtils.isCommand(Message(MessageType.CHAT, "hello", "", ""))
    isCommand shouldBe false

    isCommand = MessageUtils.isCommand(Message(MessageType.CHAT, "/nick", "", ""))
    isCommand shouldBe true
  }

  @Test
  fun `verify a message can be converted into a command`() {

    val command = MessageUtils.asCommand(Message(MessageType.CHAT, "/history 10", "john", ""))
    command.type shouldBe CommandType.GET_LATEST_MSG
    command.commandArg shouldBe "10"
    command.sender shouldBe "john"
  }

  @Test
  fun `verify a message cant' be converted into a command when unknown type`() {

    val command = MessageUtils.asCommand(Message(MessageType.CHAT, "/nick gazza", "john", ""))
    command.type shouldBe CommandType.UNKNOWN
    command.commandArg shouldBe "gazza"
    command.sender shouldBe "john"
  }
}
