package com.study.langchain4jspringboot.controller.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.langchain4jspringboot.ai.assistant.QwenAssistant;
import com.study.langchain4jspringboot.controller.chat.dto.RetrievedRecord;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.stream.Collectors;

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

    private final QwenAssistant qwenAssistant;

    private final ObjectMapper objectMapper;

    @GetMapping("/new-session")
    public Long newSession() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        return snowflake.nextId();
    }

    @GetMapping(value = "/stream/flux", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStreamFlux(@RequestParam(value = "sessionId") Long sessionId,
                                   @RequestParam(value = "message") String message,
                                   @RequestParam(value = "role", required = false, defaultValue = "智能问答助手") String role,
                                   @RequestParam(value = "extraInfo", required = false, defaultValue = "") String extraInfo) {
        return qwenAssistant.chatStream(sessionId, role, message, extraInfo);
    }

    @GetMapping(value = "/stream/sse", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamSse(@RequestParam(value = "sessionId") Long sessionId,
                                                     @RequestParam(value = "message") String message,
                                                     @RequestParam(value = "role", required = false, defaultValue = "智能问答助手") String role,
                                                     @RequestParam(value = "extraInfo", required = false, defaultValue = "") String extraInfo) {
        //参考的源码里的写法
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();

        TokenStream tokenStream = qwenAssistant.chatStream1(sessionId, role, message, extraInfo);
        tokenStream.onPartialResponse(partialResponse -> sink.tryEmitNext(ServerSentEvent.builder(partialResponse).event("AiMessage").build()));
        tokenStream.onRetrieved(contents ->
                sink.tryEmitNext(ServerSentEvent.builder(toJson(convert(contents))).event("Retrieved").build()));
        tokenStream.onError(sink::tryEmitError);
        tokenStream.onCompleteResponse(aiMessageResponse -> sink.tryEmitComplete());
        tokenStream.start();

        return sink.asFlux();
    }

    private Set<RetrievedRecord> convert(List<Content> contents) {
        if (CollUtil.isEmpty(contents)) {
            return Collections.emptySet();
        }
        TreeSet<Content> urlContentSet = contents.stream()
                .filter(content ->
                        content.textSegment() != null && content.textSegment().metadata() != null
                                && content.textSegment().metadata().containsKey(URL))
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(content -> content.textSegment().metadata().getString(URL)))));
        if (CollUtil.isEmpty(urlContentSet)) {
            return Collections.emptySet();
        }
        return urlContentSet.stream().map(content -> {
            String url = content.textSegment().metadata().getString(URL);
            String fileName = Optional.ofNullable(content.textSegment().metadata().getString(FILE_NAME)).orElse(FileUtil.getName(url));
            String absolutePath = Optional.ofNullable(content.textSegment().metadata().getString(ABSOLUTE_DIRECTORY_PATH)).orElse("");
            return new RetrievedRecord(url, fileName, absolutePath);
        }).collect(Collectors.toSet());
    }

    private <D> String toJson(D t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
