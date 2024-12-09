package com.sharok.esquela.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @SneakyThrows
    public void sendKafkaMessage(String topic, Object message) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, message);
        RecordMetadata metadata = kafkaTemplate.send(record).get().getRecordMetadata();
        log.info("Message sent successfully to topic {} -> metadata {}", topic, metadata);
        log.info(String.format("Message sent %s", message));
    }

    public void flush() {
        kafkaTemplate.flush();
    }
}
