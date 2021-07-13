package components.commerce;

import GenratedCode.RseStock;
import GenratedCode.RseStocks;

import java.io.Serializable;
import java.util.*;

public class Stocks implements Serializable {
    private Map<String,Stock> stocks;

    public Map<String,Stock> getStocks() {
        return stocks;
    }

    public void setStocks(Map<String,Stock> stocks) {
        this.stocks = stocks;
    }

    public Stocks(Map<String,Stock> stocks) {
        this.stocks = stocks;
    }

    public static Stocks createStocks(RseStocks rseStocks) {
        Map<String,Stock> stockMap = new HashMap<>();
        for (RseStock stock : rseStocks.getRseStock()) {
            Stock stockFromGenCode = Stock.createStockFromGenCode(stock);
            stockMap.put(stockFromGenCode.getSymbol().toUpperCase(),stockFromGenCode);
        }
        return new Stocks(stockMap);
    }

    @Override
    public String toString() {
       String  stocksList="";
        for(Map.Entry<String, Stock> entry :stocks.entrySet())
       {
           stocksList+=entry.getValue().toString();
       }
       return stocksList;
    }
    public boolean addNewStock(String CompanyName, String Symbol, int price)
    {
        for(Map.Entry<String,Stock> entry : stocks.entrySet())
        {
            if(entry.getKey().equals(Symbol) || entry.getValue().getCompanyName().equals(Symbol))
                return  false;
        }

        stocks.put(Symbol,new Stock(Symbol,CompanyName,price));
        return  true;
    }
}
