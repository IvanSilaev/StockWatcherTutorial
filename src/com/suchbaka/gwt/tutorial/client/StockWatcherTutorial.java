package com.suchbaka.gwt.tutorial.client;

import com.suchbaka.gwt.tutorial.shared.FieldVerifier;
import com.suchbaka.gwt.tutorial.shared.TableNames;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcherTutorial implements EntryPoint {

	private VerticalPanel mainPanel;
	private FlexTable stocksFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newSymbolTextBox;
	private Button addStockButton;
	private Label lastUpdatedLabel;
	
	public void onModuleLoad() {
		mainPanel = new VerticalPanel();
		stocksFlexTable = new FlexTable();
		addPanel = new HorizontalPanel();
		newSymbolTextBox = new TextBox();
		addStockButton = new Button();
		lastUpdatedLabel = new Label();
		
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
		
	}
}
