package it.unipd.dei.sagrone.servlet;

import it.unipd.dei.sagrone.database.*;
import it.unipd.dei.sagrone.filter.AdminFilter;
import it.unipd.dei.sagrone.resource.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.json.JSONObject;

import java.awt.datatransfer.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Show the list of the products that belong to a sagra and manages the update and elimination of a product.
 */

public class ProductServlet extends AbstractDatabaseServlet {

    /**
     *  Message generated during operations on data
     */
    Message m = null;

    /**
     * //Show the list of products that belong to a specific sagra
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SHOW_PRODUCTS);

        //request parameter
        int id_sagra = -1;

        //List of product
        List<Product> product_list = new ArrayList<>();

        //List of categories
        List<Category> categories = null;

        String requestURI = req.getRequestURI();

        try {

            // retrieve Sagra ID from session
            id_sagra = checkSession_getSagraId(req,"List of products");

            //check if the id sagra exists
            if ((Integer) id_sagra == null) {
                id_sagra = -1;
            }
            if (id_sagra == -1) {
                m=new Message("Id sagra cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot list/query Products: invalid sagra ID.");
            } else {

                // set the id of the sagra as the resource in the log context
                LogContext.setResource(String.valueOf(id_sagra));

                //Retrieve all the categories
                categories = new SearchCategoriesDAO(getConnection()).access().getOutputParam();

                //Create a list of all the products of the Sagra.
                for (Category c : categories) {
                    product_list.addAll(new SearchProductByCategoryDAO(getConnection(), id_sagra, c, false).access().getOutputParam());
                }

                req.setAttribute("productList", product_list);
                req.setAttribute("message", m);

                req.getRequestDispatcher("/jsp/admin/new-show-products.jsp").forward(req, res);

            }
        } catch (NumberFormatException ex) {
            if (id_sagra == -1) {
                m=new Message("Invalid sagra ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot list/query Product: invalid sagra ID.", ex);
            } else {
                m=new Message("Invalid order ID provided.", "E4B2", ex.getMessage());
                LOGGER.error("Cannot list/query Product: invalid order ID.", ex);
            }
        } catch (SQLException ex) {
            m=new Message("Cannot list product: unexpected error while accessing the database.", "E5A1", ex.getMessage());
            LOGGER.error("Cannot list product: unexpected error while accessing the database.", ex);
        }

    }


    /**
     * //Based on the action received, update or delete a product from the database
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        String op1 = req.getRequestURI();

        String op = op1.substring(op1.indexOf("/", 1));

        switch (op) {

            case "/seeprod/update":
                updateProduct(req, res);
                break;
            case "/seeprod/delete":
                deleteProduct(req, res);
                break;
        }
    }

    /**
     * //Update the product from the database
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    void updateProduct(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SHOW_PRODUCTS);

        //The product that has to be modified
        Product oldp=null;
        int oldId_sagra=-1;
        String oldName=null;

        //New product
        Product p = null;

        //List of product
        List<Product> product_list = new ArrayList<>();

        //List of categories
        List<Category> categories = null;

        byte[] oldPhoto=null;
        String oldPhotoType=null;

        try {

            //Retrieves the parameters of the old product
            //Retrieve id sagra from session
            oldId_sagra = checkSession_getSagraId(req,"Update");
            oldName=parseOldNameProduct(req);

            //check for the old product id sagra
            if (oldId_sagra == -1) {
                m = new Message("Old Id sagra cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot update Product: invalid old sagra ID.");
            }

            //check for the old product name
            if (oldName == null || oldName.trim().isEmpty()) {
                m = new Message("old name cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot update Product: invalid old name.");
            }

            LogContext.setResource(Integer.toString(oldId_sagra));
            LogContext.setResource(oldName);

            //Retrieve the old product
            oldp=new SearchProductByIdDAO(getConnection(),oldId_sagra,oldName).access().getOutputParam();

            //Retrieve all the categories
            categories = new SearchCategoriesDAO(getConnection()).access().getOutputParam();

            //Create a list of all the products of the Sagra.
            for (Category c : categories) {
                product_list.addAll(new SearchProductByCategoryDAO(getConnection(), oldId_sagra, c, false).access().getOutputParam());
            }

            //check if the old product exists
            if (oldp== null){

                m = new Message("Invalid product name ", "E4B2", "Bad Request");
                LOGGER.error("The name of the searched product is not valid.");
            }else{

                for( Product pr : product_list){

                    if(pr.getName().equals(oldName)){
                        oldPhoto=pr.getPhoto();
                        oldPhotoType=pr.getPhotoType();

                    }
                }
                //Creation of the update product
                p = parseUpdateRequest(req, oldPhoto,oldPhotoType);

                if (p.getIdSagra() ==-1) {
                    m = new Message("Invalid Id sagra ", "E4B2", "Bad Request");
                    LOGGER.error("Cannot update Product: invalid sagra ID.");
                }
                //check for product name
                else if (p.getName() == null || p.getName().trim().isEmpty()) {
                    m = new Message("Name cannot be null", "E4B1", "Bad Request");
                    LOGGER.error("Cannot update Product: invalid name.");
                }
                //check for product category
                else if (p.getCategory() == null) {
                    m = new Message("Category cannot be null", "E4B1", "Bad Request");
                    LOGGER.error("Cannot update Product: invalid category.");
                }
                else{

                    //Update of the product
                    new UpdateProductDAO(getConnection(), p,oldName,oldId_sagra).access();

                    m = new Message(String.format("Product %s successfully updated.", p.getName()));
                    LOGGER.info("Product %s successfully updated.", p.getName());
                }
            }

        }catch (MimeTypeParseException ex) {
            m=new Message("Invalid image type provided.", "E4A4", ex.getMessage());
            LOGGER.error("Invalid data provided.", ex);
        }catch(NumberFormatException e)
        {
            m=new Message("Invalid Product specified: invalid field", "E4A8", e.getMessage());
            LOGGER.error("Invalid Product specified: invalid field ", e);
        }catch(SQLException e)
        {
            m=new Message("Cannot update the specified Product: unexpected error while accessing the database.", "E200", e.getMessage());
            LOGGER.error("Cannot update the specified Product: unexpected error while accessing the database.", e);
        }catch(IllegalArgumentException e)
        {
            m=new Message("Invalid data provided.", "E4A8", e.getMessage());
            LOGGER.error("Invalid data provided.", e);
        }
        try{

            product_list.clear();

            //Create a list of all the products of the Sagra.
            for (Category c : categories) {
                product_list.addAll(new SearchProductByCategoryDAO(getConnection(), oldId_sagra, c, false).access().getOutputParam());
            }

            req.setAttribute("productList", product_list);

            req.setAttribute("message", m);

            req.getRequestDispatcher("/jsp/admin/new-show-products.jsp").forward(req, res);



        }catch (Exception ex) {
            LOGGER.error(
                    new StringFormattedMessage("Unable to send response after successfuly update of product %s.",
                            p.getName()), ex);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * //Delete product from the database
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    void deleteProduct(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.DELETE_PRODUCT);

        //The product that has to be modified
        Product oldp=null;
        int oldId_sagra=-1;
        String oldName=null;

        //List of product
        List<Product> product_list = new ArrayList<>();

        //List of categories
        List<Category> categories = null;

        try {

            //Retrieves the parameters of the old product
            //Retrieve id sagra from session
            oldId_sagra = checkSession_getSagraId(req,"Delete");
            oldName=parseOldNameProduct(req);

            //check for the old product id sagra
            if (oldId_sagra == -1) {
                m = new Message("Old Id sagra cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot delete Product: invalid old sagra ID.");
            }

            //check for the old product name
            if (oldName == null || oldName.trim().isEmpty()) {
                m = new Message("old name cannot be null", "E4B1", "Bad Request");
                LOGGER.error("Cannot delete Product: invalid old name.");
            }


            LogContext.setResource(Integer.toString(oldId_sagra));
            LogContext.setResource(oldName);

            //Retrieve the product to be deleted
            oldp=new SearchProductByIdDAO(getConnection(),oldId_sagra,oldName).access().getOutputParam();

            //check if the product exists
            if (oldp== null){

                m = new Message("Invalid product name ", "E4B2", "Bad Request");
                LOGGER.error("The name of the searched product is not valid.");
            }else{

                //Delete of the product
                new DeleteProductDAO(getConnection(),oldName,oldId_sagra).access();
                m=new Message(String.format("Product %s successfully deleted.", oldName));
                LOGGER.info("Product %s successfully deleted.", oldId_sagra);
            }

        }catch(SQLException e)
        {
            m=new Message("Cannot delete the specified Product: unexpected error while accessing the database.", "E5A1", e.getMessage());
            LOGGER.error("Cannot delete the specified Product: unexpected error while accessing the database.", e);
        }catch(NumberFormatException e)
        {
            m=new Message("Invalid Product specified: id_sagra must be integer.", "E4B2", e.getMessage());
            LOGGER.error("Invalid user specified: id_sagra must be integer.", e);
        }catch(IllegalArgumentException e)
        {
            m=new Message("Invalid data provided.", "E4B2", e.getMessage());
            LOGGER.error("Invalid data provided.", e);
        }
        try {

            //Retrieve all the categories
            categories = new SearchCategoriesDAO(getConnection()).access().getOutputParam();

            //Create a list of all the products of the Sagra.
            for (Category c : categories) {
                product_list.addAll(new SearchProductByCategoryDAO(getConnection(), oldId_sagra, c, false).access().getOutputParam());
            }

            req.setAttribute("productList", product_list);

            //req.setAttribute("product", p);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/jsp/admin/new-show-products.jsp").forward(req, res);


        }
        catch (Exception ex) {
            LOGGER.error(
                    new StringFormattedMessage("Unable to send response after successfuly deleting a product %s.",
                            oldp.getName()), ex);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }

    }

    /**
     * Parses the HTTP request and returns a list contain the name and the id sagra of the old product
     *
     * @param req the HTTP request.
     *
     * @return the list of the old product parameter created from the HTTP request.
     *
     * @throws ServletException       if something goes wrong while parsing the request.
     * @throws IOException            if something goes wrong while parsing the request.
     * @throws MimeTypeParseException if something goes wrong while parsing the request.
     */
    private String parseOldNameProduct(HttpServletRequest req) throws ServletException, IOException {

        String name = "";
        Part oldNamePart = req.getPart("Oldname");

        try (InputStream is = oldNamePart.getInputStream()) {
            name = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

            LOGGER.info("element: %s", name);
        }

        return name;
    }

    /**
     * Parses the HTTP request and returns a new {@code Product} from it.
     *
     * @param req the HTTP request.
     * @return the {@code Product} object created from the HTTP request.
     *
     * @throws ServletException       if something goes wrong while parsing the request.
     * @throws IOException            if something goes wrong while parsing the request.
     * @throws MimeTypeParseException if something goes wrong while parsing the request.
     */
    private Product parseUpdateRequest(HttpServletRequest req, byte[] oldphoto, String oldphototype) throws ServletException, IOException, MimeTypeParseException {

        // request parameters
        int id_sagra=-1;
        String name="";
        String description = null;
        double price = -1;
        boolean bar = false;
        boolean available = false;
        String category = null;
        byte[] photo = null;
        String photo_type = null;

        id_sagra = checkSession_getSagraId(req,"List of products");

        // retrieves the request parameters
        //if the parameter is null and empty then the parameter of the old product is kept
        for (Part p : req.getParts()) {

            switch (p.getName()) {

                case "name":

                    try (InputStream is = p.getInputStream()) {

                        String param= new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

                        if(param!=null && !param.isEmpty()){
                            name = param;
                        }

                        LOGGER.info("element: %s", name);
                    }
                    break;

                case "description":
                    try (InputStream is = p.getInputStream()) {

                        String param= new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

                        if(param!=null && !param.isEmpty()){
                            description = param;
                        }

                        LOGGER.info("element: %s", description);
                    }
                    break;

                case "price":
                    try (InputStream is = p.getInputStream()) {

                        String param= new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

                        if(param!=null && !param.isEmpty()){

                            price = Double.parseDouble(param);
                        }

                        LOGGER.info("element: %s", price);
                    }
                    break;

                case "bar":
                    try (InputStream is = p.getInputStream()) {

                        String param= new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

                        if(param!=null && !param.isEmpty()){
                            bar = Boolean.parseBoolean(param);
                        }

                        LOGGER.info("element bar: %s", bar);
                    }
                    break;

                case "available":
                    try (InputStream is = p.getInputStream()) {

                        String param= new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

                        if(param!=null && !param.isEmpty()){
                            available = Boolean.parseBoolean(param);
                        }

                        LOGGER.info("element av: %s", available);
                    }
                    break;

                case "category":
                    try (InputStream is = p.getInputStream()) {

                        String param= new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

                        if(param!=null && !param.isEmpty()){
                            category = param;
                        }

                        LOGGER.info("element: %s", category);
                    }
                    break;

                case "photo":
                    photo_type = p.getContentType();

                    //check if the photo is provided
                    if (!photo_type.equals("application/octet-stream")) {
                        switch (photo_type.toLowerCase().trim()) {

                            case "image/png":
                            case "image/jpeg":
                            case "image/jpg":
                                // nothing to do
                                break;

                            default:
                                LOGGER.error("Unsupported MIME media type %s for product photo.", photo_type);

                                throw new MimeTypeParseException(
                                        String.format("Unsupported MIME media type %s for product photo.",
                                                photo_type));
                        }

                        try (InputStream is = p.getInputStream()) {
                            photo = is.readAllBytes();

                        }
                    }
                    else{

                        photo= oldphoto;
                        photo_type=oldphototype;
                    }
                        break;

            }

        }

        // creates a new product from the request parameters
        return new Product( name,id_sagra,description,price,bar,available,category,photo,photo_type);
    }
    /**
     * Check and get the id_sagra in the current session
     *
     * @param req the HTTP request.
     *
     * @return the id_sagra in the session.
     *
     * @throws ServletException         if something goes wrong while getting the session and id_sagra.
     * @throws NumberFormatException    if something goes wrong while getting the session and id_sagra.
     */
    int checkSession_getSagraId(HttpServletRequest req, String operation) throws ServletException, NumberFormatException{

        final HttpSession session = req.getSession(false);
        Integer id_sagra = null;

        // it should not happen here
        if (session == null) {
            LOGGER.error(String.format("No session ",operation));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("No session ",operation));
        }

        final String admin = (String) session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE);

        // it should not happen here
        if (admin == null || admin.isBlank() || admin.isEmpty() ) {
            LOGGER.error(String.format("No admin in session %s.", operation, session.getId()));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("No admin in session %s.", operation, session.getId()));
        }

        id_sagra = (Integer) session.getAttribute(AdminFilter.SAGRA_ATTRIBUTE);

        if(id_sagra == null || id_sagra<1) throw new NumberFormatException();
        return id_sagra;
    }

}


