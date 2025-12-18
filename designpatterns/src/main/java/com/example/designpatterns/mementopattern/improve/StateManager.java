package com.example.designpatterns.mementopattern.improve;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StateManager {
    private final String storagePath;
    private final Map<String, EnhancedMemento> memoryCache;
    private final int maxCacheSize;
    public StateManager(String storagePath) {
        this.storagePath = storagePath;
        this.memoryCache = new LinkedHashMap(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxCacheSize;
            }
        };
        this.maxCacheSize = 100;
        initStorage();
    }
    private void initStorage() {
        try {
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }
    public EnhancedMemento createMemento(String description, Map<String, Serializable> states) {
        EnhancedMemento memento = new EnhancedMemento(description);

        for (Map.Entry<String, Serializable> entry : states.entrySet()) {
            memento.putState(entry.getKey(), entry.getValue());
        }

        // 添加系统信息
        memento.putMetadata("system_user", System.getProperty("user.name"));
        memento.putMetadata("system_time", String.valueOf(System.currentTimeMillis()));

        return memento;
    }
    public void saveToFile(EnhancedMemento memento) throws IOException {
        // 保存到内存缓存
        memoryCache.put(memento.getId(), memento);

        // 保存到文件
        Path filePath = Paths.get(storagePath, memento.getId() + ".mem");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(memento);
        }

        // 保存元数据索引
        updateMetadataIndex(memento);
    }
    public EnhancedMemento loadFromFile(String mementoId) throws IOException, ClassNotFoundException {
        // 首先检查缓存
        if (memoryCache.containsKey(mementoId)) {
            return memoryCache.get(mementoId);
        }

        // 从文件加载
        Path filePath = Paths.get(storagePath, mementoId + ".mem");
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Memento not found: " + mementoId);
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            EnhancedMemento memento = (EnhancedMemento) ois.readObject();
            memoryCache.put(mementoId, memento);
            return memento;
        }
    }
    public List<EnhancedMemento> listMementos() throws IOException {
        List<EnhancedMemento> mementos = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(storagePath), "*.mem")) {
            for (Path file : stream) {
                String filename = file.getFileName().toString();
                String mementoId = filename.substring(0, filename.length() - 4); // 移除 .mem 后缀
                try {
                    mementos.add(loadFromFile(mementoId));
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to load memento from file: " + file);
                }
            }
        }

        // 按时间戳排序（最新的在前面）
        mementos.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));
        return mementos;
    }
    public void deleteMemento(String mementoId) throws IOException {
        // 从缓存移除
        memoryCache.remove(mementoId);

        // 从文件系统删除
        Path filePath = Paths.get(storagePath, mementoId + ".mem");
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // 从索引移除
        removeFromMetadataIndex(mementoId);
    }

    private void updateMetadataIndex(EnhancedMemento memento) throws IOException {
        Path indexFile = Paths.get(storagePath, "_index.csv");

        String entry = String.format("%s,%s,%s,%s\n",
                memento.getId(),
                memento.getTimestamp().getTime(),
                memento.getDescription().replace(",", ";"),
                memento.getMetadata("system_user"));

        Files.write(indexFile, entry.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    private void removeFromMetadataIndex(String mementoId) throws IOException {
        Path indexFile = Paths.get(storagePath, "_index.csv");
        if (!Files.exists(indexFile)) {
            return;
        }

        List<String> lines = Files.readAllLines(indexFile);
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            if (!line.startsWith(mementoId + ",")) {
                newLines.add(line);
            }
        }

        Files.write(indexFile, newLines, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public void clearAll() throws IOException {
        memoryCache.clear();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(storagePath), "*.mem")) {
            for (Path file : stream) {
                Files.delete(file);
            }
        }

        Path indexFile = Paths.get(storagePath, "_index.csv");
        if (Files.exists(indexFile)) {
            Files.delete(indexFile);
        }
    }
}
