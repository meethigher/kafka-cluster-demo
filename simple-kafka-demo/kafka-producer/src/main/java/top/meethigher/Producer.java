package top.meethigher;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

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

    public void produceByTransaction() throws Exception {
        int count = 1000;
        CountDownLatch latch = new CountDownLatch(count);
        // 开启幂等写入。事务模式要求该参数开启。默认false
        properties.put("enable.idempotence", "true");
        // 事务编号
        properties.put("transactional.id", "my-producer-ts-1");
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.initTransactions();
            producer.beginTransaction();
            for (int i = 0; i < count; i++) {
                String value = String.valueOf(System.currentTimeMillis());
                value = "aaa";
                /**
                 * 生产者发送数据给分区的策略
                 * 参考两个参数partitioner.ignore.keys和partitioner.class
                 */
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
                Callback callback = (metadata, exception) -> {
                    log.info("sent key={},value={} to topic={},partition={}",
                            record.key(), record.value(), metadata.topic(), metadata.partition());
                    latch.countDown();
                };
                producer.send(record, callback);
            }
            latch.await();
            producer.abortTransaction();// 回滚所有数据

            LockSupport.park();
        }
    }
}
