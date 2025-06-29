package top.meethigher;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    private final Properties properties;
    private final String topic;

    public Consumer(Properties properties, String topic) {
        this.properties = properties;
        this.topic = topic;
    }

    public void consume() throws Exception {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties)) {
            consumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("consumed record with key={}, value={}",
                            record.key(),
                            record.value());
                }
            }
        }
    }
}
