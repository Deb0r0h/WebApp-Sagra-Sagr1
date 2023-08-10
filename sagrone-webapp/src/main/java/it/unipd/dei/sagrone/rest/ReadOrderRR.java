
package it.unipd.dei.sagrone.rest;



import it.unipd.dei.sagrone.database.SearchOrderDAO;

import it.unipd.dei.sagrone.resource.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST resource for listing an {@link Order}.
 */
public class ReadOrderRR extends AbstractRR{

    /**
     * Creates a new REST resource for listing an {@link Order}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ReadOrderRR(final HttpServletRequest req, HttpServletResponse res, Connection con){
        super(Actions.LIST_PRODUCT, req,res,con);
    }


    /**
     * Performs the actual logic needed for retrieving the {@link Order}.
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    @Override
    protected void doServe() throws IOException {
        List<Order> ol=null;
        Message m=null;
        int id_sagra=-1;
        int id_order=-1;


        try {

            //retrieving the id of the sagra and the id of the order from the URI
            String path= req.getRequestURI();
            path=path.substring(path.lastIndexOf("order") +5);
            path=path.substring(1);

            String[] splitted= path.split("/");

            id_order=Integer.parseInt(splitted[0]);
            id_sagra=Integer.parseInt(splitted[1]);


            ol=new SearchOrderDAO(con,id_sagra,id_order).access().getOutputParam();

            if (ol != null && ol.size() == 1) {
                LOGGER.info("Order successfully retrieved.");

                res.setStatus(HttpServletResponse.SC_OK);
                ol.get(0).toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while retrieving order.");

                m = new Message("Cannot retrieve order: unexpected error.", "E5A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot retrieve order: unexpected database error.", ex);

            m = new Message("Cannot retrieve order: unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } catch(IndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
            LOGGER.warn("Cannot retrieve order: wrong format for URI /order/{order}/{sagra}.", ex);

            m = new Message("Cannot retrieve order: wrong format for URI /product/{order}/{sagra}.", "E4A7",
                    ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        }


    }
}
