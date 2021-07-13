package servlets;

import UserManager.Item;
import UserManager.User;
import Utils.ServletUtils;
import Utils.SessionUtils;
import components.commerce.RitzpaStockManager;
import components.commerce.Stock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(name = "AddingNewStockServlet ", urlPatterns = {"/AddingNewStockServlet"})
public class AddingNewStockServlet extends HttpServlet  {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String userName = SessionUtils.getUserName(request);
        String companyName = request.getParameter("CompanyName");
        String Symbol = request.getParameter("Symbol");
        String StocksAmount = request.getParameter("StocksAmount");
        String CompanyValue = request.getParameter("CompanyValue");
        if(!Symbol.toUpperCase().equals(Symbol))
        {
            out.println("Error!!! Symbol must be Upper case");
            return;
        }

        int stockRate = Integer.parseInt(CompanyValue)/Integer.parseInt(StocksAmount);
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        User user = manager.getUserFromName(userName);
        if(manager.getStocks().getStocks().containsKey(Symbol))
        {
            out.println("Error!! Stock symbol " +Symbol+" already exist in the system" );
            return;
        }
        for(Map.Entry<String,Stock> stocks : manager.getStocks().getStocks().entrySet())
        {
            if(stocks.getValue().getCompanyName().equals(companyName))
            {
                out.println("Error this company name has stock already");
                return;
            }
        }
        if(Integer.parseInt(CompanyValue)<=0 || Integer.parseInt(StocksAmount)<=0)
        {
            out.println("Error  company value and stocks amount must be greater than zero");
            return;
        }
        if(manager.getStocks().addNewStock(companyName,Symbol,stockRate)) {
            out.println("The New Stock has added successfully");
            user.addNewItemToUser(new Item(Symbol,Integer.parseInt(StocksAmount)));
        }
            else
        {
            out.println("Error!! company name and stock symbol must be unique");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest(request,response);
    }
}
