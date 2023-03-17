package com.example.kafkaconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class KafkaListenerService {

    private AtomicInteger atomicInteger = new AtomicInteger(1);

    @KafkaListener(topics = "${kafka-config.topic.topic1}")
    public void topic1Listener(ConsumerRecord<?, ?> record) {
        Optional message = Optional.ofNullable(record.value());
        log.info("+++++++++++++ kafka-second消费数据" + message.get() + " ++++++++++++++");
    }


    @KafkaListener(topics = "${kafka-config.topic.topic2}", groupId = "${kafka-config.group.group1}")
    public void topic2Group1(ConsumerRecord<?, ?> record) {
        log.info("消费者组{}收到消息{}", "group1", record.value());
    }

    @KafkaListener(topics = "${kafka-config.topic.topic2}", groupId = "${kafka-config.group.group2}")
    public void topic2Group2(ConsumerRecord<?, ?> record) {
        log.info("消费者组{}收到消息{}", "group2", record.value());
    }

    @KafkaListener(topics = "${kafka-config.topic.topic2}", groupId = "${kafka-config.group.group3}")
    public void topic2Group3(ConsumerRecord<?, ?> record) {
        log.error("模拟消费失败：{}", atomicInteger.getAndIncrement());
        int i = 1 / 0;
        log.info("消费者组{}收到消息{}", "group3", record.value());
    }
}
