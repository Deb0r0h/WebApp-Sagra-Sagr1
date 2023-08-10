
package it.unipd.dei.sagrone.rest;


import it.unipd.dei.sagrone.database.SearchProductByCategoryDAO;
import it.unipd.dei.sagrone.resource.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST resource for listing {@link Product}s of a given category and a given sagra.
 */
public class ListProductByCategoryRR extends AbstractRR{


    /**
     * Creates a new REST resource for listing {@code Product}s of a given category and a given sagra.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ListProductByCategoryRR(final HttpServletRequest req, HttpServletResponse res, Connection con){
        super(Actions.LIST_PRODUCT, req,res,con);
    }


    @Override
    protected void doServe() throws IOException {
        List<Product> pl=null;
        String category=null;
        Message m=null;
        int id_sagra=-1;


        try {

            //retrieving the category and the id of the sagra from the URI
            String path= req.getRequestURI();
            path=path.substring(path.lastIndexOf("category") +8);

            path=path.substring(1);

            String[] splitted= path.split("/");

            category=splitted[0];
            category=category.replaceAll("_"," ");

            id_sagra=Integer.parseInt(splitted[1]);

            LogContext.setResource(String.format("%s %d",category,id_sagra));

            //retrieving products for the given category
            pl=new SearchProductByCategoryDAO(con,id_sagra,new Category(category),true).access().getOutputParam();

            LOGGER.info("Product(s) successfully listed.");
            res.setStatus(HttpServletResponse.SC_OK);

            if (pl != null) {
                List<Product> productsWithPhotoPath=new ArrayList<>();
                for(Product p: pl){
                    if(p.hasPhoto()){

                        String photoPath= String.format("%s://%s:%d%s/load-product-photo/?name=%s&sagra=%d",req.getScheme(),req.getServerName(),req.getServerPort(),req.getContextPath(),p.getName().replaceAll(" ","+"),p.getIdSagra());
                        productsWithPhotoPath.add(new Product(p.getName(),p.getIdSagra(),p.getDescription(),p.getPrice(),p.getBar(),p.getAvailable(),p.getCategory(),p.getPhoto(),p.getPhotoType(),photoPath));
                    }else{
                        productsWithPhotoPath.add(p);
                    }
                }
                new ResourceList<Product>(productsWithPhotoPath).toJSON(res.getOutputStream());
            }else {
                new ResourceList<Product>(new ArrayList<Product>()).toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list products(s): unexpected database error.", ex);

            m = new Message("Cannot list products(s): unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } catch(IndexOutOfBoundsException | NullPointerException |NumberFormatException ex) {
            LOGGER.warn("Cannot list the products: wrong format for URI /product/category/{category}/{sagra}.", ex);

            m = new Message("Cannot list the products: wrong format for URI /product/category/{category}/{sagra}.", "E4A7",
                    ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        }
    }
}
