package com.study.langchain4jspringboot.ai.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-21:28
 * @description:com.study.langchain4jspringboot.ai.tool
 * @version:1.0
 */
@Component
public class MathTool implements ITool{

    @Tool("两个数相加")
    public double addition(double a, double b) {
        return a + b;
    }

    @Tool("两个数相减")
    public double subtraction(@P("被减数") double a, @P("减数") double b) {
        return a - b;
    }

    @Tool("两个数相乘")
    public double multiplication(double a, double b) {
        return a * b;
    }

    @Tool("两个数相除，除数不能为0")
    public double division(@P("被除数") double a, @P("除数") double b) {
        return Math.round(a / b);
    }

    @Tool("平方根")
    double squareRoot(double x) {
        return Math.sqrt(x);
    }

}
