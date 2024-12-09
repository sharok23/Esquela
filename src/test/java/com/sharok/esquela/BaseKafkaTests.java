package com.sharok.esquela;

import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@EmbeddedKafka(
        topics = {"student-events-test"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@DirtiesContext
public class BaseKafkaTests {}
