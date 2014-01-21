package com.acertainsupplychain.client.workload;

import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.interfaces.OrderManager;

public class WorkloadConfiguration {

    private OrderManager orderManager = null;
    private ItemSupplier localClient = null;
    private int warmUpRuns = 10;
    private int numActualRuns = 30;
    private int stepsPerInteraction = 3;
    private float percentLocalClientInteraction = 20f;

    public WorkloadConfiguration(OrderManager orderManager, 
            ItemSupplier localClient) {
        this.setLocalClient(localClient);
        this.setOrderManager(orderManager);
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public ItemSupplier getLocalClient() {
        return localClient;
    }

    public void setLocalClient(ItemSupplier localClient) {
        this.localClient = localClient;
    }

    public int getNumActualRuns() {
        return numActualRuns;
    }

    public void setNumActualRuns(int numActualRuns) {
        this.numActualRuns = numActualRuns;
    }

    public int getWarmUpRuns() {
        return warmUpRuns;
    }

    public void setWarmUpRuns(int warmUpRuns) {
        this.warmUpRuns = warmUpRuns;
    }

    public int getStepsPerInteraction() {
        return stepsPerInteraction;
    }

    public void setStepsPerInteraction(int stepsPerInteraction) {
        this.stepsPerInteraction = stepsPerInteraction;
    }

    public float getPercentLocalClientInteraction() {
        return percentLocalClientInteraction;
    }

    public void setPercentLocalClientInteraction(
            float percentLocalClientInteraction) {
        this.percentLocalClientInteraction = percentLocalClientInteraction;
    }
}
