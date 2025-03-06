package com.study.langchain4jspringboot.ai.embeddingstore;

import cn.hutool.core.collection.CollUtil;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static dev.langchain4j.data.document.Document.*;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-05-15:20
 * @description:com.study.langchain4jspringboot.ai.config.store
 * @version:1.0
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class EmbeddingStoreConfiguration {

//    @Bean
//    public EmbeddingStore<TextSegment> inMemoryEmbeddingStore() {
//        return new InMemoryEmbeddingStore<>();
//    }
    @Bean
    @Primary
    public EmbeddingStore<TextSegment> redisEmbeddingStore(RedisProperties redisProperties,
                                                           EmbeddingModel embeddingModel){
        return RedisEmbeddingStore.builder()
                .host(redisProperties.getHost())
                .port(redisProperties.getPort())
                .user(redisProperties.getUsername())
                .password(redisProperties.getPassword())
                .prefix("myEmbedding:")
                //重要：保证创建的索引维度与embeddingModel一致
                .dimension(embeddingModel.dimension())
                //查询时把这些字段也带出来（不一定都有，不同的数据源有不同的字段）
                .metadataKeys(CollUtil.newArrayList(URL,FILE_NAME,ABSOLUTE_DIRECTORY_PATH))
                .build();
    }

}
