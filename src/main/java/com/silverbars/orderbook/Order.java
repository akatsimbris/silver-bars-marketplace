package com.silverbars.orderbook;

import java.util.Objects;

/**
 * Order class. Assume orders can only be created and cancelled but not amended.
 * To amend you would need to cancel and create a new one.
 */
public class Order {
    // Unique order identifier
    private final String orderId;
    // unique user identifier
    private final String userId;
    private final double quantity;
    private final double price;
    private final Side side;


    /**
     * @param orderId unique Order identifier
     * @param userId unique user identifier
     * @param quantity order quantity in kilograms
     * @param price price per kilogram in pounds
     * @param orderType buy or sell order
     */
    public Order(String orderId, String userId, double quantity, double price, Side orderType) {
        Objects.requireNonNull(orderId, "Missing orderId");
        Objects.requireNonNull(userId, "Missing userId");
        Objects.requireNonNull(orderType, "Missing order type");
        if( quantity <= 0){
            throw new IllegalArgumentException("Invalid quantity. Must be greater than 0");
        }
        if( price <= 0 ){
            throw new IllegalArgumentException("Invalid price. Must be greater than 0");
        }
        this.orderId = orderId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.side = orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side=" + side +
                '}';
    }
}
