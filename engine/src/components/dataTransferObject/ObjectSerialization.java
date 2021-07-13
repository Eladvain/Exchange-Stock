package components.dataTransferObject;

import components.commerce.RitzpaStockManager;

import java.io.*;

public class ObjectSerialization {
    public static void  saveCurrentSystemState(RitzpaStockManager manager) throws IOException {

        File f = new File(RitzpaStockManager.SystemSavingFile);
        if (!f.exists()) {
            f.createNewFile();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(RitzpaStockManager.SystemSavingFile))) {
            out.writeObject(manager);
        }

    }

    public static  RitzpaStockManager reloadSystemState() throws IOException, ClassNotFoundException {
        RitzpaStockManager manager=null;
        File f = new File(RitzpaStockManager.SystemSavingFile);
        if (!f.exists()) {
            throw new FileNotFoundException("File is not exist");
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(RitzpaStockManager.SystemSavingFile))) {
            manager = (RitzpaStockManager) in.readObject();
            return manager;
        }
    }
    public static  RitzpaStockManager reloadSystemState(InputStream inputStream) throws IOException, ClassNotFoundException {
        RitzpaStockManager manager=null;
        try (ObjectInputStream in = new ObjectInputStream(inputStream)) {
            manager = (RitzpaStockManager) in.readObject();
            return manager;
        }
    }
}
