package com.acertainsupplychain.business;

import java.util.List;

/**
 * An OrderStep instance contains a quantity ordered against specific items, all
 * managed by a specific item supplier.
 */
public final class OrderStep {

    /**
     * The list of items ordered and their quantities.
     */
    private final List<ItemQuantity> items;

    /**
     * The ID of the item supplier that manages the items.
     */
    private final int supplierId;

    /**
     * Constructs an OrderStep instance with given supplier, item, and quantity.
     */
    public OrderStep(int supplierId, List<ItemQuantity> items) {
        this.supplierId = supplierId;
        this.items = items;
    }

    /**
     * @return the items
     */
    public List<ItemQuantity> getItems() {
        return items;
    }

    /**
     * @return the supplierId
     */
    public int getSupplierId() {
        return supplierId;
    }

}
