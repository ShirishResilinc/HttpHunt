package com.httphunt.exceptions;

public class FailureStepException extends Throwable {
	private static final long serialVersionUID = 1L;
	String message;

	public FailureStepException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

	@SuppressWarnings("unchecked")
	public static <T extends Exception, R> R throwException(Exception t) throws T {
	    throw (T) t;
	}
}
