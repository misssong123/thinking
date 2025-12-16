package thinking.consistenthash;

import thinking.consistenthash.util.DataRecord;
import thinking.consistenthash.util.TableNode;

import java.util.*;

/**
 * 一致性Hash算法完整Demo
 */
public class ConsistentHashDemo {

    public static void main(String[] args) {
        System.out.println("=== 一致性Hash算法Demo ===");
        System.out.println("作者: Java架构师");
        System.out.println("功能: 虚拟节点、新增节点、删除节点的分表逻辑\n");

        // 创建分表管理器
        int initialTables = 3;
        int virtualNodesPerTable = 100;

        ShardingTableManager manager = new ShardingTableManager(
                initialTables, virtualNodesPerTable
        );

        // 阶段1: 初始数据插入
        System.out.println("\n=== 阶段1: 初始数据插入 ===");
        insertInitialData(manager, 1000);
        manager.printDataDistribution();

        // 阶段2: 增加新表
        System.out.println("\n=== 阶段2: 增加新表 ===");
        manager.addNewTable();
        manager.printDataDistribution();
        manager.printMigrationStats();

        // 阶段3: 插入更多数据
        System.out.println("\n=== 阶段3: 插入更多数据 ===");
        insertMoreData(manager, 500);
        manager.printDataDistribution();

        // 阶段4: 再增加一个表
        System.out.println("\n=== 阶段4: 再增加一个表 ===");
        manager.addNewTable();
        manager.printDataDistribution();
        manager.printMigrationStats();

        // 阶段5: 移除一个表
        System.out.println("\n=== 阶段5: 移除表（表1） ===");
        manager.removeTable(1);
        manager.printDataDistribution();
        manager.printMigrationStats();

        // 阶段6: 验证数据一致性
        System.out.println("\n=== 阶段6: 验证数据一致性 ===");
        verifyDataConsistency(manager);

        // 阶段7: 性能测试
        System.out.println("\n=== 阶段7: 性能测试 ===");
        performanceTest(manager);

        System.out.println("\n=== Demo完成 ===");
    }

    /**
     * 插入初始数据
     */
    private static void insertInitialData(ShardingTableManager manager, int count) {
        System.out.println("插入 " + count + " 条初始数据");

        Random random = new Random(42); // 固定种子确保可重复
        for (int i = 0; i < count; i++) {
            String id = "user_" + String.format("%08d", i);
            String data = "用户数据#" + i + "_" + random.nextInt(1000);
            manager.addData(id, data);
        }
    }

    /**
     * 插入更多数据
     */
    private static void insertMoreData(ShardingTableManager manager, int count) {
        System.out.println("插入 " + count + " 条新数据");

        Random random = new Random(123);
        int startId = 1000;

        for (int i = 0; i < count; i++) {
            String id = "user_" + String.format("%08d", startId + i);
            String data = "新用户数据#" + (startId + i) + "_" + random.nextInt(1000);
            manager.addData(id, data);
        }
    }

    /**
     * 验证数据一致性
     */
    private static void verifyDataConsistency(ShardingTableManager manager) {
        System.out.println("验证数据一致性...");

        ConsistentHash<TableNode> hash = manager.getConsistentHash();
        int errors = 0;
        int checked = 0;

        // 随机检查一些数据
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int randomId = random.nextInt(1500);
            String id = "user_" + String.format("%08d", randomId);

            DataRecord record = manager.getData(id);
            if (record != null) {
                checked++;

                // 验证数据是否在正确的表上
                TableNode expectedTable = hash.getNode(id);
                if (!record.getCurrentNode().equals(expectedTable)) {
                    System.err.println("数据不一致: " + id);
                    System.err.println("  当前表: " + record.getCurrentNode());
                    System.err.println("  期望表: " + expectedTable);
                    errors++;
                }
            }
        }

        System.out.println("验证完成: 检查 " + checked + " 条数据，发现 " + errors + " 个错误");
    }

    /**
     * 性能测试
     */
    private static void performanceTest(ShardingTableManager manager) {
        System.out.println("性能测试: 10000次查找操作");

        ConsistentHash<TableNode> hash = manager.getConsistentHash();
        Random random = new Random();

        long startTime = System.currentTimeMillis();

        // 执行查找操作
        for (int i = 0; i < 10000; i++) {
            String key = "test_key_" + random.nextInt(1000000);
            hash.getNode(key);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("查找性能:");
        System.out.println("  总耗时: " + duration + "ms");
        System.out.println("  平均耗时: " + String.format("%.3f", duration / 10000.0) + "ms/次");
        System.out.println("  QPS: " + String.format("%.0f", 10000.0 / duration * 1000));
    }
}
