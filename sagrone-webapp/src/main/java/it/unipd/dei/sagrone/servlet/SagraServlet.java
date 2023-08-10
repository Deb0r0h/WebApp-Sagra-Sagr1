package it.unipd.dei.sagrone.servlet;

import it.unipd.dei.sagrone.database.SearchSagraByNameDAO;
import it.unipd.dei.sagrone.resource.Actions;
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Message;
import it.unipd.dei.sagrone.resource.Sagra;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Searches sagras: if there is no name parameter from GET all sagras are searched (empty string passed to the DAO),
 * if there is a name parameter only sagras containing that pattern in the name are searched.
 */

@WebServlet(name = "SagraServlet", urlPatterns = { "" })
public class SagraServlet extends AbstractDatabaseServlet{
    /**
     * Searches sagras: if there is no name parameter from GET all sagras are searched (empty string passed to the DAO),
     * if there is a name parameter only sagras containing that pattern in the name are searched.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     *
     * @throws ServletException if any error occurs while executing the servlet.
     */

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SHOW_SAGRAS);

        List<Sagra> sagras = null;
        Message m = null;
        String name = null;


        try {
            name = req.getParameter("name");
            if (name == null)
                name = "";

            name = name.trim();
            sagras = new SearchSagraByNameDAO(getConnection(), name).access().getOutputParam();

            if(!"".equals(name)) {
                m = new Message("Sagras successfully searched.");
                req.setAttribute("message", m);
            }
            LOGGER.info("Sagras with name containing pattern [%s] are successfully searched", name);

        } catch (SQLException e) {
            m = new Message("Cannot search for sagras: unexpected error while accessing the database.", "E5A1",
                    e.getMessage());
            req.setAttribute("message", m);
            LOGGER.error("Cannot search for sagras: unexpected error while accessing the database.", e);
        }
        try {

            req.setAttribute("sagrasList", sagras);
            req.getRequestDispatcher("/jsp/show-sagras.jsp").forward(req, res);

        } catch(Exception e) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when searching sagra containing pattern [%s]", name));
            throw e;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

