package com.example.kafkaproducer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class Test {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendAsy(String topic, T message) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
//                System.out.println(topic + " - 生产者 发送消息失败：" + throwable.getMessage());
                System.out.println("发送失败");
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                ProducerRecord<String, Object> producerRecord = stringObjectSendResult.getProducerRecord();
                RecordMetadata recordMetadata = stringObjectSendResult.getRecordMetadata();
                Object value = producerRecord.value();
                int partition = recordMetadata.partition();
                System.out.println("发送->"+value+"到分区"+partition);
                //成功的处理
//                System.out.println(topic + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }
    @Scheduled(initialDelay = 3000,fixedDelay = 99999999)
    public void test(){
        int i=0;
        while(true){
            sendAsy("interface_monitoring_dynamic_allocation",++i+"");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
