package com.suchbaka.gwt.tutorial.client;

import java.io.Serializable;

public class DelistedException extends Exception implements Serializable {
	private static final long serialVersionUID = -6369232142571629907L;
	
	private String symbol;

	public DelistedException() {
	}

	public DelistedException(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
}
