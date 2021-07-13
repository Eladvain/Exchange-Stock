package components.dataTransferObject;

import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;

import java.sql.Timestamp;

public class DataAsStrings {
    public String Amount;
    public String buyerUser;
    public String sellerUser;
    public String LMTOTMKT;
    public String Price;
    public String Name;
    public String Initialize;
    public DataAsStrings(TransactionData data ,String seller ,String buyer)
    {
        this.Amount =String.valueOf(data.getAmountOfStocks());
        this.LMTOTMKT = data.transactionMktOrLmt== TransactionsCommands.LMT?"LMT":"MKT";
        this.Name = data.getTimeStamp();
        this.Price= String.valueOf(data.getLimitPrice());
        this.buyerUser = buyer;
        this.sellerUser = seller;
    }
    public DataAsStrings(TransactionData data ,String userName)
    {
        Amount =String.valueOf(data.getAmountOfStocks());
        Initialize = userName;
        LMTOTMKT = data.transactionMktOrLmt== TransactionsCommands.LMT?"LMT":"MKT";
        Name = data.getTimeStamp();
        Price = String.valueOf(data.getLimitPrice());
    }
    public String getAmount() {
        return Amount;
    }

    public String getInitialize() {
        return Initialize;
    }

    public String getLMTOTMKT() {
        return LMTOTMKT;
    }

    public String getName() {
        return Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setInitialize(String initialize) {
        Initialize = initialize;
    }

    public void setLMTOTMKT(String LMTOTMKT) {
        this.LMTOTMKT = LMTOTMKT;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
