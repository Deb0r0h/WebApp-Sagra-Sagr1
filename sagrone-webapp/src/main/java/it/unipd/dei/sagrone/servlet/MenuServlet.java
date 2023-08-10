
package it.unipd.dei.sagrone.servlet;


import it.unipd.dei.sagrone.database.SearchSagraByIdDAO;
import it.unipd.dei.sagrone.filter.CashierFilter;
import it.unipd.dei.sagrone.resource.*;
import it.unipd.dei.sagrone.utils.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.util.List;


/**
 * Shows the page to place an order, possibly with pre-compiled quantities if the id of an order to modify is passed as parameter.
 */
@WebServlet(name="MenuServlet",value="/menu/*")
public class MenuServlet extends AbstractDatabaseServlet {

    /**
     * Shows the page to place an order, possibly with pre-compiled quantities if the id of an order to modify is passed as parameter.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_ORDER_MENU);

        Message m=null;
        int id_sagra=-1; //the id of the sagra to list the product for

        String op=req.getRequestURI();
        op=op.substring(op.lastIndexOf("menu")+4);

        if(op.startsWith("/")) op=op.substring(1);


        HttpSession session = req.getSession(false);

        try{

            //if there is no id sagra in the parameters the request is not valid
            if(req.getParameter("sagra")==null){
                LOGGER.error("No sagra parameter. Cannot show menu.");
                m=new Message("No sagra selected.",ErrorCode.EMPTY_INPUT_FIELDS.getErrorCode(), "Unable to load menu.");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/").forward(req,res);
                return;
            }

            id_sagra = Integer.parseInt(req.getParameter("sagra"));

            try {
                // forwards the control to the create-employee-result JSP
                List<Sagra> s=new SearchSagraByIdDAO(getConnection(),id_sagra).access().getOutputParam();
                if(s == null || s.isEmpty()) throw new IllegalStateException("wrong parameter passed");
            } catch(Exception e) {
                m=new Message("Unable to find the given sagra.",ErrorCode.WRONG_INPUT_FIELDS.getErrorCode(),"Unable to find the given sagra.");
                LOGGER.warn("Loading menu  - unable find the given sagra..");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/menu.jsp").forward(req,res);
                LogContext.removeIPAddress();
                LogContext.removeAction();
                LogContext.removeResource();
                return;
            }



            //it is the first time calling this page and the user is not logged in
            if (session == null) {
                session=req.getSession(true);
                session.setAttribute("sagra",id_sagra);

            }else{
                if(session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE) == null){
                    session.setAttribute(CashierFilter.SAGRA_ATTRIBUTE,id_sagra);
                }else{
                    if(session.getAttribute(CashierFilter.SAGRA_ATTRIBUTE) == null ||  (Integer) session.getAttribute(CashierFilter.SAGRA_ATTRIBUTE) != id_sagra){
                        m=new Message("Inconsistent data - Login again.",ErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(),"Inconsistent data - Login again.");
                        LOGGER.warn("Inconsistent data - Login again.");
                        req.setAttribute("message",m);
                        req.getRequestDispatcher("/jsp/login.jsp").forward(req,res);
                        return;
                    }
                }


            }
        }catch (NumberFormatException e){
            LOGGER.error("Unable to parse the sagra passed.",e);
            m=new Message("Unable to load menu.",ErrorCode.WRONG_INPUT_FIELDS.getErrorCode(), "Unable to load menu.");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/").forward(req,res);
            return;
        }


        switch(op){
            case "mod":
            case "mod/":
                //access allowed only to authenticated cahiers

                final String cashier= (String) session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE);
                if(cashier==null || cashier.isBlank()){
                    LOGGER.error("No cashier in session %s. cannot modify order", session.getId());
                    LogContext.removeIPAddress();
                    LogContext.removeAction();
                    //throw  new ServletException(String.format("No cashier in session %s. cannot modify order", session.getId()));
                    m=new Message("Unable to load menu. You are not logged in",ErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(), "Unable to load menu.");
                    req.setAttribute("message",m);
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req,res);
                    return;
                }
                LogContext.setUser(cashier);


                createModifyOrderMenu(req,res,id_sagra, session);
                break;

            case "":
                if(req.getParameter("order")!=null){
                    LOGGER.error("Unauthorized access");
                    m=new Message("Unauthorized access.",ErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(), "Unable to load menu.");
                    req.setAttribute("message",m);
                    req.getRequestDispatcher("/").forward(req,res);
                    return;
                }
                createOrderMenu(req,res,id_sagra);
                break;
            default:
                // the requested operation is unknown
                m=new Message("Unknown operation required.",ErrorCode.WRONG_URI_FORMAT.getErrorCode(),"Wrong format URI.");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/menu.jsp").forward(req, res);
                break;
        }
    }


    private void createOrderMenu(HttpServletRequest req, HttpServletResponse res, int id_sagra) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_ORDER_MENU);
        LogContext.setResource(String.format("sagra %d",id_sagra));



        Message m=null;


        if(id_sagra<0){
            LOGGER.warn("No sagra provided.");
            m = new Message("No sagra provided.", ErrorCode.EMPTY_INPUT_FIELDS.getErrorCode(), "No sagra provided");
        }else{
            m = new Message(String.format("Products for sagra %d successfully retrieved.", id_sagra));
            LOGGER.info("Products for sagra %d successfully retrieved.", id_sagra);

        }

        try {
            // forwards the control to the create-employee-result JSP
            req.getRequestDispatcher("/jsp/menu.jsp").forward(req, res);
        } catch(Exception e) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when searching products for sagra %d.", id_sagra), e);
            throw e;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    private void createModifyOrderMenu(HttpServletRequest req, HttpServletResponse res, int id_sagra, HttpSession session) throws ServletException, IOException{
        Message m= null;
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_MODIFY_ORDER_MENU);


        //if there is no id order in the parameters, the request is not valid.
        //forward the request to the list of orders showing a message
        if(req.getParameter("order")==null){
            LOGGER.error("No order parameter. Cannot show menu.");
            m=new Message("No order selected.",ErrorCode.EMPTY_INPUT_FIELDS.getErrorCode(), "Unable to load menu.");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/menu").forward(req,res);
            return;
        }


        LogContext.setResource(req.getParameter("order"));



        try{
            req.setAttribute("message", m);
            req.getRequestDispatcher("/jsp/menu.jsp").forward(req,res);

        }  catch (IOException e) {
            m=new Message("Unable to load menu.",ErrorCode.RESOURCE_NOT_FOUND.getErrorCode(), "Unable to load menu.");
            LOGGER.error("Unable to load menu.",e);
            req.setAttribute("message",m);
            req.getRequestDispatcher("/").forward(req,res);
        } catch (Exception e){
            m=new Message("Unable to modify order. Something went wrong.",ErrorCode.UNEXPECTED_ERROR.getErrorCode(),"Unable to modify order. Something went wrong.");
            LOGGER.warn("Loading menu to place a new order - Something went wrong.");
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/menu.jsp").forward(req,res);
        } finally {
            LogContext.removeAction();
            LogContext.removeResource();
            LogContext.removeIPAddress();
        }
    }
}
