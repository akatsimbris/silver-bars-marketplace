package com.silverbars.orderbook;

public class OrderBookException extends Exception{
    public OrderBookException() {
    }

    public OrderBookException(String message) {
        super(message);
    }

    public OrderBookException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderBookException(Throwable cause) {
        super(cause);
    }

    public OrderBookException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
