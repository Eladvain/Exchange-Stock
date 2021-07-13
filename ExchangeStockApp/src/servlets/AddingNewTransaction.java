package servlets;

import UserManager.Item;
import UserManager.User;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import components.commerce.RitzpaStockManager;
import components.commerce.Stock;
import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@WebServlet(name = "AddingNewTransaction ", urlPatterns = {"/addTransaction"})
public class AddingNewTransaction extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        if(request.getParameter("type123") != null) {
            Integer price = null;
            String submitObj = request.getParameter("type123");
            String stockName = request.getParameter("stockNameOfUser");
            String commerceType = request.getParameter("Commerce Type");
            String rateLimit = request.getParameter("rate_limit");
            String numOfStocks = request.getParameter("numOfStocks");
            int numberOfStocksWhichExecuted = 0;
            int numberOFStocksToBuyOrSell= Integer.parseInt(numOfStocks);
            String userName = SessionUtils.getUserName(request);
            RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
            TransactionsCommands commands;
            if(submitObj.equals("SellStocks"))
            {

                User user = manager.getUserFromName(userName);
                for(Item item :user.getHoldingStocks().getItemList())
                {
                    if(item.getSymbol().equals(stockName))
                    {
                        if(item.getQuantity()<Integer.parseInt(numOfStocks))
                        {
                            out.println("Errorrr!! you can't sell more stocks than you have");
                            return ;
                        }
                    }
                }
            }
            if(commerceType.equals("LMT"))
                commands= TransactionsCommands.LMT;
            else if (commerceType.equals("MKT"))
                commands = TransactionsCommands.MKT;
            else if(commerceType.equals("FOK"))
                commands =TransactionsCommands.FOK;
            else
                commands=TransactionsCommands.IOC;
            if(commands!=TransactionsCommands.MKT)
            {
                price = Integer.parseInt(rateLimit);
            }
            TransactionData.buyOrSel TransType = ((submitObj.equals("SellStocks")) ? TransactionData.buyOrSel.sell : TransactionData.buyOrSel.buy);
            TransactionData newTrans = new TransactionData(stockName,TransType,Integer.parseInt(numOfStocks),price,userName);
            List<TransactionData> executeCurrentTrans = manager.applyTransaction(newTrans, commands);
            for (TransactionData trans: executeCurrentTrans){
                trans.toString();
                numberOfStocksWhichExecuted+=trans.getAmountOfStocks();
            }
            if(numberOFStocksToBuyOrSell== numberOfStocksWhichExecuted)
            {
                out.println("The Transaction executed fully");
                return;
            }

            if(numberOFStocksToBuyOrSell> numberOfStocksWhichExecuted && numberOfStocksWhichExecuted!=0)
            {
                out.println("The Transaction executed partially");
                return;
            }
            if(numberOfStocksWhichExecuted==0)
            {
                out.println("The Transaction dose not executed at all");
                return;
            }

        }
        else {
            String stringSecond = request.getParameter("GetStocksName");
            if (stringSecond.equals("false"))
                processRequestSellStocks(request, response);
            else if (stringSecond.equals("True"))
                processRequestBuyStocks(request, response);
            else if (stringSecond.equals("1"))
                processRequestWithNothing(request, response);
        }
    }

    public void processRequestWithNothing(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String a = gson.toJson("1");
        PrintWriter out = response.getWriter();
        out.println(a);
    }
    public void processRequestSellStocks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        String userName = SessionUtils.getUserName(request);
        response.setContentType("text/html");
        User user =  manager.getUserFromName(userName);
        List<Item> listOfStocks = user.getHoldingStocks().getItemList();
        List<String> listOfNameStock = new ArrayList<>();
        for (Item item:listOfStocks){
            listOfNameStock.add(item.getSymbol());
        }
        Gson gson = new Gson();
        String a = gson.toJson(listOfNameStock);
        out.println(a);

    }

    public void processRequestBuyStocks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        response.setContentType("text/html");
        Gson gson = new Gson();
        System.out.println(manager.getStocks().getStocks());
        String a = gson.toJson(manager.getStocks().getStocks());
        PrintWriter out = response.getWriter();
        System.out.println(a);

        out.println(a);
    }
}


