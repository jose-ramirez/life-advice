package org.josers.localstackdemo.messages.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.josers.localstackdemo.messages.SendLifeAdviceMessage;

import java.io.FileNotFoundException;

public interface AbstractMessageHandler<T> {

    Class<T> getMessageType();

    void handle(SendLifeAdviceMessage message) throws JsonProcessingException, FileNotFoundException;
}
