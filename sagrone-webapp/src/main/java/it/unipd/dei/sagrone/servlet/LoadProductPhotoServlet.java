

package it.unipd.dei.sagrone.servlet;


import it.unipd.dei.sagrone.database.LoadProductPhotoDAO;
import it.unipd.dei.sagrone.resource.Actions;
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Loads the photo of a product.
 */
public final class LoadProductPhotoServlet extends AbstractDatabaseServlet {

    /**
     * Loads the photo of a product.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     *
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.LOAD_PRODUCT_PHOTO);

        // request parameters
        int id_sagra = -1;
        String name = null;

        // model
        Product p = null;

        try {

            // retrieves the request parameter
            id_sagra = Integer.parseInt(req.getParameter("sagra"));
            name=req.getParameter("name");

            LogContext.setResource(String.format("%s : %s",req.getParameter("sagra"),req.getParameter("name")));

            // creates a new object for accessing the database and loading the photo of an employees
            p = new LoadProductPhotoDAO(getConnection(), id_sagra, name).access().getOutputParam();



            if (p.hasPhoto()) {
                res.setContentType(p.getPhotoType());
                res.getOutputStream().write(p.getPhoto());
                res.getOutputStream().flush();

                LOGGER.info("Photo for product %s successfully sent.", name);
            } else {
                LOGGER.info("Product %s of sagra %d has no profile photo and/or valide MIME media type specified.", name, id_sagra);

                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (Exception ex) {
            LOGGER.error("Unable to load the photo of the product.", ex);

            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeUser();
        }

    }

}
