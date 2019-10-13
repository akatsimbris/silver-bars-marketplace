package com.silverbars.orderbook.impl;

import com.silverbars.orderbook.Order;
import com.silverbars.orderbook.OrderBook;
import com.silverbars.orderbook.OrderBookException;
import com.silverbars.orderbook.Side;

import java.util.List;
import java.util.Objects;

public class LiveOrderBook implements OrderBook {

    private final OrderBookSide sellSideBook;
    private final OrderBookSide buySideBook;

    public LiveOrderBook(final OrderBookSide sellSideBook, final OrderBookSide buySideBook) {
        Objects.requireNonNull(sellSideBook, "Missing sellSideBook");
        Objects.requireNonNull(buySideBook, "Missing buySideBook");
        if( !(sellSideBook.getSide() == Side.SELL)){
            throw new IllegalArgumentException("Incorrect argument passed to sellSideBook");
        }
        if( !(buySideBook.getSide() == Side.BUY)){
            throw new IllegalArgumentException("Incorrect argument passed to buySideBook");
        }

        this.sellSideBook = sellSideBook;
        this.buySideBook = buySideBook;
    }

    @Override
    public void addOrder(final Order order) throws OrderBookException {
        Objects.requireNonNull(order);

        getOrderBookSide(order.getSide()).addOrder(order);
    }

    @Override
    public void cancelOrder(final Order order) throws OrderBookException {
        Objects.requireNonNull(order);

        getOrderBookSide(order.getSide()).cancelOrder(order);
    }

    @Override
    public List<String> getSummary(final Side side) {
        return getOrderBookSide(side).getSummary();
    }

    private OrderBookSide getOrderBookSide(final Side side){
        return side == Side.SELL ? sellSideBook : buySideBook;
    }
}
