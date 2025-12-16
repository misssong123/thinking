package thinking.consistenthash.util;

import lombok.Getter;

import java.util.Objects;
// 表节点
@Getter
public class TableNode {
    private final String tableName;
    private final int index;
    private final String description;

    public TableNode(String tableName, int index) {
        this.tableName = tableName;
        this.index = index;
        this.description = "表" + index + "(" + tableName + ")";
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
