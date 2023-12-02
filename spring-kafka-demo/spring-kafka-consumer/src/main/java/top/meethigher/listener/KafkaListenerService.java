package top.meethigher.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class KafkaListenerService {

    private AtomicInteger atomicInteger = new AtomicInteger(1);

    @KafkaListener(topics = "${kafka-config.topic.topic1}")
    public void topic1Listener(ConsumerRecord<?, ?> record,
                               Consumer<?, ?> consumer) {
        log.info("topic={},partition={},group={},data={}", record.topic(), record.partition(), consumer.groupMetadata().groupId(), record.value());

    }


    @KafkaListener(topics = "${kafka-config.topic.topic2}", groupId = "${kafka-config.group.group1}")
    public void topic2Group1(ConsumerRecord<?, ?> record, Consumer<?, ?> consumer) {
        log.info("topic={},partition={},group={},data={}", record.topic(), record.partition(), consumer.groupMetadata().groupId(), record.value());
    }

    @KafkaListener(topics = "${kafka-config.topic.topic2}", groupId = "${kafka-config.group.group2}")
    public void topic2Group2(ConsumerRecord<?, ?> record, Consumer<?, ?> consumer) {
        log.info("topic={},partition={},group={},data={}", record.topic(), record.partition(), consumer.groupMetadata().groupId(), record.value());
    }

    /**
     * 出错的重试机制支持两种
     * 1. 默认是快速重试10次，无间隔时间，如果最后还是失败，则自动commit
     * 2. Spring单独提供了RetryableTopic注解，及配套使用的DltHandler注解用于重试，及失败后回调。原理是单独建了一个retry topic用来存放失败的消息。
     */
    @KafkaListener(topics = "${kafka-config.topic.topic2}", groupId = "${kafka-config.group.group3}")
//    @org.springframework.kafka.annotation.RetryableTopic(attempts = "5", backoff = @org.springframework.retry.annotation.Backoff(delay = 5000, maxDelay = 10000, multiplier = 2))
    public void topic2Group3(ConsumerRecord<?, ?> record, Consumer<?, ?> consumer) {
        int i = 1 / 0;
        log.info("topic={},partition={},group={},data={}", record.topic(), record.partition(), consumer.groupMetadata().groupId(), record.value());
    }

    @DltHandler
    public void listenDlt(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          @Header(KafkaHeaders.OFFSET) long offset) {
        log.error("DLT Received: {} from {} @ {}", in, topic, offset);
    }
}