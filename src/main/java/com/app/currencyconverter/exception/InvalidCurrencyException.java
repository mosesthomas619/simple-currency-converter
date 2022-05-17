package com.app.currencyconverter.exception;

public class InvalidCurrencyException extends RuntimeException{

    public InvalidCurrencyException(final String message) {
        super(message);
    }
}
