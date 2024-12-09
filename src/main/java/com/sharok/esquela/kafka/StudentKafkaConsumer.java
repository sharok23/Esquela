package com.sharok.esquela.kafka;

import com.sharok.esquela.contract.StudentMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StudentKafkaConsumer {

    @KafkaListener(
            topics = "${spring.kafka.topic.student}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(StudentMessage message) {
        log.info("Message received -> {}", message);
    }
}
