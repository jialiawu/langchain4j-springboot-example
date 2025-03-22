package com.study.langchain4jspringboot.ai.websearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * searxng配置
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-07-20:16
 * @description:com.study.langchain4jspringboot.ai.websearch
 * @version:1.0
 */
@Data
@ConfigurationProperties(prefix = "searxng")
public class SearxngProperties {

    private String baseUrl;

    private String engines;

}
