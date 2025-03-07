package com.study.langchain4jspringboot.ai.websearch;

import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.web.search.WebSearchEngine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-07-9:52
 * @description:com.study.langchain4jspringboot.ai.websearch
 * @version:1.0
 */
@Configuration
@EnableConfigurationProperties(SearxngProperties.class)
public class WebSearchConfiguration {

    @Bean
    public WebSearchEngine searXNGWebSearchEngine(SearxngProperties properties) {
        Map<String, Object> additionalParams = new HashMap<>();
        //配置搜索引擎为必应
        additionalParams.put("engines", properties.getEngines());
        return SearXNGWebSearchEngine.builder()
                .baseUrl(properties.getBaseUrl())
                .optionalParams(additionalParams)
                .logRequests(true)
                .logResponses(true)
                .build();
    }


}
