package org.acme.rest;

import java.util.List;

import org.acme.service.ChatService;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/chat")
public class ChatResource {

    @GET
    @Path("/poem")
    @Produces(MediaType.TEXT_PLAIN)
    public String poem() {
        
        return chatService.writePoem("spring", 4);

    }

    @POST
    @Path("/ask")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String ask(String message) {
        
        return chatService.askAnything(1, message);

    }

    @POST
    @Path("/ask/rag")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String askWithRag(String message) {
        
        try{
            String ragFilesPath = "/opt/liferay/tests/rag-files";
            List<Document> documents = FileSystemDocumentLoader.loadDocuments(ragFilesPath);
            
            InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
            EmbeddingStoreIngestor.ingest(documents, embeddingStore);

            OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey("demo")
            .modelName("gpt-4o-mini")
            .temperature(0.3)
            .logRequests(true)
            .logResponses(true)
            .build();

            ChatService assistant = AiServices.builder(ChatService.class)
            .chatLanguageModel(model)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
            .build();
    
            return assistant.askRag(message);

        }catch(Exception ex){
            ex.printStackTrace();
            return "RAG unavailable : " + ex.getMessage();
        }

    }

    @Inject
    ChatService chatService;

}
