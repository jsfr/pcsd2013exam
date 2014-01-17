package com.acertainsupplychain.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.ItemSupplierMessageTag;
import com.acertainsupplychain.utils.OrderManagerResponse;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class ItemSupplierHTTPMessageHandler extends AbstractHandler {

    public ItemSupplierHTTPMessageHandler(ItemSupplier supplier) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String requestURI;
        ItemSupplierMessageTag messageTag;

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        requestURI = request.getRequestURI();

        messageTag = SupplyChainUtility
                .convertURItoItemSupplierMessageTag(requestURI);

        // the RequestURI before the switch
        if (messageTag == null) {
            System.out.println("Unknown message tag");
        } else {
            String xml;
            OrderManagerResponse orderManagerResponse;
            // Write requests should not be handled
            switch (messageTag) {
            case GETORDERS:
                // TODO
                break;
            case EXECUTESTEP:
                // TODO
                break;
            default:
                System.out.println("Unhandled message tag");
                break;
            }
            // Mark the request as handled so that the HTTP response can be sent
            baseRequest.setHandled(true);
        }
    }
}
