package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;
import com.silverbars.orderbook.OrderBookException;
import com.silverbars.orderbook.Side;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LiveOrderBookTest {
    @Mock
    private OrderBookSide buysSideBook;
    @Mock
    private OrderBookSide sellSideBook;
    @Mock
    private Order buyOrder;
    @Mock
    private Order sellOrder;

    private LiveOrderBook orderBook;

    @Before
    public void setUp() throws Exception {
        when(buysSideBook.getSide()).thenReturn(Side.BUY);
        when(sellSideBook.getSide()).thenReturn(Side.SELL);

        when(buyOrder.getSide()).thenReturn(Side.BUY);
        when(sellOrder.getSide()).thenReturn(Side.SELL);

        orderBook = new LiveOrderBook(sellSideBook, buysSideBook);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresSellSideBook() throws Exception{
        new LiveOrderBook(null, buysSideBook);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresBuySideBook() throws Exception{
        new LiveOrderBook(sellSideBook, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorInvalidSellSideBook() throws Exception{
        new LiveOrderBook(buysSideBook, buysSideBook);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorInvalidBuySideBook() throws Exception{
        new LiveOrderBook(sellSideBook, sellSideBook);
    }


    @Test
    public void addBuyOrder() throws Exception {
        orderBook.addOrder(buyOrder);

        verify(buysSideBook).addOrder(buyOrder);
    }

    @Test
    public void addSellOrder() throws Exception {
        orderBook.addOrder(sellOrder);

        verify(sellSideBook).addOrder(sellOrder);
    }

    @Test
    public void cancelBuyOrder() throws Exception{
        orderBook.cancelOrder(buyOrder);
        verify(buysSideBook).cancelOrder(buyOrder);
    }

    @Test
    public void cancelSellOrder() throws Exception {
        orderBook.cancelOrder(sellOrder);
        verify(sellSideBook).cancelOrder(sellOrder);
    }


    @Test
    public void getBuySummary() {
        orderBook.getSummary(Side.BUY);
        verify(buysSideBook).getSummary();
    }

    @Test
    public void getSellSummary() {
        orderBook.getSummary(Side.SELL);
        verify(sellSideBook).getSummary();
    }
}