package com.acertainsupplychain.server;

import com.acertainsupplychain.business.CertainItemSupplier;
import com.acertainsupplychain.interfaces.ItemSupplier;

/**
 * Starts the ItemSupplier HTTP server.
 */
public class ItemSupplierHTTPServer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ItemSupplier supplier = new CertainItemSupplier(
                Integer.valueOf(args[0]));
        ItemSupplierHTTPMessageHandler handler = new ItemSupplierHTTPMessageHandler(
                supplier);
        if (SupplyChainHTTPServerUtility.createServer(Integer.valueOf(args[1]),
                handler)) {
            ;
        }
    }
}