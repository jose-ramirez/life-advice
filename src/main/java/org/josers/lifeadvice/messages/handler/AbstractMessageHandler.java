package org.josers.lifeadvice.messages.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.josers.lifeadvice.messages.SendLifeAdviceMessage;

import java.io.FileNotFoundException;

public interface AbstractMessageHandler<T> {

    Class<T> getMessageType();

    void handle(SendLifeAdviceMessage message) throws JsonProcessingException, FileNotFoundException;
}
