package servlets;

import UserManager.Item;
import UserManager.User;
import Utils.ServletUtils;
import Utils.SessionUtils;
import components.commerce.RitzpaStockManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "UserBalanceServlet", urlPatterns = {"/UserBalanceServlet"})
public class UserBalanceServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Printtttttttttt");
        PrintWriter out = response.getWriter();
        String userName = SessionUtils.getUserName(request);
        RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
        User user =manager.getUserFromName(userName);
        if(request.getParameter("USERBALANCE")!=null)
        {
            out.println(user.getAmountOfMoney());
            return;
        }
        String amount =request.getParameter("AmountOfMoney");
        user.changeMoneyToUser(Integer.parseInt(amount));
        out.println(user.getAmountOfMoney());
    }
    @Override
    protected void doGet(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest(request,response);
    }
}
