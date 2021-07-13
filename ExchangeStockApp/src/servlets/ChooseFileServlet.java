package servlets;

import GenratedCode.RizpaStockExchangeDescriptor;
import UserManager.Item;
import UserManager.User;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import components.commerce.RitzpaStockManager;
import components.commerce.Stock;
import components.dataTransferObject.JaxbXmlToObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static components.dataTransferObject.JaxbXmlToObject.*;


@WebServlet(name = "ChooseFileServlet", urlPatterns = {"/ChooseFileServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ChooseFileServlet<FileItem> extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter out = response.getWriter();
        try {
            Part part = request.getPart("fake-key-1");
            String xmlFile = extractFileName(part);
            stringBuilder.append(readFromInoutString(part.getInputStream())).append("\n");
            RitzpaStockManager manager = ServletUtils.getUserManager(getServletContext());
            String userName = SessionUtils.getUserName(request);
            System.out.println("aaaaaaaaaaaaaaaaaaladdd");
            response.setContentType("text/html");
            StringBuilder errorr = new StringBuilder();
            System.out.println(xmlFile);
            List<Object> listOfObject = new ArrayList<>();
            RizpaStockExchangeDescriptor rtz = JaxbXmlToObject.JaxbXml2Object(part.getInputStream());

            if(isXmlValid3(rtz,errorr)) {
                listOfObject = RitzpaStockManager.createFromGenCode(rtz);
                User user = manager.getUserFromName(userName);
                List<Item> items = (List<Item>) (listOfObject.get(1));
                List<Stock> StocksList = (List<Stock>) (listOfObject.get(0));

                if (checkValidityOfItems(items, StocksList, manager)) {
                    boolean isItemExist=false;
                    for (Item item : items) {
                        for(Item userItem: user.getHoldingStocks().getItemList())
                        {
                            if(item.getSymbol().equals(userItem.getSymbol()))
                            {
                                isItemExist=true;
                                userItem.setQuantity(userItem.getQuantity()+item.getQuantity());
                                break;
                            }
                        }
                        if(!isItemExist)
                            user.addNewItemToUser(item);
                    }
                    for (Stock stock : StocksList) {
                        if(!manager.getStocks().getStocks().containsKey(stock.getSymbol()))
                        {
                        manager.getStocks().addNewStock(stock.getCompanyName(), stock.getSymbol(), stock.getPrice());
                    }
                    }
                    out.println("The file as loaded succesfully");
                }
                else
                {
                    out.println("Errorrr!!! load was failed ");
                }
            }
            else
            {
                out.println(errorr);
            }
        }
        catch (Exception ex)
        {
            out.println("Error!! loading file has failed");
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
    private String readFromInoutString(InputStream inoutStream)
    {
        return new Scanner(inoutStream).useDelimiter("\\Z").next();
    }
    public boolean checkValidityOfItems(List<Item> listOfItems , List<Stock> listOfStocks, RitzpaStockManager manager)
    {
        boolean isAllStocksAcuure = false;
        for (Item item: listOfItems)
        {
            for(Stock stock: listOfStocks)
            {
                if(item.getSymbol().equals(stock.getSymbol()))
                {
                    isAllStocksAcuure=true;
                    break;
                }
            }
            if(!isAllStocksAcuure)
            {
                return false;
            }
            isAllStocksAcuure=false;
        }
        return true ;
    }
}
