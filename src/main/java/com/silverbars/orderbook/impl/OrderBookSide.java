package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;
import com.silverbars.orderbook.OrderBookException;
import com.silverbars.orderbook.Side;

import java.util.List;

public interface OrderBookSide {
    /**
     *
     * @return Side of this OrderBookSide
     */
    Side getSide();

    /**
     * Adds order to OrderBookSide amending the quantity of the appropriate OrderLevelImpl
     * @param order
     */
    void addOrder(Order order) throws OrderBookException;

    /**
     * Cancels the order
     * @param order
     * @throws OrderBookException if Order is not found or cancellation results in an invalid OrderLevelImpl
     */
    void cancelOrder(Order order) throws OrderBookException;

    /**
     * Returns order summary. Will be sorted on the price.
     * @return Order summaries sorted in ascending price for Sell side and descending price for Buy side.
     */
    List<String> getSummary();
}
