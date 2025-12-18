package com.example.designpatterns.mementopattern.improve;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EnhancedMemento implements Memento{
    private final String id;
    private final Date timestamp;
    private final String description;
    private final Map<String, Serializable> stateMap;
    private final Map<String, String> metadata;

    public EnhancedMemento(String id, String description) {
        this.id = id;
        this.description = description;
        this.timestamp = new Date();
        this.stateMap = new ConcurrentHashMap<>();
        this.metadata = new ConcurrentHashMap<>();
        this.metadata.put("created_at", timestamp.toString());
        this.metadata.put("id", id);
        this.metadata.put("description", description);
    }

    public EnhancedMemento(String description) {
        this(UUID.randomUUID().toString(), description);
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getTimestamp() {
        return new Date(timestamp.getTime());
    }

    @Override
    public String getDescription() {
        return description;
    }

    // 状态操作方法
    public void putState(String key, Serializable value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        stateMap.put(key, value);
    }

    public Serializable getState(String key) {
        return stateMap.get(key);
    }
    public <T> T getState(String key, Class<T> type) {
        Serializable value = stateMap.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public boolean containsKey(String key) {
        return stateMap.containsKey(key);
    }

    public Set<String> getStateKeys() {
        return Collections.unmodifiableSet(stateMap.keySet());
    }

    // 元数据操作
    public void putMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public String getMetadata(String key) {
        return metadata.get(key);
    }
    public Map<String, String> getAllMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    // 深拷贝方法
    public EnhancedMemento deepCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return (EnhancedMemento) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deep copy memento", e);
        }
    }
    // 转换为可读字符串
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Memento ID: ").append(id).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("States: ").append(stateMap.size()).append("\n");

        for (Map.Entry<String, Serializable> entry : stateMap.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ");
            Object value = entry.getValue();
            if (value instanceof String) {
                String str = (String) value;
                sb.append(str.length() > 50 ? str.substring(0, 50) + "..." : str);
            } else {
                sb.append(value);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
    // 序列化辅助方法
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }
}
