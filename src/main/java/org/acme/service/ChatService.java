package org.acme.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface ChatService{

    //@SystemMessage("You are a professional poet")
    @UserMessage("""
        Write a poem about {topic}. The poem should be {lines} lines long.
    """)

    String writePoem(String topic, int lines);

    String askAnything(@MemoryId int memoryId, @UserMessage String message);

    String askRag(@UserMessage String message);

}

