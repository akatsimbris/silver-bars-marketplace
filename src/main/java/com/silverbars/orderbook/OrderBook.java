package com.silverbars.orderbook;

import java.util.List;

public interface OrderBook {
    /**
     * Add order to OrderBook
     * @param order
     * @throws OrderBookException if error resulted in order not being added.
     */
    void addOrder(Order order) throws OrderBookException;

    /**
     * Cancel order from OrderBook
     * @param order
     * @throws OrderBookException
     */
    void cancelOrder(Order order) throws OrderBookException;

    /**
     * Retrieve a summary for either Buy or Sell side of OrderBook
     * @param side
     * @return summary details sorted in ascending price for sell side book and descending price for buy side book
     */
    List<String> getSummary(Side side);
}
