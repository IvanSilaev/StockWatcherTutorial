package com.suchbaka.gwt.tutorial.shared;

public enum TableNames {
	SYMBOL("Symbol"),
	PRICE("Price"),
	CHANGE("Change"),
	REMOVE("Remove");
	
	private final String text;
	
	private TableNames(final String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
