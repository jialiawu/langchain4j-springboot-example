package com.study.langchain4jspringboot.ai.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:wjl
 * @see:
 * @since:
 * @date:2025-03-04-19:42
 * @description:com.study.langchain4jspringboot.ai.memory
 * @version:1.0
 */
public class DbChatMemoryStore implements ChatMemoryStore {

    Map<String, List<ChatMessage>> memory = new HashMap<>();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMessage> chatMessages = memory.get((String) memoryId);
        return chatMessages != null ? chatMessages : List.of();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        //todo 有淘汰机制，如何全量入库？？
        memory.put((String) memoryId, messages);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        memory.remove((String)memoryId);
    }
}
