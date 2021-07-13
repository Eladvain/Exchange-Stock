package components.commerce.Transaction;

import java.io.Serializable;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionData implements Serializable {

    public enum buyOrSel  {
        buy,
        sell,
        executed
    }

    ;

    public TransactionData(String Symbol, buyOrSel type, int amount, Integer limit) {
        timeStamp  = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
        limitPrice = limit;
        amountOfStocks = amount;
        symbolOfStock = Symbol;
        typeOfTrans = type;
        totalPriceTrans = 0;
    }

    public TransactionData(String Symbol, buyOrSel type, int amount, Integer limit, TransactionsCommands mktOrLmt,String buyer,String Seller) {
        timeStamp  = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
        limitPrice = limit;
        amountOfStocks = amount;
        symbolOfStock = Symbol;
        typeOfTrans = type;
        totalPriceTrans = 0;
        transactionMktOrLmt = mktOrLmt;
        UserNameBuyer = buyer;
        UserNameSeller=Seller;
    }
    public TransactionData(String Symbol, buyOrSel type, int amount, Integer limit,String userName)
    {
        timeStamp  = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
        limitPrice = limit;
        amountOfStocks = amount;
        symbolOfStock = Symbol;
        typeOfTrans = type;
        totalPriceTrans = 0;
        UserApplyTransaction = userName;
    }
    public TransactionsCommands transactionMktOrLmt;
    private String timeStamp;
    public String UserNameSeller;
    public String UserNameBuyer;
    public String UserApplyTransaction = null;
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp()
    {
        timeStamp  = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
    }

    private buyOrSel typeOfTrans;

    public buyOrSel getTypeOfTrans() {
        return typeOfTrans;
    }

    public void setTypeOfTrans(buyOrSel typeOfTrans) {
        this.typeOfTrans = typeOfTrans;
    }

    private Integer limitPrice;

    public Integer getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(int limitPrice) {
        this.limitPrice = limitPrice;
    }

    private int amountOfStocks;

    public int getAmountOfStocks() {
        return amountOfStocks;
    }

    public void setAmountOfStocks(int amountOfStocks) {
        this.amountOfStocks = amountOfStocks;
    }

    private String symbolOfStock;

    public String getSymbolOfStock() {
        return symbolOfStock;
    }

    public void setSymbolOfStock(String symbolOfStock) {
        this.symbolOfStock = symbolOfStock;
    }

    private int totalPriceTrans;

    public int getTotalPriceTrans() {
        return totalPriceTrans;
    }

    public void setTotalPriceTrans(int totalPriceTrans) {
        this.totalPriceTrans = totalPriceTrans;
    }

    @Override
    public String toString()
    {
        StringBuilder str =new StringBuilder("");
        if(typeOfTrans == buyOrSel.executed)
        {
            str.append("#");
                str.append(String.format("%-12s",totalPriceTrans));
            str.append("|");
        }
        str.append(String.format("%-7s",limitPrice));
        str.append("|");
        str.append(String.format("%-9s",amountOfStocks));
        str.append("|");
        str.append(String.format("%-18s",timeStamp));
        str.append("#");
        return str.toString();
    }
}
