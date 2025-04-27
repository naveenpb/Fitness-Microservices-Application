package com.fitness.activityservice.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
//    select amqpcore queue only , not any other queue , tell in video why we n
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.queue.name}")
    private String queuename;

    @Bean
    public Queue activityQueue(){
        return new Queue(queuename,true); //durable means even if rabbitmq server restarts it will hold the message.
    }

    @Bean
    public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue , DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    //this will do the job of converting java object to json , if we donot add the converter we need to manually serialize and deseralize the message so its better.
    //Serialization is the process of converting an object (in memory) into a byte stream so it can be saved to a file, sent over a network, or stored in a database.
    // it’s the process of converting a byte stream (from a file, network, etc.) back into an actual object in memory.
    //Yes, serialization and deserialization are used when packaging and unpackaging messages in messaging systems like RabbitMQ,
}
