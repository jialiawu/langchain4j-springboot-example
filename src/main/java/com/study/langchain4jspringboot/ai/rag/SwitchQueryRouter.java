package com.study.langchain4jspringboot.ai.rag;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.QueryRouter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.List;

import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

/**
 * 联网开关的查询路由
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-07-20:51
 * @description:com.study.langchain4jspringboot.ai.rag
 * @version:1.0
 */
public class SwitchQueryRouter implements QueryRouter {

    private final Collection<ContentRetriever> contentRetrievers;

    public SwitchQueryRouter(ContentRetriever... contentRetrievers) {
        this(asList(contentRetrievers));
    }

    public SwitchQueryRouter(Collection<ContentRetriever> contentRetrievers) {
        this.contentRetrievers = unmodifiableCollection(ensureNotEmpty(contentRetrievers, "contentRetrievers"));
    }

    @Override
    public Collection<ContentRetriever> route(Query query) {
        //获取联网开关是否打开
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        boolean webSearchEnable = Boolean.parseBoolean(request.getParameter("webSearchEnable"));
        if (!webSearchEnable) {
            //开关关闭，不走联网检索
            return contentRetrievers.stream().filter(contentRetriever -> !(contentRetriever instanceof WebSearchContentRetriever)).toList();
        }
        return contentRetrievers;
    }
}
