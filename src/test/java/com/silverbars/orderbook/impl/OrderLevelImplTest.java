package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;
import com.silverbars.orderbook.Side;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderLevelImplTest {
    private static final double PRICE_THRESHOLD = 0.005;
    private static final double QUANTITY_THRESHOLD = 0.05;

    private final static double QUANTITY = 3.2;
    private final static double PRICE = 2.5;

    final Order order0 = new Order("0", "user1", QUANTITY, PRICE, Side.SELL);


    private OrderLevelImpl orderLevel;

    @Before
    public void setUp() throws Exception {
        orderLevel = new OrderLevelImpl(PRICE);
        orderLevel.addOrder(order0);
    }

    @Test
    public void getQuantity() {
        assertEquals(OrderLevel.getQuantityAsBigDecimal(QUANTITY), orderLevel.getQuantity());
    }

    @Test
    public void getPrice() {
        assertEquals(OrderLevel.getPriceAsBigDecimal(PRICE), orderLevel.getPrice());
    }

    @Test( expected = NullPointerException.class)
    public void addOrderWithNullOrder() {
        orderLevel.addOrder(null);
    }

    @Test( expected = IllegalArgumentException.class)
    public void addOrderWithInvalidPrice(){
        orderLevel.addOrder( new Order( "1", "user1", 1, 2.505, Side.SELL));
    }

    @Test
    public void addOrder() {
        final Order order1 = new Order("1", "user1", 2.1, PRICE, Side.SELL);
        orderLevel.addOrder(order1);
        assertEquals(5.3, orderLevel.getQuantity().doubleValue(),  QUANTITY_THRESHOLD);
    }


    @Test
    public void removeOrder() {
        final Order order1 = new Order("1", "user1", 2.1, PRICE, Side.SELL);
        orderLevel.removeOrder(order1);
        assertEquals(1.1, orderLevel.getQuantity().doubleValue(),  QUANTITY_THRESHOLD);
    }

    @Test
    public void removeOrderWithEqualQuantity(){
        final Order order1 = new Order("1", "user1", QUANTITY, PRICE, Side.SELL);
        orderLevel.removeOrder(order1);
        assertEquals(0, orderLevel.getQuantity().doubleValue(), QUANTITY_THRESHOLD);
    }

    @Test(expected = NullPointerException.class)
    public void removeOrderWithNullOrder(){
        orderLevel.removeOrder(null);
    }

    @Test(expected = IllegalArgumentException.class )
    public void removeOrderWithInvalidPrice(){
        orderLevel.removeOrder( new Order("1", "user1", 2, 2.49, Side.SELL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeOrderWIthTooHighQuantity(){
        orderLevel.removeOrder( new Order("1", "user1", 3.3, PRICE, Side.SELL));
    }
}