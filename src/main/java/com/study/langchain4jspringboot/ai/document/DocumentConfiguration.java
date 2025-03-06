package com.study.langchain4jspringboot.ai.document;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
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

    @Bean
    public DocumentParser documentParser(){
        return new ApacheTikaDocumentParser();
    }

    @Bean
    public DocumentSplitter documentSplitter(){
        return DocumentSplitters.recursive(300, 20);
    }

}
