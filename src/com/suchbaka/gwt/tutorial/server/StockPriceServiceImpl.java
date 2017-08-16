package com.suchbaka.gwt.tutorial.server;

import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.suchbaka.gwt.tutorial.client.DelistedException;
import com.suchbaka.gwt.tutorial.client.StockPrice;
import com.suchbaka.gwt.tutorial.client.StockPriceService;

public class StockPriceServiceImpl extends RemoteServiceServlet implements StockPriceService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5446279580117618471L;
	private static final double MAX_PRICE = 100.0;
	private static final double MAX_PRICE_CHANGE = 0.02;
	
	@Override
	public StockPrice[] getPrices(String[] symbols) throws DelistedException {
		Random rnd = new Random();
		
		StockPrice[] prices = new StockPrice[symbols.length];
		for(int i = 0; i < symbols.length; i++) {
			
			if(symbols[i].equals("ERR")) {
				throw new DelistedException();
			}
			
			double price = rnd.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE * (rnd.nextDouble() * 2f - 1f);
			
			prices[i] = new StockPrice(symbols[i], price, change);
		}
		
		return prices;
	}
	
}
