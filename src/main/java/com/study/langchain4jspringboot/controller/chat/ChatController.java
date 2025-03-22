package com.study.langchain4jspringboot.controller.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.langchain4jspringboot.ai.assistant.QwenAssistant;
import com.study.langchain4jspringboot.controller.chat.vo.RetrievedRecordResponse;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.stream.Collectors;

import static com.study.langchain4jspringboot.convert.ContentConvert.convertToRecord;
import static dev.langchain4j.data.document.Document.*;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-10:32
 * @description:com.study.langchain4jspringboot.cotroller
 * @version:1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    /**
     * AI SERVICE
     */
    private final QwenAssistant qwenAssistant;

    private final ObjectMapper objectMapper;

    /**
     * 创建一个新的会话
     *
     * @return 会话id
     */
    @GetMapping("/new-session")
    public String newSession() {
        //此处可将session于用户做关联，以保存一些常用的信息
        return IdUtil.simpleUUID();
    }

    /**
     * 流式聊天
     *
     * @param sessionId       会话id
     * @param role            设定角色
     * @param question        原始问题
     * @param webSearchEnable 是否开启网页搜索
     * @param extraInfo       额外信息（暂未实现）
     * @return
     */
    @GetMapping(value = "/stream/flux", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStreamFlux(@RequestParam(value = "sessionId") String sessionId,
                                       @RequestParam(value = "role", required = false, defaultValue = "智能问答助手") String role,
                                       @RequestParam(value = "question") String question,
                                       @RequestParam(value = "webSearchEnable", required = false, defaultValue = "false") Boolean webSearchEnable,
                                       @RequestParam(value = "extraInfo", required = false, defaultValue = "") String extraInfo) {
        return qwenAssistant.chatStreamFlux(sessionId, role, question, extraInfo);
    }

    /**
     * 流式聊天（SSE），方便前端根据KEY渲染不同的内容
     *
     * @param sessionId       会话id
     * @param role            设定角色
     * @param question        原始问题
     * @param webSearchEnable 是否开启网页搜索
     * @param extraInfo       额外信息（暂未实现）
     * @return
     */
    @GetMapping(value = "/stream/sse", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamSse(@RequestParam(value = "sessionId") String sessionId,
                                                       @RequestParam(value = "role", required = false, defaultValue = "智能问答助手") String role,
                                                       @RequestParam(value = "question") String question,
                                                       @RequestParam(value = "webSearchEnable", required = false, defaultValue = "false") Boolean webSearchEnable,
                                                       @RequestParam(value = "extraInfo", required = false, defaultValue = "") String extraInfo) {
        //参考的源码里的写法
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();

        TokenStream tokenStream = qwenAssistant.chatStreamTokenStream(sessionId, role, question, extraInfo);
        //rag回调
        tokenStream.onRetrieved(contents ->
                //前端可监听Retrieved时间，展示命中的文件
                sink.tryEmitNext(ServerSentEvent.builder(toJson(convertToRecord(contents))).event("Retrieved").build()));
        //消息片段回调
        tokenStream.onPartialResponse(partialResponse -> sink.tryEmitNext(ServerSentEvent.builder(partialResponse).event("AiMessage").build()));
        //错误回调
        tokenStream.onError(sink::tryEmitError);
        //结束回调
        tokenStream.onCompleteResponse(aiMessageResponse -> sink.tryEmitComplete());
        tokenStream.start();
        return sink.asFlux();
    }

    private <D> String toJson(D t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
