package com.acertainsupplychain.server;

import com.acertainsupplychain.business.CertainOrderManager;
import com.acertainsupplychain.interfaces.OrderManager;

/**
 * Starts the master bookstore HTTP server.
 */
public class OrderManagerHTTPServer {

        /**
         * @param args
         */
        public static void main(String[] args) {
                OrderManager ordermanager = new CertainOrderManager(Integer.valueOf(args[0]));
                OrderManagerHTTPMessageHandler handler = new OrderManagerHTTPMessageHandler(ordermanager);
                if (SupplyChainHTTPServerUtility.createServer(Integer.valueOf(args[1]), handler)) {
                        ;
                }
        }

}
