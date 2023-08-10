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
import it.unipd.dei.sagrone.resource.LogContext;
import it.unipd.dei.sagrone.resource.Message;
import java.awt.datatransfer.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * This servlet is used to insert a new product into the database.
 */

public class InsertProductServlet extends AbstractDatabaseServlet
{

    /**
     * Insert product into the database
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if any error occurs while executing the servlet.
     * @throws IOException      if any error occurs in the client/server communication.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_PRODUCT);


        Product product = null;
        Message m = null;

        List<Product> productList = new ArrayList<>();
        List<Category> categories = null;

        try
        {
          product = parseRequest(req);
          new InsertProductDAO(getConnection(),product).access();
          LOGGER.info("Product %d successfully created in the database.", product.getIdSagra());

        }
        catch (NumberFormatException e)
        {
            m = new Message(String.format("Cannot insert the product %s. Invalid input parameters.",product.getName()),"E5A5","");
            LOGGER.error("Cannot insert the product. Invalid input parameters.", e);
        }
        catch (SQLException e)
        {
            if ("23505".equals(e.getSQLState()))
            {
                m = new Message(String.format("Cannot insert the product: product %s  already exists.",product.getName()),
                        "E5A5","");

                LOGGER.error(new StringFormattedMessage("Cannot insert the product: product %s already exists.",product.getName(), e));
            }
            else
            {
                m = new Message(String.format("Cannot insert the product %s: unexpected error while accessing the database.",product.getName()), "E5A5", "");

                LOGGER.error("Cannot insert the product: unexpected error while accessing the database.", e);
            }
        }
        catch (MimeTypeParseException e)
        {
            m = new Message(String.format("Unsupported MIME media type for product photo. Expected: image/png or image/jpeg."), "E4A4","Bad Request");
            LOGGER.error("Unsupported MIME media type", e);
        }

        try
        {

            int id_sagra_temp = checkSession_getSagraId(req);
            // stores the product and the message as a request attribute
            //req.setAttribute("product", product);
            //req.setAttribute("message", m);

            // forwards the control to the insert-product-result JSP
            //req.getRequestDispatcher("/jsp/admin/insert-product-results.jsp").forward(req, res);
            //req.getRequestDispatcher("/jsp/admin/new-show-products.jsp").forward(req, res);
            //Retrieve all the categories
            categories = new SearchCategoriesDAO(getConnection()).access().getOutputParam();

            //Create a list of all the products of the Sagra.
            for (Category c : categories) {
                productList.addAll(new SearchProductByCategoryDAO(getConnection(), id_sagra_temp, c, false).access().getOutputParam());
            }

            req.setAttribute("productList", productList);

            //req.setAttribute("product", p);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/jsp/admin/new-show-products.jsp").forward(req, res);





        }
        catch (Exception e)
        {
            LOGGER.error(new StringFormattedMessage("Unable to send response after successfuly creation of product %s.",product.getName(), e));
            //throw e;
        }
        finally
        {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }

    }

    /**
     * Parses the HTTP request and returns a new Product from it.
     *
     * @param req the HTTP request.
     *
     * @return the Product object created from the HTTP request.
     *
     * @throws ServletException       if something goes wrong while parsing the request.
     * @throws IOException            if something goes wrong while parsing the request.
     * @throws MimeTypeParseException if something goes wrong while parsing the request.
     */
    private Product parseRequest(HttpServletRequest req) throws ServletException, IOException, MimeTypeParseException
    {
        int id_sagra  = -1;
        String name = null;
        String description = null;
        double price = -1;
        boolean bar = false;
        boolean available = false;
        String category = null;
        byte[] photo = null;
        String photo_type = null;


        id_sagra = checkSession_getSagraId(req);

        for(Part p : req.getParts())
        {
            switch (p.getName())
            {


                case "name":
                    try(InputStream is = p.getInputStream())
                    {
                        name = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    }
                    break;
                case "description":
                    try(InputStream is = p.getInputStream())
                    {
                        description = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    }
                    break;
                case "price":
                    try(InputStream is = p.getInputStream())
                    {
                           price = Double.parseDouble(new String(is.readAllBytes(), StandardCharsets.UTF_8).trim());
                    }
                    break;
                case "bar":
                    try(InputStream is = p.getInputStream())
                    {
                        bar = Boolean.parseBoolean(new String(is.readAllBytes(), StandardCharsets.UTF_8).trim());
                    }
                    break;
                case "available":
                    try(InputStream is = p.getInputStream())
                    {
                        available = Boolean.parseBoolean(new String(is.readAllBytes(), StandardCharsets.UTF_8).trim());
                    }
                    break;
                case "category":
                    try(InputStream is = p.getInputStream())
                    {
                        category = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    }
                    break;
                case "photo":
                    photo_type = p.getContentType();
                    if(!photo_type.equals("application/octet-stream"))
                    {
                        switch (photo_type.toLowerCase().trim()) {
                            case "image/png":
                            case "image/jpeg":
                            case "image/jpg":
                                break;
                            default:
                                LOGGER.error("Unsupported MIME media type %s for product photo.", photo_type);
                                throw new MimeTypeParseException(String.format("Unsupported MIME media type %s for product photo.", photo_type));
                        }

                        try (InputStream is = p.getInputStream())
                        {
                            photo = is.readAllBytes();
                        }
                    }
                    else
                    {
                        photo_type = null;
                    }
                        break;

            }
        }


        //creates a new product from the request
        return new Product(name,id_sagra,description,price,bar,available,category,photo,photo_type);
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
    private int checkSession_getSagraId(HttpServletRequest req) throws ServletException, NumberFormatException
    {
        Integer id_sagra = null;
        final HttpSession session = req.getSession(false);
        if (session == null)
        {
            LOGGER.error("No session. Cannot insert a product.");
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException("No session. Cannot insert a product.");
        }

        final String admin = (String) session.getAttribute(AdminFilter.ADMIN_ATTRIBUTE);
        if (admin == null || admin.isBlank() || admin.isEmpty() )
        {
            LOGGER.error(String.format("Unauthorized attempt to product on session %s.",session.getId()));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("Unauthorized attempt to product on session %s.",session.getId()));
        }

        id_sagra = (Integer) session.getAttribute(AdminFilter.SAGRA_ATTRIBUTE);

        if(id_sagra <1 || id_sagra == null) throw new NumberFormatException();
        return id_sagra;
    }
}
