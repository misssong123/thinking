package thinking.consistenthash.util;

/**
 * 迁移结果
 */
public class MigrationResult <T>{
    public final T node;
    public final boolean migrated;
    public final T fromNode;
    public final T toNode;

    public MigrationResult(T node, boolean migrated, T fromNode, T toNode) {
        this.node = node;
        this.migrated = migrated;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }
}
