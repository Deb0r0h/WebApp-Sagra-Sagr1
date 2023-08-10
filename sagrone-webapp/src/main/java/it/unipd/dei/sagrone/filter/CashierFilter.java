
package it.unipd.dei.sagrone.filter;

import it.unipd.dei.sagrone.resource.LogContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Checks for successful authentication of a cashier to allow for accessing the resources that are meant to be accessible only for cashier users.
 */
public class CashierFilter implements Filter {
    /**
     * A LOGGER available for all the subclasses.
     */
    protected static final Logger LOGGER = LogManager.getLogger(CashierFilter.class, StringFormatterMessageFactory.INSTANCE);

    /**
     * The name of the cashier attribute in the session
     */
    public static final String CASHIER_ATTRIBUTE = "cashier";

    /**
     * The name of the sagra attribute in the session
     */
    public static final String SAGRA_ATTRIBUTE = "sagra";

    /**
     * The configuration for the filter
     */
    private FilterConfig config = null;

    /**
     * The connection pool to the database.
     */
    private DataSource ds;

    @Override
    public void init(FilterConfig config) throws ServletException {
        if (config == null) {
            LOGGER.error("Filter configuration cannot be null.");
            throw new ServletException("Filter configuration cannot be null.");
        }
        this.config = config;

        // the JNDI lookup context
        InitialContext cxt;

        try {
            cxt = new InitialContext();
            ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/sagrone");
        } catch (NamingException e) {
            ds = null;

            LOGGER.error("Unable to acquire the connection pool to the database.", e);

            throw new ServletException("Unable to acquire the connection pool to the database", e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        LogContext.setIPAddress(servletRequest.getRemoteAddr());

        try {
            if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
                LOGGER.error("Only HTTP requests/responses are allowed.");
                throw new ServletException("Only HTTP requests/responses are allowed.");
            }

            // Safe to downcast at this point.
            final HttpServletRequest req = (HttpServletRequest) servletRequest;
            final HttpServletResponse res = (HttpServletResponse) servletResponse;

            LOGGER.info("request URL =  %s", req.getRequestURL());

            final HttpSession session = req.getSession(false);

            // if we do not have a session, try to authenticate the user
            if (session == null) {

                LOGGER.warn("Authentication required to access resource %s with method %s.", req.getRequestURI(), req.getMethod());

                // try to authenticate the user
                //forwarding the request to the login page
                LOGGER.info("Forwarding the request to the login page");
                req.getRequestDispatcher("/jsp/login.jsp").forward(req,res);
                return;
            } else {

                final String cashier = (String) session.getAttribute(CASHIER_ATTRIBUTE);
                final Integer sagra = (Integer) session.getAttribute(SAGRA_ATTRIBUTE);

                // there might exist a session but without any user in it
                if (cashier == null || cashier.isBlank() || sagra == null ) {

                    // invalidate the session
                    session.invalidate();

                    LOGGER.warn("Authentication required to access resource %s with method %s. Session %s exists but no cashier or sagra found in session. Session invalidated.", req.getRequestURI(), req.getMethod(), session.getId());


                    // try to authenticate the user
                    //forwarding the request to the login page
                    LOGGER.info("Forwarding the request to the login page");
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req,res);
                    return;
                }
            }

            // the user is properly authenticated and in session, continue the processing
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            LOGGER.error("Unable to perform the protected resource filtering.", e);
            throw e;
        } finally {
            LogContext.removeUser();
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }

    @Override
    public void destroy() {
        config = null;
        ds = null;
    }
}
