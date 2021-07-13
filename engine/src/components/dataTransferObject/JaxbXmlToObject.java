package components.dataTransferObject;

import GenratedCode.RizpaStockExchangeDescriptor;
import GenratedCode.RseItem;
import GenratedCode.RseStock;
import GenratedCode.RseUser;
import UserManager.Item;

import javax.management.InstanceNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class JaxbXmlToObject{
    private final static String JAXB_XML = "GenratedCode";
    public final static String XML_TYPE = "text/xml";
    //"src/resources/ex1-small.xml"
    public static RizpaStockExchangeDescriptor JaxbXml2Object(String path) throws JAXBException, FileNotFoundException {
        RizpaStockExchangeDescriptor rizpaStockExchangeDescriptors = null;
        File newFile = new File(path);
            InputStream inputStream = new FileInputStream(newFile);
            rizpaStockExchangeDescriptors = desirealize(inputStream);
            return rizpaStockExchangeDescriptors;

    }
    public static RizpaStockExchangeDescriptor JaxbXml2Object(InputStream stream) throws JAXBException, FileNotFoundException {
        RizpaStockExchangeDescriptor rizpaStockExchangeDescriptors = null;
        rizpaStockExchangeDescriptors = desirealize(stream);
        return rizpaStockExchangeDescriptors;

    }


    public static RizpaStockExchangeDescriptor desirealize(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML);
        Unmarshaller u = jc.createUnmarshaller();
        return (RizpaStockExchangeDescriptor) u.unmarshal(in);
    }

    public static boolean isXmlValid(RizpaStockExchangeDescriptor rtz, StringBuilder errorDescription) {
        Set<String> symbolHashSet = new HashSet<>();
        Set<String> companyNameHashSet = new HashSet<>();
        Set<String> userNameHashSet = new HashSet<>();
        for (RseStock stock : rtz.getRseStocks().getRseStock()) {
            if (stock.getRseSymbol().contains(" ")) {
                errorDescription.append ("The symbol of " + stock.getRseSymbol() + " stock must not contain space");
                return false;
            }else if(!stock.getRseSymbol().toUpperCase().equals(stock.getRseSymbol()))
            {
                errorDescription.append ("The symbol of " + stock.getRseSymbol() + " stock must contain only Uppercase letters");
                return false;
            }
            else if (stock.getRseSymbol().isEmpty() || stock.getRseCompanyName().isEmpty()) {
                errorDescription.append("The symbol or company name must not be empty ");
                return false;
            }
            else if(stock.getRsePrice()<=0)
            {
                errorDescription.append("The stock price must be greater than zero");
                return false;
            }
            else {
                if (symbolHashSet.contains(stock.getRseSymbol().toUpperCase())) {
                    errorDescription.append("Error The Symbol " + stock.getRseSymbol() + " is belong to two stocks" +
                            ", supposed to be unique");
                    return false;
                } else {
                    symbolHashSet.add(stock.getRseSymbol().toUpperCase());
                }
                if (companyNameHashSet.contains(stock.getRseCompanyName().toUpperCase())) {
                    errorDescription.append("Error The company Name " + stock.getRseCompanyName() +
                            " is already exist in other stock" +
                            ", supposed to be unique");
                    return false;
                } else {
                    companyNameHashSet.add(stock.getRseCompanyName().toUpperCase());
                }
            }


        }

        /*for(RseUser users : rtz.getRseUsers().getRseUser())
        {
            if(userNameHashSet.contains(users.getName().toUpperCase()))
            {
                errorDescription.append("Error! user name must be unique, "+users.getName()+" appear more than once");
                return false;
            }
            userNameHashSet.add(users.getName().toUpperCase());
            if(!isUserStocksInTheSystem(users,symbolHashSet,errorDescription))
                return  false;
        }
        */

        return true;
    }

    public static boolean isXmlValid3(RizpaStockExchangeDescriptor rtz, StringBuilder errorDescription) {
        Set<String> symbolHashSet = new HashSet<>();
        Set<String> companyNameHashSet = new HashSet<>();
        for (RseStock stock : rtz.getRseStocks().getRseStock()) {
            if (stock.getRseSymbol().contains(" ")) {
                errorDescription.append ("The symbol of " + stock.getRseSymbol() + " stock must not contain space");
                return false;
            }else if(!stock.getRseSymbol().toUpperCase().equals(stock.getRseSymbol()))
            {
                errorDescription.append ("The symbol of " + stock.getRseSymbol() + " stock must contain only Uppercase letters");
                return false;
            }
            else if (stock.getRseSymbol().isEmpty() || stock.getRseCompanyName().isEmpty()) {
                errorDescription.append("The symbol or company name must not be empty ");
                return false;
            }
            else if(stock.getRsePrice()<=0)
            {
                errorDescription.append("The stock price must be greater than zero");
                return false;
            }
            else {
                if (symbolHashSet.contains(stock.getRseSymbol().toUpperCase())) {
                    errorDescription.append("Error The Symbol " + stock.getRseSymbol() + " is belong to two stocks" +
                            ", supposed to be unique");
                    return false;
                } else {
                    symbolHashSet.add(stock.getRseSymbol().toUpperCase());
                }
                if (companyNameHashSet.contains(stock.getRseCompanyName().toUpperCase())) {
                    errorDescription.append("Error The company Name " + stock.getRseCompanyName() +
                            " is already exist in other stock" +
                            ", supposed to be unique");
                    return false;
                } else {
                    companyNameHashSet.add(stock.getRseCompanyName().toUpperCase());
                }
            }
        }
        return true;
    }



    public static boolean isUserStocksInTheSystem(RseUser user , Set<String> stocksName, StringBuilder errorDesc)
    {
        for(RseItem item : user.getRseHoldings().getRseItem())
        {
            if(!stocksName.contains(item.getSymbol()))
            {
                errorDesc.append("Error! the stock "+ item.getSymbol() +" is not recognize in the commerce system");
                return false;
            }
        }
        return true;
    }
}