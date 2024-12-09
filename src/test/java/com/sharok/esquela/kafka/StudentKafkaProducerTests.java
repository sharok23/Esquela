package com.sharok.esquela.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sharok.esquela.BaseKafkaTests;
import com.sharok.esquela.model.Student;
import com.sharok.esquela.service.StudentService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class StudentKafkaProducerTests extends BaseKafkaTests {

    @Autowired private StudentKafkaProducer studentKafkaProducer;

    @Autowired private EmbeddedKafkaBroker embeddedKafka;

    @MockBean private StudentService studentService;

    @Test
    public void testProduce() {
        Student student =
                Student.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .uploadedAt(LocalDateTime.now())
                        .build();
        String message = "Student data processed";

        studentKafkaProducer.produce(message, student);

        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps("studentTestGroup", "true", embeddedKafka);
        consumerProps.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        consumerProps.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);

        ConsumerFactory<String, String> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "student-events-test");

        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(
                        consumer, "student-events-test", Duration.ofSeconds(10));

        assertNotNull(record, "Message not found in topic");
        assertTrue(record.value().contains(student.getId().toString()));
        assertTrue(record.value().contains(message));
        consumer.close();
    }

    @Test
    public void testProduceSerializationFailure() {
        Student student = mock(Student.class);
        when(student.getId()).thenThrow(new RuntimeException("Mocked exception"));
        String message = "Student data failed";

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> studentKafkaProducer.produce(message, student));
        assertEquals("Mocked exception", exception.getMessage());
    }
}
