package memory.allocate.simulator.model;

public class JobModel {

    private int jobId;
    private int size;
    private int blockId;
    private String jobName;
    private boolean isDone;

    public JobModel(int jobId, int size, String jobName) {
        this.jobId = jobId;
        this.size = size;
        this.blockId = -1;
        this.jobName = jobName;
        this.isDone = false;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
