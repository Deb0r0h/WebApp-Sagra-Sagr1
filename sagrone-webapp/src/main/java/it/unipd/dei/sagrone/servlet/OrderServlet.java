
package it.unipd.dei.sagrone.servlet;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import it.unipd.dei.sagrone.database.*;
import it.unipd.dei.sagrone.database.SearchCashierDAO;
import it.unipd.dei.sagrone.filter.AdminFilter;
import it.unipd.dei.sagrone.filter.CashierFilter;
import it.unipd.dei.sagrone.resource.*;
import it.unipd.dei.sagrone.utils.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Management of orders
 */
@WebServlet(name = "OrderServlet", urlPatterns = { "/orders/*", "/payedorders/*" })
public class OrderServlet extends AbstractDatabaseServlet {

    final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int checkSession_getSagraId(HttpServletRequest req, String operation) throws ServletException, NumberFormatException{

        final HttpSession session = req.getSession(false);
        Integer id_sagra = null;
        // it should not happen here
        if (session == null) {
            LOGGER.error(String.format("No session. Cannot %s order.",operation));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("No session. Cannot %s order.",operation));
        }
        final String cashier = (String) session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE);

        final String admin = (String) session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE);

        // it should not happen here
        if ((cashier == null || cashier.isBlank() || cashier.isEmpty() ) && (admin == null || admin.isBlank() || admin.isEmpty())) {
            LOGGER.error(String.format("Unauthorized attempt to %s order on session %s.", operation, session.getId()));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("Unauthorized attempt to %s order on session %s.", operation, session.getId()));
        }
        if (cashier != null || !cashier.isBlank() || !cashier.isEmpty() ){
            id_sagra = (Integer) session.getAttribute(CashierFilter.SAGRA_ATTRIBUTE);
        }else{
            id_sagra = (Integer) session.getAttribute(AdminFilter.SAGRA_ATTRIBUTE);
        }

        if(id_sagra == null || id_sagra<1) throw new NumberFormatException();
        return id_sagra;
    }

    int checkSession_getUserId(HttpServletRequest req, String operation, Connection con) throws ServletException, NumberFormatException{

        final HttpSession session = req.getSession(false);
        Integer id_user = null;
        Integer id_sagra = null;
        // it should not happen here
        if (session == null) {
            LOGGER.error(String.format("No session. Cannot %s order.",operation));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("No session. Cannot %s order.",operation));
        }
        final String cashier = (String) session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE);

        final String admin = (String) session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE);

        // if(both) it should not happen here
        if ((cashier == null || cashier.isBlank() || cashier.isEmpty() ) && (admin == null || admin.isBlank() || admin.isEmpty())) {
            LOGGER.error(String.format("Unauthorized attempt to %s order on session %s.", operation, session.getId()));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("Unauthorized attempt to %s order on session %s.", operation, session.getId()));
        }
        if (cashier != null && !cashier.isBlank() && !cashier.isEmpty() ){
            LOGGER.error("Get sagra from cashier");
            id_sagra = (Integer) session.getAttribute(CashierFilter.SAGRA_ATTRIBUTE);
        }else{
            LOGGER.error("Get sagra from admin");
            id_sagra = (Integer) session.getAttribute(AdminFilter.SAGRA_ATTRIBUTE);
        }


        if(id_sagra == null || id_sagra<1) throw new NumberFormatException();

        try{
            LOGGER.error("Cashier: %s", cashier);
            LOGGER.error("Admin: %s", admin);
            List<User> users = new SearchCashierDAO(con, cashier, id_sagra).access().getOutputParam();



            if(users.size() == 0){
               throw new ServletException(String.format("No UserID found %s.", operation, session.getId()));
            }
            if(users.size() == 1) id_user = (Integer) users.get(0).getId();
            else{//check scan the list to find if there is a user with the same name
                for(User u : users){
                    if(u.getUsername().equals(cashier)) id_user = (Integer) u.getId();
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error looking up user ID with name: %s and Id_sagra: %d",cashier,id_sagra, ex);
        }

        if(id_user == null || id_user<1) throw new NumberFormatException();
        LOGGER.error("ID_User: %s", id_user);
        return id_user;
    }

    /**
     * Show orders with the given parameters.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Message m=null;
        String requestURI = req.getRequestURI();
        LOGGER.info("URI: %s", requestURI);
        String orderPart = requestURI.substring(requestURI.indexOf("/", 1));

        switch (orderPart) {
            case "/orders":
                LOGGER.info("NOT PAYED");
                getOrder(req,res,false);//only unpaid
                break;
            case "/payedorders":
                LOGGER.info("PAYED");
                getOrder(req,res,true);//only paid
                break;
            case "/payedorders/pdf":
                LOGGER.info("PDF");
                getOrderPDF(req,res);
                break;
            case "/orders/delete":
                // delete the passed order
                deleteOrder(req, res);
                break;
            default:
                // handle invalid URL path
                m = new Message("Invalid URL path.", "E4A7", "Invalid path or resource");
                LOGGER.error("Invalid URL path: %s", orderPart);
                req.setAttribute("message",m);
                //req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
                res.sendRedirect(String.format("%s/orders",req.getContextPath()));
                break;
        }
    }

    private void getOrder(HttpServletRequest req, HttpServletResponse res, boolean paid) throws IOException, ServletException {

        Message m=null;
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SHOW_ORDERS);

        int id_sagra = -1;
        int id_order= -1;
        List<Order> orders_list = null; //list of orders

        try {
            // Get the ID from session
            id_sagra = checkSession_getSagraId(req,"GET");
            LOGGER.info("Authorized access to order page of sagra %d",id_sagra);

            // set the id of the sagra as the resource in the log context
            LogContext.setResource(String.valueOf(id_sagra));

            // Check for the presence of the Id_orders parameter
            id_order = Integer.parseInt(req.getParameter("IDorder") == null || req.getParameter("IDorder").equals("") ? "-1" : req.getParameter("IDorder"));

            if(req.getAttribute("payment") != null){//from payment
                id_order = -1;
            }
            orders_list = new SearchOrderDAO(getConnection(), id_sagra, id_order, paid, null, null, null, null).access().getOutputParam();
            LOGGER.info("Orders successfully listed.");

            // Check for the presence of the sort parameter
            String sort = req.getParameter("sort") == null || req.getParameter("sort").equals("") ? "": req.getParameter("sort");

            //sort the list
            switch (sort) {
                case "ID":
                    Collections.sort(orders_list, Comparator.comparingInt(Order::getId));
                    break;
                case "REVERSE_ID":
                    Collections.sort(orders_list, Comparator.comparingInt(Order::getId).reversed());
                    break;
                case "OLDEST_TIME":
                    Collections.sort(orders_list, Comparator.comparing(Order::getOrderTime));
                    break;
                default:
                    Collections.sort(orders_list, Comparator.comparing(Order::getOrderTime).reversed());
            }

            req.setAttribute("sorted", sort);

            if(req.getAttribute("confirmation") != null){
                LOGGER.info("Confirmation present");
            }else{
                LOGGER.info("Confirmation not present");
            }

        } catch (NumberFormatException ex) {
            if (id_sagra == -1) {
                m = new Message("Invalid sagra ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot list/query Order: invalid sagra ID.", ex);
            } else {
                m = new Message("Invalid order ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot list/query Order: invalid order ID.", ex);
            }
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (SQLException ex) {
            m = new Message("Cannot list order: unexpected error while accessing the database.", "E5A1", ex.getMessage());
            LOGGER.error("Cannot list order: unexpected error while accessing the database.", ex);
            req.setAttribute("message",m);
            req.getRequestDispatcher("/").forward(req,res);
        }

        try {
            req.setAttribute("orders", orders_list);
            if(paid){
                req.setAttribute("payed", true);
            }

            // forwards the control to the create-employee-result JSP
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (Exception e) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when listing Orders."), e);
            throw e;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Shows the products for the given sagra.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Message m=null;
        String requestURI = req.getRequestURI();
        String orderPart = requestURI.substring(requestURI.indexOf("/", 1));

        switch (orderPart) {
            case "/orders":
                doGet(req,res);
                break;
            case "/orders/pay":
                // pay the order
                payOrder(req, res);
                break;

            case "/payedorders/unpay":
                // unpay the order
                unPayOrder(req, res);
                break;
            default:
                // the requested operation is unknown
                m = new Message("Invalid URL path.", "E4A7", "Invalid path or resource");
                LOGGER.error("Invalid URL path: %s", orderPart);
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
                break;
        }
    }

    private void payOrder(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        Message m=null;
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CHANGE_ORDER);

        int id_user = -1;
        int id_order = -1;
        int id_sagra = -1;
        Timestamp payment_time = null;

        try {
            // Check for Order id
            id_order = Integer.parseInt(req.getParameter("IDorder") == null || req.getParameter("IDorder").equals("")? "-1" : req.getParameter("IDorder"));
            if (id_order < 1) {
                m = new Message("Id order cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot change Order: invalid order ID.");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
            }
            // Get the user ID from session
            id_user = checkSession_getUserId(req,"PayOrder",getConnection());
            LOGGER.info("Authorized access to order page of user %d",id_user);

            // Get the ID from session
            id_sagra = checkSession_getSagraId(req,"DELETE");
            LOGGER.info("Authorized access to order page of sagra %d",id_sagra);

            // If present take it, otherwise use the server date
            String date = req.getParameter("timestamp") == null || req.getParameter("timestamp").equals("") ? dateformat.format(new Date()): req.getParameter("timestamp");
            payment_time = new Timestamp(dateformat.parse(date).getTime());

            // set the id of the order as the resource in the log context
            LogContext.setResource(req.getParameter("id_order"));
            // set the id of the user as the resource in the log context
            LogContext.setResource(req.getParameter("id_user"));

            new PayOrderDAO(getConnection(), id_order, id_user, payment_time).access();

        } catch (NumberFormatException ex) {
            if (id_order < 1) {
                m = new Message("Invalid order ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid order ID.", ex);
            } else if (id_user == -1) {
                m = new Message("Invalid user ID.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid user ID.", ex);
            } else {
                m = new Message("Invalid input provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid input provided.", ex);
            }
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (ParseException ex) {
            m = new Message("Invalid timestamp provided.", "E4B2", ex.getMessage());
            LOGGER.error("Cannot change Order: invalid timestamp provided.", ex);
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (SQLException ex) {
            m = new Message("Cannot change Order: unexpected error while accessing the database.", "E5A1", ex.getMessage());
            LOGGER.error("Cannot change Order: unexpected error while accessing the database.", ex);
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        }

        try {
            m = new Message(String.valueOf(id_order));
            req.setAttribute("confirmation",m);
            req.setAttribute("payment",true);
            // redirects user to cashiers list page
            //res.sendRedirect(String.format("%s/orders",req.getContextPath()));
            getOrder(req, res, false);
        } catch (Exception ex) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when updating ORDER %d.", id_order), ex);
            throw ex;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    private void getOrderPDF(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        Message m=null;
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SHOW_ORDERS);

        int id_order = -1;
        int id_sagra = -1;

        try {
            // Check for Order id
            id_order = Integer.parseInt(req.getParameter("IDorder") == null || req.getParameter("IDorder").equals("")? "-1" : req.getParameter("IDorder"));
            if (id_order < 1) {
                m = new Message("Id order cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot change Order: invalid order ID.");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
            }

            // Get the ID from session
            id_sagra = checkSession_getSagraId(req,"GET");
            LOGGER.info("Authorized access to order page of sagra %d",id_sagra);

            // set the id of the order as the resource in the log context
            LogContext.setResource(req.getParameter("id_order"));

            // generate PDF with order information
            ByteArrayOutputStream pdfStream = generateOrderPDF(id_sagra, id_order);
            byte[] pdfBytes = pdfStream.toByteArray();
            pdfStream.close();

            // set response headers
            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition", "attachment;filename=order-" + id_order + ".pdf");
            res.setContentLength(pdfBytes.length);

            // write PDF to response output stream
            ServletOutputStream outputStream = res.getOutputStream();
            outputStream.write(pdfBytes);
            outputStream.flush();
            outputStream.close();

        } catch (NumberFormatException ex) {
            if (id_order < 1) {
                m = new Message("Invalid order ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid order ID.", ex);
            } else {
                m = new Message("Invalid input provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid input provided.", ex);
            }
            req.setAttribute("message", m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (Exception ex) {
            m = new Message("Error generating PDF for the order", "E5A1", ex.getMessage());
            LOGGER.error("Error generating PDF for order " + id_order, ex);
            req.setAttribute("message", m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        }
    }

    private ByteArrayOutputStream generateOrderPDF(int sagraId, int orderId) throws Exception {
        List<Order> orderList = new SearchOrderDAO(getConnection(), sagraId, orderId, true, null, null, null, null).access().getOutputParam();
        if (orderList.size() != 1) {
            throw new Exception("Error generating PDF for order " + orderId + "");
        }
        Order order = orderList.get(0);

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, pdfStream);
        document.open();
        //make a title larger

        document.add(new Paragraph("Order information:",new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD)));
        document.add(new Paragraph("Client name: " + order.getClientName()));
        document.add(new Paragraph("Email: " + order.getEmail()));
        document.add(new Paragraph("Client number: " + order.getClientNum()));
        document.add(new Paragraph("Table number: " + order.getTableNumber()));
        document.add(new Paragraph("User ID: " + order.getIdUser()));
        document.add(new Paragraph("Order time: " + order.getOrderTime()));
        document.add(new Paragraph("Payment time: " + order.getPaymentTime()));
        document.add(new Paragraph("Order Content: "));
        //subparagraph with loop of the order content
        Paragraph subPara = new Paragraph();
        subPara.setIndentationLeft(20);
        //process the order content
        List<OrderContent> orderContentList = order.getOrderContent();
        LOGGER.info("Order content size: %d",orderContentList.size());
        String orderContent = "";
        for (OrderContent orderContentItem : orderContentList) {
            orderContent = String.format("%s%s x %s\n",orderContent,orderContentItem.getProductName(),orderContentItem.getQuantity());
        }
        subPara.add(orderContent);
        document.add(subPara);

        document.close();
        return pdfStream;
    }

    private void unPayOrder(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        Message m=null;
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CHANGE_ORDER);

        int id_order = -1;
        int id_sagra = -1;

        try {
            // Check for Order id
            id_order = Integer.parseInt(req.getParameter("IDorder") == null || req.getParameter("IDorder").equals("")? "-1" : req.getParameter("IDorder"));
            if (id_order < 1) {
                m = new Message("Id order cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot unPay Order: invalid order ID.");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
            }

            // Get the ID from session
            id_sagra = checkSession_getSagraId(req,"DELETE");
            LOGGER.info("Authorized access to order page of sagra %d",id_sagra);

            // set the id of the order as the resource in the log context
            LogContext.setResource(req.getParameter("id_order"));

            new UnPayOrderDAO(getConnection(), id_order).access();


        } catch (NumberFormatException ex) {
            if (id_order < 1) {
                m = new Message("Invalid order ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid order ID.", ex);
            } else {
                m = new Message("Invalid input provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot change Order: invalid input provided.", ex);
            }
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (SQLException ex) {
            m = new Message("Cannot change Order: unexpected error while accessing the database.", "E5A1", ex.getMessage());
            LOGGER.error("Cannot change Order: unexpected error while accessing the database.", ex);
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        }

        try {
            // redirects user to cashiers list page
            res.sendRedirect(String.format("%s/orders",req.getContextPath()));

        } catch (Exception ex) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when updating ORDER %d.", id_order), ex);
            throw ex;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    private void deleteOrder(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        Message m=null;
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.DELETE_ORDER);

        int id_order = -1;
        int id_sagra=-1;

        try {
            // Check for Order id
            id_order = Integer.parseInt(req.getParameter("IDorder") == null ? "-1" : req.getParameter("IDorder"));
            if (id_order < 1) {
                m = new Message("Id order cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot delete Order: invalid order ID.");
                req.setAttribute("message",m);
                req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
            }
            // Check for Sagra id from session

            // Get the ID from session
            id_sagra = checkSession_getSagraId(req,"DELETE");
            LOGGER.info("Authorized access to order page of sagra %d",id_sagra);

            // set the id of the order as the resource in the log context
            LogContext.setResource(req.getParameter("IDorder"));

            new DeleteOrderDAO(getConnection(), id_order).access();

        } catch (NumberFormatException e) {
            if (id_order < 1) {
                m = new Message("Invalid order ID provided.", "E4B2", e.getMessage());
                LOGGER.error("Cannot delete Order: invalid order ID.", e);
            } else {
                m = new Message("Invalid input provided.", "E4B2", e.getMessage());
                LOGGER.error("Cannot delete Order: invalid input provided.", e);
            }
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        } catch (SQLException e) {
            m = new Message("Cannot delete the specified Order: unexpected error while accessing the database.", "E5A1", e.getMessage());
            LOGGER.error("Cannot delete the specified Order: unexpected error while accessing the database.", e);
            req.setAttribute("message",m);
            req.getRequestDispatcher("/jsp/cashier/show-orders.jsp").forward(req, res);
        }

        try{
            res.sendRedirect(String.format("%s/orders",req.getContextPath()));
        } catch (Exception e) {
            LOGGER.error(new StringFormattedMessage("Unable to send response when deleting the specified ORDER %d.", id_order), e);
            throw e;
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

}
