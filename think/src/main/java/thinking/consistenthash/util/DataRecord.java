package thinking.consistenthash.util;

import lombok.Getter;

// 数据记录
@Getter
public class DataRecord {
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
