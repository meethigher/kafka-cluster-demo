package top.meethigher;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    private final Properties properties;

    private final String topic;

    public Producer(Properties properties, String topic) {
        this.properties = properties;
        this.topic = topic;
    }


    public void produce() throws Exception {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            while (true) {
                String value = String.valueOf(System.currentTimeMillis());
                /**
                 * 生产者发送数据给分区的策略
                 * 参考两个参数partitioner.ignore.keys和partitioner.class
                 */
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
                Callback callback = (metadata, exception) -> log.info("sent key={},value={} to topic={},partition={}",
                        record.key(), record.value(), metadata.topic(), metadata.partition());
                producer.send(record, callback);
                System.in.read();



            }
        }
    }
}
