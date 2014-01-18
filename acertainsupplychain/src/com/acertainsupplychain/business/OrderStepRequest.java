package com.acertainsupplychain.business;

import com.acertainsupplychain.utils.ItemSupplierMessageTag;

public class OrderStepRequest {

    private OrderStep step;
    private ItemSupplierMessageTag tag;

    public OrderStepRequest(OrderStep step, ItemSupplierMessageTag tag) {
        this.step = step;
        this.tag = tag;
    }

    public ItemSupplierMessageTag getMessageType() {
        return tag;
    }

    public OrderStep getStep() {
        return step;
    }
}
