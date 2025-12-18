package com.example.designpatterns.mementopattern.improve;

import java.io.Serializable;
import java.util.*;

public class DocumentEditor implements Memorable{
    private String title;
    private StringBuilder content;
    private List<String> tags;
    private Map<String, Object> properties;
    private Date lastModified;

    public DocumentEditor() {
        this.content = new StringBuilder();
        this.tags = new ArrayList<>();
        this.properties = new HashMap<>();
        this.lastModified = new Date();
    }

    public void setTitle(String title) {
        this.title = title;
        updateLastModified();
    }

    public void appendContent(String text) {
        this.content.append(text);
        updateLastModified();
    }
    public void insertContent(int position, String text) {
        if (position >= 0 && position <= content.length()) {
            content.insert(position, text);
            updateLastModified();
        }
    }

    public void deleteContent(int start, int end) {
        if (start >= 0 && end <= content.length() && start <= end) {
            content.delete(start, end);
            updateLastModified();
        }
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            updateLastModified();
        }
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
        updateLastModified();
    }
    @Override
    public Memento saveToMemento(String description) {
        EnhancedMemento memento = new EnhancedMemento(description);

        // 保存所有状态
        memento.putState("title", title != null ? title : "");
        memento.putState("content", content.toString());
        memento.putState("tags", new ArrayList<>(tags));

        // 保存properties（需要确保值是可序列化的）
        Map<String, Serializable> serializableProps = new HashMap<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getValue() instanceof Serializable) {
                serializableProps.put(entry.getKey(), (Serializable) entry.getValue());
            }
        }
        memento.putState("properties", (Serializable) serializableProps);

        memento.putState("lastModified", lastModified.getTime());

        // 添加应用特定的元数据
        memento.putMetadata("editor_version", "1.0");
        memento.putMetadata("content_length", String.valueOf(content.length()));
        memento.putMetadata("tag_count", String.valueOf(tags.size()));

        return memento;
    }
    @Override
    public void restoreFromMemento(Memento memento) {
        if (!(memento instanceof EnhancedMemento)) {
            throw new IllegalArgumentException("Invalid memento type");
        }

        EnhancedMemento enhancedMemento = (EnhancedMemento) memento;

        // 恢复状态
        this.title = enhancedMemento.getState("title", String.class);

        String savedContent = enhancedMemento.getState("content", String.class);
        this.content = new StringBuilder(savedContent != null ? savedContent : "");

        List<String> savedTags = enhancedMemento.getState("tags", List.class);
        this.tags = new ArrayList<>(savedTags != null ? savedTags : Collections.emptyList());

        Map<String, Serializable> savedProps = enhancedMemento.getState("properties", Map.class);
        if (savedProps != null) {
            this.properties = new HashMap<>(savedProps);
        }

        Long savedTime = enhancedMemento.getState("lastModified", Long.class);
        this.lastModified = savedTime != null ? new Date(savedTime) : new Date();
    }
    @Override
    public String getStateSummary() {
        return String.format("Document: %s (Length: %d, Tags: %d, Modified: %s)",
                title != null ? title : "Untitled",
                content.length(),
                tags.size(),
                lastModified);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content.toString();
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    private void updateLastModified() {
        this.lastModified = new Date();
    }
}
