package com.app.currencyconverter.controller;



import com.app.currencyconverter.exception.InvalidCurrencyException;
import com.app.currencyconverter.exception.TimeOutException;
import com.app.currencyconverter.domain.ErrorResponse;
import com.app.currencyconverter.exception.InvalidAmountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String COLON = "  :  ";


	@ExceptionHandler(InvalidAmountException.class)
	protected ResponseEntity<ErrorResponse> invalidAmountException(InvalidAmountException ex) {
		String errorRef = logError(ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage() + COLON + errorRef), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidCurrencyException.class)
	protected ResponseEntity<ErrorResponse> invalidCurrencyException(InvalidCurrencyException ex) {
		String errorRef = logError(ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage() + COLON + errorRef), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TimeOutException.class)
	protected ResponseEntity<ErrorResponse> timeOutException(TimeOutException ex) {
		String errorRef = logError(ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage() + COLON + errorRef), HttpStatus.GATEWAY_TIMEOUT);
	}

	private String logError(Exception ex) {
		String errorRef = "Error Reference: " + UUID.randomUUID();
		logger.error(errorRef, ex);
		return errorRef;
	}

}