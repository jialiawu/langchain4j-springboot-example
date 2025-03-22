package com.study.langchain4jspringboot.ai.tool;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 系统工具
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-21:43
 * @description:com.study.langchain4jspringboot.ai.tool
 * @version:1.0
 */
@Component
public class OsTool implements ITool{

    @Tool("打开计算器")
    public void openCalculator() {
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("获取电脑cpu数量")
    public Integer availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}
