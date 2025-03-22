package com.study.langchain4jspringboot.controller.chat.vo;

import java.util.Collection;
import java.util.List;

/**
 * 检索结果
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-06-21:01
 * @description:com.study.langchain4jspringboot.controller.chat.dto
 * @version:1.0
 */
public record RetrievedRecordResponse(String fileId,String fileName, String url, String absolutePath, Collection<String> texts) {
}
