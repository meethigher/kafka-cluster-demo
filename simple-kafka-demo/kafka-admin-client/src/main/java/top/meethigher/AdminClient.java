package top.meethigher;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;

public class AdminClient {
    private final Properties properties;


    public AdminClient(Properties properties) {
        this.properties = properties;
    }

    /**
     * 实际使用时，分区数应大于kafka broker数
     * 备份数与kafka broker数一致即可
     * 不论消费者或是生产者，都可以初始化topic，参考org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration#kafkaAdmin()，本质上都依赖于org.apache.kafka.clients.admin.AdminClient
     * 但要注意服务端开启自动创建auto.create.topics.enable=true，默认就为true
     */
    public void createTopic(String topicName, int numPartitions, int replicationFactor) {
        try (org.apache.kafka.clients.admin.AdminClient adminClient = KafkaAdminClient.create(properties)) {
            adminClient.createTopics(Collections.singletonList(
                    new NewTopic(
                            topicName,
                            numPartitions,
                            (short) replicationFactor)));
        }
    }

}
