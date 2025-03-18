package com.study.langchain4jspringboot.ai.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-05-22:25
 * @description:com.study.langchain4jspringboot.ai.config.rag
 * @version:1.0
 */
@Configuration
public class RagConfiguration {

    /**
     * naive RAG
     *
     * @param embeddingModel
     * @param embeddingStore
     * @return
     */
    @Bean
    @Primary
    public ContentRetriever embeddingStoreContentRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.50)
                .build();
    }


    @Bean
    public ContentRetriever webSearchContentRetriever(WebSearchEngine webSearchEngine) {
        return WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(5)
                .build();
    }

    @Bean
    public RetrievalAugmentor retrievalAugmentor(@Qualifier("embeddingStoreContentRetriever") ContentRetriever embeddingStoreContentRetriever,
                                                 @Qualifier("webSearchContentRetriever") ContentRetriever webSearchContentRetriever) {
        //默认路由
//        QueryRouter queryRouter = new DefaultQueryRouter(embeddingStoreContentRetriever, webSearchContentRetriever);
        //联网开关路由
        QueryRouter queryRouter = new SwitchQueryRouter(embeddingStoreContentRetriever, webSearchContentRetriever);
        //todo Prompt管理
        PromptTemplate promptTemplate = PromptTemplate.from(
                """
                        {{userMessage}}
                        
                        综合以下相关信息回答问题
                        --------------------------
                        检索到的信息
                        {{contents}}""");
        DefaultContentInjector contentInjector = DefaultContentInjector.builder().promptTemplate(promptTemplate).build();

        return DefaultRetrievalAugmentor.builder()
                .contentInjector(contentInjector)
                .queryRouter(queryRouter)
                .build();
    }

}
