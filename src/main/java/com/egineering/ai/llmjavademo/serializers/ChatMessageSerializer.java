package com.egineering.ai.llmjavademo.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.langchain4j.data.message.ChatMessage;

import java.io.IOException;

public class ChatMessageSerializer extends JsonSerializer<ChatMessage> {

    @Override
    public void serialize(ChatMessage chatMessage, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("text", chatMessage.text());
        jsonGenerator.writeStringField("type", chatMessage.type().name());
        jsonGenerator.writeEndObject();
    }
}
