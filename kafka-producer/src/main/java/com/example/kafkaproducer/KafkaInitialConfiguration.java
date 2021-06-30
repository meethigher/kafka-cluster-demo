package com.example.kafkaproducer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaInitialConfiguration {
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("interface_monitoring_dynamic_allocation",3, (short) 2 );
    }
}
