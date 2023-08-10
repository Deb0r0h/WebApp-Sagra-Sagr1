
package it.unipd.dei.sagrone.rest;


import it.unipd.dei.sagrone.database.SearchCategoriesWithAvailableProductsDAO;
import it.unipd.dei.sagrone.resource.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for listing {@link Category}(ies) with at least one available product for the given sagra.
 */
public class ListCategoriesAvailableRR extends AbstractRR{

    /**
     * Creates a new REST resource for listing {@code Category}(ies) with at least one available product for the given sagra.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ListCategoriesAvailableRR(final HttpServletRequest req, HttpServletResponse res, Connection con){
        super(Actions.LIST_CATEGORIES, req,res,con);
    }


    /**
     * Performs the actual logic needed to list the categories with at least one available product.
     *
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    @Override
    protected void doServe() throws IOException {
        Message m=null;
        List<Category> categories=null;
        int id_sagra=-1;


        try {

            //retrieving the id of the sagra from the URI
            String path= req.getRequestURI();
            path=path.substring(path.lastIndexOf("available") +9);

            id_sagra=Integer.parseInt(path.substring(1));

            LogContext.setResource(String.format("categories: %d",id_sagra));

            // creates a new DAO for accessing the database and lists the products
            categories = new SearchCategoriesWithAvailableProductsDAO(con,id_sagra).access().getOutputParam();

            if (categories != null) {
                LOGGER.info("Category(ies) successfully listed.");

                res.setStatus(HttpServletResponse.SC_OK);
                new ResourceList(categories).toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while listing category(ies) with at least one available product.");
                m = new Message("Cannot list category(ies) with at least one available product: unexpected error.", "E5A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list category(ies) with at least one available product: unexpected database error.", ex);

            m = new Message("Cannot list category(ies) with at least one available product: unexpected database error.", "E5A1", ex.getMessage()); res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } catch(IndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
            LOGGER.warn("Cannot list the categories: wrong format for URI /categories/available/{category}.", ex);

            m = new Message("Cannot list the categories: wrong format for URI /categories/available/{category}.", "E4A7",
                    ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        }
    }
}
