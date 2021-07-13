package servlets;

import UserManager.User;
import Utils.ServletUtils;
import Utils.SessionUtils;
import components.commerce.RitzpaStockManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.rmi.CORBA.Util;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "loginServlet", urlPatterns = {"/loginShortResponse"})
public class loginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        if(request.getParameter("checkingSession")!=null)
        {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            String userName = SessionUtils.getUserName(request);
            if(userName!=null) {
                out.println("pages/UserMainForm/MainForm.html" + "?userName=" + userName);
                return;
            }
            else
            {
                out.println("");
                return;
            }
        }
    String admin = request.getParameter("admin");

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    ///System.out.println(SessionUtils.getUserName(request));
        if(admin!=null)
        {
            String userName = request.getParameter("username");
            StringBuilder adminName = Utils.ServletUtils.getAdmin(getServletContext(),userName);
            if(adminName.toString().equals(userName)) {
                RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
                User newUser =new User(userName);
                newUser.type = "Admin";
                manager.users.addNewUserToList(newUser);
                out.println("pages/AdminPage/AdminPage.html");
                return;
            }
            else
            {
                out.println("");
                return;
            }
        }
        else {
            String userName = request.getParameter("username");
            RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
            User newUser =new User(userName);
            newUser.type="Trader";
            manager.users.addNewUserToList(newUser);
            SessionUtils.saveUserName(request, response, userName);
            out.println("pages/UserMainForm/MainForm.html");
            System.out.println("mmmmmmmmmmmmnmnmnmnmnmnmn");
            out.flush();
        }
    }
    @Override
    protected void doGet(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest(request,response);
    }
}
