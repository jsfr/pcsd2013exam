package com.acertainsupplychain.business;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.acertainsupplychain.exception.InvalidWorkflowException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.utils.SupplyChainConstants;

public class CertainOrderManager implements OrderManager {
    private static String filePath = "/home/jens/repos/pcsd2013exam/acertainsupplychain/src/server.properties";
    private Map<Integer, List<StepStatus>> workflowMap;
    private HashMap<Integer, String> supplierServers;
    private int nextWorkflowId;

    public CertainOrderManager() {
        this.workflowMap = new HashMap<Integer, List<StepStatus>>();
        this.nextWorkflowId = 0;
        try {
            initializeSupplierMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeSupplierMapping() throws Exception {
        Properties props = new Properties();
        this.supplierServers = new HashMap<Integer, String>();
        props.load(new FileInputStream(filePath));
        String supplierAddresses = props
                        .getProperty(SupplyChainConstants.KEY_SUPPLIERS);
        for (String s : supplierAddresses
                        .split(SupplyChainConstants.SUPPLIER_SERV_SPLIT_REGEX)) {
                String[] supplier = s.split(SupplyChainConstants.SUPPLIER_ID_SPLIT_REGEX);
                if (!supplier[1].toLowerCase().startsWith("http://")) {
                        supplier[1] = new String("http://" + supplier[1]);
                }
                if (!supplier[1].endsWith("/")) {
                        supplier[1] = new String(supplier + "/");
                }
                supplierServers.put(Integer.valueOf(supplier[0]) , supplier[1]);
        }
    }
    
/*    private void waitForSupplierUpdates(
        List<Future<SupplierResult>> supplierFutures) {
        List<SupplierResult> supplierServers = new ArrayList<SupplierResult>();
        for (Future<SupplierResult> supplierServer : supplierFutures) {
                try {
                        // block until the future result is available
                        supplierServers.add(supplierServer.get());
                } catch (InterruptedException e) {
                        e.printStackTrace();
                } catch (ExecutionException e) {
                        e.printStackTrace();
                }
        }
    
        for (SupplierResult supplierServer : supplierServers) {
                if (!supplierServer.isSuccessful()) {
                        // Remove the server from the list of servers - fail stop model
                        this.supplierServers.remove(supplierServer.getServerAddress());
                }
        }
    }*/

    @Override
    public int registerOrderWorkflow(List<OrderStep> steps)
            throws OrderProcessingException {
        int workflowId = this.nextWorkflowId;

        
        //TODO
        
        this.nextWorkflowId++;
        return workflowId;
    }

    @Override
    public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
            throws InvalidWorkflowException {
        if (!workflowMap.containsKey(orderWorkflowId)) {
            throw new InvalidWorkflowException(); 
        }
        return workflowMap.get(orderWorkflowId);
    }
}