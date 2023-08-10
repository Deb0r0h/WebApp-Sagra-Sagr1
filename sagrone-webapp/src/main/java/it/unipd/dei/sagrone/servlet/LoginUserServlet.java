package it.unipd.dei.sagrone.servlet;


import it.unipd.dei.sagrone.database.LoginUserDAO;
import it.unipd.dei.sagrone.filter.AdminFilter;
import it.unipd.dei.sagrone.filter.CashierFilter;
import it.unipd.dei.sagrone.resource.Actions;
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Message;
import it.unipd.dei.sagrone.resource.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.core.util.NameUtil.md5;

/**
 *
 * This servlet is responsible for users authentication via HTTP Post method
 */

@WebServlet(name = "LoginUsersServlet", value = "/login")
public class LoginUserServlet extends AbstractDatabaseServlet{

    /**
     *
     * Shows the products for the given sagra.
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
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req,res);
    }

    /**
     *
     * This servlet aims to log in the users if present in the system.
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
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.LOGIN);

        String username = (String) req.getParameter("username");
        String password = (String)req.getParameter("password");
        if(password != null)
            password = md5(password);

        List<User> users = new ArrayList<User>();
        Message m=null;
        if (username == null) {
            LOGGER.error("The username provided cannot be null.");
            m=new Message("The username provided cannot be null");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
        }
        else if(username.isEmpty()){
            LOGGER.error("The username provided cannot be empty.");
            m=new Message("The username provided cannot be empty");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
        }
        else if(password == null) {
            LOGGER.error("The password provided cannot be null.");
            m=new Message("The password provided cannot be null");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
        }
        else if(password.isEmpty()){
            LOGGER.error("The password provided cannot be empty.");
            m=new Message("The password provided cannot be empty");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
        }else {         //fix by Diego, there was no 'else' -> servlet tried DAO access even with null user/pw. there might be better ways.
            try {
                LOGGER.info("Trying to retrieve user with values: u '%s' p '%s'",username,password);
                users = new LoginUserDAO(getConnection(), username, password).access().getOutputParam();
                if (users == null ||users.isEmpty()) {
                    m = new Message("The user is not present in the system", "E5A3", "Missing user");
                    LOGGER.error("Problems with user: %s", m.getMessage());
                    req.setAttribute("message", m);
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
                } else {
                    LOGGER.info("The users %s is logged in.", users.get(0).getUsername());
                    // setting appropriate session attributes:
                    HttpSession session = req.getSession();
                    session.setAttribute(CashierFilter.CASHIER_ATTRIBUTE, users.get(0).getUsername());
                    session.setAttribute(CashierFilter.SAGRA_ATTRIBUTE,users.get(0).getIdSagra());
                    if(users.get(0).isAdmin()){
                        session.setAttribute(AdminFilter.ADMIN_ATTRIBUTE,users.get(0).getUsername());
                        res.sendRedirect(String.format("%s/users",req.getContextPath()));
                    }else {
                        session.setAttribute(AdminFilter.ADMIN_ATTRIBUTE, null); //to prevent mess after a double ADMIN then CASHIER login.
                        res.sendRedirect(String.format("%s/orders", req.getContextPath()));
                    }
                }

            } catch (SQLException e) {
                m = new Message("An error occurred SQL", "E5A1", e.getMessage());
                LOGGER.error("stacktrace: %s", m.getMessage());
                req.setAttribute("message", m);
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
            } finally {
                LogContext.removeIPAddress();
                LogContext.removeAction();
            }


        }

    }
}
