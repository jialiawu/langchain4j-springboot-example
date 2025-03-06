package com.study.langchain4jspringboot.ai.embeddingmodel;

import com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties.EmbeddingModelProperties;
import com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties.QwenProperties;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-05-15:22
 * @description:com.study.langchain4jspringboot.ai.config.embedding
 * @version:1.0
 */
@Configuration
@EnableConfigurationProperties(QwenProperties.class)
public class EmbeddingModelConfiguration {


//    @Bean
//    public EmbeddingModel embeddingModel(){
//        return new BgeSmallEnV15QuantizedEmbeddingModel();
//    }

    @Bean
    @Primary
    public EmbeddingModel qwenEmbeddingModel (QwenProperties qwenProperties){
        EmbeddingModelProperties properties = qwenProperties.getEmbeddingModel();
        return QwenEmbeddingModel.builder()
                .apiKey(properties.getApiKey())
                .modelName(properties.getModelName())
                .build();
    }

}
