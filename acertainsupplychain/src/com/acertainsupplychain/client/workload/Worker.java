package com.acertainsupplychain.client.workload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.exception.InvalidItemException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.interfaces.OrderManager.StepStatus;

public class Worker implements Callable<WorkerRunResult> {
    private WorkloadConfiguration config = null;
    private OrderManager orderManager;
    private ItemSupplier localClient;
    private int numOrderManagerInteraction;
    private int numSuccessfulOrderManagerInteraction;
    private Random rand;

    public Worker(WorkloadConfiguration config) throws Exception {
        orderManager = config.getOrderManager();
        localClient = config.getLocalClient();
        this.config = config;
        rand = new Random();
    }

    /**
     * Run the appropriate interaction while trying to maintain the configured
     * distributions
     * 
     * Updates the counts of total runs and successful runs for customer
     * interaction
     * 
     * @param chooseInteraction
     * @return
     */
    private boolean runInteraction(float chooseInteraction) {
        try {
            if (chooseInteraction < config.getPercentLocalClientInteraction()) {
                runLocalClientInteraction();
            } else {
                numOrderManagerInteraction++;
                runOrderManagerInteraction();
                numSuccessfulOrderManagerInteraction++;
            }
        } catch (OrderProcessingException ex) {
            return false;
        }
        return true;
    }

    private void runLocalClientInteraction() throws InvalidItemException {
        Set<Integer> itemIds = new HashSet<Integer>();
        for(int i = 0; i < 5; i++) {
            if (rand.nextBoolean()) {
                itemIds.add(i);
            }
        }
        localClient.getOrdersPerItem(itemIds);
    }

    private void runOrderManagerInteraction() throws OrderProcessingException {
        List<OrderStep> workflow = new ArrayList<OrderStep>();
        List<ItemQuantity> items;
        List<StepStatus> statusList;
        int workflowId;
        int maxQuantity = 1000;

        for (int i = 0; i < config.getStepsPerInteraction(); i++) {
            items = new ArrayList<ItemQuantity>();
            for (int j = 0; j < 5; j++) {
                if (rand.nextBoolean()) {
                    items.add(new ItemQuantity(j, rand.nextInt(maxQuantity )));
                }
            }
            workflow.add(new OrderStep(0, items));
        }

        workflowId = orderManager.registerOrderWorkflow(workflow);

        do {
            statusList = orderManager.getOrderWorkflowStatus(workflowId);
        } while (statusList.contains(StepStatus.REGISTERED));
    }

    /**
     * Run the workloads trying to respect the distributions of the interactions
     * and return result in the end
     */
    public WorkerRunResult call() throws Exception {
        int count = 1;
        long startTimeInNanoSecs = 0;
        long endTimeInNanoSecs = 0;
        int successfulInteractions = 0;
        long timeForRunsInNanoSecs = 0;
        float chooseInteraction;

        // Perform the warmup runs
        while (count++ <= config.getWarmUpRuns()) {
            chooseInteraction = rand.nextFloat() * 100f;
            runInteraction(chooseInteraction);
        }

        count = 1;
        numOrderManagerInteraction = 0;
        numSuccessfulOrderManagerInteraction = 0;

        // Perform the actual runs
        startTimeInNanoSecs = System.nanoTime();
        while (count++ <= config.getNumActualRuns()) {
            chooseInteraction = rand.nextFloat() * 100f;
            if (runInteraction(chooseInteraction)) {
                successfulInteractions++;
            }
        }
        endTimeInNanoSecs = System.nanoTime();
        timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
        return new WorkerRunResult(successfulInteractions,
                timeForRunsInNanoSecs, config.getNumActualRuns(),
                numSuccessfulOrderManagerInteraction,
                numOrderManagerInteraction);
    }
}