package com.acertainsupplychain.business;

import com.acertainsupplychain.utils.ItemSupplierMessageTag;

public class OrderStepRequest {

    private OrderStep step;
    private int stepId;
    private ItemSupplierMessageTag tag;
    private int workflowId;

    public OrderStepRequest(OrderStep step, ItemSupplierMessageTag tag,
            int workflowId, int stepId) {
        this.step = step;
        this.tag = tag;
        this.workflowId = workflowId;
        this.stepId = stepId;
    }

    public ItemSupplierMessageTag getMessageType() {
        return tag;
    }

    public OrderStep getStep() {
        return step;
    }

    public int getStepId() {
        return stepId;
    }

    public int getWorkflowId() {
        return workflowId;
    }
}
