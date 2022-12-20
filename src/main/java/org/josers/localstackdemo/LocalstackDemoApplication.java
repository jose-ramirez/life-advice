package org.josers.localstackdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootApplication(
	exclude = {
		org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class
	}
)
public class LocalstackDemoApplication {

	@Bean
	public Random randomSelector() {
		return new Random();
	}
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public List<String> lifeAdvices() {
		try (var messagesReader = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:messages.txt")))){
			var messagesList = new ArrayList<String>();
			while (true) {
				var s = messagesReader.readLine();
				if (s != null) {
					messagesList.add(s);
				}else break;
			}
			return messagesList;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(LocalstackDemoApplication.class, args);
	}

}
