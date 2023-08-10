
package it.unipd.dei.sagrone.rest;


import it.unipd.dei.sagrone.database.InsertOrderDAO;
import it.unipd.dei.sagrone.resource.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.EOFException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A REST resource for creating an {@link Order}.
 */
public class CreateOrderRR extends AbstractRR {

    /**
     * Creates a new REST resource for creating an {@code Order}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public CreateOrderRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.CREATE_ORDER,req,res,con);

    }

    /**
     * Performs the actual logic needed for creating an order.
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    @Override
    protected void doServe() throws IOException {
        Order finalOrder=null;
        List<OrderContent> content=null;
        Message m = null;

        try{

            final Order o=Order.fromJSON(req.getInputStream());
            LogContext.setResource("order");

            //input validation
            if(o.getClientName() == null || o.getClientNum()<0 || o.getOrderContent()==null){
                LOGGER.error("Invalid arguments passed order.");
                m = new Message("Cannot create the order: wrong attributes passed.", "E4A9", null);
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                m.toJSON(res.getOutputStream());
                return;
            }

            if(o.getEmail() != null){
                Pattern emailPattern = Pattern.compile("[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.]@[a-zA-Z0-9.]+\\.[a-zA-Z0-9.]+");
                Matcher matcher = emailPattern.matcher(o.getEmail());

                if (!matcher.find()){
                    LOGGER.error("Invalid arguments passed order.");
                    m = new Message("Cannot create the order: wrong attributes passed.", "E4A9", null);
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    m.toJSON(res.getOutputStream());
                    return;
                }
            }



            int id_sagra=-1;
            boolean first=true;
            for(OrderContent c: o.getOrderContent()){
                if(first){
                    id_sagra=c.getIdSagra();
                    first=false;
                }
                if(c.getProductName()==null || c.getQuantity()<0 || id_sagra != c.getIdSagra()){
                    LOGGER.error("Invalid arguments passed order.");
                    m = new Message("Cannot create the order: wrong attributes passed.", "E4A9", null);
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    m.toJSON(res.getOutputStream());
                    return;
                }else if (c.getQuantity()==0){
                    //if quantity is 0 there is no need to add the product to the order
                    o.getOrderContent().remove(c);
                }
            }

            if(o.getOrderContent().isEmpty()){
                LOGGER.error("Invalid arguments passed order.");
                m = new Message("Cannot create the order: wrong attributes passed.", "E4A9", null);
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                m.toJSON(res.getOutputStream());
                return;
            }


            finalOrder= new Order(o.getId(),o.getClientName(),o.getEmail(),o.getClientNum(),o.getTableNumber(),-1,new Timestamp(System.currentTimeMillis()),null, o.getOrderContent());

            int id_order=new InsertOrderDAO(con,finalOrder).access().getOutputParam();

            if(id_order<0){
                LOGGER.error("Fatal error while creating the order.");

                m = new Message("Cannot create the order: unexpected error.", "E5A5", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }else{
                final Order returnedOrder=new Order(id_order,finalOrder.getClientName(), finalOrder.getEmail(), finalOrder.getClientNum(), finalOrder.getTableNumber(), finalOrder.getIdUser(), finalOrder.getOrderTime(), finalOrder.getPaymentTime(),finalOrder.getOrderContent());
                LOGGER.info("Order successfully created.");
                res.setStatus(HttpServletResponse.SC_CREATED);
                returnedOrder.toJSON(res.getOutputStream());
            }





        } catch (EOFException ex) {
            LOGGER.warn("Cannot create the order: no Order JSON object found in the request.", ex);

            m = new Message("Cannot create the order: no Order JSON object found in the request.", "E4A8",
                    ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        } catch(SQLException ex ){
            LOGGER.error("Cannot create order: unexpected database error.", ex);

            m = new Message("Cannot create order: unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } catch(Exception ex ){
            LOGGER.error("Cannot create order: something went wrong.", ex);

            m = new Message("Cannot create order: something went wrong.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}
