package org.josers.localstackdemo;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.josers.localstackdemo.messages.ForwardLifeAdviceMessage;
import org.josers.localstackdemo.messages.SendLifeAdviceMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
class AppIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Autowired
	private AmazonSQSAsync amazonSQS;

	@Value("${cloud.aws.sqs.in-queue-name}")
	private String inQueueName;

	@Value("${cloud.aws.sqs.out-queue-url}")
	private String outQueueUrl;

	@BeforeAll
	static void beforeAll() throws IOException, InterruptedException {
		localstack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "send-lifeadvice-queue");
		localstack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "forward-lifeadvice-queue");
	}

	@Test
	@SneakyThrows
	void testMessageHandlerHandlesMessages() {

		SendLifeAdviceMessage healthRelatedAdvice = SendLifeAdviceMessage.builder()
				.from("from-id")
				.to("to-id")
				.build();

		queueMessagingTemplate.convertAndSend(inQueueName, healthRelatedAdvice);

		await()
			.pollDelay(3, SECONDS)
			.atMost(5, SECONDS)
			.untilAsserted(() -> assertMessageIsSent(healthRelatedAdvice));
	}

	@SneakyThrows
	private void assertMessageIsSent(SendLifeAdviceMessage request) {
		var receivedMessages = amazonSQS.receiveMessage(outQueueUrl).getMessages();
		assertEquals(1, receivedMessages.size());
		var convertedMessage = mapper.readValue(receivedMessages.get(0).getBody(), ForwardLifeAdviceMessage.class);
		assertNotNull(convertedMessage.getMessage());
		assertEquals(request.getFrom(), convertedMessage.getFrom());
		assertEquals(request.getTo(), convertedMessage.getTo());
	}
}
