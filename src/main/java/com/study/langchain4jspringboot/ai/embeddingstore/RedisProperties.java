package com.study.langchain4jspringboot.ai.embeddingstore;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis配置参数
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-05-16:55
 * @description:com.study.langchain4jspringboot.config
 * @version:1.0
 */
@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String host;

    private Integer port;

    private String username;

    private String password;
}
