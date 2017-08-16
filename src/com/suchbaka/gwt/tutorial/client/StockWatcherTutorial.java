package com.suchbaka.gwt.tutorial.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.suchbaka.gwt.tutorial.shared.TableNames;
import com.google.gwt.user.client.Timer;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcherTutorial implements EntryPoint {
	
	private static final int REFRESH_INTERVAL = 5000;
	private static final String JSON_URL = GWT.getModuleBaseURL() + "stockPrices?q=";

	private VerticalPanel mainPanel;
	private FlexTable stocksFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newSymbolTextBox;
	private Button addStockButton;
	private Button removeStockButton;
	private Label lastUpdatedLabel;

	
	private ArrayList<String> stocks;
	private Label errorMsgLabel = new Label();
	
	private Timer refreshTimer;
	
	public void onModuleLoad() {
		mainPanel = new VerticalPanel();
		stocksFlexTable = new FlexTable();
		addPanel = new HorizontalPanel();
		newSymbolTextBox = new TextBox();
		addStockButton = new Button();
		lastUpdatedLabel = new Label();
		
		stocks = new ArrayList<>();
		
		refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshWatchList();
			}
		};
		
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		
		// Есть ли разница между вызовом метода setText(String text); и перадчей имени кнопки в констурктор new Button(String text); ?
		// И что лучше?
		addStockButton.setText("Add");
		
		stocksFlexTable.setText(0, 0, TableNames.SYMBOL.toString());
		stocksFlexTable.setText(0, 1, TableNames.PRICE.toString());
		stocksFlexTable.setText(0, 2, TableNames.CHANGE.toString());
		stocksFlexTable.setText(0, 3, TableNames.REMOVE.toString());
		
		stocksFlexTable.setCellPadding(6);
		
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
		
		newSymbolTextBox.addStyleDependentName("area");
		
		errorMsgLabel.setStyleName("errorMessage");
		errorMsgLabel.setVisible(false);
		
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		
		mainPanel.add(errorMsgLabel);
		mainPanel.add(stocksFlexTable);
		
		addPanel.addStyleName("addPanel");
		
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
		stocksFlexTable.setWidget(row, 2, new Label());
		stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
		
		removeStockButton = new Button();
		removeStockButton.setText("x");
		removeStockButton.addStyleDependentName("remove");
		
		removeStockButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeStock(symbol);
			}
		});
		
		stocksFlexTable.setWidget(row, 3, removeStockButton);
		
		refreshWatchList();
	}
	

	private void removeStock(String symbol) {
		int removedIndex = stocks.indexOf(symbol);
		stocks.remove(removedIndex);
		
		stocksFlexTable.removeRow(removedIndex + 1);
		
		//refreshWatchList();
	}
	
	private void refreshWatchList() {
		if(stocks.size() == 0) {
			return;
		}
	
		String url = JSON_URL;
		
		Iterator<String> iter = stocks.iterator();
		
		while(iter.hasNext()) {
			url+= iter.next();
			
			if(iter.hasNext()) {
				url += "+";
			}
		}
		
		url = URL.encode(url);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		
		try {
			Request requset = builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(200 == response.getStatusCode()) {
						updateTable(JsonUtils.<JsArray<StockData>>safeEval(response.getText()));
					}
					else {
						displayError("Couldn't retrieve JSON (" + response.getStatusText() + ")");
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					displayError("Couldn't retrieve JSON");
				}
			});
		} catch(RequestException e) {
			displayError("Couldn't retrieve JSON");
		}
	}

	protected void displayError(String string) {
		errorMsgLabel.setText("Error: " + string);
		errorMsgLabel.setVisible(true);
	}

	private void updateTable(JsArray<StockData> jsArray) {
		for(int i = 0; i < jsArray.length(); i++) {
			updateTable(jsArray.get(i));
		}
		
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(
				DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
		lastUpdatedLabel.setText("Last update : " 
				+ dateFormat.format(new Date()));
		errorMsgLabel.setVisible(false);
	}

	private void updateTable(StockData stockPrice) {
		if(!stocks.contains(stockPrice.getSymbol())) {
			return;
		}
		
		int row = stocks.indexOf(stockPrice.getSymbol()) + 1;
		
		String priceText = NumberFormat.getFormat("#,##0.00")
				.format(stockPrice.getPrice());
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		
		String changeText = changeFormat.format(stockPrice.getChange());
		String changeTextPrecent = changeFormat.format(stockPrice.getChange());
		
		stocksFlexTable.setText(row, 1, priceText);
		Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
		changeWidget.setText(changeText + "(" + changeTextPrecent  + "%)");
		
		String changeStyleName = "noChange";
		if(stockPrice.getChangePercent() < -0.1f) {
			changeStyleName = "negativeChange";
		}
		
		else if(stockPrice.getChangePercent() > 0.1f) {
			changeStyleName = "positiveChange";
		}
		
		changeWidget.setStyleName(changeStyleName);
	}
}
