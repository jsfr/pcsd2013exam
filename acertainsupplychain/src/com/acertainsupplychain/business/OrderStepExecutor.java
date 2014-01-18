package com.acertainsupplychain.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OrderStepExecutor {

    ExecutorService exec;

    public OrderStepExecutor(int maxReplicatorThreads) {
        exec = Executors.newFixedThreadPool(maxReplicatorThreads);
    }

    public List<Future<OrderStepResult>> executeWorkflow(Map<Integer,String> suppliers, List<OrderStepRequest> requests) {

        List<Future<OrderStepResult>> results = new ArrayList<Future<OrderStepResult>>();

        for(OrderStepRequest step : requests) {
            OrderStepTask task = new OrderStepTask(suppliers.get(step.getStep().getSupplierId()), step);
            results.add(exec.submit(task));
        }

        return results;
    }
}
