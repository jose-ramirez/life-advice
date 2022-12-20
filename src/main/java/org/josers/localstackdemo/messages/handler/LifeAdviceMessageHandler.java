package org.josers.localstackdemo.messages.handler;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.josers.localstackdemo.LifeAdviceMessageService;
import org.josers.localstackdemo.messages.ForwardLifeAdviceMessage;
import org.josers.localstackdemo.messages.SendLifeAdviceMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LifeAdviceMessageHandler implements AbstractMessageHandler<SendLifeAdviceMessage> {

    @Value("${cloud.aws.sqs.out-queue-url}")
    private String outQueueUrl;

    private final ObjectMapper mapper;

    private final AmazonSQSAsync awsClient;

    private final LifeAdviceMessageService lifeAdviceMessageService;

    @Override
    public Class<SendLifeAdviceMessage> getMessageType() {
        return SendLifeAdviceMessage.class;
    }

    @Override
    @SqsListener(value = "${cloud.aws.sqs.in-queue-url}")
    public void handle(SendLifeAdviceMessage myMsg) throws JsonProcessingException {
        var fwdMessage = ForwardLifeAdviceMessage.builder()
                .from(myMsg.getFrom())
                .to(myMsg.getTo())
                .message(lifeAdviceMessageService.getMessage())
                .build();
        log.info("Received: {}", mapper.writeValueAsString(myMsg));
        log.info("Sending {}", mapper.writeValueAsString(fwdMessage));
        awsClient.sendMessage(outQueueUrl, mapper.writeValueAsString(fwdMessage));
    }
}
