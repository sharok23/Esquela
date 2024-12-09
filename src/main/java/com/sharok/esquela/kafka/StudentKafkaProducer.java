package com.sharok.esquela.kafka;

import com.sharok.esquela.contract.StudentMessage;
import com.sharok.esquela.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudentKafkaProducer extends KafkaProducer {
    private final String studentTopic;

    public StudentKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.topic.student}") String studentTopic) {
        super(kafkaTemplate);
        this.studentTopic = studentTopic;
    }

    public void produce(String messages, Student student) {
        StudentMessage message =
                new StudentMessage(messages, student.getId(), student.getUploadedAt());
        log.info("Sending message to {}", studentTopic);
        sendKafkaMessage(studentTopic, message);
        log.info("Message published to {}", studentTopic);
    }
}
