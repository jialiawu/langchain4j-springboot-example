package com.study.langchain4jspringboot.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.study.langchain4jspringboot.controller.chat.vo.RetrievedRecordResponse;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;

import java.util.*;

import static dev.langchain4j.data.document.Document.*;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-22-15:51
 * @description:com.study.langchain4jspringboot.convert
 * @version:1.0
 */
public class ContentConvert {

    /**
     * 将原始查询到的内容转为包含文件名和url的集合
     *
     * @param contents 命中的内容
     * @return 转换后的结果集
     */
    public static Set<RetrievedRecordResponse> convertToRecord(List<Content> contents) {
        if (CollUtil.isEmpty(contents)) {
            return Collections.emptySet();
        }
        return convertToRecord0(contents.stream().map(Content::textSegment).toList());
    }

    /**
     * 将原始查询到的内容转为包含文件名和url的集合
     * todo 后续改为根据fileId判断唯一性
     * @param textSegments 命中的文本段
     * @return 转换后的结果集
     */
    public static Set<RetrievedRecordResponse> convertToRecord0(List<TextSegment> textSegments) {
        if (CollUtil.isEmpty(textSegments)) {
            return Collections.emptySet();
        }
        HashSet<RetrievedRecordResponse> records = new HashSet<>();
        for (TextSegment textSegment : textSegments) {
            String url = textSegment.metadata().getString(URL);
            if (StrUtil.isBlank(url)) {
                continue;
            }
            String fileId = Optional.ofNullable(textSegment.metadata().getString("file_id")).orElse("0");
            String fileName = Optional.ofNullable(textSegment.metadata().getString(FILE_NAME)).orElse(FileUtil.getName(url));
            String absolutePath = Optional.ofNullable(textSegment.metadata().getString(ABSOLUTE_DIRECTORY_PATH)).orElse("");
            String text = Optional.ofNullable(textSegment.text()).orElse("");

            RetrievedRecordResponse existed = records.stream().filter(record -> StrUtil.equals(url, record.url())).findFirst().orElse(null);
            if (existed != null) {
                existed.texts().add(text);
            } else {
                records.add(new RetrievedRecordResponse(fileId, fileName, url, absolutePath, CollUtil.newArrayList(text)));
            }
        }
        return records;
    }


}
