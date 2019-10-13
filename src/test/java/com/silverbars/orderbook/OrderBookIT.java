package com.silverbars.orderbook;

import com.silverbars.orderbook.impl.LiveOrderBook;
import com.silverbars.orderbook.impl.OrderBookSideImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderBookIT {
    private final static double quantity1 = 2.5;
    private final static double price1 = 2.99;
    private final static double quantity2 = 4.2;
    private final static double price2 = 2.95;
    private final static double quantity3 = 1.2;
    private final static double price3 = 2.99;
    private final static double quantity4 = 3.1;
    private final static double price4 = 2.59;
    private final static double quantity5 = 3.2;
    private final static double price5 = 2.95;


    private final static Order order1 = new Order("1", "userId1", quantity1, price1, Side.SELL);
    private final static Order order2 = new Order("2", "userId2", quantity2, price2, Side.SELL);
    private final static Order order3 = new Order("3", "userId3", quantity3, price3, Side.SELL);
    private final static Order order4 = new Order("4", "userId2", quantity4, price4, Side.BUY);
    private final static Order order5 = new Order("5", "userId4", quantity5, price5, Side.BUY);


    @Test
    public void liveOrderBook() throws Exception{
        final OrderBook orderBook = new LiveOrderBook( new OrderBookSideImpl(Side.SELL), new OrderBookSideImpl(Side.BUY));
        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);
        orderBook.addOrder(order4);
        orderBook.addOrder(order5);

        List<String> sellOrderSummary = orderBook.getSummary(Side.SELL);
        List<String> buyOrderSummary = orderBook.getSummary(Side.BUY);

        assertEquals(2, sellOrderSummary.size());
        assertEquals("4.2 kg for £2.95", sellOrderSummary.get(0));
        assertEquals("3.7 kg for £2.99", sellOrderSummary.get(1));

        assertEquals(2, buyOrderSummary.size());
        assertEquals("3.2 kg for £2.95", buyOrderSummary.get(0));
        assertEquals("3.1 kg for £2.59", buyOrderSummary.get(1));

        orderBook.cancelOrder(order2);
        orderBook.cancelOrder(order5);

        sellOrderSummary = orderBook.getSummary(Side.SELL);
        buyOrderSummary = orderBook.getSummary(Side.BUY);

        assertEquals(1, sellOrderSummary.size());
        assertEquals("3.7 kg for £2.99", sellOrderSummary.get(0));

        assertEquals(1, buyOrderSummary.size());
        assertEquals("3.1 kg for £2.59", buyOrderSummary.get(0));

    }

}
