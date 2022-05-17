package com.app.currencyconverter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Used to make response for error message.
 */

@JsonRootName(value = "message")
public class ErrorResponse {

	/**
	 * The message.
	 */
	@JsonProperty
	private String message;

	public ErrorResponse() {
	}

	/**
	 * Constructor to create a message to send in a controller response.
	 *
	 * @param message
	 * 		string representation of message to pass
	 */
	public ErrorResponse(final String message) {
		setMessage(message);
	}

	//Getters and Setters

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ErrorResponse{"
				+ "message='" + message + '\''
				+ '}';
	}

}
