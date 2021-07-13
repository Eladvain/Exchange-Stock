package UserManager;

import GenratedCode.RseItem;

import java.io.Serializable;

public class Item implements Serializable {
    private int quantity=0;
    private String Symbol;
    private int AmountOfSellingStocks=0;

    public int getAmountOfSellingStocks() {
        return AmountOfSellingStocks;
    }

    public void setAmountOfSellingStocks(int amountOfSellingStocks) {
        AmountOfSellingStocks = amountOfSellingStocks;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public Item(RseItem rseItem)
    {
        quantity = rseItem.getQuantity();
        Symbol = rseItem.getSymbol();
    }
    public Item(String symbol, int AmountOfStocks)
    {
        this.quantity=AmountOfStocks;
        this.Symbol = symbol;
    }
}
