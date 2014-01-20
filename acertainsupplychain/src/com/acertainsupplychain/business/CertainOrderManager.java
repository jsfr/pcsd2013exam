package com.acertainsupplychain.business;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.acertainsupplychain.exception.InvalidWorkflowException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.utils.ItemSupplierMessageTag;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainFormatter;

public class CertainOrderManager implements OrderManager {
    private static String filePath = "/home/jens/repos/pcsd2013exam/acertainsupplychain/src/server.properties";
    private OrderStepExecutor executor;
    private FileHandler fh;
    private Logger logger;
    private int maxOrderStepThreads = 10;
    private HashMap<Integer, String> supplierServers;
    private int workflowId;
    private Map<Integer, List<Future<OrderStepResult>>> workflowMap;

    public CertainOrderManager(int managerId) {
        try {
            workflowMap = new HashMap<Integer, List<Future<OrderStepResult>>>();
            workflowId = 0;
            logger = Logger.getLogger("CertainOrderManagerLog");
            logger.setUseParentHandlers(false);
            fh = new FileHandler("CertainOrdermanager" + managerId + ".log");
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            fh.setFormatter(new SupplyChainFormatter());
            executor = new OrderStepExecutor(maxOrderStepThreads, logger, fh);
            initializeSupplierMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the mapping between supplier ids and their server address
     * using the set filePath.
     * 
     * @throws Exception
     */
    private void initializeSupplierMapping() throws Exception {
        Properties props = new Properties();
        supplierServers = new HashMap<Integer, String>();
        props.load(new FileInputStream(filePath));
        String supplierAddresses = props
                .getProperty(SupplyChainConstants.KEY_SUPPLIERS);
        for (String s : supplierAddresses
                .split(SupplyChainConstants.SUPPLIER_SERV_SPLIT_REGEX)) {
            String[] supplier = s
                    .split(SupplyChainConstants.SUPPLIER_ID_SPLIT_REGEX);
            if (!supplier[1].toLowerCase().startsWith("http://")) {
                supplier[1] = new String("http://" + supplier[1]);
            }
            if (!supplier[1].endsWith("/")) {
                supplier[1] = new String(supplier[1] + "/");
            }
            supplierServers.put(Integer.valueOf(supplier[0]), supplier[1]);
        }
    }

    @Override
    public synchronized List<StepStatus> getOrderWorkflowStatus(
            int orderWorkflowId) throws InvalidWorkflowException {
        if (!workflowMap.containsKey(orderWorkflowId)) {
            throw new InvalidWorkflowException();
        }

        List<Future<OrderStepResult>> workflow = workflowMap
                .get(orderWorkflowId);
        List<StepStatus> results = new ArrayList<StepStatus>();

        for (Future<OrderStepResult> r : workflow) {
            try {
                OrderStepResult res = r.get(50, TimeUnit.MILLISECONDS);
                if (res.getSuccesful()) {
                    results.add(StepStatus.SUCCESSFUL);
                } else {
                    results.add(StepStatus.FAILED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                results.add(StepStatus.REGISTERED);
            }
        }

        return results;
    }

    @Override
    public synchronized int registerOrderWorkflow(List<OrderStep> steps)
            throws OrderProcessingException {
        int id = workflowId;
        List<OrderStepRequest> requests = new ArrayList<OrderStepRequest>();
        int stepId = 0;

        synchronized (logger) {
            stepId = 0;
            logger.log(Level.INFO, "WORKFLOW BEGIN " + workflowId);
            for (OrderStep s : steps) {
                logger.log(Level.INFO, "ORDERSTEP " + stepId);
                logger.log(Level.INFO, "SUPPLIERID " + s.getSupplierId());
                stepId++;
                for (ItemQuantity i : s.getItems()) {
                    logger.log(Level.INFO,
                            "ITEM " + i.getItemId() + " " + i.getQuantity());
                }
            }
            fh.flush();

            for (OrderStep s : steps) {
                if (supplierServers.containsKey(s.getSupplierId())) {
                    requests.add(new OrderStepRequest(s,
                            ItemSupplierMessageTag.EXECUTESTEP, id, stepId));
                    stepId++;
                } else {
                    logger.log(Level.INFO, "WORKFLOW CANCELLED");
                    fh.flush();
                    throw new OrderProcessingException();
                }
            }
            List<Future<OrderStepResult>> orderStepFutures = executor
                    .executeWorkflow(supplierServers, requests);
            workflowMap.put(id, orderStepFutures);

            logger.log(Level.INFO, "WORKFLOW END");
            fh.flush();
        }

        workflowId++;
        return id;
    }
}