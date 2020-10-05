package com.uncharted.chat.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val RABBITMQ_EXCHANGE = "chatExchange"

/**
 * Send a message to both RabbitMq queues
 */
const val MESSAGE_ROUTING_KEY = "chat.websocket.persistence"

@Configuration
class RabbitMqConfig {

  @Bean
  fun amqpTemplate(connectionFactory: ConnectionFactory): RabbitTemplate? {
    val rabbitTemplate = RabbitTemplate(connectionFactory)
    rabbitTemplate.messageConverter = jackson2Converter()
    return rabbitTemplate
  }

  @Bean
  fun amqpAdmin(connectionFactory: ConnectionFactory): AmqpAdmin {
    return RabbitAdmin(connectionFactory)
  }

  @Bean
  fun jackson2Converter(): MessageConverter {
    val objectMapper = jacksonObjectMapper().registerKotlinModule()
    return Jackson2JsonMessageConverter(objectMapper)
  }

  @Bean
  fun eventBinding(): Declarables {

    /**
     * Define a RabbitMq Exchange of type Topic
     * The Exchange will be automatically created
     */
    val topicExchange = TopicExchange(RABBITMQ_EXCHANGE, true, false)

    /**
     * This topic is used to propagate messages to Websocket clients
     */
    val webSocketQueue = Queue("chatMessageQueue", true)

    /**
     * This topic is used to persist messages on arrival
     */
    val persistenceQueue = Queue("chatPersistenceMessageQueue", true)

    return Declarables(
      webSocketQueue, persistenceQueue, topicExchange,
      /*
        Send messages to websocket queue using 'chat.hello.world' or 'chat.all.all'
       */
      BindingBuilder.bind(webSocketQueue).to(topicExchange).with("chat.*.*"),
      /*
        Send messages to persistence queue using 'chat.anything.persistence'
        To send a message to both queues use 'chat.all.persistence'
        To send a message to the websocket queue only use 'chat.all.nopersistence'
       */
      BindingBuilder.bind(persistenceQueue).to(topicExchange).with("chat.*.persistence")
    )
  }
}
