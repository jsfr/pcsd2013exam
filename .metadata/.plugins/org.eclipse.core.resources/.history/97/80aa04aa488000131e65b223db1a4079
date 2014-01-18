package com.acertainsupplychain.utils;

import com.acertainsupplychain.business.OrderStepResult;
import com.acertainsupplychain.exception.OrderProcessingException;

public class OrderStepResponse {

    private OrderProcessingException exception = null;
    private OrderStepResult result = null;

    public OrderStepResponse() {

    }

    public OrderStepResponse(OrderProcessingException exception,
            OrderStepResult result) {
        this.setException(exception);
        this.setResult(result);
    }

    public OrderProcessingException getException() {
        return exception;
    }

    public void setException(OrderProcessingException exception) {
        this.exception = exception;
    }

    public OrderStepResult getResult() {
        return result;
    }

    public void setResult(OrderStepResult result) {
        this.result = result;
    }

}
