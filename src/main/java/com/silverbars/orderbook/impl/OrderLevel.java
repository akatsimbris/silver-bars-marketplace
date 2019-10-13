package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An OrderLevel represents the summary of all orders of the same price.
 */
public interface OrderLevel {
    /**
     * @return price of the OrderLevel
     */
    BigDecimal getPrice();

    /**
     * @return summed quantity of all orders for the OrderLevel's price
     */
    BigDecimal getQuantity();

    /**
     * Add order to this OrderLevel. Will result in the Order's quantity being added to the OrderLevel's quantity
     * @param order
     */
    void addOrder(Order order);

    /**
     * Remove Order from OrderLevel. Order's quantity removed from OrderLevel's quantity.
     * @param order
     */
    void removeOrder(Order order);

    /**
     * Helper method to get a BigDecimal representation of the quantity double. Ensures consistency in the BigDecimal's scale and rounding mode
     * @param quantity
     * @return
     */
    static BigDecimal getQuantityAsBigDecimal(final double quantity){
        return BigDecimal.valueOf(quantity).setScale(1, RoundingMode.HALF_UP); // quantity precision assumed to be to 1dp
    }

    /**
     * Helper method to get a BigDecimal representation of the price. Ensures consistency in the BigDecimal's scale and rounding mode.
     * @param price
     * @return
     */
    static BigDecimal getPriceAsBigDecimal(final double price){
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP); // price in GBP so precision to 2dp
    }


}
