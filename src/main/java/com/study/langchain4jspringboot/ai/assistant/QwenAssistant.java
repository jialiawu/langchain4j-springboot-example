package com.study.langchain4jspringboot.ai.assistant;

import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

//@AiService
public interface QwenAssistant {

    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    String chat(@V("role") String role,
                @V("userMessage") String userOriginMessage,
                @V("extraInfo") String extraInfo);

    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    Flux<String> chatStream(@MemoryId Long sessionId,
                            @V("role") String role,
                            @V("userMessage") String userOriginMessage,
                            @V("extraInfo") String extraInfo);

    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    TokenStream chatStream1(@MemoryId Long sessionId,
                           @V("role") String role,
                           @V("userMessage") String userOriginMessage,
                           @V("extraInfo") String extraInfo);

}