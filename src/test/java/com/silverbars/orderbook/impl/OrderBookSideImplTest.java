package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;
import com.silverbars.orderbook.OrderBookException;
import com.silverbars.orderbook.Side;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookSideImplTest {
    private final static double quantity1 = 2.5;
    private final static double price1 = 2.99;
    private final static double quantity2 = 4.2;
    private final static double price2 = 2.95;
    private final static double quantity3 = 1.2;
    private final static double price3 = 2.99;

    private final static Order order1 = new Order("1", "userId1", quantity1, price1, Side.SELL);
    private final static Order order2 = new Order("2", "userId2", quantity2, price2, Side.SELL);
    private final static Order order3 = new Order("3", "userId2", quantity3, price3, Side.SELL);

    @Mock
    private Lock readLock;
    @Mock
    private Lock writeLock;

    private Map<BigDecimal, OrderLevelImpl> orderLevels;

    private Map<String, Order> orders;

    private OrderBookSideImpl orderBookSide;

    @Before
    public void setUp() throws Exception {
        orderLevels = new HashMap<>();
        orders = new HashMap<>();
        orderBookSide = new OrderBookSideImpl(Side.SELL, orderLevels, orders);
        orderBookSide.setWriteLock(writeLock);
        orderBookSide.setReadLock(readLock);

        orderBookSide.addOrder(order1);
        orderBookSide.addOrder(order2);
        orderBookSide.addOrder(order3);
    }

    @Test
    public void testConstructor(){
        final OrderBookSide sellSideBook = new OrderBookSideImpl(Side.SELL);
        assertTrue(sellSideBook.getSide() == Side.SELL);

        final OrderBookSide buySideBook = new OrderBookSideImpl(Side.BUY);
        assertTrue(buySideBook.getSide() == Side.BUY);
    }

    @Test
    public void getSide() {
        assertTrue(orderBookSide.getSide() == Side.SELL);
    }

    @Test
    public void addNewOrder() throws Exception{
        reset(writeLock);
        final InOrder inOrder = inOrder(writeLock);

        final Order order = new Order("4", "user1", 5.5, 3.27, Side.BUY);
        orderBookSide.addOrder(order);
        // check order has been added to orders map
        assertTrue( orders.containsKey("4"));
        assertEquals(order, orders.get("4"));

        // Check there's an OrderLevel with price at 3.27
        final OrderLevel orderLevel = orderLevels.get(OrderLevel.getPriceAsBigDecimal(3.27));
        assertEquals(OrderLevel.getQuantityAsBigDecimal(5.5), orderLevel.getQuantity());
        assertEquals(OrderLevel.getPriceAsBigDecimal(3.27), orderLevel.getPrice());

        inOrder.verify(writeLock, times(1)).lock();
        inOrder.verify(writeLock, times(1)).unlock();
    }

    @Test
    public void addingMultipleOrders() {
        // verify all orders have been added to map as expected
        assertEquals( 3, orders.size());
        assertEquals( order1, orders.get("1"));
        assertEquals( order2, orders.get("2"));
        assertEquals( order3, orders.get("3"));

        // Check OrderLevel
        assertEquals( 2, orderLevels.size());
        OrderLevel orderLevel = orderLevels.get(OrderLevel.getPriceAsBigDecimal(2.99));
        assertEquals(OrderLevel.getQuantityAsBigDecimal(quantity1+quantity3), orderLevel.getQuantity());
        assertEquals(OrderLevel.getPriceAsBigDecimal(2.99), orderLevel.getPrice());
        orderLevel = orderLevels.get(OrderLevel.getPriceAsBigDecimal(2.95));
        assertEquals(OrderLevel.getQuantityAsBigDecimal(quantity2), orderLevel.getQuantity());
        assertEquals(OrderLevel.getPriceAsBigDecimal(2.95), orderLevel.getPrice());



    }

    @Test(expected = OrderBookException.class)
    public void addPreviouslySubmittedOrder() throws Exception {
        reset(writeLock);
        final InOrder inOrder = inOrder(writeLock);
        try {
            orderBookSide.addOrder(order1);
        }
        finally {
            inOrder.verify(writeLock).lock();
            inOrder.verify(writeLock).unlock();
        }

    }

    @Test
    public void cancelOrder() throws Exception {
        reset(writeLock);
        final InOrder inOrder = inOrder(writeLock);

        orderBookSide.cancelOrder(order3);

        // verify all orders have been added to map as expected
        assertEquals( 2, orders.size());
        assertEquals( order1, orders.get("1"));
        assertEquals( order2, orders.get("2"));

        // Check OrderLevel
        assertEquals( 2, orderLevels.size());
        OrderLevel orderLevel = orderLevels.get(OrderLevel.getPriceAsBigDecimal(2.99));
        assertEquals(OrderLevel.getQuantityAsBigDecimal(quantity1), orderLevel.getQuantity());
        assertEquals(OrderLevel.getPriceAsBigDecimal(2.99), orderLevel.getPrice());
        orderLevel = orderLevels.get(OrderLevel.getPriceAsBigDecimal(2.95));
        assertEquals(OrderLevel.getQuantityAsBigDecimal(quantity2), orderLevel.getQuantity());
        assertEquals(OrderLevel.getPriceAsBigDecimal(2.95), orderLevel.getPrice());


        inOrder.verify(writeLock).lock();
        inOrder.verify(writeLock).unlock();
    }

    @Test(expected = OrderBookException.class)
    public void cancelUnknownOrder() throws Exception {
        reset(writeLock);
        final InOrder inOrder = inOrder(writeLock);
        try {
            orderBookSide.cancelOrder( new Order("XXX", "user", 1.2, 3.5, Side.SELL) );
        }
        finally {
            inOrder.verify(writeLock).lock();
            inOrder.verify(writeLock).unlock();
        }
    }


    @Test
    public void cancelAllOrders() throws Exception{
        // verify initial state
        assertEquals(3, orders.size());
        assertEquals(2, orderLevels.size());


        orderBookSide.cancelOrder( order1 );
        orderBookSide.cancelOrder( order2 );
        orderBookSide.cancelOrder( order3 );

        //check Orders is empty
        assertEquals(0, orders.size());

        // check OrderBookLevel is empty
        assertEquals(0, orderLevels.size());
    }

    @Test
    public void getSummary() {
        final InOrder inOrder = inOrder(writeLock);

        final List<String> summary = orderBookSide.getSummary();
        assertEquals(2, summary.size());
        assertEquals("4.2 kg for £2.95", summary.get(0));
        assertEquals("3.7 kg for £2.99", summary.get(1));

        inOrder.verify(writeLock).lock();
        inOrder.verify(writeLock).unlock();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifySummary() {
        final List<String> summary = orderBookSide.getSummary();
        summary.add("Cannot do this");
    }


    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifyEmptySummary() {
        final OrderBookSide orderBookSide = new OrderBookSideImpl(Side.BUY);
        orderBookSide.getSummary().add("Cannot do this");
    }


    @Test
    public void summaryChangesAfterAddingAndRemovingOrders() throws Exception{
        // check initial state
        List<String> summary = orderBookSide.getSummary();
        assertEquals(2, summary.size());
        assertEquals("4.2 kg for £2.95", summary.get(0));
        assertEquals("3.7 kg for £2.99", summary.get(1));

        final Order order10 = new Order("10", "user1", 4.5, 2.8, Side.SELL);
        orderBookSide.addOrder( order10 );
        summary = orderBookSide.getSummary();
        assertEquals(3, summary.size());
        assertEquals("4.5 kg for £2.80", summary.get(0));
        assertEquals("4.2 kg for £2.95", summary.get(1));
        assertEquals("3.7 kg for £2.99", summary.get(2));

        final Order order11 = new Order("11", "user2", 7.1, 3.1, Side.SELL);
        orderBookSide.addOrder( order11 );
        summary = orderBookSide.getSummary();
        assertEquals(4, summary.size());
        assertEquals("4.5 kg for £2.80", summary.get(0));
        assertEquals("4.2 kg for £2.95", summary.get(1));
        assertEquals("3.7 kg for £2.99", summary.get(2));
        assertEquals("7.1 kg for £3.10", summary.get(3));

        orderBookSide.cancelOrder( order10 );
        summary = orderBookSide.getSummary();
        assertEquals(3, summary.size());
        assertEquals("4.2 kg for £2.95", summary.get(0));
        assertEquals("3.7 kg for £2.99", summary.get(1));
        assertEquals("7.1 kg for £3.10", summary.get(2));

    }

    @Test
    public void priceDescendsForBuyOrderSummary() throws Exception{
        final OrderBookSide orderBookSide = new OrderBookSideImpl(Side.BUY);
        orderBookSide.addOrder( new Order("10", "user1", 4.5, 2.8, Side.BUY));
        orderBookSide.addOrder( new Order("11", "user2", 7.1, 3.1, Side.BUY));
        orderBookSide.addOrder( new Order("12", "user2", 5.1, 2.99, Side.BUY));

        final List<String> summary = orderBookSide.getSummary();
        assertEquals(3, summary.size());
        assertEquals("7.1 kg for £3.10", summary.get(0));
        assertEquals("5.1 kg for £2.99", summary.get(1));
        assertEquals("4.5 kg for £2.80", summary.get(2));
    }

    @Test
    public void emptyOrderBookSummary() {
        final OrderBookSide orderBookSide = new OrderBookSideImpl(Side.BUY);
        // tets we return empty list rather than null
        assertEquals(0, orderBookSide.getSummary().size());
    }

}