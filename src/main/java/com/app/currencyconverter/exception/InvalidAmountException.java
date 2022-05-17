package com.app.currencyconverter.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(final String message) {
        super(message);
    }

}
