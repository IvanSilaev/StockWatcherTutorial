package com.suchbaka.gwt.tutorial.client;

import java.io.Serializable;

public class StockPrice implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6524985291755288234L;
	private String symbol;
	private double price;
	private double change;
	
	public StockPrice() {
	}
	
	public StockPrice(String symbol, double price, double change) {
		this.symbol = symbol;
		this.price = price;
		this.change = change;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getPrice() {
		return price;
	}
	
	public double getChangePrecent() {
		return 100.0 * this.change / this.price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}
	
	
}
