package it.unipd.dei.sagrone.servlet;

import it.unipd.dei.sagrone.filter.AdminFilter;
import it.unipd.dei.sagrone.filter.CashierFilter;
import it.unipd.dei.sagrone.resource.Actions;
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Lets logged users log out of their account.
 */
public final class LogoutServlet extends AbstractDatabaseServlet {

    /**
     * Logs out current user.
     *
     * @param req
     *            the HTTP request from the client.
     * @param res
     *            the HTTP response from the server.
     *
     * @throws ServletException
     *             if any error occurs while executing the servlet.
     * @throws IOException
     *             if any error occurs in the client/server communication.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.LOGOUT);
        String user = null;
        //PART 1/2: LOGOUT
        try {
            HttpSession session = req.getSession(false);
            if(session != null){            //if a valid session is found:
                user = (String)session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE);
                if(user!=null){ //if logged-in user is an admin:
                    LogContext.setUser(user);
                    LOGGER.info("Admin %s logged out.",user);
                } else {
                    user = (String)session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE);
                    if(user!=null){ //if logged-in user is a cashier
                        LogContext.setUser(user);
                        LOGGER.info("Cashier %s logged out.",user);
                    }
                    else LOGGER.info("Logout attempt from non-authenticated session."); //if there's no logged-in user.
                }

                session.invalidate();
            }
        } catch (IllegalStateException ex) {
            LOGGER.error("Invalid session: no user to log out.", ex);
        }

        //PART 2/2: REDIRECTION
        try {
            // redirects user to the sagra list.
            res.sendRedirect(String.format("%s/",req.getContextPath()));
        } catch(Exception ex) {
            LOGGER.error("Unable to perform redirection after logout attempt.");
            throw ex;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeUser();
        }
    }

}