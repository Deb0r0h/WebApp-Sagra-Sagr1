
package it.unipd.dei.sagrone.servlet;




import com.itextpdf.text.*;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import it.unipd.dei.sagrone.database.SearchAllAvailableProductBySagraDAO;
import it.unipd.dei.sagrone.database.SearchOrderDAO;
import it.unipd.dei.sagrone.database.SearchSagraByIdDAO;
import it.unipd.dei.sagrone.filter.AdminFilter;
import it.unipd.dei.sagrone.filter.CashierFilter;
import it.unipd.dei.sagrone.resource.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Sends to the client (downloads) the summary of an order.
 * In particular this class is intended to be used by a user that is logged in as a cashier and allows to retriece a PDF file
 * that in the first page summarizes the order (simulates a receipt) and in the following pages prints the tickets to be used by
 * the kitchen and/or the bar to keep track of the ordered products.
 */
public class DownloadCashierPDFServlet extends AbstractDatabaseServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


        final HttpSession session = req.getSession(false);
        Integer id_sagra = null;
        int id_order=-1;
        final DecimalFormat df = new DecimalFormat("0.00");

        // it should not happen here
        if (session == null) {
            LOGGER.error(String.format("No session. Cannot create pdf."));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("No session. Cannot create pdf."));
        }
        final String cashier = (String) session.getAttribute(CashierFilter.CASHIER_ATTRIBUTE);
        id_sagra=(Integer) session.getAttribute(CashierFilter.SAGRA_ATTRIBUTE);

        // it should not happen here
        if (cashier== null || cashier.isBlank() || cashier.isEmpty() || id_sagra ==null ) {
            LOGGER.error(String.format("Unauthorized attempt to print pdf on session %s.", session.getId()));
            LogContext.removeIPAddress();
            LogContext.removeAction();
            throw new ServletException(String.format("Unauthorized attempt to print pdf on session %s.", session.getId()));
        }




        res.setContentType("application/pdf");
        res.setHeader("Content-disposition","inline; filename='Downloaded.pdf'");

        Message m=null;
        try {
            id_order=Integer.parseInt(req.getParameter("order"));


            List<Sagra> sagras=new SearchSagraByIdDAO(getConnection(),id_sagra).access().getOutputParam();

            //retrieve the order
            List<Order> orders= new SearchOrderDAO(getConnection(),id_sagra,id_order,true,null,null,null,null).access().getOutputParam();





            //spacing attributes
            float titleLineSpacing=20f;
            float titleFontSize=20;
            float linespacing=10f;
            float fontsize=10;
            //writing the document to send to the client
            Document document = new Document(PageSize.A4, 40f, 40f, 40f, 40f);

            PdfWriter.getInstance(document, res.getOutputStream());

            document.open();

            if(orders == null || orders.isEmpty() || sagras==null || sagras.isEmpty() ){
                Paragraph p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing,"ORDER",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing, String.format("%d",id_order),FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing,"NOT VALID",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);


            }else{
                Sagra sagra=sagras.get(0);
                Order order=orders.get(0);

                Paragraph p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing,sagra.getName() == null? "Sagra" : sagra.getName() ,FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(linespacing,(sagra.getAddress() == null || sagra.getCity() ==null)? "" : String.format("%s, %s", sagra.getCity(),sagra.getAddress()) ,FontFactory.getFont(FontFactory.HELVETICA, fontsize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(linespacing, String.format("Order: %d",id_order),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                document.add(p);

                p = new Paragraph(new Phrase(linespacing, String.format("Customer: %s",order.getClientName()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                document.add(p);

                p = new Paragraph(new Phrase(linespacing, String.format("People: %d",order.getClientNum()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                document.add(p);

                if(order.getTableNumber() ==null){
                    p = new Paragraph(new Phrase(linespacing, "TAKE-AWAY",FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                    document.add(p);
                }else{
                    p = new Paragraph(new Phrase(linespacing, String.format("Table: %s",order.getTableNumber()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                    document.add(p);
                }


                p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);




                PdfPTable table = new PdfPTable(3);
                PdfPCell cell=null;

                cell = new PdfPCell(new Paragraph(new Phrase(linespacing,"PRODUCT: ",FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                cell.setBorder(Rectangle.BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(new Phrase(linespacing,"QUANTITY: ",FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                cell.setBorder( Rectangle.RIGHT | Rectangle.LEFT | Rectangle.BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(new Phrase(linespacing,"PRICE: ",FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                cell.setBorder(Rectangle.BOTTOM);
                table.addCell(cell);


                //while printing I also prepare the list of items that need to go to the kitchen/bar
                List<OrderContent> kitchen= new ArrayList<>();
                List<OrderContent> bar=new ArrayList<>();
                double total=0;

                List<Product> prod=new SearchAllAvailableProductBySagraDAO(getConnection(),id_sagra).access().getOutputParam();

                if(prod == null){
                    p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                    p.setAlignment(Element.ALIGN_CENTER);
                    document.add(p);

                    p = new Paragraph(new Phrase(titleLineSpacing,"SOMETHING WENT WORNG, PLEASE RE-ORDER" ,FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                    p.setAlignment(Element.ALIGN_CENTER);
                    document.add(p);

                    p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                    p.setAlignment(Element.ALIGN_CENTER);
                    document.add(p);
                }else{

                    for(OrderContent c : order.getOrderContent()){
                        for(Product pr : prod){
                            if(pr.getName() != null && pr.getName().equals(c.getProductName())){
                                if(pr.isBar()) {
                                    bar.add(c);
                                }else{
                                    kitchen.add(c);
                                }
                                break;
                            }
                        }
                        cell = new PdfPCell(new Paragraph(new Phrase(linespacing,c.getProductName(),FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);
                        cell = new PdfPCell(new Paragraph(new Phrase(linespacing,String.format("X %d",c.getQuantity()),FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        cell = new PdfPCell(new Paragraph(new Phrase(linespacing,df.format(c.getPrice()),FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        total+= c.getPrice()*c.getQuantity();

                    }

                    cell = new PdfPCell(new Paragraph(new Phrase(linespacing," ",FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                    cell.setBorder( Rectangle.TOP );
                    table.addCell(cell);

                    cell = new PdfPCell(new Paragraph(new Phrase(linespacing,"TOTAL: ",FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                    cell.setBorder( Rectangle.TOP );
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Paragraph(new Phrase(linespacing,df.format(total),FontFactory.getFont(FontFactory.HELVETICA, fontsize))));
                    cell.setBorder( Rectangle.TOP );
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

                    document.add(table);



                    if(!kitchen.isEmpty()){
                        //NEW PAGE, PAPERS GOING INTO THE KITCHEN
                        document.newPage();
                        p = new Paragraph(new Phrase(titleLineSpacing," KITCHEN ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                        p.setAlignment(Element.ALIGN_CENTER);
                        document.add(p);

                        p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                        p.setAlignment(Element.ALIGN_CENTER);
                        document.add(p);

                        p = new Paragraph(new Phrase(linespacing, String.format("Order: %d",id_order),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                        document.add(p);

                        p = new Paragraph(new Phrase(linespacing, String.format("Client: %s",order.getClientName()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                        document.add(p);

                        p = new Paragraph(new Phrase(linespacing, String.format("People: %d",order.getClientNum()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                        document.add(p);

                        if(order.getTableNumber() ==null){
                            p = new Paragraph(new Phrase(linespacing, "TAKE-AWAY",FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                            document.add(p);
                        }else{
                            p = new Paragraph(new Phrase(linespacing, String.format("Table: %s",order.getTableNumber()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                            document.add(p);
                        }

                        p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                        p.setAlignment(Element.ALIGN_CENTER);
                        document.add(p);

                        for(OrderContent c: kitchen){
                            p = new Paragraph(new Phrase(titleLineSpacing," ------- ------- ------ ------ ------ ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);
                            p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);

                            p = new Paragraph(new Phrase(titleLineSpacing,c.getProductName(),FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_LEFT);
                            document.add(p);

                            p = new Paragraph(new Phrase(titleLineSpacing,String.format("X %d",c.getQuantity()),FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);

                            p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);
                        }
                    }


                    if(!bar.isEmpty()){
                        //NEW PAGE, PAPERS GOING INTO THE BAR
                        document.newPage();

                        p = new Paragraph(new Phrase(titleLineSpacing," BAR ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                        p.setAlignment(Element.ALIGN_CENTER);
                        document.add(p);

                        p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                        p.setAlignment(Element.ALIGN_CENTER);
                        document.add(p);

                        p = new Paragraph(new Phrase(linespacing, String.format("Order: %d",id_order),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                        document.add(p);

                        p = new Paragraph(new Phrase(linespacing, String.format("Client: %s",order.getClientName()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                        document.add(p);

                        p = new Paragraph(new Phrase(linespacing, String.format("People: %d",order.getClientNum()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                        document.add(p);

                        if(order.getTableNumber() ==null){
                            p = new Paragraph(new Phrase(linespacing, "TAKE-AWAY",FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                            document.add(p);
                        }else{
                            p = new Paragraph(new Phrase(linespacing, String.format("Table: %s",order.getTableNumber()),FontFactory.getFont(FontFactory.HELVETICA, fontsize)));

                            document.add(p);
                        }

                        p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                        p.setAlignment(Element.ALIGN_CENTER);
                        document.add(p);

                        for(OrderContent c: bar){
                            p = new Paragraph(new Phrase(titleLineSpacing," ------- ------- ------ ------ ------ ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);
                            p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);

                            p = new Paragraph(new Phrase(titleLineSpacing,c.getProductName(),FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_LEFT);
                            document.add(p);

                            p = new Paragraph(new Phrase(titleLineSpacing,String.format("X %d",c.getQuantity()),FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);

                            p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                            p.setAlignment(Element.ALIGN_CENTER);
                            document.add(p);
                        }
                    }

                }






            }



            document.close();

            LOGGER.info("Created pdf for order %d.", id_order);
        } catch (DocumentException e) {
            m = new Message(String.format("Cannot create pdf for order %d.", id_order), "E5A5", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        } catch (NumberFormatException e) {
            m = new Message(String.format("Cannot create pdf for order %d. Empty input field", id_order), "E5B1", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        } catch (SQLException e) {
            m = new Message(String.format("Cannot create pdf for order %d.", id_order), "E5A5", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        } catch (Exception e) {
            m = new Message(String.format("Cannot create pdf for order %d. Invalid order", id_order), "E5B2", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        }
    }
}
