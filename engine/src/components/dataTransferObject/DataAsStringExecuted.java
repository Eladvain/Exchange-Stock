package components.dataTransferObject;

import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;

public class DataAsStringExecuted {

        private String Amount;
        private String buyerUser;
        private String sellerUser;
        private String LMTOTMKT;
        private String Price;
        private String Name;

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getLMTOTMKT() {
        return LMTOTMKT;
    }

    public void setLMTOTMKT(String LMTOTMKT) {
        this.LMTOTMKT = LMTOTMKT;
    }

    public String getBuyerUser() {
        return buyerUser;
    }

    public void setBuyerUser(String buyerUser) {
        this.buyerUser = buyerUser;
    }

    public String getSellerUser() {
        return sellerUser;
    }

    public void setSellerUser(String sellerUser) {
        this.sellerUser = sellerUser;
    }

    public DataAsStringExecuted(TransactionData data, String seller, String buyer) {
            this.Amount = String.valueOf(data.getAmountOfStocks());
            this.LMTOTMKT = data.transactionMktOrLmt == TransactionsCommands.LMT ? "LMT" : "MKT";
            this.Name = data.getTimeStamp();
            this.Price = String.valueOf(data.getLimitPrice());
            this.buyerUser = buyer;
            this.sellerUser = seller;
        }
    }

