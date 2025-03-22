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
import redis.clients.jedis.search.schemafields.NumericField;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TextField;

import java.util.HashMap;

import static dev.langchain4j.data.document.Document.*;

/**
 * 向量存储配置
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
        //元数据Map，初始化时会根据这个map构建索引
        HashMap<String, SchemaField> metadataConfig = new HashMap<>() {{
            put(URL, TextField.of("$." + URL).as(URL).weight(1.0));
            put(FILE_NAME, TextField.of("$." + FILE_NAME).as(FILE_NAME).weight(1.0));
            put(ABSOLUTE_DIRECTORY_PATH, TextField.of("$." + ABSOLUTE_DIRECTORY_PATH).as(ABSOLUTE_DIRECTORY_PATH).weight(1.0));
            put("file_id", TextField.of("$." + "file_id").as("file_id").weight(1.0));
            //根据业务需求，添加额外的元数据字段，例如此处添加了文件权限scope字段
            put("scope", NumericField.of("$." + "scope").as("scope"));
        }};
        return RedisEmbeddingStore.builder()
                .host(redisProperties.getHost())
                .port(redisProperties.getPort())
                .user(redisProperties.getUsername())
                .password(redisProperties.getPassword())
                //索引名
                .indexName("my-embedding-index")
                //键前缀
                .prefix("myEmbedding:")
                //重要：保证创建的索引维度与embeddingModel一致
                .dimension(embeddingModel.dimension())
                /*
                元数据，可以理解为除了原文和向量数组外的其他信息，比如文件名，文件路径等
                    查询时把这些字段也带出来（不一定都有，不同的数文件来源有不同的字段）
                    metadataKeys传入所有的元数据字段名，这个api会把所有字段指定为TextField
                 */
//                .metadataKeys(CollUtil.newArrayList(URL,FILE_NAME,ABSOLUTE_DIRECTORY_PATH))
                /*
                metadataConfig自定一个元数据Map, key为元数据字段名，value为SchemaField，这个api可以自定义的指定字段类型
                * */
                .metadataConfig(metadataConfig)
                .build();
    }

}
