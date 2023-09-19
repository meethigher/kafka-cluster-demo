package com.example.kafkaproducer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class KafkaProduceService {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka-config.topic.topic1}")
    private String topic1;

    @Value("${kafka-config.topic.topic2}")
    private String topic2;

    private AtomicInteger i = new AtomicInteger(0);

    public <T> void sendAsy(String topic, T message) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("发送消息失败");
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                ProducerRecord<String, Object> producerRecord = stringObjectSendResult.getProducerRecord();
                RecordMetadata recordMetadata = stringObjectSendResult.getRecordMetadata();
                Object value = producerRecord.value();
                int partition = recordMetadata.partition();
                log.info("发送->" + value + "到分区" + partition);
            }
        });
    }

    /**
     * FixedRate：不管前一个任务执行是否执行完毕，总是匀速执行新任务。
     * FixedDelay：当前一个任务执行完毕后，等待固定的时间间隔，再执行下一次任务。
     */
    @Scheduled(initialDelay = 3000, fixedRate = 1000)
    public void test() {
        sendAsy(topic1, i.getAndIncrement() + "");
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 60 * 60)
    public void test1() {
        sendAsy(topic2, "测试消费者组");
    }
}
