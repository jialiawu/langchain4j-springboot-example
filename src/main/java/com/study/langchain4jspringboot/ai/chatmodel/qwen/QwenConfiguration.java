package com.study.langchain4jspringboot.ai.chatmodel.qwen;

import com.study.langchain4jspringboot.ai.assistant.QwenAssistant;
import com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties.ChatModelProperties;
import com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties.QwenProperties;
import com.study.langchain4jspringboot.ai.memory.DbChatMemoryStore;
import com.study.langchain4jspringboot.ai.tool.ITool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-17:12
 * @description:com.study.langchain4jspringboot.config
 * @version:1.0
 */
@Configuration
@EnableConfigurationProperties(QwenProperties.class)
public class QwenConfiguration {

    @Bean
    @ConditionalOnProperty(QwenProperties.PREFIX + ".chat-model.api-key")
    QwenChatModel qwenChatModel(QwenProperties properties, List<ChatModelListener> chatModelListenerList) {
        ChatModelProperties chatModelProperties = properties.getChatModel();
        return QwenChatModel.builder()
                .baseUrl(chatModelProperties.getBaseUrl())
                .apiKey(chatModelProperties.getApiKey())
                .modelName(chatModelProperties.getModelName())
                .temperature(chatModelProperties.getTemperature())
                .topP(chatModelProperties.getTopP())
                .topK(chatModelProperties.getTopK())
                .enableSearch(chatModelProperties.getEnableSearch())
                .seed(chatModelProperties.getSeed())
                .repetitionPenalty(chatModelProperties.getRepetitionPenalty())
                .temperature(chatModelProperties.getTemperature())
                .stops(chatModelProperties.getStops())
                .maxTokens(chatModelProperties.getMaxTokens())
                .listeners(chatModelListenerList)
                .build();
    }

    @ConditionalOnProperty(QwenProperties.PREFIX + ".chat-model.api-key")
    @Bean
    public QwenStreamingChatModel qwenStreamingChatModel(QwenProperties properties, List<ChatModelListener> chatModelListenerList) {
        ChatModelProperties chatModelProperties = properties.getChatModel();
        return QwenStreamingChatModel.builder()
                .baseUrl(chatModelProperties.getBaseUrl())
                .apiKey(chatModelProperties.getApiKey())
                .modelName(chatModelProperties.getModelName())
                .temperature(chatModelProperties.getTemperature())
                .topP(chatModelProperties.getTopP())
                .topK(chatModelProperties.getTopK())
                .enableSearch(chatModelProperties.getEnableSearch())
                .seed(chatModelProperties.getSeed())
                .repetitionPenalty(chatModelProperties.getRepetitionPenalty())
                .temperature(chatModelProperties.getTemperature())
                .stops(chatModelProperties.getStops())
                .maxTokens(chatModelProperties.getMaxTokens())
                .listeners(chatModelListenerList)
                .build();
    }

    @Bean
    public ChatMemoryStore dbChatMemoryStore(){
        return new DbChatMemoryStore();
    }


    @Bean
    @ConditionalOnProperty(QwenProperties.PREFIX + ".chat-model.api-key")
    public QwenAssistant qwenAssistant(QwenChatModel qwenChatModel,
                                       QwenStreamingChatModel qwenStreamingChatModel,
                                       ChatMemoryStore dbChatMemoryStore,
                                       Collection<ITool> tools,
                                       ContentRetriever contentRetriever,
                                       RetrievalAugmentor retrievalAugmentor) {

        return AiServices.builder(QwenAssistant.class)
                .streamingChatLanguageModel(qwenStreamingChatModel)
                .chatLanguageModel(qwenChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(10)
                        .chatMemoryStore(dbChatMemoryStore)
                        .build())
                .tools(tools.toArray()) //注意这个地方传集合的话必须传Collection<Object>，不能传非Object类型的集合
                //.contentRetriever(contentRetriever) //naive
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

}
