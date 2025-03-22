package com.study.langchain4jspringboot.ai.assistant;

import dev.langchain4j.service.*;
import reactor.core.publisher.Flux;

/**
 * AiService，聊天API入口
 *
 * @author JiaLia
 */
//@AiService
public interface QwenAssistant {

    /**
     * 聊天
     * @param role 设定角色，通过@V注解替换掉system-message.txt中的role变量
     * @param question 原始问题，通过@V注解替换掉user-message.txt中的question变量
     * @param extraInfo 额外信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    @Deprecated
    String chat(
            @V("role") String role,
            @V("question") String question,
            @V("extraInfo") String extraInfo);

    /**
     * 聊天流式输出
     * @param sessionId 会话id，通过@MemoryId指定
     * @param role 设定角色，通过@V注解替换掉system-message.txt中的role变量
     * @param question 原始问题，通过@V注解替换掉user-message.txt中的question变量
     * @param extraInfo 额外信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/system-message.txt")
    @UserMessage(fromResource = "prompt/user-message.txt")
    Flux<String> chatStreamFlux(
            @MemoryId String sessionId,
            @V("role") String role,
            @V("question") String question,
            @V("extraInfo") String extraInfo);


    /**
     * 聊天流式输出，返回TokenStream
     * @param sessionId 会话id，通过@MemoryId指定
     * @param role 设定角色，通过@V注解替换掉system-message.txt中的role变量
     * @param question 原始问题，通过@V注解替换掉user-message.txt中的question变量
     * @param extraInfo 额外信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/system-message.txt")
    // 注意：UserMessage会在检索增强时被带入到查询条件中，所以尽量不要放太多无关的文本。如果需要可以在RAG中使用ContentInjector
    @UserMessage(fromResource = "prompt/user-message.txt")
    TokenStream chatStreamTokenStream(
            @MemoryId String sessionId,
            @V("role") String role,
            @V("question") String question,
            @V("extraInfo") String extraInfo);

}