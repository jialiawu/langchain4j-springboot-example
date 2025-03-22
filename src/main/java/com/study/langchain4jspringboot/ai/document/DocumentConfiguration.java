package com.study.langchain4jspringboot.ai.document;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文档配置
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-05-21:32
 * @description:com.study.langchain4jspringboot.ai.config.document
 * @version:1.0
 */
@Configuration
public class DocumentConfiguration {

    //fixme 以下两个bean待验证线程安全

    /**
     * 文档解析器，使用apache tika解析
     * @return
     */
    @Bean
    public DocumentParser documentParser(){
        return new ApacheTikaDocumentParser();
    }

    /**
     * 文档切分器，使用段落递归切分
     * @return
     */
    @Bean
    public DocumentSplitter documentSplitter(){
        // 段落最大长度300，段落间最大重叠字符20
        return DocumentSplitters.recursive(300, 20);
    }

}
