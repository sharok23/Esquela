package com.sharok.esquela.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    private final String student;

    public KafkaTopicConfig(@Value("${spring.kafka.topic.student}") String student) {
        this.student = student;
    }

    @Bean
    public NewTopic studentTopic() {
        return TopicBuilder.name(student).partitions(1).replicas(1).build();
    }
}
