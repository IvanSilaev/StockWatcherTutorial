package com.suchbaka.gwt.tutorial.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.suchbaka.gwt.tutorial.shared.TableNames;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcherTutorial implements EntryPoint {

	private VerticalPanel mainPanel;
	private FlexTable stocksFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newSymbolTextBox;
	private Button addStockButton;
	private Button removeStockButton;
	private Label lastUpdatedLabel;
	
	private ArrayList<String> stocks;
	
	public void onModuleLoad() {
		mainPanel = new VerticalPanel();
		stocksFlexTable = new FlexTable();
		addPanel = new HorizontalPanel();
		newSymbolTextBox = new TextBox();
		addStockButton = new Button();
		lastUpdatedLabel = new Label();
		
		stocks = new ArrayList<>();
		
		// Есть ли разница между вызовом метода setText(String text); и перадчей имени кнопки в констурктор new Button(String text); ?
		// И что лучше?
		addStockButton.setText("Add");
		
		stocksFlexTable.setText(0, 0, TableNames.SYMBOL.toString());
		stocksFlexTable.setText(0, 1, TableNames.PRICE.toString());
		stocksFlexTable.setText(0, 2, TableNames.CHANGE.toString());
		stocksFlexTable.setText(0, 3, TableNames.REMOVE.toString());
		
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);
		
		RootPanel.get("stockList").add(mainPanel);
		
		newSymbolTextBox.setFocus(true);
		
		addStockButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addStock();
			}
		});
		
		newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					addStock();
				}
			}
		});
	}
	
	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		
		newSymbolTextBox.setFocus(true);
		
		if(!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
			Window.alert("'" + symbol + "' is not a valid symbol.");
			newSymbolTextBox.selectAll();
			return;
		}
		
		newSymbolTextBox.setText("");
		
		if(stocks.contains(symbol)) {
			Window.alert("'" + symbol + "' already exist.");
			return;
		}
		
		int row = stocksFlexTable.getRowCount();
		
		stocks.add(symbol);
		stocksFlexTable.setText(row, 0, symbol);
		
		removeStockButton = new Button();
		removeStockButton.setText("x");
		
		removeStockButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeStock(symbol);
			}
		});
		
		stocksFlexTable.setWidget(row, 3, removeStockButton);
	}
	
	private void removeStock(String symbol) {
		int removedIndex = stocks.indexOf(symbol);
		stocks.remove(removedIndex);
		
		stocksFlexTable.removeRow(removedIndex + 1);
	}
}
