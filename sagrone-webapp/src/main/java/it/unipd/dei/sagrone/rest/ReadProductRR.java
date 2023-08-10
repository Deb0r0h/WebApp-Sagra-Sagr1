
package it.unipd.dei.sagrone.rest;

import it.unipd.dei.sagrone.database.SearchProductByIdDAO;

import it.unipd.dei.sagrone.resource.Actions;
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Message;
import it.unipd.dei.sagrone.resource.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST resource for listing a {@link Product} given the sagra and its name.
 */
public class ReadProductRR extends AbstractRR{
    /**
     * Creates a new REST resource for listing a {@link Product} given the sagra and its name.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ReadProductRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.READ_PRODUCT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        Product p = null;
        Message m = null;
        String name=null;
        int id_sagra=-1;



        try {


            //retrieving the name of the product and the id of the sagra from the URI
            String path= req.getRequestURI();
            path=path.substring(path.lastIndexOf("product") +7);

            path=path.substring(1);

            String[] splitted= path.split("/");

            name=splitted[0];

            name=name.replaceAll("_"," ");

            id_sagra=Integer.parseInt(splitted[1]);

            LogContext.setResource(String.format("%s %d",name,id_sagra));

            // creates a new DAO for accessing the database and reads the product
            p = new SearchProductByIdDAO(con, id_sagra, name).access().getOutputParam();

            if (p != null) {
                LOGGER.info("Product successfully read.");

                res.setStatus(HttpServletResponse.SC_OK);
                Product productWithPhotoPath=null;

                if(p.hasPhoto()){
                    String photoPath= String.format("%s://%s:%d%s/load-product-photo/?name=%s&sagra=%d",req.getScheme(),req.getServerName(),req.getServerPort(),req.getContextPath(),p.getName().replaceAll(" ","+"),p.getIdSagra());
                    productWithPhotoPath=new Product(p.getName(),p.getIdSagra(),p.getDescription(),p.getPrice(),p.getBar(),p.getAvailable(),p.getCategory(),p.getPhoto(),p.getPhotoType(),photoPath);
                }else{
                    productWithPhotoPath=p;
                }

                productWithPhotoPath.toJSON(res.getOutputStream());
            } else {
                LOGGER.warn("Product not found. Cannot read it.");

                m = new Message(String.format("Product %s for sagra %d not found. Cannot read it.", name,id_sagra), "E5A3", null);
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                m.toJSON(res.getOutputStream());
            }
        } catch (IndexOutOfBoundsException | NumberFormatException |NullPointerException ex) {
            LOGGER.warn("Cannot read the product: wrong format for URI /product/{name}/{sagra}.", ex);

            m = new Message("Cannot read the product: wrong format for URI /product/{name}/{sagra}.", "E4A7",
                    ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot read the product: unexpected database error.", ex);

            m = new Message("Cannot read the product: unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }

    }
}
