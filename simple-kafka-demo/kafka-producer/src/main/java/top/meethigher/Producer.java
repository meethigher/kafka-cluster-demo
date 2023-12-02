package top.meethigher;

import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {

    private static Logger log = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) throws Exception {
        // 配置生产者属性
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.0.101:9092,10.0.0.102:9092,10.0.0.103:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // 配置 SASL_PLAINTEXT 认证
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"alice\" password=\"alice-secret\";");

        // 创建生产者实例
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 发送消息到指定主题
        String topic = "meethigher";
        String key = "timestamp";

        while (true) {
            // 发送消息
            String value= String.valueOf(System.currentTimeMillis());
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record, (metadata, exception) -> log.info("sent key={},value={} to topic={},partition={}", record.key(), record.value(), metadata.topic(), metadata.partition()));
            System.in.read();
        }

        // 关闭生产者
//        producer.close();
    }
}