package com.acertainsupplychain.business;

import java.util.concurrent.Callable;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.client.SupplyChainClientConstants;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class OrderStepTask implements Callable<OrderStepResult> {

    private OrderStepRequest request;
    private String server;
    private HttpClient client;

    public OrderStepTask(String server, OrderStepRequest request) {
        this.server = server;
        this.request = request;
        this.client = new HttpClient();

        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(SupplyChainClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        client.setThreadPool(new QueuedThreadPool(
                SupplyChainClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        client.setTimeout(SupplyChainClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);

        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderStepResult call() throws Exception {
        String dataSetXmlString = SupplyChainUtility.serializeObjectToXMLString(request.getStep());

        Buffer requestContent = new ByteArrayBuffer(dataSetXmlString);

        ContentExchange exchange = new ContentExchange();
        String urlString = this.server
                + request.getMessageType();


        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);
        OrderStepResult retval = new OrderStepResult(true);

        try {
            SupplyChainUtility.SendAndRecv(this.client, exchange);
        } catch (OrderProcessingException e) {
            retval.setSuccessful(false);
        }

        return retval;
    }
}
