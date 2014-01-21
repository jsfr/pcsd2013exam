package com.acertainsupplychain.client.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainsupplychain.business.CertainItemSupplier;
import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.client.ItemSupplierHTTPProxy;
import com.acertainsupplychain.exception.InvalidItemException;
import com.acertainsupplychain.exception.OrderProcessingException;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.server.ServerRunnable;
import com.acertainsupplychain.server.ServerRunnable.Server;

/**
 * Tests a single ItemSupplier. The supplier is expected to have id = 0 and
 * itemids = [0;4]
 */
public class ItemSupplierTest {
    public enum AtomicityTestSupplier {
        CHECKER, INCREMENTER;
    }

    private static ItemSupplier client;
    private static int id = 0;
    private static boolean localTest = false;
    private static int maxQuantity = 1000;

    private static Random r = new Random();

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            if (localTest) {
                client = new CertainItemSupplier(id);
            } else {
                // Not joining on the threads and letting them die
                // with the VM is kind of dirty, but it works for our purpose.
                new Thread(new ServerRunnable(Integer.toString(id), "8082",
                        Server.ITEMSUPPLIER_SERVER)).start();
                client = new ItemSupplierHTTPProxy(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomicity() throws Exception {
        class ItemSupplierRunnable implements Runnable {
            private ItemSupplier proxy;
            private AtomicityTestSupplier type;

            public ItemSupplierRunnable(AtomicityTestSupplier type)
                    throws Exception {
                proxy = new ItemSupplierHTTPProxy(id);
                this.type = type;
            }

            @Override
            public void run() {
                switch (type) {
                case CHECKER:
                    List<ItemQuantity> resItems = null;
                    Set<Integer> itemIds = new HashSet<Integer>();
                    for (int i = 0; i < 5; i++) {
                        itemIds.add(i);
                    }
                    List<ItemQuantity> initItems = null;
                    try {
                        initItems = proxy.getOrdersPerItem(itemIds);
                    } catch (InvalidItemException e1) {
                        e1.printStackTrace();
                    }
                    for (int i = 0; i < 100; i++) {
                        try {
                            resItems = proxy.getOrdersPerItem(itemIds);
                        } catch (InvalidItemException e) {
                            e.printStackTrace();
                        }
                        int firstDiff = -1;
                        int diff = -1;
                        for (ItemQuantity item : resItems) {
                            if (firstDiff < 0) {
                                for (ItemQuantity initItem : initItems) {
                                    if (item.getItemId() == initItem
                                            .getItemId()) {
                                        firstDiff = item.getQuantity()
                                                - initItem.getQuantity();
                                        diff = firstDiff;
                                        break;
                                    }
                                }
                            } else {
                                for (ItemQuantity initItem : initItems) {
                                    if (item.getItemId() == initItem
                                            .getItemId()) {
                                        diff = item.getQuantity()
                                                - initItem.getQuantity();
                                        break;
                                    }
                                }
                            }
                            assertEquals(firstDiff, diff);
                        }
                    }
                    break;
                case INCREMENTER:
                    List<ItemQuantity> items = new ArrayList<ItemQuantity>();
                    for (int i = 0; i < 5; i++) {
                        items.add(new ItemQuantity(i, 1)); // Adding one to each
                                                           // item id
                    }
                    OrderStep step = new OrderStep(id, items);
                    for (int i = 0; i < 100; i++) {
                        try {
                            proxy.executeStep(step);
                        } catch (OrderProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }
        Thread t0 = new Thread(new ItemSupplierRunnable(
                AtomicityTestSupplier.CHECKER));
        Thread t1 = new Thread(new ItemSupplierRunnable(
                AtomicityTestSupplier.INCREMENTER));
        t0.start();
        t1.start();
        t0.join();
        t1.join();
    }

    @Test
    public void testExecuteStep() throws OrderProcessingException {
        List<ItemQuantity> items = new ArrayList<ItemQuantity>();
        for (int i = 0; i < 50; i++) {
            items.add(new ItemQuantity(r.nextInt(5), r.nextInt(maxQuantity)));
        }
        OrderStep step = new OrderStep(id, items);
        client.executeStep(step);
    }

    @Test(expected = InvalidItemException.class)
    public void testExecuteStepInvalidItemId() throws OrderProcessingException {
        List<ItemQuantity> items = new ArrayList<ItemQuantity>();
        items.add(new ItemQuantity(-1, 1));
        OrderStep step = new OrderStep(id, items);
        client.executeStep(step);
    }

    @Test(expected = OrderProcessingException.class)
    public void testExecuteStepNegativeQuantity()
            throws OrderProcessingException {
        List<ItemQuantity> items = new ArrayList<ItemQuantity>();
        items.add(new ItemQuantity(0, -1));
        OrderStep step = new OrderStep(id, items);
        client.executeStep(step);
    }

    @Test(expected = OrderProcessingException.class)
    public void testExecuteStepWrongSupplierId()
            throws OrderProcessingException {
        OrderStep step = new OrderStep(id + 1, new ArrayList<ItemQuantity>());
        client.executeStep(step);
    }

    @Test
    public void testGetOrdersPerItem() throws OrderProcessingException {
        Set<Integer> itemIds = new HashSet<Integer>();
        for (int i = 0; i < 5; i++) {
            itemIds.add(i);
        }
        List<ItemQuantity> itemsBefore = client.getOrdersPerItem(itemIds);
        assertEquals(itemsBefore.size(), itemIds.size());

        OrderStep step = new OrderStep(id, itemsBefore);
        client.executeStep(step);

        List<ItemQuantity> itemsAfter = client.getOrdersPerItem(itemIds);
        assertEquals(itemsAfter.size(), itemIds.size());

        for (ItemQuantity i : itemsAfter) {
            int quantityBefore = itemsBefore.get(i.getItemId()).getQuantity();
            int quantityAfter = i.getQuantity();
            assertEquals(2 * quantityBefore, quantityAfter);
        }
    }

    @Test(expected = InvalidItemException.class)
    public void testGetOrdersPerItemInvalidItemId() throws InvalidItemException {
        Set<Integer> itemIds = new HashSet<Integer>();
        itemIds.add(-1);
        client.getOrdersPerItem(itemIds);
    }
}