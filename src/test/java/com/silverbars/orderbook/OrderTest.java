package com.silverbars.orderbook;

import org.junit.Before;
import org.junit.Test;

import javax.print.DocFlavor;

import java.util.Queue;

import static org.junit.Assert.*;

public class OrderTest {
    private final static String ORDER_1 = "order1";
    private final static String USER_1 = "user1";
    private final static double QUANTITY1 = 2.5;
    private final static double PRICE_1 = 3.0;

    private Order order;

    @Before
    public void setUp() throws Exception {
        order = new Order(ORDER_1, USER_1, QUANTITY1, PRICE_1, Side.BUY);
    }

    @Test(expected = NullPointerException.class)
    public void invalidOrderId() {
        new Order(null, USER_1, QUANTITY1, PRICE_1, Side.BUY);
    }

    @Test(expected = NullPointerException.class)
    public void invalidUserId() {
        new Order(ORDER_1, null, QUANTITY1, PRICE_1, Side.BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQuatity() {
        new Order(ORDER_1, USER_1, 0, PRICE_1, Side.BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPrice() {
        new Order(ORDER_1, USER_1, QUANTITY1, 0, Side.BUY);
    }

    @Test(expected = NullPointerException.class)
    public void invalidOrderType() {
        new Order(ORDER_1, USER_1, QUANTITY1, PRICE_1, null);
    }

    @Test
    public void getOrderId() {
        assertEquals(ORDER_1, order.getOrderId());
    }

    @Test
    public void getUserId() {
        assertEquals(USER_1, order.getUserId());
    }

    @Test
    public void getQuantity() {
        assertEquals(QUANTITY1, order.getQuantity(), 0.01);
    }

    @Test
    public void getPrice() {
        assertEquals(PRICE_1, order.getPrice(), 0.01);
    }

    @Test
    public void getSide() {
        assertEquals(Side.BUY, order.getSide());
    }
}