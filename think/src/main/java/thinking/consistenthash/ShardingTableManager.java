package thinking.consistenthash;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 分表管理器
 */
public class ShardingTableManager {

    // 表节点
    public static class TableNode {
        private final String tableName;
        private final int index;
        private final String description;

        public TableNode(String tableName, int index) {
            this.tableName = tableName;
            this.index = index;
            this.description = "表" + index + "(" + tableName + ")";
        }

        public String getTableName() {
            return tableName;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TableNode tableNode = (TableNode) o;
            return index == tableNode.index &&
                    Objects.equals(tableName, tableNode.tableName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableName, index);
        }
    }

    // 数据记录
    public static class DataRecord {
        private final String id;
        private final String data;
        private TableNode currentNode;
        private TableNode previousNode;
        private final long createTime;
        private long updateTime;

        public DataRecord(String id, String data, TableNode node) {
            this.id = id;
            this.data = data;
            this.currentNode = node;
            this.previousNode = node;
            this.createTime = System.currentTimeMillis();
            this.updateTime = createTime;
        }

        public void updateNode(TableNode newNode) {
            this.previousNode = this.currentNode;
            this.currentNode = newNode;
            this.updateTime = System.currentTimeMillis();
        }

        public String getId() { return id; }
        public String getData() { return data; }
        public TableNode getCurrentNode() { return currentNode; }
        public TableNode getPreviousNode() { return previousNode; }

        @Override
        public String toString() {
            return "DataRecord{" +
                    "id='" + id + '\'' +
                    ", table=" + currentNode +
                    (previousNode != null && !previousNode.equals(currentNode) ?
                            " (从" + previousNode + "迁移)" : "") +
                    '}';
        }
    }

    private final ConsistentHash<TableNode> consistentHash;
    private final Map<String, DataRecord> dataStore = new ConcurrentHashMap<>();
    private final Map<TableNode, List<DataRecord>> tableData = new ConcurrentHashMap<>();

    private final int virtualNodesPerTable;
    private final ConsistentHash.HashFunction hashFunction;

    // 迁移监听器
    private final List<MigrationListener> migrationListeners = new ArrayList<>();

    public interface MigrationListener {
        void onMigrationStart(TableNode from, TableNode to, List<String> dataIds);
        void onMigrationComplete(TableNode from, TableNode to, int migratedCount);
        void onMigrationError(TableNode from, TableNode to, String error);
    }

    public ShardingTableManager(int initialTables, int virtualNodesPerTable) {
        this.virtualNodesPerTable = virtualNodesPerTable;
        this.hashFunction = new ConsistentHash.MurmurHash();
        this.consistentHash = new ConsistentHash<>(hashFunction, virtualNodesPerTable);

        // 初始化表
        initializeTables(initialTables);
    }

    /**
     * 初始化表
     */
    private void initializeTables(int tableCount) {
        System.out.println("=== 初始化分表 ===");
        System.out.println("创建 " + tableCount + " 个表，每个表 " + virtualNodesPerTable + " 个虚拟节点");

        for (int i = 0; i < tableCount; i++) {
            TableNode table = new TableNode("user_table_" + String.format("%04d", i), i);
            consistentHash.addPhysicalNode(table);
            tableData.put(table, new ArrayList<>());
        }

        System.out.println("分表初始化完成");
        consistentHash.printNodeDistribution();
    }

    /**
     * 添加数据
     */
    public DataRecord addData(String id, String data) {
        // 确定数据应该放在哪个表
        TableNode table = consistentHash.getNode(id);

        // 创建数据记录
        DataRecord record = new DataRecord(id, data, table);

        // 存储数据
        dataStore.put(id, record);
        tableData.computeIfAbsent(table, k -> new ArrayList<>()).add(record);

        System.out.println("添加数据: " + id + " -> " + table);
        return record;
    }

    /**
     * 获取数据
     */
    public DataRecord getData(String id) {
        DataRecord record = dataStore.get(id);
        if (record != null) {
            // 检查数据是否还在正确的表上
            TableNode expectedTable = consistentHash.getNode(id);
            if (!record.getCurrentNode().equals(expectedTable)) {
                System.out.println("警告: 数据 " + id + " 应该迁移到表 " + expectedTable);
            }
        }
        return record;
    }

    /**
     * 增加新表
     */
    public void addNewTable() {
        int currentTables = tableData.size();
        TableNode newTable = new TableNode(
                "user_table_" + String.format("%04d", currentTables),
                currentTables
        );

        System.out.println("\n=== 增加新表: " + newTable + " ===");

        // 添加迁移监听器
        MigrationMonitor monitor = new MigrationMonitor();
        addMigrationListener(monitor);

        try {
            // 1. 添加新节点到哈希环
            consistentHash.addPhysicalNode(newTable);
            tableData.put(newTable, new ArrayList<>());

            // 2. 触发数据迁移
            migrateDataForNewTable(newTable);

            // 3. 等待迁移完成
            monitor.waitForCompletion();

            System.out.println("新表添加完成: " + newTable);

        } finally {
            removeMigrationListener(monitor);
        }
    }

    /**
     * 移除表
     */
    public void removeTable(int tableIndex) {
        TableNode tableToRemove = findTableByIndex(tableIndex);
        if (tableToRemove == null) {
            System.out.println("表 " + tableIndex + " 不存在");
            return;
        }

        System.out.println("\n=== 移除表: " + tableToRemove + " ===");

        // 添加迁移监听器
        MigrationMonitor monitor = new MigrationMonitor();
        addMigrationListener(monitor);

        try {
            // 1. 迁移该表上的所有数据
            List<DataRecord> records = tableData.get(tableToRemove);
            if (records != null && !records.isEmpty()) {
                System.out.println("需要迁移 " + records.size() + " 条数据");
                migrateDataForRemovedTable(tableToRemove, records);
            }

            // 2. 从哈希环中移除
            consistentHash.removePhysicalNode(tableToRemove);

            // 3. 清理数据结构
            tableData.remove(tableToRemove);
            for (DataRecord record : records) {
                dataStore.remove(record.getId());
            }

            // 4. 等待迁移完成
            monitor.waitForCompletion();

            System.out.println("表移除完成: " + tableToRemove);

        } finally {
            removeMigrationListener(monitor);
        }
    }

    /**
     * 为新表迁移数据
     */
    private void migrateDataForNewTable(TableNode newTable) {
        // 找出需要迁移的数据
        List<MigrationTask> migrationTasks = new ArrayList<>();

        for (Map.Entry<String, DataRecord> entry : dataStore.entrySet()) {
            String dataId = entry.getKey();
            DataRecord record = entry.getValue();

            // 检查数据是否应该迁移到新表
            TableNode expectedTable = consistentHash.getNode(dataId);
            if (expectedTable.equals(newTable) && !record.getCurrentNode().equals(newTable)) {
                migrationTasks.add(new MigrationTask(record, record.getCurrentNode(), newTable));
            }
        }

        System.out.println("需要迁移 " + migrationTasks.size() + " 条数据到新表");

        // 执行迁移
        executeMigration(migrationTasks);
    }

    /**
     * 为移除的表迁移数据
     */
    private void migrateDataForRemovedTable(TableNode removedTable, List<DataRecord> records) {
        List<MigrationTask> migrationTasks = new ArrayList<>();

        for (DataRecord record : records) {
            // 重新计算数据应该去哪个表
            TableNode expectedTable = consistentHash.getNode(record.getId());
            if (!expectedTable.equals(removedTable)) {
                migrationTasks.add(new MigrationTask(record, removedTable, expectedTable));
            }
        }

        System.out.println("需要迁移 " + migrationTasks.size() + " 条数据到其他表");

        // 执行迁移
        executeMigration(migrationTasks);
    }

    /**
     * 迁移任务
     */
    private static class MigrationTask {
        final DataRecord record;
        final TableNode fromTable;
        final TableNode toTable;

        MigrationTask(DataRecord record, TableNode fromTable, TableNode toTable) {
            this.record = record;
            this.fromTable = fromTable;
            this.toTable = toTable;
        }
    }

    /**
     * 执行迁移
     */
    private void executeMigration(List<MigrationTask> tasks) {
        if (tasks.isEmpty()) {
            return;
        }

        // 分组按目标表
        Map<TableNode, List<MigrationTask>> tasksByTarget = tasks.stream()
                .collect(Collectors.groupingBy(task -> task.toTable));

        // 执行迁移
        for (Map.Entry<TableNode, List<MigrationTask>> entry : tasksByTarget.entrySet()) {
            TableNode targetTable = entry.getKey();
            List<MigrationTask> tableTasks = entry.getValue();

            List<String> dataIds = tableTasks.stream()
                    .map(task -> task.record.getId())
                    .collect(Collectors.toList());

            // 通知监听器迁移开始
            TableNode fromTable = tableTasks.get(0).fromTable;
            notifyMigrationStart(fromTable, targetTable, dataIds);

            try {
                int migratedCount = 0;

                // 模拟迁移过程
                for (MigrationTask task : tableTasks) {
                    // 从原表移除
                    List<DataRecord> fromList = tableData.get(task.fromTable);
                    if (fromList != null) {
                        fromList.remove(task.record);
                    }

                    // 更新数据记录
                    task.record.updateNode(task.toTable);

                    // 添加到新表
                    tableData.computeIfAbsent(task.toTable, k -> new ArrayList<>())
                            .add(task.record);

                    migratedCount++;

                    // 模拟迁移延迟
                    Thread.sleep(10);
                }

                // 通知监听器迁移完成
                notifyMigrationComplete(fromTable, targetTable, migratedCount);

            } catch (Exception e) {
                notifyMigrationError(fromTable, targetTable, e.getMessage());
                System.err.println("迁移错误: " + e.getMessage());
            }
        }
    }

    /**
     * 查找表
     */
    private TableNode findTableByIndex(int index) {
        return tableData.keySet().stream()
                .filter(table -> table.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    /**
     * 添加迁移监听器
     */
    public void addMigrationListener(MigrationListener listener) {
        migrationListeners.add(listener);
    }

    /**
     * 移除迁移监听器
     */
    public void removeMigrationListener(MigrationListener listener) {
        migrationListeners.remove(listener);
    }

    private void notifyMigrationStart(TableNode from, TableNode to, List<String> dataIds) {
        for (MigrationListener listener : migrationListeners) {
            listener.onMigrationStart(from, to, dataIds);
        }
    }

    private void notifyMigrationComplete(TableNode from, TableNode to, int migratedCount) {
        for (MigrationListener listener : migrationListeners) {
            listener.onMigrationComplete(from, to, migratedCount);
        }
    }

    private void notifyMigrationError(TableNode from, TableNode to, String error) {
        for (MigrationListener listener : migrationListeners) {
            listener.onMigrationError(from, to, error);
        }
    }

    /**
     * 迁移监控器
     */
    private class MigrationMonitor implements MigrationListener {
        private final Object lock = new Object();
        private int expectedMigrations = 0;
        private int completedMigrations = 0;
        private boolean migrationStarted = false;

        @Override
        public void onMigrationStart(TableNode from, TableNode to, List<String> dataIds) {
            synchronized (lock) {
                expectedMigrations++;
                migrationStarted = true;
                System.out.println("迁移开始: " + from + " -> " + to + " (" + dataIds.size() + "条数据)");
            }
        }

        @Override
        public void onMigrationComplete(TableNode from, TableNode to, int migratedCount) {
            synchronized (lock) {
                completedMigrations++;
                System.out.println("迁移完成: " + from + " -> " + to + " (" + migratedCount + "条数据)");
                lock.notifyAll();
            }
        }

        @Override
        public void onMigrationError(TableNode from, TableNode to, String error) {
            System.err.println("迁移错误: " + from + " -> " + to + ": " + error);
        }

        public void waitForCompletion() {
            synchronized (lock) {
                while (migrationStarted && completedMigrations < expectedMigrations) {
                    try {
                        System.out.println("等待迁移完成... (" + completedMigrations + "/" + expectedMigrations + ")");
                        lock.wait(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                System.out.println("所有迁移完成 (" + completedMigrations + "/" + expectedMigrations + ")");
            }
        }
    }

    /**
     * 打印数据分布
     */
    public void printDataDistribution() {
        System.out.println("\n=== 数据分布统计 ===");
        System.out.println("总数据量: " + dataStore.size());
        System.out.println("分表数量: " + tableData.size());

        for (Map.Entry<TableNode, List<DataRecord>> entry : tableData.entrySet()) {
            TableNode table = entry.getKey();
            List<DataRecord> records = entry.getValue();

            System.out.printf("  %s: %d 条数据 (%.1f%%)\n",
                    table,
                    records.size(),
                    dataStore.isEmpty() ? 0 : records.size() * 100.0 / dataStore.size());
        }
    }

    /**
     * 打印迁移统计
     */
    public void printMigrationStats() {
        consistentHash.getStats().printStats();
    }

    /**
     * 获取一致性Hash实例
     */
    public ConsistentHash<TableNode> getConsistentHash() {
        return consistentHash;
    }
}
