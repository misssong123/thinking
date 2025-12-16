package thinking.consistenthash;

public class VirtualNode<T> {
    final T physicalNode;
    final String virtualKey;
    final int hash;

    VirtualNode(T physicalNode, String virtualKey, int hash) {
        this.physicalNode = physicalNode;
        this.virtualKey = virtualKey;
        this.hash = hash;
    }

    @Override
    public String toString() {
        return physicalNode + "#" + virtualKey;
    }
}