package com.acertainsupplychain.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;

import com.acertainsupplychain.client.SupplyChainClientConstants;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public final class SupplyChainUtility {
    /**
     * Convert a request URI to the message tags supported
     * 
     * @param requestURI
     * @return
     */
    public static ItemSupplierMessageTag convertURItoItemSupplierMessageTag(
            String requestURI) {

        try {
            ItemSupplierMessageTag messageTag = ItemSupplierMessageTag
                    .valueOf(requestURI.substring(1).toUpperCase());
            return messageTag;
        } catch (IllegalArgumentException ex) {
            ; // Enum type matching failed so non supported message
        } catch (NullPointerException ex) {
            ; // RequestURI was empty
        }
        return null;
    }

    /**
     * Convert a request URI to the message tags supported
     * 
     * @param requestURI
     * @return
     */
    public static OrderManagerMessageTag convertURItoOrderManagerMessageTag(
            String requestURI) {

        try {
            OrderManagerMessageTag messageTag = OrderManagerMessageTag
                    .valueOf(requestURI.substring(1).toUpperCase());
            return messageTag;
        } catch (IllegalArgumentException ex) {
            ; // Enum type matching failed so non supported message
        } catch (NullPointerException ex) {
            ; // RequestURI was empty
        }
        return null;
    }

    /**
     * De-serializes an xml string to object
     * 
     * @param xmlObject
     * @return
     */
    public static Object deserializeXMLStringToObject(String xmlObject) {
        Object dataObject = null;
        XStream xmlStream = new XStream(new StaxDriver());
        dataObject = xmlStream.fromXML(xmlObject);
        return dataObject;
    }

    /**
     * Returns the message of the request as a string
     * 
     * @param request
     * @return xml string
     * @throws IOException
     */
    public static String extractPOSTDataFromRequest(HttpServletRequest request)
            throws IOException {
        Reader reader = request.getReader();
        int len = request.getContentLength();

        // Request must be read into a char[] first
        char res[] = new char[len];
        reader.read(res);
        reader.close();
        return new String(res);
    }

    public static SupplyChainResponse SendAndRecv(HttpClient client,
            ContentExchange exchange) throws OrderProcessingException {
        int exchangeState;
        try {
            client.send(exchange);
        } catch (IOException ex) {
            throw new OrderProcessingException(
                    SupplyChainClientConstants.strERR_CLIENT_REQUEST_SENDING,
                    ex);
        }

        try {
            exchangeState = exchange.waitForDone(); // block until the response
            // is available
        } catch (InterruptedException ex) {
            throw new OrderProcessingException(
                    SupplyChainClientConstants.strERR_CLIENT_REQUEST_SENDING,
                    ex);
        }

        if (exchangeState == HttpExchange.STATUS_COMPLETED) {
            try {
                SupplyChainResponse supplyChainResponse = (SupplyChainResponse) SupplyChainUtility
                        .deserializeXMLStringToObject(exchange
                                .getResponseContent().trim());
                OrderProcessingException ex = supplyChainResponse
                        .getException();
                if (ex != null) {
                    throw ex;
                }
                return supplyChainResponse;

            } catch (UnsupportedEncodingException ex) {
                throw new OrderProcessingException(
                        SupplyChainClientConstants.strERR_CLIENT_RESPONSE_DECODING,
                        ex);
            }
        } else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
            throw new OrderProcessingException(
                    SupplyChainClientConstants.strERR_CLIENT_REQUEST_EXCEPTION);
        } else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
            throw new OrderProcessingException(
                    SupplyChainClientConstants.strERR_CLIENT_REQUEST_TIMEOUT);
        } else {
            throw new OrderProcessingException(
                    SupplyChainClientConstants.strERR_CLIENT_UNKNOWN);
        }
    }

    /**
     * Serializes an object to an xml string
     * 
     * @param object
     * @return
     */
    public static String serializeObjectToXMLString(Object object) {
        String xmlString;
        XStream xmlStream = new XStream(new StaxDriver());
        xmlString = xmlStream.toXML(object);
        return xmlString;
    }
}