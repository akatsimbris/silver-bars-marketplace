package com.silverbars.orderbook.impl;

import com.google.common.annotations.VisibleForTesting;
import com.silverbars.orderbook.Order;
import com.silverbars.orderbook.OrderBookException;
import com.silverbars.orderbook.Side;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class OrderBookSideImpl implements OrderBookSide{
    private final Logger log = Logger.getLogger(OrderBookSideImpl.class);
    private final static Comparator<OrderLevel> SELL_SIDE_COMPARATOR = (ol1, ol2) -> ol1.getPrice().compareTo(ol2.getPrice());
    private final static String SUMMARY_FORMAT = "%.1f kg for £%.2f";

    private final Side side;

    private final Map<BigDecimal, OrderLevelImpl> orderLevels; // keyed on price  - use map for fast access

    // Sorted summary of orders - assume that this will be accessed very frequently so do not produce on the fly
    private List<String> orderSummary = Collections.EMPTY_LIST;

    private Map<String, Order> orders;

    // Used for accessing orderSummary List.
    private Lock readLock;
    // WriteLock required as need a Lock when adding or cancelling an order as these operations involve updating orderLevels, orderSummary and orders
    // collections and need to ensure that updates are perfomed as an atomic operation to ensure data integrity
    private Lock writeLock;

    public OrderBookSideImpl(Side side) {
        this(side, new HashMap<>(), new HashMap<>());
    }

    @VisibleForTesting
    OrderBookSideImpl(final Side side, final Map<BigDecimal, OrderLevelImpl> orderLevels, final Map<String, Order> orders) {
        this.side = side;
        this.orders = orders;
        this.orderLevels = orderLevels;
        final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
        this.readLock = rwLock.readLock();
        this.writeLock = rwLock.writeLock();
    }


    @Override
    public Side getSide() {
        return side;
    }

    /**
     * Adds an order to the OrderBookSide. This implementation is not idempotent, so if an order is added that has already
     * been submitted, a OrderBookException is thrown.
     * @param order
     * @throws OrderBookException
     */
    @Override
    public void addOrder(final Order order) throws OrderBookException{
        writeLock.lock();
        try{
            // if Order has been previously submitted, throw exception
            if( orders.containsKey(order.getOrderId())){
                log.error("Invalid call to addOrder: Order has been previously submitted: " + order);
                throw new OrderBookException("Order has been previously submitted: " + order.getOrderId());
            }
            orders.put(order.getOrderId(), order);
            final BigDecimal orderPrice = OrderLevel.getPriceAsBigDecimal(order.getPrice());

            final OrderLevel orderLevel = orderLevels.computeIfAbsent( orderPrice, a -> new OrderLevelImpl(order.getPrice()));
            orderLevel.addOrder(order);

            // Now rebuild order summary
            rebuildOrderSummary();
        }
        finally {
            writeLock.unlock();
        }

    }

    /**
     * Performs cancellation of Orders. This implementation is not idempotent so if the order is cancelled more than once a
     * OrderBookException is thrown.
     * @param order
     * @throws OrderBookException if an order is cancelled that was not previously submitted or if an order in cancelled more than once.
     */
    @Override
    public void cancelOrder(final Order order) throws OrderBookException {
        writeLock.lock();
        try {
            if( !orders.containsKey(order.getOrderId())){
                log.error("Order could not be found, or was previously cancelled: " + order);
                throw new OrderBookException("Order could not be found, or was previously cancelled: " + order);
            }
            orders.remove(order.getOrderId());

            final BigDecimal price = OrderLevel.getPriceAsBigDecimal(order.getPrice());
            final OrderLevel orderLevel = orderLevels.get(price);
            if( orderLevel == null){
                log.error("OrderLevel could not be found for Order: " + order);
                throw new OrderBookException("OrderLevel could not be found for Order: " + order);
            }
            orderLevel.removeOrder(order);

            // If OrderLevel quantity is zero, we remove it from map
            if( orderLevel.getQuantity().equals( OrderLevel.getQuantityAsBigDecimal(0))){
                orderLevels.remove(price);
            }

            // now rebuild order summary
            rebuildOrderSummary();
        }
        finally {
            writeLock.unlock();
        }

    }

    @Override
    public List<String> getSummary() {
        readLock.lock();
        try {
            return orderSummary;
        }
        finally {
            readLock.unlock();
        }
    }


    private void rebuildOrderSummary(){
        orderSummary=  Collections.unmodifiableList( orderLevels.values().stream()
                .sorted( side == Side.SELL ? SELL_SIDE_COMPARATOR : SELL_SIDE_COMPARATOR.reversed())
                .map(this::mapToString)
                .collect(Collectors.toList()) );
    }

    private String mapToString(final OrderLevel orderLevel){
        return String.format(SUMMARY_FORMAT, orderLevel.getQuantity(), orderLevel.getPrice());
    }

    @VisibleForTesting
    void setReadLock(final Lock lock){
        this.readLock = lock;
    }

    @VisibleForTesting
    void setWriteLock(final Lock lock){
        this.writeLock = lock;
    }
}
