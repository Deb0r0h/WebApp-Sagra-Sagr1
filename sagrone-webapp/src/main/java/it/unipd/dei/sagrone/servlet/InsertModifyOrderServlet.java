package it.unipd.dei.sagrone.servlet;

import it.unipd.dei.sagrone.database.InsertOrderDAO;
import it.unipd.dei.sagrone.database.SearchCashierDAO;
import it.unipd.dei.sagrone.database.SearchProductByIdDAO;
import it.unipd.dei.sagrone.filter.CashierFilter;
import it.unipd.dei.sagrone.resource.*;
import it.unipd.dei.sagrone.utils.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This servlet is used both for customers and cashiers to insert a new order into database,
 * while it's used only by cashiers to modify existing orders.
 */
@WebServlet(name = "InsertModifyOrderServlet", value = "/neworder")
public class InsertModifyOrderServlet extends AbstractDatabaseServlet{
    /**
     * The function analyzes the incoming requests and adds into the database a new order, in case there is an
     * existing one with the same id, it will substitute it (i.e. order modification performed from a cashier)
     *
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LogContext.setIPAddress(request.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_MODIFY_ORDER_MENU);
        Message msg;

        Integer id_sagra = null;
        Integer id_order = null;
        final Short client_num;
        final String client_name;
        final String email;
        final String table_number;


        final HttpSession session = request.getSession(false);
        try {

            id_sagra = (Integer) session.getAttribute(CashierFilter.SAGRA_ATTRIBUTE);
            id_order = request.getParameter("id_order") != null ?  Integer.parseInt(request.getParameter("id_order")) : -1;

            client_name = request.getParameter("client_name");
            client_num = request.getParameter("client_num") != null ? Short.parseShort(request.getParameter("client_num")) : null;
            email = request.getParameter("email");
            table_number = request.getParameter("table_number");
        }
        catch (NumberFormatException e){
            msg = new Message(
                    "Cannot detect from which sagra the request comes from. Please retry.",
                    ErrorCode.WRONG_INPUT_FIELDS.getErrorCode(),
                    "There was a problem while understanding from which sagra the order was sent."
                    );
            LOGGER.warn("Cannot retrieve the sagra's id. (POST request to: %s from %s)",
                    request.getRequestURI(),
                    request.getRemoteHost());
            request.setAttribute("message",msg);
            LogContext.removeAction();
            LogContext.removeResource();
            LogContext.removeIPAddress();
            if(id_order != null && id_order > -1) {  // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request, response);
                return;
            }else{                                   // Return to the user
                request.getRequestDispatcher("/menu").forward(request, response);
                return;
            }
        }
        catch(IllegalStateException e){
            msg = new Message(
                    "Invalid request",
                    ErrorCode.UNEXPECTED_ERROR.getErrorCode(),
                    "Invalid request"
            );
            LOGGER.warn("Invalid session object during the order info retrieval. (POST request to: %s from %s)",
                    request.getRequestURI(),
                    request.getRemoteHost());
            request.setAttribute("message",msg);
            LogContext.removeAction();
            LogContext.removeResource();
            LogContext.removeIPAddress();
            if(id_order != null && id_order > -1) {  // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request, response);
                return;
            }else{                                   // Return to the user
                request.getRequestDispatcher("/menu").forward(request, response);
                return;
            }
        }
        catch (NullPointerException e){
            msg = new Message(
                    "Something went wrong",
                    ErrorCode.UNEXPECTED_ERROR.getErrorCode(),
                    "Some error occurred while retrieving order infos");
            request.setAttribute("message", msg);
            LOGGER.warn("Fail to retrieve order info. (POST request to: %s from %s)",
                    request.getRequestURI(),
                    request.getRemoteHost());
            LogContext.removeAction();
            LogContext.removeResource();
            LogContext.removeIPAddress();
            if(id_order != null && id_order > -1) {  // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request, response);
                return;
            }else{                                   // Return to the user
                request.getRequestDispatcher("/menu").forward(request, response);
                return;
            }
        }

        String[] products = request.getParameterValues("products");
        String[] quantities = request.getParameterValues("quantity");

        if(products == null || quantities == null || products.length != quantities.length || products.length == 0){
            msg = new Message(
                    "Some error while retrieving the order",
                    ErrorCode.WRONG_INPUT_FIELDS.getErrorCode(),
                    "There data received is invalid"
            );
            StringBuilder sb = new StringBuilder();
            sb.append("Products: ");
            if(products != null){ for (String s: products) sb.append(s).append(" "); } else sb.append("null ");
            sb.append("\nQuantities: ");
            if(quantities != null){ for (String s: quantities) sb.append(s).append(" "); } else sb.append("null ");
            LOGGER.warn(
                    "Invalid lists of products/quantities provided with the request from %s. %s",
                    request.getRemoteHost(), sb.toString());
            request.setAttribute("message",msg);
            if(id_order > -1)   // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request,response);
            else                                    // Return to the user
                request.getRequestDispatcher("/menu").forward(request,response);
            return;
        }

        // At this point is_sagra, id_order, products and quantities should be correct

        try{
            // Get cashier id from session and check if it's allowed to perform modification on orders
            final String cashier = (String) session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE);
            final List<User> cashiers = new SearchCashierDAO(getConnection(), cashier, id_sagra).access().getOutputParam();
            if((cashier == null || cashier.isBlank() || cashiers.isEmpty()) && id_order > -1){
                // Someone who is not a valid cashier is tring to modify an order
                msg = new Message(
                        "Can't recognize the cashier",
                        ErrorCode.UNAUTHORIZED_ACCESS.getErrorCode(),
                        "Can't authenticate the cashier"
                );
                request.setAttribute("message", msg);
                LOGGER.warn("Unauthorized request, cashier id %s not found for sagra %d. id order: %d",
                        cashier,
                        id_sagra,
                        id_order);
                request.getRequestDispatcher("/orders").forward(request,response);
                return;
            }
            // Cashier verified!

            // Email check
            if(email != null){
                Pattern emailPattern = Pattern.compile("[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.]@[a-zA-Z0-9.]+\\.[a-zA-Z0-9.]+");
                Matcher matcher = emailPattern.matcher(email);

                if (!matcher.find()) throw new AssertionError();
            }

            if(client_num == null) throw new NullPointerException();

            List<OrderContent> order_content = new ArrayList<>();

            double price;
            double tot = 0;
            int quantity;

            for (int i=0; i<products.length; i++) {
                if((quantity = Short.parseShort(quantities[i])) < 0) throw new NumberFormatException();
                // Get main info about one order content
                price = new SearchProductByIdDAO(getConnection(), id_sagra, products[i]).access().getOutputParam().getPrice();
                tot += quantity * price;
                // Add the order content into the list
                order_content.add(new OrderContent(id_sagra, -1, products[i], price, Short.parseShort(quantities[i])));
            }
            Order newOrder = new Order(
                    id_order,               // either -1 or a valid order id
                    client_name,
                    email,
                    client_num,
                    table_number,
                    -1,
                    new Timestamp(System.currentTimeMillis()),
                    null,
                    order_content);

            final int generated_order_id = new InsertOrderDAO(getConnection(), newOrder).access().getOutputParam();
            if(generated_order_id == -1)
                throw new Exception();      // It should not be here since an order is always inserted into DB

            if(id_order < 0){
                // New order inserted by a client
                msg = new Message("Order correctly inserted");
                request.setAttribute("message", msg);
                request.setAttribute("generated_order_id", generated_order_id);
                request.setAttribute("total_price", tot);
                LOGGER.info("Order correctly inserted. Order ID: %d", generated_order_id);

                // forwards the control to the inserted-order JSP
                request.getRequestDispatcher("/jsp/inserted-order.jsp").forward(request, response);
                return;
            }
            else{
                // The order is sent by the cashier page
                msg = new Message("Order correctly modified");
                request.setAttribute("message", msg);
                LOGGER.info("Order correctly modified. Order ID: %d", id_order);

                // forwards the control to the inserted-order JSP
                response.sendRedirect(String.format("%s/orders",request.getContextPath()));
                //request.getRequestDispatcher("/orders").forward(request, response);
                return;
            }
        }
        catch(AssertionError err){
            msg = new Message("Invalid email");
            LOGGER.info("Wrong email inserted for the new order");
            request.setAttribute("message",msg);
            if(id_order > -1)                       // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request,response);
            else                                    // Return to the user
                request.getRequestDispatcher("/menu").forward(request,response);
            return;
        }
        catch (NullPointerException err){
            msg = new Message(
                    "The number of people must be specified");
            request.setAttribute("message", msg);
            LOGGER.error("Null pointer for client_num detected while inserting a new order");
            if(id_order > -1)                       // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request,response);
            else                                    // Return to the user
                request.getRequestDispatcher("/menu").forward(request,response);
            return;
        }
        catch (NumberFormatException e){
            msg = new Message("The number of the item selected can't be less than 0");
            request.setAttribute("message", msg);
            StringBuilder sb = new StringBuilder();
            sb.append("Products: ");
            for (String s: products) sb.append(s).append(" ");
            sb.append("\nQuantities: ");
            for (String s: quantities) sb.append(s).append(" ");
            LOGGER.info(
                    "Invalid quantity received. %s",
                    sb.toString()
                    );
            if(id_order > -1)                       // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request,response);
            else                                    // Return to the user
                request.getRequestDispatcher("/menu").forward(request,response);
            return;
        }
        catch (SQLException e){
            msg = new Message(
                    "Something went wrong while inserting the order",
                    ErrorCode.UNEXPECTED_ERROR.getErrorCode(),
                    "Some internal error occurred");
            request.setAttribute("message", msg);
            LOGGER.warn("Database error while inserting a new order or modifying an existing one. Sagra id: %s, Order id: %s, Products: %s",
                    id_sagra,
                    id_order,
                    products);
            if(id_order > -1)                       // Return to the cashier
                request.getRequestDispatcher("/orders").forward(request,response);
            else                                    // Return to the user
                request.getRequestDispatcher("/menu").forward(request,response);
            return;
        }
        catch (Exception e) {
            msg = new Message(
              "Unable to add a new order, something went wrong",
                    ErrorCode.UNEXPECTED_ERROR.getErrorCode(),
                    "The insertion of a new order failed"
            );
            request.setAttribute("message", msg);
            LOGGER.error("An exception occurred while inserting a new order. Can't get connection");
            request.getRequestDispatcher("/menu").forward(request,response);
            return;
        }
        finally {
            LogContext.removeAction();
            LogContext.removeResource();
            LogContext.removeIPAddress();

        }
    }
}











