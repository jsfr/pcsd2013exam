package com.acertainsupplychain.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.exception.InvalidItemException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.ItemSupplierMessageTag;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainResponse;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class ItemSupplierHTTPProxy implements ItemSupplier {

    private HttpClient client = null;
    private String filePath = "/home/jens/repos/pcsd2013exam/acertainsupplychain/src/server.properties";
    private String serverAddress = null;

    public ItemSupplierHTTPProxy(int supplierId) throws Exception {
        initializeServerAddress(supplierId);
        client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(SupplyChainClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        client.setThreadPool(new QueuedThreadPool(
                SupplyChainClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        client.setTimeout(SupplyChainClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
        client.start();
    }

    /**
     * Initializes the server address found in the given filePath.
     * Will fail if the id cannot be found in the file.
     * 
     * @param supplierId
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void initializeServerAddress(int supplierId)
            throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(filePath));
        String supplierAddresses = props
                .getProperty(SupplyChainConstants.KEY_SUPPLIERS);
        for (String s : supplierAddresses
                .split(SupplyChainConstants.SUPPLIER_SERV_SPLIT_REGEX)) {
            String[] supplier = s
                    .split(SupplyChainConstants.SUPPLIER_ID_SPLIT_REGEX);
            if (Integer.valueOf(supplier[0]) == supplierId) {
                if (!supplier[1].toLowerCase().startsWith("http://")) {
                    supplier[1] = new String("http://" + supplier[1]);
                }
                if (!supplier[1].endsWith("/")) {
                    supplier[1] = new String(supplier[1] + "/");
                }
                serverAddress = supplier[1];
                break;
            }
        }
    }

    @Override
    public void executeStep(OrderStep step) throws OrderProcessingException {
        String orderStepXmlString = SupplyChainUtility
                .serializeObjectToXMLString(step);
        Buffer requestContent = new ByteArrayBuffer(orderStepXmlString);

        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + ItemSupplierMessageTag.EXECUTESTEP;
        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);
        SupplyChainUtility.SendAndRecv(client, exchange);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds)
            throws InvalidItemException {
        String itemIdsXmlString = SupplyChainUtility
                .serializeObjectToXMLString(itemIds);
        Buffer requestContent = new ByteArrayBuffer(itemIdsXmlString);

        SupplyChainResponse result = null;

        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + ItemSupplierMessageTag.GETORDERS;
        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);
        try {
            result = SupplyChainUtility.SendAndRecv(client, exchange);
        } catch (OrderProcessingException e) {
            throw (InvalidItemException) e;
        }
        return (List<ItemQuantity>) result.getResultList();
    }

    public void stopClient() {
        try {
            this.client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
