package com.acertainsupplychain.client.workload;

public class AggregateResult {

    private int numWorkers;
    private double aggregateThroughput;
    private double averageLatency;
    private double succRatio;
    private double orderManagerXactRatio;

    public AggregateResult(int numWorkers, double aggregateThroughput,
            double averageLatency, double succRatio,
            double orderManagerXactRatio) {
        this.setAggregateThroughput(aggregateThroughput);
        this.setAverageLatency(averageLatency);
        this.setNumWorkers(numWorkers);
        this.setOrderManagerXactRatio(orderManagerXactRatio);
        this.setSuccRatio(succRatio);
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    public double getAggregateThroughput() {
        return aggregateThroughput;
    }

    public void setAggregateThroughput(double aggregateThroughput) {
        this.aggregateThroughput = aggregateThroughput;
    }

    public double getAverageLatency() {
        return averageLatency;
    }

    public void setAverageLatency(double averageLatency) {
        this.averageLatency = averageLatency;
    }

    public double getSuccRatio() {
        return succRatio;
    }

    public void setSuccRatio(double succRatio) {
        this.succRatio = succRatio;
    }

    public double getOrderManagerXactRatio() {
        return orderManagerXactRatio;
    }

    public void setOrderManagerXactRatio(double orderManagerXactRatio) {
        this.orderManagerXactRatio = orderManagerXactRatio;
    }
}
