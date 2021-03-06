package com.acertainsupplychain.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class OrderStepExecutor {

    ExecutorService exec;
    FileHandler fh;
    Logger logger;

    public OrderStepExecutor(int maxThreads, Logger logger, FileHandler fh) {
        exec = Executors.newFixedThreadPool(maxThreads);
        this.logger = logger;
        this.fh = fh;
    }

    public List<Future<OrderStepResult>> executeWorkflow(
            Map<Integer, String> suppliers, List<OrderStepRequest> requests) {

        List<Future<OrderStepResult>> results = new ArrayList<Future<OrderStepResult>>();

        for (OrderStepRequest step : requests) {
            OrderStepTask task = new OrderStepTask(suppliers.get(step.getStep()
                    .getSupplierId()), step, logger, fh);
            results.add(exec.submit(task));
        }

        return results;
    }
}
