package Utils;

import javax.servlet.ServletContext;
import components.commerce.RitzpaStockManager;;
public class ServletUtils {
    private static final String RITZPA_STOCK_MANAGER = "RitzpaStockManager";
    private static final Object userManagerLock = new Object();
    private static final String ADMIN_SYSTEM = "admin-system";
    private static final Object adminLock = new Object();

    public static RitzpaStockManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(RITZPA_STOCK_MANAGER) == null) {
                servletContext.setAttribute(RITZPA_STOCK_MANAGER, new RitzpaStockManager());
            }
            return (RitzpaStockManager) servletContext.getAttribute(RITZPA_STOCK_MANAGER);
        }
    }

    public static StringBuilder getAdmin(ServletContext servletContext,String userName) {

        synchronized (adminLock) {
            if (servletContext.getAttribute(ADMIN_SYSTEM) == null) {
                servletContext.setAttribute(ADMIN_SYSTEM, new StringBuilder(userName));
            }
            return (StringBuilder) servletContext.getAttribute(ADMIN_SYSTEM);
        }
    }
}