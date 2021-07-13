package components.commerce;

import GenratedCode.RseStock;
import components.commerce.Transaction.TransactionData;
import sun.awt.Symbol;

import java.io.Serializable;
import java.util.*;

public class Stock implements Serializable {
    public Stock(String symbol, String companyName, int price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.amountOfSellingsStocks=0;
        this.totalTransactionsPrice=0;
        holdingBuyingTrans = new ArrayList<>();
        holdingSalesTrans = new ArrayList<>();
        executedTrans = new ArrayList<>();
        stockPriceList=new ArrayList<>();
        xChartLocation= new ArrayList<>();
    }

    private List<TransactionData> holdingSalesTrans;
    public List<Integer> stockPriceList;
    public List<String> xChartLocation;
    public List<TransactionData> getHoldingSalesTrans() {
        return holdingSalesTrans;
    }

    public List<TransactionData> getHoldingBuyingTrans() {
        return holdingBuyingTrans;
    }

    public List<TransactionData> getExecutedTrans() {
        return executedTrans;
    }

    private List<TransactionData> holdingBuyingTrans;
    private List<TransactionData> executedTrans;





    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String newSymbol) {
        symbol = newSymbol;
    }

    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String newCompanyName) {
        this.companyName = newCompanyName;
    }

    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int newPrice) {
        this.price = newPrice;
    }

    //private int amountOfTransaction;
    private int amountOfSellingsStocks;
    public int getAmountOfTransaction() {
        return amountOfSellingsStocks;
    }
    public void addNumberOfStocks(int amount)
    {
        amountOfSellingsStocks+=amount;
    }
    private double totalTransactionsPrice;

    public double getTotalTransactionsPrice() {
        return totalTransactionsPrice;
    }
    public  void addTotalTransactionPrice(int amount)
    {
        totalTransactionsPrice+= amount;
    }


    public static Stock createStockFromGenCode(RseStock rseStock) {
        return new Stock(rseStock.getRseSymbol(), rseStock.getRseCompanyName(), rseStock.getRsePrice());
    }

    @Override
    public String toString() {
        return "Symbol: " + this.symbol + "\ncompany name: " + this.companyName + "\nprice: " + this.price+"\n"
                +"amount of transactions: " + this.amountOfSellingsStocks + "\nsum of total transactin price: "+this.totalTransactionsPrice
                +"\n"+"-----------------------------------------------\n";
    }
    public String getListOfTransactionAsString()
    {
        String rowSplitted="";
        for(int i=0;i<125;i++)
        {
            rowSplitted+="-";
        }
        String str="";
      int maxBetweenAllLists = Math.max(holdingBuyingTrans.size(), holdingSalesTrans.size());
      int max  = Math.max(maxBetweenAllLists,executedTrans.size());
      str+=String.format("%-50s","Executed Transactions");
      str+="#";
        str+=String.format("%-36s","Buying Requests");
        str+="#";
        str+=String.format("%-36s","Selling requests");
        str+="#\n";
        str+=rowSplitted;
        str+="\n";
        str+="#";
        str+=String.format("%-12s","Total price");
        str+="|";
        str+=String.format("%-7s","price");
        str+="|";
        str+=String.format("%-9s","Quantity");
        str+="|";
        str+=String.format("%-18s","Date");
        str+="#";



        str+=String.format("%-7s","price");
        str+="|";
        str+=String.format("%-9s","Quantity");
        str+="|";
        str+=String.format("%-18s","Date");
        str+="#";

        str+=String.format("%-7s","price");
        str+="|";
        str+=String.format("%-9s","Quantity");
        str+="|";
        str+=String.format("%-18s","Date");
        str+="#";
        str+="\n";
    str+=rowSplitted;
    str+="\n";
      for(int i=0;i<max;i++)
      {
          if(executedTrans.size()>i)
          {
              str+=executedTrans.get(i);
          }
          else
          {
              str+="#";
              str+=String.format("%-12s"," ");
                str+="|";
              str+=String.format("%-7s"," ");
              str+="|";
              str+=String.format("%-9s"," ");
              str+="|";
              str+=String.format("%-18s"," ");
              str+="#";
          }
          if(holdingBuyingTrans.size()>i)
          {
              str+=holdingBuyingTrans.get(i);
          }
          else
          {
              str+=String.format("%-7s"," ");
              str+="|";
              str+=String.format("%-9s"," ");
              str+="|";
              str+=String.format("%-18s"," ");
              str+="#";
          }
          if(holdingSalesTrans.size()>i)
          {
              str+=holdingSalesTrans.get(i);
          }
          else
          {
              str+=String.format("%-7s"," ");
              str+="|";
              str+=String.format("%-9s"," ");
              str+="|";
              str+=String.format("%-18s"," ");
              str+="#";
          }
          str+="\n";
          str+=rowSplitted;
          str+="\n";
      }
      return  str;
    }
}

