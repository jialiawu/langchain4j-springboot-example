package com.study.langchain4jspringboot.ai.assistant;

import dev.langchain4j.service.*;
import reactor.core.publisher.Flux;

//@AiService
public interface QwenAssistant {

    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    String chat(@V("role") String role,
                @V("question") String question,
                @V("extraInfo") String extraInfo);

    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    Flux<String> chatStreamFlux(@MemoryId String sessionId,
                                @V("role") String role,
                                @V("question") String question,
                                @V("extraInfo") String extraInfo);


    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt") // UserMessage会在检索增强时被带入到查询条件中，不要放太多无关的文本
    TokenStream chatStreamTokenStream(@MemoryId String sessionId,
                                      @V("role") String role,
                                      @V("question") String question,
                                      @V("extraInfo") String extraInfo);

}