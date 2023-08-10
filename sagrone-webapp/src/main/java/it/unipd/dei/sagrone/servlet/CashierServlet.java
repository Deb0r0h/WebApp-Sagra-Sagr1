package it.unipd.dei.sagrone.servlet;

import it.unipd.dei.sagrone.database.DeleteCashierDAO;
import it.unipd.dei.sagrone.database.InsertCashierDAO;
import it.unipd.dei.sagrone.database.SearchCashierDAO;
import it.unipd.dei.sagrone.database.UpdateCashierDAO;
import it.unipd.dei.sagrone.filter.AdminFilter;
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Message;
import it.unipd.dei.sagrone.resource.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
    ERROR CODES:
    E4B2: 'Wrong input' -> Integer cast error/Invalid Sagra ID and/or user ID provided OR inserted pw is too weak.
    E5A5: Exception in User constructor -> Invalid user/pw/id_sagra/(id) provided.
    E5A2: 'Resource exists' -> Primary key or 'unique' constraint violation.
    E4A1: DB error.
    E4A5: Unsupported operation.
    E5A4: 'Dependent resources' -> Tried to delete a cashier that has associated orders in the DB.
 */


/**
 * Manages cashier-related operations
 */

public class CashierServlet extends AbstractDatabaseServlet{

    /**
     *  Keeps a queue of messages generated during operations on data to be then showed at the
     *  moment of visualization
     */
    ArrayList<Message> m = new ArrayList<>();
    /**
     * Criteria for password strength
     */
    String regex_psw = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$";

    int checkSession_getSagraId(HttpServletRequest req, String operation) throws ServletException, NumberFormatException{

        final HttpSession session = req.getSession(false);
        Integer id_sagra = null;
        // it should not happen here: it means the filter is not working!
        if (session == null) {
            LOGGER.fatal(String.format("No session. Cannot %s cashiers.",operation));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("No session. Cannot %s cashiers.",operation));
        }
        final String admin = (String) session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE);

        // it should not happen here: it means the filter is not working!
        if (admin == null || admin.isBlank() || admin.isEmpty() ) {
            LOGGER.fatal(String.format("Unauthorized attempt to %s cashiers on session %s.", operation, session.getId()));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("Unauthorized attempt to %s cashiers on session %s.", operation, session.getId()));
        }

        id_sagra = (Integer) session.getAttribute(AdminFilter.SAGRA_ATTRIBUTE);
        if(id_sagra == null || id_sagra<1) throw new NumberFormatException();
        LogContext.setUser((String)session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE));
        return id_sagra;
    }

    /**
     * Shows the list of currently registered cashiers.
     * Also shows the editing interface if provided an edit=1 parameter.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     *
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction("Users->showList");

        int id_sagra = -1;

        // model
        List<User> users = null;

        try {

            id_sagra = checkSession_getSagraId(req,"LIST");
            LOGGER.info("Authorized access to user page of sagra %d",id_sagra);
            // AT THIS point, we should have confirmed a valid session and a valid admin login.
            // IN THEORY THERE'S NO NEED TO CHECK ADMIN<->SAGRA RELATIONSHIP since it is set SERVER SIDE
            // Thus a logged admin accessing the user change page will always be prompted to HIS SAGRA's user change page.

            users = new SearchCashierDAO(getConnection(), id_sagra).access().getOutputParam();
            m.add(new Message("Users successfully listed."));
            LOGGER.info("Users successfully listed.");


        } catch (NumberFormatException ex) {
            m.add(new Message("Invalid sagra ID provided.", "E4B2", ex.getMessage()));
            LOGGER.fatal("Active session has an invalid sagra ID!!!", ex);
        } catch (SQLException ex) {
            m.add(new Message("Cannot list users: unexpected error while accessing the database.", "E5A1", ex.getMessage()));
            LOGGER.error("Cannot list users: unexpected error while accessing the database.", ex);
        }

        //END OF OPERATIONS: OUTPUT BELOW

        try {
            // stores the employee list and the message as a request attribute
            req.setAttribute("userList", users);
            req.setAttribute("messages", m);

            // forwards the control to the search-employee-result JSP
            req.getRequestDispatcher("/jsp/admin/users.jsp").forward(req, res);
            m.clear();  //To clear message list after they're sent to client.

        } catch(Exception ex) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when listing Users."), ex);
            throw ex;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeUser();
        }

    }

    /**
     * Replies to various requests performing operations involving cashiers.
     * Will finally redirect to the user list page.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String op = req.getRequestURI();

        //Unified id_sagra retrieval: this has been done to condense here all session-related work.
        int id_sagra = -1;
        try{
            //remove everything prior to /users/ (included) and use the remainder as indicator for the required operation
            op = op.substring(op.lastIndexOf("users") + 6);
            id_sagra = checkSession_getSagraId(req,String.format("POST_%s",op));
            switch (op) {
                case "update":
                    // update cashier info/credentials
                    updateCashier(req, id_sagra);
                    break;
                case "create":
                    // update cashier info/credentials
                    createCashier(req, id_sagra);
                    break;
                case "delete":
                    // update cashier info/credentials
                    deleteCashier(req, id_sagra);
                    break;
                default:
                    // the requested operation is unknown
                    m.add(new Message("Unsupported operation","E4A5",""));
            }
        } catch (NumberFormatException ex) {
            m.add(new Message(String.format("Invalid sagra ID provided in user %s (POST) attempt.",op), "E4B2", ex.getMessage()));
            LOGGER.fatal(String.format("Active session has an invalid sagra ID!!! in user %s (POST) attempt",op), ex);  //FATAL since sagra id is set server side!!!!!!!!
        } catch (IndexOutOfBoundsException ex){
            m.add(new Message(String.format("Invalid operation %s.",op), "E4A5", ex.getMessage()));
            LOGGER.fatal(String.format("Unknown or invalid POST operation communicated %s",op), ex);
        }


        try {
            // redirects user to cashiers list page
            res.sendRedirect(String.format("%s/users",req.getContextPath()));
        } catch(Exception ex) {
            LOGGER.error(new StringFormattedMessage(String.format("Unable to send response after POST %s operation.",op)), ex);
            throw ex;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
            LogContext.removeUser();
        }

    }


    /**
     * Method invoked by doPost when the intended operation is Cashier account update.
     *
     * @param req the HTTP request from the client.
     * @param id_sagra the ID of the Sagra the users operates in.
     *
     */
    void updateCashier(HttpServletRequest req, int id_sagra){
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction("user_update");

        // request User entity parameters
        int id = -1;
        String username = null;
        String password = null;
        Map<String, String[]> paramMap = null;

        // model
        User u = null;

        // retrieves the request parameters
        paramMap = req.getParameterMap();

        for (String s : paramMap.keySet()) {
            try {
                id = Integer.parseInt(s);
                // set the user id as the resource in the log context (to be saved in case of exception)
                LogContext.setResource(req.getParameter("id"));
                username = paramMap.get(s)[0];
                password = paramMap.get(s)[1];  //password field is sent anyways (blank if unchanged).

                // new User instantiation from received parameters
                // PLEASE NOTE: ADMIN TAG HARDCODED TO FALSE (editing allowed for cashier users only).
                // This makes impossible updating a cashier to ad admin account.
                // PLEASE NOTE: DATA CHECK IS CARRIED OUT IN THE USER RESOURCE CONSTRUCTOR.
                if (password.isBlank() || password.isEmpty()) {   //USERNAME CHANGE ONLY
                    u = new User(username, id_sagra, id);           //(here, the admin tag is hardcoded to false inside the constructor.)
                } else{
                    u = new User(username, password, id_sagra, false, id);    //USER AND PW CHANGE
                    //Insertion in a User object ensures that the provided data is valid.
                    //Safety compliance:
                    if (!password.matches(regex_psw)) throw new InvalidParameterException();
                }


                // creates a new object for accessing the database and updates the user
                new UpdateCashierDAO(getConnection(), u).access();

                m.add(new Message(String.format("User %d successfully updated.", id)));

                LOGGER.info("User %d successfully updated.", id);

            } catch (InvalidParameterException ex){
                m.add(new Message("Cannot update user: provided password is too weak.", "E4B2", ex.getMessage()));
                LOGGER.error("Cannot update user: provided password is too weak.");
            } catch(NumberFormatException ex){    //TO CATCH if 'id' in the form is not INTEGER
                m.add(new Message("Cannot update user: invalid user specified.", "E4B2", ex.getMessage()));
                LOGGER.error("Invalid user specified: id(s) must be integer.", ex);
            } catch(IllegalArgumentException ex){ //TO CATCH blanks, whitespaces ecc.
                m.add(new Message("Cannot update user: invalid data provided.", "E5A5", ex.getMessage()));
                LOGGER.error("Invalid data provided.", ex);
            } catch(SQLException ex){
                if("23505".equals(ex.getSQLState())){
                    m.add(new Message("Cannot update user: username already in use.", "E5A2", ex.getMessage()));
                    LOGGER.error("Cannot update user: username already in use.",ex.getSQLState());
                }else{
                    m.add(new Message("Cannot update user: unexpected error while accessing the database.", "E5A1", ex.getMessage()));
                    LOGGER.error("Cannot update user: unexpected error while accessing the database (%s).",ex.getSQLState(), ex);
                }
            }

        }

    }

    /**
     * Method invoked by doPost when the intended operation is Cashier account insertion.
     *
     * @param req the HTTP request from the client.
     * @param id_sagra the ID of the Sagra the users operates in.
     *
     */
    void createCashier(HttpServletRequest req, int id_sagra){
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction("user_insertion");

        // request User entity parameters
        String username = null;
        String password = null;

        // model
        User u = null;

        try {
            // retrieves the request parameters
            username = req.getParameter("newUsername");
            password = req.getParameter("newPassword");

            LogContext.setResource(username);   //since we do not know the user ID we'll use its username

            // new User instantiation from received parameters
            // PLEASE NOTE: ADMIN TAG HARDCODED TO FALSE (insertion allowed for cashier users only).
            // This makes impossible inserting new admins through the portal.
            u = new User(username, password, id_sagra, false);
            //Insertion in a User object ensures that the provided data is valid.
            //Safety compliance:
            if (!password.matches(regex_psw)) throw new InvalidParameterException();

            // creates a new object for accessing the database and inserts the user
            new InsertCashierDAO(getConnection(), u).access();


            m.add(new Message(String.format("New user '%s' successfully inserted.", username)));

            LOGGER.info("New user with username %s successfully inserted.", username);
        } catch (InvalidParameterException ex){
                m.add(new Message("Cannot create user: provided password is too weak.", "E4B2", ex.getMessage()));
                LOGGER.error("Cannot create user: provided password is too weak.");
        } catch (IllegalArgumentException ex) { //TO CATCH blanks, whitespaces ecc.
            m.add(new Message("Cannot create user: invalid data provided.", "E5A5", ex.getMessage()));
            LOGGER.error("Invalid data provided.", ex);
        }  catch (SQLException ex) {
            if("23505".equals(ex.getSQLState())){
                m.add(new Message("Cannot create user: username already in use.", "E5A2", ex.getMessage()));
                LOGGER.error("Cannot create user: username already in use.",ex.getSQLState());
            }else{
                m.add(new Message("Cannot create user: unexpected error while accessing the database.", "E5A1", ex.getMessage()));
                LOGGER.error("Cannot create user: unexpected error while accessing the database (%s).",ex.getSQLState(), ex);
            }
        }

    }

    /**
     * Method invoked by doPost when the intended operation is Cashier account deletion.
     *
     * @param req the HTTP request from the client.
     * @param id_sagra the ID of the Sagra the users operates in.
     *
     */
    void deleteCashier(HttpServletRequest req, int id_sagra){
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction("user_deletion");

        // request User entity parameters
        int id = -1;

        // model
        User u = null;

        try {
            // retrieves the request parameters
            id = Integer.parseInt(req.getParameter("del"));

            // set the user id as the resource in the log context
            // at this point we know it is a valid integer
            LogContext.setResource(req.getParameter("id"));

            // new User instantiation from received parameters
            // The DAO won't allow admin deletion: no further check needed.
            u = new User(id_sagra,id);

            //m.add(new Message(String.format("will delete %d from sagra %d.", id,id_sagra)));

            // creates a new object for accessing the database and inserts the user
            switch (new DeleteCashierDAO(getConnection(), u).access().getOutputParam().toString()){
                case "0":
                    m.add(new Message(String.format("Cannot remove user %d.", id),"E4B2","Cannot remove user."));
                    LOGGER.info("Cannot remove user %d.", id);
                    break;
                case "1":
                    m.add(new Message(String.format("User %d successfully deleted.", id)));
                    LOGGER.info("User %d successfully deleted.", id);
                    break;
                case "2":
                    m.add(new Message(String.format("Cannot delete user: internal error.", id),"E4A1","Internal DB error!"));
                    LOGGER.fatal("[CASHIER-REMOVAL] WARNING: SINGLE REQUEST CAUSED MULTIPLE USER DELETION, AMBIGUOUS IDs OR DB ACCESS PROBLEM.");
            }

        } catch (NumberFormatException ex) {    //TO CATCH if 'id' in the form is not INTEGER
            m.add(new Message("Cannot delete user: invalid user specified.", "E5B2", ex.getMessage()));
            LOGGER.error("Invalid user specified: id(s) must be integer.", ex);
        } catch (IllegalArgumentException ex) { //TO CATCH blanks, whitespaces ecc.
            m.add(new Message("Cannot delete user: invalid data provided.", "E5A5", ex.getMessage()));
            LOGGER.error("Invalid data provided.", ex);
        } catch (SQLException ex) {
            if("23503".equals(ex.getSQLState())){
                m.add(new Message("Cannot delete user: associated orders exist.", "E5A2", ex.getMessage()));
                LOGGER.error("Cannot delete cashier: associated orders exist.",ex.getSQLState());
            }else{
                m.add(new Message("Cannot delete user: internal error.", "E5A1", ex.getMessage()));
                LOGGER.error("Cannot delete user: unexpected error while accessing the database (%s).",ex.getSQLState(), ex);
            }
        }

    }


}
