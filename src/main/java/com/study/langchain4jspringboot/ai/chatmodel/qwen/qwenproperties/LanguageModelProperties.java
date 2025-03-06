package com.study.langchain4jspringboot.ai.chatmodel.qwen.qwenproperties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LanguageModelProperties {

    private String baseUrl;
    private String apiKey;
    private String modelName;
    private Double topP;
    private Integer topK;
    private Boolean enableSearch;
    private Integer seed;
    private Float repetitionPenalty;
    private Float temperature;
    private List<String> stops;
    private Integer maxTokens;
}
