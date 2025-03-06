package com.study.langchain4jspringboot.controller.document;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.data.document.loader.ClassPathDocumentLoader.loadDocument;

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

    private final DocumentParser documentParser;

    private final DocumentSplitter documentSplitter;

    private final EmbeddingStore<TextSegment> embeddingStore;

    private final EmbeddingModel embeddingModel;

    @PostMapping("/load/resource")
    public String resourceDocumentEmbeddingAndStore() {
        List<Document> documentList = ClassPathDocumentLoader.loadDocuments("documents/*.txt", documentParser);
        return embeddingAndStore(documentList);
    }

    @PostMapping("/load/file")
    public String fileDocumentEmbeddingAndStore(@RequestParam MultipartFile... files) throws IOException {
        List<Document> documentList = Arrays.stream(files).map(file -> {
            try {
                return documentParser.parse(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return embeddingAndStore(documentList);
    }

    @PostMapping("/load/url")
    public String urlDocumentEmbeddingAndStore(@RequestParam("fileUrls") List<String> fileUrls) {

        List<Document> documentList = fileUrls.stream().map(
                        fileUrl -> UrlDocumentLoader.load(URLUtil.encode(fileUrl, StandardCharsets.UTF_8), documentParser))
                .toList();

        return embeddingAndStore(documentList);
    }

    private String embeddingAndStore(List<Document> documentList) {
        List<TextSegment> segments = documentSplitter.splitAll(documentList);

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        embeddingStore.addAll(embeddings, segments);
        return StrUtil.format("将{}个文档，切分为：{}个段存入向量库", documentList.size(), segments.size());
    }

    @GetMapping("/query")
    public List<String> searchFromEmbeddingStore(@RequestParam("query") String query) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
//        List<EmbeddingMatch<TextSegment>> relevants = embeddingStore.findRelevant(queryEmbedding, 10);
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(
                EmbeddingSearchRequest.builder()
                        .minScore(0.6)
                        .maxResults(10)
                        .queryEmbedding(queryEmbedding).build());

        return search.matches().stream().map(embeddingMatch -> embeddingMatch.embedded().text()).toList();
    }

}
