package com.example.kafkatestconsumersecond.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaListenerService {
    @KafkaListener(topics = "${kafka-config.topic.interface-monitoring-dynamic-allocation}")
    public void interfaceMonitorDynamicAllocation(ConsumerRecord<?, ?> record) {
        Optional message = Optional.ofNullable(record.value());
        System.out.println("+++++++++++++ kafka-second消费数据"+message.get()+" ++++++++++++++");
    }
}
