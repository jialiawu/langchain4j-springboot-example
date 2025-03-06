package com.study.langchain4jspringboot.service;


import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.model.output.structured.Description;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 高德天气api
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-22:58
 * @description:com.study.langchain4jspringboot.service
 * @version:1.0
 */
@Service
public class GaodeWeather {

    private final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Value("${gaode.baseUrl}")
    private String baseUrl;

    @Value("${gaode.api-key}")
    private String apiKey;

    public List<Forecasts> getWeather(String city) {
        //高德天气api
        String url = baseUrl + "?key=" + apiKey + "&city=" + city + "&extensions=all";
        String resp = HttpUtil.get(url);
        WeatherApiResponse weatherApiResponse = null;
        try {
            weatherApiResponse = objectMapper.readValue(resp, WeatherApiResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if ("0".equals(weatherApiResponse.status)) {
            throw new RuntimeException("获取高德天气信息失败！");
        }
        return weatherApiResponse.forecasts();
    }


    public record WeatherApiResponse(
            String status,
            String count,
            String info,
            String infocode,
            List<Forecasts> forecasts) {
    }

    @Description("预报天气信息数据")
    public record Forecasts(
            @Description("省份名") String province,
            @Description("城市名") String city,
            @Description("区域编码") String adcode,
            @Description("预报发布时间") String reporttime,
            @Description("预报数据 list 结构，元素 cast,按顺序为当天、第二天、第三天的预报数据") List<Casts> casts
    ) {
    }

    @Description("天气信息数据")
    public record Casts(
            @Description("日期") String date,
            @Description("星期几") String week,
            @Description("白天天气现象") String dayweather,
            @Description("晚上天气现象") String nightweather,
            @Description("白天温度") String daytemp,
            @Description("晚上温度") String nighttemp,
            @Description("白天风向") String daywind,
            @Description("晚上风向") String nightwind,
            @Description("白天风力") String daypower,
            @Description("晚上风力") String nightpower
    ) {
    }
    
}
