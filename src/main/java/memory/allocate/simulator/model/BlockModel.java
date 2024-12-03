package memory.allocate.simulator.model;

public class BlockModel {

    private int blockId;
    private int size;
    private int[] processIds;
    private boolean isAllocated;
    private boolean isFull;
    private int usedSize;

    public BlockModel(int blockId, int size) {
        this.blockId = blockId;
        this.size = size;
        this.processIds = new int[size];
        this.isAllocated = false;
        this.isFull = false;
        this.usedSize = 0;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int[] getProcessIds() {
        return processIds;
    }

    public void setProcessIds(int[] processIds) {
        this.processIds = processIds;
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    public void setAllocated(boolean allocated) {
        isAllocated = allocated;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public int getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(int usedSize) {
        this.usedSize = usedSize;
    }
}
