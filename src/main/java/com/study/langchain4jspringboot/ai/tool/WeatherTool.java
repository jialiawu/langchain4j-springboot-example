package com.study.langchain4jspringboot.ai.tool;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.langchain4jspringboot.service.GaodeWeather;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-22:03
 * @description:com.study.langchain4jspringboot.ai.tool
 * @version:1.0
 */
@Component
@RequiredArgsConstructor
public class WeatherTool implements ITool{

    private final GaodeWeather gaodeWeather;

    @Tool("获取天气信息")
    public List<GaodeWeather.Forecasts> getWeather(@P("城市名称") String city) {
        return gaodeWeather.getWeather(city);
    }

}
