package com.ubi.masterservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TopicConfig {

    private String topicName="master_topic_add";

    private String topicDelete="master_delete";

    private String topicUpdateName="master_topic_update";

    private String topicPartialUpdate="master_topic_student_patch";

    private String topicPromote="master_topic_promote";

    private String topicDemote="master_topic_demote";

    private String topicVerifyByTeacher="master_topic_verify_by_teacher";

    private String topicVerifyByPrincipal="master_topic_verify_by_principal";

    private String bootstrapServers="localhost:9092";

    @Bean
    public NewTopic topic()
    {
        return TopicBuilder.name(topicName)
                .partitions(5)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topic2()
    {
        return TopicBuilder.name(topicName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic3()
    {
        return TopicBuilder.name(topicName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic4()
    {
        return TopicBuilder.name(topicName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic5()
    {
        return TopicBuilder.name(topicName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic6()
    {
        return TopicBuilder.name(topicDelete)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic7()
    {
        return TopicBuilder.name(topicDelete)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic8()
    {
        return TopicBuilder.name(topicDelete)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic9()
    {
        return TopicBuilder.name(topicDelete)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic10()
    {
        return TopicBuilder.name(topicDelete)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic11()
    {
        return TopicBuilder.name(topicUpdateName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic12()
    {
        return TopicBuilder.name(topicUpdateName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic13()
    {
        return TopicBuilder.name(topicUpdateName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic14()
    {
        return TopicBuilder.name(topicUpdateName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic15()
    {
        return TopicBuilder.name(topicUpdateName)
                .partitions(5)
                .compact().build();
    }

    @Bean
    public NewTopic topic16()
    {
        return TopicBuilder.name(topicPartialUpdate)
                .partitions(4)
                .compact().build();
    }

    @Bean
    public NewTopic topic17()
    {
        return TopicBuilder.name(topicPartialUpdate)
                .partitions(4)
                .compact().build();
    }

    @Bean
    public NewTopic topic18()
    {
        return TopicBuilder.name(topicPartialUpdate)
                .partitions(4)
                .compact().build();
    }

    @Bean
    public NewTopic topic19()
    {
        return TopicBuilder.name(topicPartialUpdate)
                .partitions(4)
                .compact().build();
    }

    @Bean
    public NewTopic topic20()
    {
        return TopicBuilder.name(topicPromote)
                .partitions(1)
                .compact().build();
    }

    @Bean
    public NewTopic topic21()
    {
        return TopicBuilder.name(topicDemote)
                .partitions(1)
                .compact().build();
    }

    @Bean
    public NewTopic topic22()
    {
        return TopicBuilder.name(topicVerifyByTeacher)
                .partitions(1)
                .compact().build();
    }

    @Bean
    public NewTopic topic23()
    {
        return TopicBuilder.name(topicVerifyByPrincipal)
                .partitions(1)
                .compact().build();
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}


