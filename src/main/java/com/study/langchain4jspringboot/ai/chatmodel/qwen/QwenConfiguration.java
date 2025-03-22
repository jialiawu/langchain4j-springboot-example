package com.study.langchain4jspringboot.ai.chatmodel.qwen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.langchain4jspringboot.ai.assistant.QwenAssistant;
import com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties.ChatModelProperties;
import com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties.QwenProperties;
import com.study.langchain4jspringboot.ai.memory.DbChatMemoryStore;
import com.study.langchain4jspringboot.ai.tool.ITool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.client.WebClient;

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

    /**
     * 千问对话模型
     * @param properties 配置参数，参数含义见https://docs.langchain4j.dev/tutorials/model-parameters
     * @param chatModelListenerList 监听器集合
     * @return 千问对话模型
     */
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

    /**
     * 千问流式对话模型
     * @param properties 配置参数，参数含义见https://docs.langchain4j.dev/tutorials/model-parameters
     * @param chatModelListenerList 监听器集合
     * @return 千问对话模型
     */
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

    /**
     * 对话记忆存存储器
     * @return 对话记忆存存储器
     */
    @Bean
    public ChatMemoryStore dbChatMemoryStore() {
        return new DbChatMemoryStore();
    }


    /**
     * 创建service实例，通过JDK动态代理实现
     * @param qwenChatModel 千问对话模型
     * @param qwenStreamingChatModel 千问流式对话模型
     * @param dbChatMemoryStore 对话记忆存存储器
     * @param tools function call工具集
     * @param contentRetriever 内容检索器
     * @param retrievalAugmentor 内容检索增强器
     * @return service实例
     */
    @Bean
    @ConditionalOnProperty(QwenProperties.PREFIX + ".chat-model.api-key")
    public QwenAssistant qwenAssistant(QwenChatModel qwenChatModel,
                                       QwenStreamingChatModel qwenStreamingChatModel,
                                       ChatMemoryStore dbChatMemoryStore,
                                       Collection<ITool> tools,
                                       //ToolProvider mcptoolProvider,
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
//                .toolProvider(mcptoolProvider)
                //.contentRetriever(contentRetriever) //naive
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }


//    @Bean
//    public ToolProvider mcptoolProvider() {
//        McpTransport transport = new HttpMcpTransport.Builder()
//                .sseUrl("http://localhost:16070/mcp/message")
//                .logRequests(true)
//                .logResponses(true)
//                .build();
//        McpClient mcpClient = new DefaultMcpClient.Builder()
//                .transport(transport)
//                .build();
//        ToolProvider toolProvider = McpToolProvider.builder()
//                .mcpClients(List.of(mcpClient))
//                .build();
//        return toolProvider;
//    }

}
