package org.josers.localstackdemo.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
public class AWSConfiguration {

    @Value("${cloud.aws.sqs.polling-timeout}")
    private Integer pollingTimeout;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync asyncSQS) {
        return new QueueMessagingTemplate(asyncSQS);
    }

    @Bean
    public MessageConverter queueMessageHandlerFactory(ObjectMapper mapper){
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setStrictContentTypeMatch(false);
        converter.setObjectMapper(mapper);
        return converter;
    }

    //This is to make tests run faster, as explained here: https://github.com/spring-attic/spring-cloud-aws/issues/504
    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSQSAsync);
        factory.setWaitTimeOut(pollingTimeout); // less than 10 sec when testing
        return factory;
    }
}
