package com.study.langchain4jspringboot.ai.rag;

import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
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
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
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

    //todo WebSearch
//    @Bean
//    public ContentRetriever webSearchContentRetriever(){
////        WebSearchEngine webSearchEngine = TavilyWebSearchEngine.builder()
////                .apiKey(System.getenv("TAVILY_API_KEY")) // get a free key: https://app.tavily.com/sign-in
////                .build();
//
//        Map<String, Object> optionalParameters = new HashMap<>();
//        optionalParameters.put("gl", "us");
//        optionalParameters.put("hl", "en");
//        optionalParameters.put("google_domain", "google.com");
//
//
//        SearchApiWebSearchEngine webSearchEngine = SearchApiWebSearchEngine.builder()

    /// /                .apiKey(SEARCHAPI_API_KEY)
//                .engine("google")
//                .optionalParameters(optionalParameters)
//                .build();
//
//        SearXNGWebSearchEngine.builder()
//                .baseUrl()
//
//
//        return WebSearchContentRetriever.builder()
//                .webSearchEngine(webSearchEngine)
//                .maxResults(3)
//                .build();
//    }
    @Bean
    public RetrievalAugmentor retrievalAugmentor(ContentRetriever embeddingStoreContentRetriever) {
        QueryRouter queryRouter = new DefaultQueryRouter(embeddingStoreContentRetriever);
        PromptTemplate promptTemplate = PromptTemplate.from(
                """
                    {{userMessage}}
                    
                    --------------------------
                    2、检索到的信息
                    {{contents}}""");
        return DefaultRetrievalAugmentor.builder()
                .contentInjector(DefaultContentInjector.builder()
                        .promptTemplate(promptTemplate)
                        .build())
                .queryRouter(queryRouter)
                .build();
    }

}
