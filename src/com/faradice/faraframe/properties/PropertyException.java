package com.faradice.faraframe.properties;

public class PropertyException extends Exception {
	public PropertyException() {
	}

	public PropertyException(String msg) {
		super(msg);
	}

	public PropertyException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
