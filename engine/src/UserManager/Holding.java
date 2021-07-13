package UserManager;

import GenratedCode.RseHoldings;
import GenratedCode.RseItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Holding implements Serializable {
   private List<Item> itemList;
   public Holding(RseHoldings holding)
   {
       itemList =new ArrayList<>();
       List<RseItem> listOfGenItems = holding.getRseItem();
       for(RseItem item : listOfGenItems)
       {
           itemList.add(new Item(item));
       }
   }
    public Holding()
    {
        itemList = new ArrayList<>();
    }
    public List<Item> getItemList() {
        return itemList;
    }
    public void addNewItem(Item item)
    {
        itemList.add(item);
    }
    public void setItemList(Holding holding) {
     this.itemList= holding.itemList;
    }
}
