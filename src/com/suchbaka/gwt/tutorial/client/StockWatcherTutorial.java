package com.suchbaka.gwt.tutorial.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.suchbaka.gwt.tutorial.shared.TableNames;
import com.google.gwt.user.client.Timer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcherTutorial implements EntryPoint {
	
	private static final int REFRESH_INTERVAL = 5000;
	

	private VerticalPanel mainPanel;
	private FlexTable stocksFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newSymbolTextBox;
	private Button addStockButton;
	private Button removeStockButton;
	private Label lastUpdatedLabel;
	
	private Timer refreshTimer;
	
	private StockPriceServiceAsync stockPriceSvc = GWT.create(StockPriceService.class);
	private Label errorMsgLabel = new Label();
	private ArrayList<String> stocks = new ArrayList<>();
	
	public void onModuleLoad() {
		mainPanel = new VerticalPanel();
		stocksFlexTable = new FlexTable();
		addPanel = new HorizontalPanel();
		newSymbolTextBox = new TextBox();
		addStockButton = new Button();
		lastUpdatedLabel = new Label();
		
		refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshWatchList();
			}
		};
		
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		
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
		
		mainPanel.add(stocksFlexTable);
		mainPanel.add(errorMsgLabel);
		
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
	}
	
	private void refreshWatchList() {
		 if (stockPriceSvc == null) {
		      stockPriceSvc = GWT.create(StockPriceService.class);
		 }
		 
		 AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>(){

			@Override
			public void onFailure(Throwable caught) {
				String details = caught.getMessage();
				
				if(caught instanceof DelistedException) {
					details = "Company '" + ((DelistedException)caught).getSymbol() + " 'was delisted";
				}
				
				errorMsgLabel.setText("Error: " + details);
				errorMsgLabel.setVisible(true);
			}

			@Override
			public void onSuccess(StockPrice[] result) {
				updateTable(result);
			}
			 
		};
		
		stockPriceSvc.getPrices(stocks.toArray(new String[0]), callback);
	}

	private void updateTable(StockPrice[] prices) {
		for(int i = 0; i < prices.length; i++) {
			updateTable(prices[i]);
		}
		
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(
				DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
		lastUpdatedLabel.setText("Last update : " 
				+ dateFormat.format(new Date()));
		
		errorMsgLabel.setVisible(false);
	}

	private void updateTable(StockPrice stockPrice) {
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
		if(stockPrice.getChangePrecent() < -0.1f) {
			changeStyleName = "negativeChange";
		}
		
		else if(stockPrice.getChangePrecent() > 0.1f) {
			changeStyleName = "positiveChange";
		}
		
		changeWidget.setStyleName(changeStyleName);
	}
}
