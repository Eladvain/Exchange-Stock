package Tasks;

import GenratedCode.RizpaStockExchangeDescriptor;
import components.commerce.RitzpaStockManager;
import components.dataTransferObject.JaxbXmlToObject;
import components.dataTransferObject.ObjectSerialization;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.UnmarshalException;
import java.util.List;

public class TaskLoadXml extends Task<List<Object>> {
    public File filePath;
    public boolean isXmlLoader;
    public StringBuilder errorDesc;
    public RitzpaStockManager manager;

    public TaskLoadXml(File FilePath, boolean xmlLoaderBool) {
        isXmlLoader = xmlLoaderBool;
        errorDesc = new StringBuilder();
        this.filePath = FilePath;
    }

    @Override
    protected List<Object> call() throws Exception {
        try {
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhheeeeeeeeeeeeeeeeeeee");

            updateMessage("loading xml file");
            updateProgress(0.33, 1);
            Thread.sleep(1000);
            RizpaStockExchangeDescriptor rtz = JaxbXmlToObject.JaxbXml2Object(filePath.getPath().toString());
            List<Object> listOfObject = RitzpaStockManager.createFromGenCode(rtz);
            if (JaxbXmlToObject.isXmlValid(rtz, errorDesc)) {

                return listOfObject;
            } else {
                updateMessage("xml validation check was failed ");
                updateProgress(0, 1);
                Thread.sleep(1000);
                return null;
            }
        } catch (FileNotFoundException | NullPointerException fnfex) {
            errorDesc.append("File  Not Found");
            return null;
        } catch (JAXBException jaxbex) {
            errorDesc.append("Error-File is not Exist in the requested path");
            return null;
        } catch (Exception ex) {
            errorDesc.append("Error in loading xml file");
            return null;
        }

    }

}

