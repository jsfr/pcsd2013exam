package com.acertainsupplychain.business;

public class OrderStepResult {

    private boolean succ;

    public OrderStepResult(boolean b) {
        this.succ = b;
    }

    public void setSuccessful(boolean b) {
        this.succ = b;
    }

    public boolean getSuccesful() {
        return this.succ;
    }
}
