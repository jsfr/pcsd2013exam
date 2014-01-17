package com.acertainsupplychain.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public final class SupplyChainUtility {
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
     * Convert a request URI to the message tags supported
     * 
     * @param requestURI
     * @return
     */
    public static OrderManagerMessageTag convertURItoOrderManagerMessageTag(String requestURI) {

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
     * Convert a request URI to the message tags supported
     * 
     * @param requestURI
     * @return
     */
    public static ItemSupplierMessageTag convertURItoItemSupplierMessageTag(String requestURI) {

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
}
