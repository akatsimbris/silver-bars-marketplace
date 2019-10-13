package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * This class is not synchronized. If multiple threads access it, it should be synchronized externally.
 */
public class OrderLevelImpl implements OrderLevel {
    private final Logger log = Logger.getLogger(OrderLevelImpl.class);

    private final BigDecimal price;
    private BigDecimal quantity;

    public OrderLevelImpl(final double price) {
        this.price = OrderLevel.getPriceAsBigDecimal(price);
        this.quantity = OrderLevel.getQuantityAsBigDecimal(0);
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    @Override
    public void addOrder(final Order order){
        validateOrder(order);
        quantity = quantity.add( OrderLevel.getQuantityAsBigDecimal(order.getQuantity()) );
    }

    @Override
    public void removeOrder(final Order order){
        validateOrder(order);

        final BigDecimal orderQuantity = OrderLevel.getQuantityAsBigDecimal(order.getQuantity() );
        // the order quantity should not be greater than the OrderLevelImpl's quantity, otherwise will end up with negative quantity
        if( orderQuantity.compareTo(quantity) > 0 ){
            throw new IllegalArgumentException("Invalid Order supplied to OrderLevelImpl::removeOrder - quantity is too high: " + order);
        }
        quantity = quantity.subtract( orderQuantity );
    }

    private void validateOrder(final Order order){
        Objects.requireNonNull(order, "Cannot supply a null Order");
        if( !checkPrice(order.getPrice())){
            log.error("Invalid order supplied to OrderLevelImpl: " + price.doubleValue() + " - " + order);
            throw new IllegalArgumentException("Invalid Order supplied to OrderLevelImpl: " + price.doubleValue() + " - " + order);
        }
    }

    private boolean checkPrice(double otherPrice){
        return price.compareTo( OrderLevel.getPriceAsBigDecimal(otherPrice)) == 0 ? true : false;
    }

    @Override
    public String toString() {
        return "OrderLevelImpl{" +
                "price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
