package com.acertainsupplychain.client.workload;

public class WorkerRunResult {

    private int successfulInteraction;
    private long timeForRunsInNanoSecs;
    private int numActualRuns;
    private int numSuccessfulOrderManagerInteraction;
    private int numOrderManagerInteraction;

    public WorkerRunResult(int successfulInteractions,
            long timeForRunsInNanoSecs, int numActualRuns,
            int numSuccessfulOrderManagerInteraction,
            int numOrderManagerInteraction) {
        this.setSuccessfulInteraction(successfulInteractions);
        this.setTimeForRunsInNanoSecs(timeForRunsInNanoSecs);
        this.setNumActualRuns(numActualRuns);
        this.setNumSuccessfulOrderManagerInteraction(numSuccessfulOrderManagerInteraction);
        this.setNumOrderManagerInteraction(numOrderManagerInteraction);
    }

    public int getSuccessfulInteraction() {
        return successfulInteraction;
    }

    public void setSuccessfulInteraction(int successfulInteraction) {
        this.successfulInteraction = successfulInteraction;
    }

    public long getTimeForRunsInNanoSecs() {
        return timeForRunsInNanoSecs;
    }

    public void setTimeForRunsInNanoSecs(long timeForRunsInNanoSecs) {
        this.timeForRunsInNanoSecs = timeForRunsInNanoSecs;
    }

    public int getNumActualRuns() {
        return numActualRuns;
    }

    public void setNumActualRuns(int numActualRuns) {
        this.numActualRuns = numActualRuns;
    }

    public int getNumSuccessfulOrderManagerInteraction() {
        return numSuccessfulOrderManagerInteraction;
    }

    public void setNumSuccessfulOrderManagerInteraction(
            int numSuccessfulOrderManagerInteraction) {
        this.numSuccessfulOrderManagerInteraction = numSuccessfulOrderManagerInteraction;
    }

    public int getNumOrderManagerInteraction() {
        return numOrderManagerInteraction;
    }

    public void setNumOrderManagerInteraction(int numOrderManagerInteraction) {
        this.numOrderManagerInteraction = numOrderManagerInteraction;
    }

}
