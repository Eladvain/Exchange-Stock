package UserManager;

import GenratedCode.RseUser;
import components.commerce.RitzpaStockManager;
import components.commerce.Transaction.TransactionData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private Holding holdingStocks;
    private String name;
    private int amountOfMoney =0;
    public String type="";
    public int getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(int amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }
    public void changeMoneyToUser(int amountOfMoneyToChange)
    {
        amountOfMoney+=amountOfMoneyToChange;
    }

    public void addNewItemToUser(Item item)
    {
        holdingStocks.addNewItem(item);
    }
public User(RseUser user)
{
    name = user.getName();
    holdingStocks = new Holding((user.getRseHoldings()));
}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Holding getHoldingStocks() {
        return holdingStocks;
    }

    public void setHoldingStocks(Holding holdingStocks) {
        this.holdingStocks = holdingStocks;
    }
    public int getAmountOfStocksBySymbol(String Symbol)
    {
        int amountOfStocks=0;
        for(Item item :holdingStocks.getItemList())
        {
            if(item.getSymbol().equals(Symbol))
            {
                amountOfStocks = item.getQuantity()- item.getAmountOfSellingStocks();
            }
        }
        return amountOfStocks;
    }
    public static List<User> setUserItems(List<TransactionData> listOfExecutedTrans, RitzpaStockManager manager)
    {
        List<User> userListToUpdate = new ArrayList<>();
        for (TransactionData trans : listOfExecutedTrans)
        {
            User seller = manager.getUserFromName(trans.UserNameSeller);
            User buyer = manager.getUserFromName(trans.UserNameBuyer);
            userListToUpdate.add(seller);
            userListToUpdate.add(buyer);
            Item item = buyer.getItemFromSymbol(trans.getSymbolOfStock());
                if(item!=null)
                {
                    item.setQuantity(item.getQuantity()+trans.getAmountOfStocks());
                }
                else
                {
                    buyer.holdingStocks.getItemList().add(new Item(trans.getSymbolOfStock(),trans.getAmountOfStocks()));
                }
                Item itemForSeller = seller.getItemFromSymbol(trans.getSymbolOfStock());
                itemForSeller.setQuantity(itemForSeller.getQuantity()-trans.getAmountOfStocks());
                itemForSeller.setAmountOfSellingStocks(itemForSeller.getAmountOfSellingStocks()-trans.getAmountOfStocks());
                if(itemForSeller.getQuantity()==0)
                {
                    seller.holdingStocks.getItemList().remove(itemForSeller);
                }
            }
        return userListToUpdate;
    }
    public  User(String name)
    {
        this.name=name;
        this.holdingStocks = new Holding();
    }
    public Item getItemFromSymbol(String symbol)
    {
        for(Item item : holdingStocks.getItemList())
        {
            if(item.getSymbol().equals(symbol))
            {
                return item;
            }
        }
        return null;
    }
}
