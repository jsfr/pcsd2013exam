package com.acertainsupplychain.utils;

import java.util.List;

import com.acertainsupplychain.exception.OrderProcessingException;

/**
 * 
 * Data Structure that we use to communicate objects and error messages from the
 * server to the client.
 * 
 */
public class SupplyChainResponse {
    private OrderProcessingException exception = null;
    private List<?> resultList;
    private int workflowId;

    public SupplyChainResponse() {
    }

    public OrderProcessingException getException() {
        return exception;
    }

    public List<?> getResultList() {
        return resultList;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setException(OrderProcessingException exception) {
        this.exception = exception;
    }

    public void setResultList(List<?> resultList) {
        this.resultList = resultList;
    }

    public void setWorkflowId(int workflowId2) {
        workflowId = workflowId2;
    }
}