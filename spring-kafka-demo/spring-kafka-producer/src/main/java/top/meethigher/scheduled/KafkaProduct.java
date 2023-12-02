package top.meethigher.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaProduct {


    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka-config.topic.topic1}")
    private String topic1;

    @Value("${kafka-config.topic.topic2}")
    private String topic2;


    public void sendAsync(String topic, Object data) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error(ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                ProducerRecord<String, Object> record = result.getProducerRecord();
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("key={},value={},partition={}", record.key(), record.value(), metadata.partition());
            }
        });
    }


    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.SECONDS)
    public void produce1() {
        sendAsync(topic1, "hello world");
    }


    @RestController
    public class TestController {

        @RequestMapping("/**")
        public String test() {
            sendAsync(topic2, "test bug");
            return "已发送" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
    }
}
