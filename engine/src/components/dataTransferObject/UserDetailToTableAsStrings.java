package components.dataTransferObject;

import UserManager.Item;
import UserManager.User;
import components.commerce.RitzpaStockManager;
import components.commerce.Stock;

import java.util.ArrayList;
import java.util.List;

public class UserDetailToTableAsStrings {
    private String Symbol ;
    private String Amount ;
    private String StockRate;

    public UserDetailToTableAsStrings(String symbol, int amount , int stockRate)
    {
        Symbol=symbol;
        Amount =String.valueOf(amount);
        StockRate = String.valueOf(stockRate);
    }

    public String getStockRate() {
        return StockRate;
    }

    public void setStockRate(String stockRate) {
        this.StockRate = stockRate;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }
    public static List<UserDetailToTableAsStrings> getUserDetailsFromUser(User user , RitzpaStockManager manager)
    {
        List<UserDetailToTableAsStrings> list= new ArrayList<>();
        for(Item item :user.getHoldingStocks().getItemList())
        {
            Stock stock = manager.getStockByKey(item.getSymbol());
            int rate = stock.getPrice();
            list.add(new UserDetailToTableAsStrings(item.getSymbol(),item.getQuantity(),rate));
        }
        return list;
    }
}
