package com.suchbaka.gwt.tutorial.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonStockData extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2000573958977627772L;
	private static final double MAX_PRICE = 100.0;
	private static final double MAX_PRICE_CHANGE = 0.02;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Random rnd = new Random();

		PrintWriter out = resp.getWriter();
		out.println('[');

		String[] stockSymbols = req.getParameter("q").split(" ");
		boolean firstSymbol = true;

		for (String stockSymbol : stockSymbols) {
			double price = rnd.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE * (rnd.nextDouble() * 2f - 1f);

			if (firstSymbol) {
				firstSymbol = false;
			} else {
				out.println(" ,");
			}
			
			out.println("  {");
		    out.print("    \"symbol\": \"");
		    out.print(stockSymbol);
		    out.println("\",");
		    out.print("    \"price\": ");
		    out.print(price);
		    out.println(',');
		    out.print("    \"change\": ");
		    out.println(change);
		    out.println("  }");

		}
		
		out.println(']');
		out.flush();
	}

}
