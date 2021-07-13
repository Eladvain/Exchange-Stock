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

@WebServlet(name = "UserDetailServlet ", urlPatterns = {"/UserDetailServlet1"})
public class UserDetailServlet extends HttpServlet {
    boolean isFirstpproach =true;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        System.out.println();
        out.println("../UserDetails/UserDetailProfile.html");
    }
    @Override
    protected void doGet(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException
    {
        System.out.println("here 1");
        String str =request.getParameter("select123");
        System.out.println(str);
        String stringSecond = request.getParameter("GetStocksName");
        String stringLoadUSers = request.getParameter("GetUserNames");
        System.out.println(stringSecond);
        if(str!=null)
        {
            System.out.println("here 2");
            processRequestCaseStockDetails(request,response);
        }
        else if(stringSecond!=null)
        {
            System.out.println("here 3");
            processRequestCaseStockSymbols(request,response);
        }
        else if (stringLoadUSers!=null)
        {
            System.out.println("here case load form ");
             processInCaseOfLoadForm(request,response);
         }
        else {

            System.out.println("here 4");
            processRequest(request, response);
        }
    }
    protected void processRequestCaseStockDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        response.setContentType("text/html");
        String s= request.getParameter("select123");
        if(s!=null)
        {
            System.out.println("The parmeter is "+s);
        }

        else
        {
            System.out.println("The parameter isnt show");
        }
        List<Object> listOfObj = new ArrayList<>();
        Stock stock = manager.getStockByKey(s);
        listOfObj.add(stock);
        String userNAme =  SessionUtils.getUserName(request);
        User user = manager.getUserFromName(userNAme);
        System.out.println("------"+manager.getStockByKey(s).getHoldingBuyingTrans().size());
        if(user!=null) {
            List<Item> list = user.getHoldingStocks().getItemList();
            for (Item item : list) {
                if (stock.getSymbol().equals(item.getSymbol())) {
                    listOfObj.add(item.getQuantity());
                    break;
                }
            }
        }
        else
        {
            listOfObj.add(0);
        }
        Gson gson = new Gson();
        String a = gson.toJson(listOfObj);
        PrintWriter out = response.getWriter();
        System.out.println(a);
        out.println(a);
    }
    protected void processRequestCaseStockSymbols(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("IN heeeerrreeee");
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());

        response.setContentType("text/html");
        Gson gson = new Gson();
        for(Map.Entry<String,Stock> entry : manager.getStocks().getStocks().entrySet())
        {
            System.out.println(entry.getKey());
        }
        System.out.println(manager.getStocks().getStocks());
        String a = gson.toJson(manager.getStocks().getStocks());
        PrintWriter out = response.getWriter();
        System.out.println(a);

        out.println(a);
    }


    protected void processInCaseOfLoadForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       System.out.println("In Proceessss request");
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        String userName =SessionUtils.getUserName(req);
        resp.setContentType("text/html");
        Gson gson = new Gson();
        List<Object> lis=new ArrayList<>();
        System.out.println(manager.users.getListOfUsers().size());
        lis.add(manager.users.getListOfUsers());
        lis.add(userName);
        String json = gson.toJson(lis);
        PrintWriter out = resp.getWriter();
        out.println(json);
    }
}
