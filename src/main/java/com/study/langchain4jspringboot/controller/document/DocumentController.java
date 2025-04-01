package com.study.langchain4jspringboot.controller.document;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.study.langchain4jspringboot.controller.chat.vo.RetrievedRecordResponse;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.study.langchain4jspringboot.convert.ContentConvert.convertToRecord0;
import static dev.langchain4j.data.document.Document.FILE_NAME;
import static dev.langchain4j.data.document.loader.ClassPathDocumentLoader.loadDocument;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-05-8:52
 * @description:com.study.langchain4jspringboot.controller
 * @version:1.0
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

    /**
     * 文档解析器
     */
    private final DocumentParser documentParser;

    /**
     * 文档切分器
     */
    private final DocumentSplitter documentSplitter;

    /**
     * 向量库
     */
    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 向量模型
     */
    private final EmbeddingModel embeddingModel;

    /**
     * 从ClassPath加载文档并向量化存储
     *
     * @return
     */
    @PostMapping("/load/resource")
    @Deprecated
    public String resourceDocumentEmbeddingAndStore() {
        List<Document> documentList = ClassPathDocumentLoader.loadDocuments("documents", documentParser);
        List<String> ids = embeddingAndStore(documentList);
        return StrUtil.format("将{}个文档，切分为：{}个段存入向量库", documentList.size(), ids.size());
    }

    /**
     * 从文件加载文档并向量化存储
     *
     * @param files 文件列表
     * @return 存储结果
     * @throws IOException
     */
    @PostMapping("/load/file")
    public String fileDocumentEmbeddingAndStore(@RequestParam MultipartFile... files) throws IOException {
        List<Document> documentList = Arrays.stream(files).map(file -> {
            try {
                Document document = documentParser.parse(file.getInputStream());
                document.metadata().put("file_id", "1");
                document.metadata().put(FILE_NAME, file.getOriginalFilename());
                //保存额外的字段（到元数据中），根据自己的业务需求添加，例如此处保存的是文档的权限字段
                document.metadata().put("scope", 1);
                return document;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        List<String> ids = embeddingAndStore(documentList);
        return StrUtil.format("将{}个文档，切分为：{}个段存入向量库", documentList.size(), ids.size());
    }

    /**
     * 从URL加载文档并向量化存储
     *
     * @param fileUrls 文件url列表
     * @return 入库结果
     */
    @PostMapping("/load/url")
    public String urlDocumentEmbeddingAndStore(@RequestParam("fileUrls") List<String> fileUrls) {
        //todo 判断文档是否存在，如果存在需要先从向量数据库中删除相关记录，再重新入库
        // 计划：使用mysql存储：文件名，url，hash值，向量数据库记录ids，其它业务字段
        List<Document> documentList = fileUrls.stream().map(
                        fileUrl -> {
                            Document document = UrlDocumentLoader.load(URLUtil.encode(fileUrl, StandardCharsets.UTF_8), documentParser);
                            document.metadata().put("file_id", "1");
                            document.metadata().put(FILE_NAME, FileUtil.getName(fileUrl));
                            //保存额外的字段（到元数据中），根据自己的业务需求添加，例如此处保存的是文档的权限字段和文件id
                            document.metadata().put("scope", 0);
                            return document;
                        })
                .toList();
        List<String> ids = embeddingAndStore(documentList);
        return StrUtil.format("将{}个文档，切分为：{}个段存入向量库", documentList.size(), ids.size());
    }

    /**
     * 切分文档并向量化存储
     *
     * @param documentList 文档列表
     * @return 向量数据库记录id
     */
    private List<String> embeddingAndStore(List<Document> documentList) {
        //切分文档，此处是根据段落
        List<TextSegment> segments = documentSplitter.splitAll(documentList);
        //将段落向量化
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        //将向量和段落（包含元数据）存入向量库
        List<String> ids = embeddingStore.addAll(embeddings, segments);
        return ids;
    }

    /**
     * 从向量库中查询
     * @param query 查询文本
     * @return 命中文本
     */
    @GetMapping("/query")
    public Set<RetrievedRecordResponse> searchFromEmbeddingStore(@RequestParam("query") String query) {
        //先将原始查询向量化
        Embedding queryEmbedding = embeddingModel.embed(query).content();
//        List<EmbeddingMatch<TextSegment>> relevants = embeddingStore.findRelevant(queryEmbedding, 10);
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(
                EmbeddingSearchRequest.builder()
                        .minScore(0.6)
                        .maxResults(10)
                        //根据业务需求，自行添加过滤条件，例如此处：知识库中的文档有权限限制，根据每个人的权限查询出不同的文档
                        .filter(metadataKey("scope").isGreaterThanOrEqualTo(0))
                        .queryEmbedding(queryEmbedding).build());
        List<TextSegment> textSegments = search.matches().stream().map(EmbeddingMatch::embedded).toList();
        return convertToRecord0(textSegments);
    }

}
