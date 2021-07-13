package Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionUtils {
    public static String USER_PARAMETER_NAME = "user-name";
    public static String USERR_ADMIN = "user_admin";
    String cookieUSerName =null;
    public static String getUserName(HttpServletRequest request)
    {
        return (String) request.getSession().getAttribute(USER_PARAMETER_NAME);
    }
    public static String getAdmin(HttpServletRequest request)
    {
        return (String) request.getSession().getAttribute(USERR_ADMIN);
    }
    public static void saveUserName(HttpServletRequest request, HttpServletResponse response, String userName)
    {
        request.getSession(true).setAttribute(USER_PARAMETER_NAME , userName);
    }
    public static void saveAdmine(HttpServletRequest request, HttpServletResponse response, String userName)
    {
        request.getSession(true).setAttribute(USERR_ADMIN , userName);
    }

}
