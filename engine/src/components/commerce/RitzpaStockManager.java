package components.commerce;

import GenratedCode.*;
import UserManager.Item;
import UserManager.User;
import UserManager.Users;
import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;

import java.io.Console;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;

public class RitzpaStockManager implements Serializable {
    public static String SystemSavingFile = "../../../ExchangeStockApp/";
    public Users users;
    public RitzpaStockManager(Stocks stocks, RseUsers genRseUsers) {
        this.stocks = stocks;
        users = new Users(genRseUsers);
        for (Map.Entry<String, Stock> entry :this.stocks.getStocks().entrySet())
        {
            entry.getValue().stockPriceList.add(entry.getValue().getPrice());
            entry.getValue().xChartLocation.add(String.valueOf(0));
        }
    }
    public RitzpaStockManager(RseStocks rseStocks)
    {
      this.stocks = Stocks.createStocks(rseStocks);
    }

    public RitzpaStockManager()
    {
    this.stocks = new Stocks(new HashMap<String,Stock>());
    this.users = new Users();
    }

    private Stocks stocks;

    public Stocks getStocks() {
        return stocks;
    }

    public void setStocks(Stocks stocks) {
        this.stocks = stocks;
    }

    public static List<Object> createFromGenCode(RizpaStockExchangeDescriptor rtz) {
        List<Object> listOfObject = new ArrayList<>();
        List<Stock> listOfStocks = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        for(RseStock rseStock : rtz.getRseStocks().getRseStock())
        {
            listOfStocks.add(Stock.createStockFromGenCode(rseStock));
        }
        for(RseItem item : rtz.getRseHoldings().getRseItem())
        {
            itemList.add(new Item(item.getSymbol(),item.getQuantity()));
        }
        listOfObject.add(listOfStocks);
        listOfObject.add(itemList);
        return listOfObject;

    }

    public Stock getStockByKey(String symbol) {
        Map stocksMap = stocks.getStocks();
        return (Stock) stocksMap.get(symbol);
    }

    public List<TransactionData> applyTransaction(TransactionData newTrans, TransactionsCommands type) {
        List<TransactionData> executedTrans =  new ArrayList<>();
        boolean isExecuted = false, isInserted = false;
        int i = 0;
        Stock stock = stocks.getStocks().get(newTrans.getSymbolOfStock().toUpperCase());
        switch (newTrans.getTypeOfTrans()) {
            case buy:
                if(type==TransactionsCommands.LMT) {
                    isExecuted = insertToExecutedListCaseBuy(newTrans, stock, executedTrans);
                }
                else if(type==TransactionsCommands.FOK)
                {
                    isExecuted = applyInCaseOfFOk(newTrans,stock,executedTrans);
                }
                else if(type==TransactionsCommands.IOC)
                {
                    isExecuted = applyInCaseOfIOC(newTrans,stock,executedTrans);
                }
                else {
                   isExecuted =  commandBuyExecuationCaseMKT(newTrans,stock,executedTrans);
                }
                if (!isExecuted && type !=TransactionsCommands.FOK && type !=TransactionsCommands.IOC) {
                    if(type == TransactionsCommands.MKT)
                        newTrans.setLimitPrice(stock.getPrice());
                    insertToBuyList(newTrans, stock);
                }
                break;
            case sell:
                if(type==TransactionsCommands.LMT) {
                    isExecuted = insertToExecutedListCaseSeller(newTrans, stock, executedTrans);
                }
                else if(type==TransactionsCommands.FOK)
                {
                    isExecuted=applyInCaseOfFOk(newTrans,stock,executedTrans);
                }
                else if(type==TransactionsCommands.IOC)
                {
                    isExecuted = applyInCaseOfIOC(newTrans,stock,executedTrans);
                }
                else {
                    isExecuted = commandSellExecuationCaseMKT(newTrans, stock, executedTrans);
                }
                if (!isExecuted && type !=TransactionsCommands.FOK && type!=TransactionsCommands.IOC) {
                    if(type == TransactionsCommands.MKT)
                        newTrans.setLimitPrice(stock.getPrice());
                    insertToSellerList(newTrans, stock);
                }
        }
        return executedTrans;
    }

    public boolean insertToExecutedListCaseSeller(TransactionData newTrans, Stock stock,List<TransactionData> executedTrans) {
        boolean isExecuted = false;
        for (int i = 0; i < stock.getHoldingBuyingTrans().size(); i++) {
            TransactionData buyingTrans = stock.getHoldingBuyingTrans().get(i);
            if (buyingTrans.getLimitPrice() >= newTrans.getLimitPrice()) {
                if (buyingTrans.getAmountOfStocks() == newTrans.getAmountOfStocks()) {
                    setIncaseOfNewSellEqualsStocksAmount(stock, newTrans, buyingTrans,executedTrans);
                    i--;
                    isExecuted = true;
                    break;
                } else if (buyingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                    setInCaseOfNewSellSmallerAmountOfStocks(stock, newTrans, buyingTrans,executedTrans);
                    isExecuted = true;
                    break;
                } else if (buyingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                    setInCaseOfNewSellBiggerStocksAmount(stock, newTrans, buyingTrans,executedTrans);
                    i--;
                }
            }
        }
        return isExecuted;
    }

    public boolean insertToExecutedListCaseBuy(TransactionData newTrans, Stock stock,List<TransactionData> executedTrans) {
        boolean isExecuted = false;
        for (int i = 0; i < stock.getHoldingSalesTrans().size(); i++) {
            TransactionData sellingTrans = stock.getHoldingSalesTrans().get(i);
            if (sellingTrans.getLimitPrice() <= newTrans.getLimitPrice()) {
                if (sellingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                    setIncaseOfNewBuyBiggerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    isExecuted = true;
                    break;
                } else if (sellingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                    setInCaseOfNewBuySmallerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    i--;

                } else {
                    setInCaseOfNewBuyEqualsStocksAmount(stock, sellingTrans, newTrans, executedTrans);
                    i--;
                    isExecuted = true;
                    break;
                }

            }
        }
        return isExecuted;
    }

    public void setIncaseOfNewBuyBiggerSellerStockAmount(Stock stock, TransactionData sellingTrans
            , TransactionData buyingTrans,List<TransactionData> executedTrans) {
        System.out.println("----------1--------------");
        int amountOfStocks = buyingTrans.getAmountOfStocks();
        buyingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        buyingTrans.setTotalPriceTrans(sellingTrans.getLimitPrice() * buyingTrans.getAmountOfStocks());
        sellingTrans.setAmountOfStocks(sellingTrans.getAmountOfStocks() - buyingTrans.getAmountOfStocks());
        stock.setPrice(sellingTrans.getLimitPrice());
        buyingTrans.setLimitPrice(sellingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(buyingTrans.getTotalPriceTrans());
        buyingTrans.setTimeStamp();
        stock.getExecutedTrans().add(buyingTrans);
       // buyingTrans.UserNameSeller=sellingTrans.UserNameSeller;
        executedTrans.add(buyingTrans);
        stock.addNumberOfStocks(buyingTrans.getAmountOfStocks());
        stock.getHoldingBuyingTrans().remove(buyingTrans);
        setAmountOfStocksByUsers(buyingTrans.UserApplyTransaction,sellingTrans.UserApplyTransaction,stock,amountOfStocks, sellingTrans.getLimitPrice());
    }

    public void setInCaseOfNewBuySmallerSellerStockAmount(Stock stock, TransactionData sellingTrans,
                                                          TransactionData buyingTrans,List<TransactionData> executedTrans) {
        System.out.println("----------2--------------");
        int amountOfStocks = sellingTrans.getAmountOfStocks();
        sellingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        sellingTrans.setTotalPriceTrans(sellingTrans.getLimitPrice() * sellingTrans.getAmountOfStocks());
        buyingTrans.setAmountOfStocks(buyingTrans.getAmountOfStocks() - sellingTrans.getAmountOfStocks());
        stock.setPrice(sellingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(sellingTrans.getTotalPriceTrans());
        sellingTrans.setTimeStamp();
        stock.getExecutedTrans().add(sellingTrans);
        //sellingTrans.UserNameBuyer = buyingTrans.UserNameBuyer;
        executedTrans.add(sellingTrans);
        stock.addNumberOfStocks(sellingTrans.getAmountOfStocks());
        stock.getHoldingSalesTrans().remove(sellingTrans);
        setAmountOfStocksByUsers(buyingTrans.UserApplyTransaction,sellingTrans.UserApplyTransaction,stock,amountOfStocks,sellingTrans.getLimitPrice());
    }

    public void setInCaseOfNewBuyEqualsStocksAmount(Stock stock, TransactionData sellingTrans,
                                                    TransactionData buyingTrans,List<TransactionData> executedTrans) {
        System.out.println("----------3--------------");
        int amountOfStocks = sellingTrans.getAmountOfStocks();
        buyingTrans.setTypeOfTrans(TransactionData.buyOrSel.executed);
        buyingTrans.setTotalPriceTrans(buyingTrans.getAmountOfStocks() * sellingTrans.getLimitPrice());
        stock.setPrice(sellingTrans.getLimitPrice());
        buyingTrans.setLimitPrice(sellingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(buyingTrans.getTotalPriceTrans());
        buyingTrans.setTimeStamp();
        stock.getExecutedTrans().add(buyingTrans);
        //buyingTrans.UserNameSeller = sellingTrans.UserNameSeller;
        executedTrans.add(buyingTrans);
        stock.addNumberOfStocks(buyingTrans.getAmountOfStocks());
        stock.getHoldingSalesTrans().remove(sellingTrans);
        setAmountOfStocksByUsers(buyingTrans.UserApplyTransaction,sellingTrans.UserApplyTransaction,stock,amountOfStocks,sellingTrans.getLimitPrice());
    }


    public void setIncaseOfNewSellEqualsStocksAmount(Stock stock, TransactionData sellingTrans,
                                                     TransactionData buyingTrans,List<TransactionData> executedTrans) {
        System.out.println("----------4--------------");
        int amountOfStocks = sellingTrans.getAmountOfStocks();
        sellingTrans.setTypeOfTrans(TransactionData.buyOrSel.executed);
        sellingTrans.setTotalPriceTrans(sellingTrans.getAmountOfStocks() * buyingTrans.getLimitPrice());
        sellingTrans.setLimitPrice(buyingTrans.getLimitPrice());
        stock.getExecutedTrans().add(sellingTrans);
        sellingTrans.setTimeStamp();
        //sellingTrans.UserNameBuyer = buyingTrans.UserNameBuyer;;
        executedTrans.add(sellingTrans);
        stock.addTotalTransactionPrice(sellingTrans.getTotalPriceTrans());
        stock.addNumberOfStocks(sellingTrans.getAmountOfStocks());
        stock.setPrice(buyingTrans.getLimitPrice());
        //stock.getHoldingSalesTrans().remove(buyingTrans);
        stock.getHoldingBuyingTrans().remove(buyingTrans);
        setAmountOfStocksByUsers(buyingTrans.UserApplyTransaction,sellingTrans.UserApplyTransaction,stock,amountOfStocks,buyingTrans.getLimitPrice());
    }

    public void setInCaseOfNewSellSmallerAmountOfStocks(Stock stock, TransactionData sellingTrans,
                                                        TransactionData buyingTrans,List<TransactionData> executedTrans) {
        System.out.println("----------5--------------");
        int amountOfStocks = sellingTrans.getAmountOfStocks();
        sellingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        sellingTrans.setTotalPriceTrans(buyingTrans.getLimitPrice() * sellingTrans.getAmountOfStocks());
        sellingTrans.setLimitPrice(buyingTrans.getLimitPrice());
        stock.getExecutedTrans().add(sellingTrans);
        //sellingTrans.UserNameBuyer = buyingTrans.UserNameBuyer;
        executedTrans.add(sellingTrans);
        sellingTrans.setTimeStamp();
        stock.addNumberOfStocks(sellingTrans.getAmountOfStocks());
        stock.addTotalTransactionPrice(sellingTrans.getTotalPriceTrans());
        stock.setPrice(buyingTrans.getLimitPrice());
        buyingTrans.setAmountOfStocks(buyingTrans.getAmountOfStocks() - sellingTrans.getAmountOfStocks());
        setAmountOfStocksByUsers(buyingTrans.UserApplyTransaction,sellingTrans.UserApplyTransaction,stock,amountOfStocks,buyingTrans.getLimitPrice());
    }

    public void setInCaseOfNewSellBiggerStocksAmount(Stock stock, TransactionData sellingTrans
            , TransactionData buyingTrans,List<TransactionData> executedTrans) {
        System.out.println("----------6--------------");
        int amountOfStocks = buyingTrans.getAmountOfStocks();
        buyingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        buyingTrans.setTotalPriceTrans(buyingTrans.getLimitPrice() * buyingTrans.getAmountOfStocks());
        stock.setPrice(buyingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(buyingTrans.getTotalPriceTrans());
        stock.getExecutedTrans().add(buyingTrans);
        buyingTrans.setTimeStamp();
        //buyingTrans.UserNameSeller = sellingTrans.UserNameSeller;
        executedTrans.add(buyingTrans);
        stock.addNumberOfStocks(buyingTrans.getAmountOfStocks());
        sellingTrans.setAmountOfStocks(sellingTrans.getAmountOfStocks() - buyingTrans.getAmountOfStocks());
        stock.getHoldingBuyingTrans().remove(buyingTrans);
        setAmountOfStocksByUsers(buyingTrans.UserApplyTransaction,sellingTrans.UserApplyTransaction,stock,amountOfStocks,buyingTrans.getLimitPrice());
    }

    public void insertToSellerList(TransactionData newTrans, Stock stock) {
        int i = 0;
        boolean isInserted = false;
        for (TransactionData seller : stock.getHoldingSalesTrans()) {

            if (newTrans.getLimitPrice() < seller.getLimitPrice()) {
                stock.getHoldingSalesTrans().add(i, newTrans);
                isInserted = true;
                break;
            }
            i++;
        }
        if (!isInserted) {
            stock.getHoldingSalesTrans().add(i, newTrans);
        }
    }


    public void insertToBuyList(TransactionData newTrans, Stock stock) {
        boolean isInserted = false;
        int i = 0;
        for (TransactionData buyer : stock.getHoldingBuyingTrans()) {
            if (newTrans.getLimitPrice() > buyer.getLimitPrice()) {
                stock.getHoldingBuyingTrans().add(i, newTrans);
                isInserted = true;
                break;
            }
            i++;
        }
        if (!isInserted) {
            stock.getHoldingBuyingTrans().add(i, newTrans);
        }
    }

    public String printAllTransactionsDetails() {
        String str = "";
        for (Map.Entry<String, Stock> entry : stocks.getStocks().entrySet()) {
            str += "Stock symbol: " + entry.getValue().getSymbol() + "\n";
            str += "Company name: " + entry.getValue().getCompanyName() + "\n";
            str += "Stock rate: " + entry.getValue().getPrice() + "\n";
            str += entry.getValue().getListOfTransactionAsString();
            str += "\n";
        }
        return str;
    }
    public boolean commandBuyExecuationCaseMKT(TransactionData newTrans, Stock stock , List<TransactionData> executedTrans)
    {
        boolean isExecuted=false;
        for (int i = 0; i < stock.getHoldingSalesTrans().size(); i++) {
            TransactionData sellingTrans = stock.getHoldingSalesTrans().get(i);
                if (sellingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                    newTrans.setLimitPrice(sellingTrans.getLimitPrice());
                    setIncaseOfNewBuyBiggerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    isExecuted = true;
                    break;
                } else if (sellingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                    setInCaseOfNewBuySmallerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    i--;

                } else {
                    newTrans.setLimitPrice(sellingTrans.getLimitPrice());
                    setInCaseOfNewBuyEqualsStocksAmount(stock, sellingTrans, newTrans, executedTrans);
                    i--;
                    isExecuted = true;
                    break;
                }
        }
        return isExecuted;
    }

    public boolean commandSellExecuationCaseMKT(TransactionData newTrans, Stock stock , List<TransactionData> executedTrans)
    {
        boolean isExecuted=false;
        for (int i = 0; i < stock.getHoldingBuyingTrans().size(); i++) {
            TransactionData buyingTrans = stock.getHoldingBuyingTrans().get(i);
            if (buyingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                newTrans.setLimitPrice(buyingTrans.getLimitPrice());
                setInCaseOfNewSellSmallerAmountOfStocks(stock, newTrans, buyingTrans,executedTrans);
                isExecuted = true;
                break;
            } else if (buyingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                setInCaseOfNewSellBiggerStocksAmount(stock,  newTrans,buyingTrans,executedTrans);
                i--;

            } else {
                newTrans.setLimitPrice(buyingTrans.getLimitPrice());
                setIncaseOfNewSellEqualsStocksAmount(stock, newTrans,buyingTrans, executedTrans);
                i--;
                isExecuted = true;
                break;
            }
        }
        return isExecuted;
    }
    public User getUserFromName(String name)
    {
        for (User itUser : users.getListOfUsers())
        {
            if(itUser.getName().equals(name))
                return itUser;
        }
        return null;
    }
    public List<String> getStocksFromUserByTransactionType(String userName ,TransactionData.buyOrSel transcationType)
    {
        List<String> listOfStocksSymbol = new ArrayList<>();

        if(transcationType == TransactionData.buyOrSel.buy) {
            for (Map.Entry<String, Stock> entry : stocks.getStocks().entrySet()) {
                listOfStocksSymbol.add(entry.getKey());
            }
        }
        else
        {
            User user =getUserFromName(userName);
          for (Item item : user.getHoldingStocks().getItemList())
          {
              listOfStocksSymbol.add(item.getSymbol());
          }
        }
        return listOfStocksSymbol;
    }
    public boolean checkValidityOfNewTransactionCommand(String userName, TransactionData.buyOrSel transType, int
            amountOfStocks,String stockSymbol, StringBuilder errorDetails, Integer limitPrice)
    {

        if( amountOfStocks<=0) {
            errorDetails.append("Amount of stocks must be greater than 0");
            return false;
        }
        if(limitPrice!=null)
            {
                if(limitPrice<=0) {
                    errorDetails.append("Price of stock must be greater than 0");
                    return false;
                }
            }
        if (transType == TransactionData.buyOrSel.buy)
                return  true;
            else {
                User user = getUserFromName(userName);
                if (user.getAmountOfStocksBySymbol(stockSymbol) >= amountOfStocks)
                    return true;
                else {
                    errorDetails.append("Error! you have " + user.getAmountOfStocksBySymbol(stockSymbol) +
                            " stocks of " + stockSymbol + ". you can not sell more than that amount");
                    return  false;
                }
            }
    }
    public Integer addNewPriceToStockList(List<TransactionData> trans,Integer Counter)
    {
        for(TransactionData newTrans :trans)
        {
            Stock stock = getStockByKey(newTrans.getSymbolOfStock());
            stock.stockPriceList.add(newTrans.getLimitPrice());
            stock.xChartLocation.add(String.valueOf(Counter));
            Counter++;
        }
        return  Counter;
    }
    public void setAmountOfStocksByUsers(String buyerName ,String sellerNAme,  Stock stock , int Amount, int stockPrice)
    {
        System.out.println("------------- amount: "+Amount+"----------- stock proce"+ stockPrice);
        User seller = this.getUserFromName(sellerNAme);
        User buyer = this.getUserFromName(buyerName);
        boolean doesBuyerHaveTheStock=false;
        for(Item item: seller.getHoldingStocks().getItemList())
        {
            if(item.getSymbol().equals(stock.getSymbol()))
            {
                item.setQuantity(item.getQuantity()-Amount);
                break;
            }
        }

        for(Item item: buyer.getHoldingStocks().getItemList())
        {
            if(item.getSymbol().equals(stock.getSymbol()))
            {
                item.setQuantity(item.getQuantity()+Amount);
                doesBuyerHaveTheStock=true;
                break;
            }
        }
        if(!doesBuyerHaveTheStock)
        {
            buyer.addNewItemToUser(new Item(stock.getSymbol(),Amount));
        }
        seller.changeMoneyToUser(Amount*stockPrice);
        buyer.changeMoneyToUser(-1*Amount*stockPrice);

    }
    public boolean applyInCaseOfFOk(TransactionData newTrans , Stock stock , List<TransactionData> executedTrans)
    {
        switch(newTrans.getTypeOfTrans())
        {
            case buy:
                if(FOKinCaseOfNewTransIsBuy(newTrans,stock,executedTrans))
                   return insertToExecutedListCaseBuy(newTrans,stock,executedTrans);
                break;
            case sell:
                if(FOKInCaseOFNewTransIsSelling(newTrans,stock,executedTrans))
                    return insertToExecutedListCaseSeller(newTrans,stock,executedTrans);
                break;
        }
        return false;

    }
    public boolean FOKinCaseOfNewTransIsBuy(TransactionData newTrans , Stock stock , List<TransactionData> executedTrans)
    {
        int amountOfStocksToBuyy = newTrans.getAmountOfStocks();
        for (int i = 0; i < stock.getHoldingSalesTrans().size(); i++) {
            TransactionData sellingTrans = stock.getHoldingSalesTrans().get(i);
            if (sellingTrans.getLimitPrice() <= newTrans.getLimitPrice()) {
                amountOfStocksToBuyy-=sellingTrans.getAmountOfStocks();
            }
            if(amountOfStocksToBuyy<=0)
                return true;
        }
        return false;
    }
    public boolean FOKInCaseOFNewTransIsSelling(TransactionData newTrans , Stock stock , List<TransactionData> executedTrans)
    {
        int amountOfStocksToBuyy = newTrans.getAmountOfStocks();
        for (int i = 0; i < stock.getHoldingBuyingTrans().size(); i++) {
            TransactionData buyingTrans = stock.getHoldingBuyingTrans().get(i);
            if (buyingTrans.getLimitPrice() >= newTrans.getLimitPrice()) {
               amountOfStocksToBuyy-=buyingTrans.getAmountOfStocks();
            }
            if(amountOfStocksToBuyy<=0)
                return true;
        }
        return false;
    }
    public boolean applyInCaseOfIOC(TransactionData newTrans , Stock stock , List<TransactionData> executedTrans)
    {
        switch(newTrans.getTypeOfTrans())
        {
            case buy:
                if(IOCinCaseOfNewTransIsBuy(newTrans,stock,executedTrans))
                    return insertToExecutedListCaseBuy(newTrans,stock,executedTrans);
                break;
            case sell:
                if(IOCInCaseOFNewTransIsSelling(newTrans,stock,executedTrans))
                    return insertToExecutedListCaseSeller(newTrans,stock,executedTrans);
                break;
        }
        return false;

    }
    public boolean IOCinCaseOfNewTransIsBuy(TransactionData newTrans , Stock stock , List<TransactionData> executedTrans)
    {
        int amountOfStocksToBuyy = 0;
        for (int i = 0; i < stock.getHoldingSalesTrans().size(); i++) {
            TransactionData sellingTrans = stock.getHoldingSalesTrans().get(i);
            if (sellingTrans.getLimitPrice() <= newTrans.getLimitPrice()) {
                amountOfStocksToBuyy+=sellingTrans.getAmountOfStocks();
            }
        }

        if(amountOfStocksToBuyy>0) {
            if(amountOfStocksToBuyy<newTrans.getAmountOfStocks())
                newTrans.setAmountOfStocks(amountOfStocksToBuyy );
            return true;
        }
        return false;
    }
    public boolean IOCInCaseOFNewTransIsSelling(TransactionData newTrans , Stock stock , List<TransactionData> executedTrans)
    {
        int amountOfStocksToBuyy = 0;
        for (int i = 0; i < stock.getHoldingBuyingTrans().size(); i++) {
            TransactionData buyingTrans = stock.getHoldingBuyingTrans().get(i);
            if (buyingTrans.getLimitPrice() >= newTrans.getLimitPrice()) {
                amountOfStocksToBuyy+=buyingTrans.getAmountOfStocks();
            }
        }
        if(amountOfStocksToBuyy>0) {
            if(amountOfStocksToBuyy<newTrans.getAmountOfStocks())
                newTrans.setAmountOfStocks(amountOfStocksToBuyy);
            return true;
        }
        return false;
    }

}
