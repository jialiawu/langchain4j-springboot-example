package com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmbeddingModelProperties {

    private String baseUrl;
    private String apiKey;
    private String modelName;
}
