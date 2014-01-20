package com.acertainsupplychain.business;

public class OrderStepResult {

    private boolean succ;

    public OrderStepResult(boolean b) {
        succ = b;
    }

    public boolean getSuccesful() {
        return succ;
    }

    public void setSuccessful(boolean b) {
        succ = b;
    }
}
