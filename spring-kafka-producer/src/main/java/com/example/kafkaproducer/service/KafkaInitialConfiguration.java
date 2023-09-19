package com.example.kafkaproducer.service;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaInitialConfiguration {


    @Value("${kafka-config.topic.topic1}")
    private String topic1;

    @Bean
    public NewTopic initialTopic() {
        /**
         * 实际使用时，分区数应大于kafka broker数
         * 备份数与kafka broker数一致即可
         * 不论消费者或是生产者，都可以初始化topic
         */
        return new NewTopic(topic1, 1, (short) 1);
    }
}
