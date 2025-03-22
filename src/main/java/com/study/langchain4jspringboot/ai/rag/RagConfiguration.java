package com.study.langchain4jspringboot.ai.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.DefaultQueryTransformer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

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
     * 基于向量数据库的内容检索器
     *
     * @param embeddingModel 嵌入模型，用于文本的向量化
     * @param embeddingStore 嵌入存储，用于存储嵌入向量
     * @return embeddingStoreContentRetriever 基于向量数据库的内容检索器
     */
    @Bean
    @Primary
    public ContentRetriever embeddingStoreContentRetriever(EmbeddingModel embeddingModel,
                                                           EmbeddingStore<TextSegment> embeddingStore) {


        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                //最大返回数量，可以理解为 limit 10
                .maxResults(10)
                //最小匹配分数，可以理解为 where score >= 0.5
                .minScore(0.50)
                /*
                过滤条件，可以理解为where ......
                    注意，如果此处使用的是redis向量数据库，查询条件字段必须包含在索引中（其它数据库未验证）
                */
                //  filter方法，用于与查询无关的过滤条件
                //.filter()
                //  dynamicFilter方法，用户与查询有关的过滤条件。例如：知识库中的文档有权限限制，根据每个人的权限查询出不同的文档
                .dynamicFilter(query -> {
                    //等价于where scope >= 0
                    return metadataKey("scope").isGreaterThanOrEqualTo(0);
                })
                .build();
    }


    /**
     *
     * 基于联网搜索的内容检索器
     * @param webSearchEngine 联网搜索引擎
     * @return webSearchContentRetriever 基于联网搜索的内容检索器
     */
    @Bean
    public ContentRetriever webSearchContentRetriever(WebSearchEngine webSearchEngine) {
        return WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                //最大查询数量
                .maxResults(5)
                .build();
    }

    /**
     * RetrievalAugmentor由很多小组件组成，结构参见官网图：</br>
     * <img src="https://docs.langchain4j.dev/assets/images/advanced-rag-fb84283d02470b835ff2f4913f08fdbf.png"></img>
     * @param embeddingStoreContentRetriever 基于嵌入模型的内容检索器
     * @param webSearchContentRetriever 基于网络搜索的内容检索器
     * @return retrievalAugmentor 检索增强器
     */
    @Bean
    public RetrievalAugmentor retrievalAugmentor(@Qualifier("embeddingStoreContentRetriever") ContentRetriever embeddingStoreContentRetriever,
                                                 @Qualifier("webSearchContentRetriever") ContentRetriever webSearchContentRetriever) {
        /*
        查询转换器，对原始query进行压缩、拓展、重写，以提交检索质量
        */
        //默认，不做任何处理
        DefaultQueryTransformer defaultQueryTransformer = new DefaultQueryTransformer();

        /*
        查询路由器，根据路由决定使用哪些ContentRetriever
        */
        //默认路由，使用全部的ContentRetriever
        //QueryRouter queryRouter = new DefaultQueryRouter(embeddingStoreContentRetriever, webSearchContentRetriever);
        //自定义路由，实现联网开关功能
        QueryRouter queryRouter = new SwitchQueryRouter(embeddingStoreContentRetriever, webSearchContentRetriever);

        /*
        内容聚合器，将所有内容检索器查询到的内容集合进行聚合，即 将Collection<List<T>>通过一定的算法合并为List<T>。内置了RRF、ReRanking
        */
        //默认，使用两阶段RRF算法
        DefaultContentAggregator defaultContentAggregator = new DefaultContentAggregator();

        //todo Prompt管理
        PromptTemplate promptTemplate = PromptTemplate.from(
                """
                        {{userMessage}}
                        
                        综合以下相关信息回答问题
                        --------------------------
                        检索到的信息
                        {{contents}}""");
        //内容注入器，根据此内容注入器将检索到内容注入到对话上下文中
        DefaultContentInjector contentInjector = DefaultContentInjector.builder().promptTemplate(promptTemplate).build();

        //组装
        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(defaultQueryTransformer)
                .queryRouter(queryRouter)
                .contentAggregator(defaultContentAggregator)
                .contentInjector(contentInjector)
                .build();
    }

}
