package com.acertainsupplychain.business;

import java.util.List;

import com.acertainsupplychain.exception.InvalidWorkflowException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.OrderManager;

public class CertainOrderManager implements OrderManager {

    @Override
    public int registerOrderWorkflow(List<OrderStep> steps)
            throws OrderProcessingException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
            throws InvalidWorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

}
