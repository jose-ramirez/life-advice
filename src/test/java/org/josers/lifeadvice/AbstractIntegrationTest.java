package org.josers.lifeadvice;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = AbstractIntegrationTest.LocalstackInitializer.class, classes = AbstractIntegrationTest.AWSTestConfig.class)
public class AbstractIntegrationTest {

    @Value("${cloud.aws.sqs.in-queue-name}")
    String inQueueName;

    private static final String localstackImageName = "localstack/localstack:0.10.0";

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse(localstackImageName))
        .withServices(SQS)
        .withExposedPorts(4576)
        .withEnv("DEFAULT_REGION", "us-east-1")
        .withEnv("LOCALSTACK_HOSTNAME", "localhost")
        .withEnv("HOSTNAME", "localhost");

    @TestConfiguration
    static class AWSTestConfig {
        @Bean
        public AmazonSQSAsync amazonSQS() {
            return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(localstack.getDefaultCredentialsProvider())
                .withEndpointConfiguration(localstack.getEndpointConfiguration(SQS))
                .build();
        }
    }

    static class LocalstackInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            var localstackProperties = TestPropertyValues.of(
                    "cloud.aws.sqs.endpoint=" + localstack.getEndpointConfiguration(SQS).getServiceEndpoint()
            );
            localstackProperties.applyTo(applicationContext);
        }
    }
}
