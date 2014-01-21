package com.acertainsupplychain.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.exception.InvalidWorkflowException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.utils.OrderManagerMessageTag;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainResponse;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class OrderManagerHTTPProxy implements OrderManager {

    private HttpClient client;
    private String filePath = "/home/jens/repos/pcsd2013exam/acertainsupplychain/src/server.properties";
    private String serverAddress = null;

    public OrderManagerHTTPProxy() throws Exception {
        initializeServerAddress();
        client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(SupplyChainClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        client.setThreadPool(new QueuedThreadPool(
                SupplyChainClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        client.setTimeout(SupplyChainClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
        client.start();
    }

    /**
     * Initializes the server address found in the set filePath.
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void initializeServerAddress() throws FileNotFoundException,
    IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(filePath));

        serverAddress = props
                .getProperty(SupplyChainConstants.KEY_ORDERMANAGER);
        if (!serverAddress.toLowerCase().startsWith("http://")) {
            serverAddress = new String("http://" + serverAddress);
        }
        if (!serverAddress.endsWith("/")) {
            serverAddress = new String(serverAddress + "/");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
            throws InvalidWorkflowException {
        String orderWorkflowIdXmlString = SupplyChainUtility
                .serializeObjectToXMLString(orderWorkflowId);
        Buffer requestContent = new ByteArrayBuffer(orderWorkflowIdXmlString);

        SupplyChainResponse result = null;

        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + OrderManagerMessageTag.GETORDER;
        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);
        try {
            result = SupplyChainUtility.SendAndRecv(client, exchange);
        } catch (OrderProcessingException e) {
            throw new InvalidWorkflowException();
        }
        return (List<StepStatus>) result.getResultList();
    }

    @Override
    public int registerOrderWorkflow(List<OrderStep> steps)
            throws OrderProcessingException {
        String listOrderStepsXmlString = SupplyChainUtility
                .serializeObjectToXMLString(steps);
        Buffer requestContent = new ByteArrayBuffer(listOrderStepsXmlString);

        SupplyChainResponse result = null;

        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + OrderManagerMessageTag.REGISTERORDER;
        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);
        result = SupplyChainUtility.SendAndRecv(client, exchange);

        return result.getWorkflowId();
    }

    public void stopClient() {
        try {
            this.client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
