
package it.unipd.dei.sagrone.servlet;




import com.itextpdf.text.*;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import it.unipd.dei.sagrone.database.SearchOrderDAO;
import it.unipd.dei.sagrone.resource.Message;
import it.unipd.dei.sagrone.resource.Order;
import it.unipd.dei.sagrone.resource.OrderContent;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;


/**
 * Sends to the client (downloads) the summary of an order.
 */
public class DownloadSummaryPDFServlet extends AbstractDatabaseServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


        int id_order=-1;
        int id_sagra=-1;
        res.setContentType("application/pdf");
        res.setHeader("Content-disposition","inline; filename='Downloaded.pdf'");
        final DecimalFormat df = new DecimalFormat("0.00");

        Message m=null;
        try {
            id_order=Integer.parseInt(req.getParameter("order"));
            id_sagra=Integer.parseInt(req.getParameter("sagra"));

            //retrieve the order
            List<Order> orders= new SearchOrderDAO(getConnection(),id_sagra,id_order,true,null,null,null,null).access().getOutputParam();





            //spacing attributes
            float titleLineSpacing=30f;
            float titleFontSize=30;
            float linespacing=10f;
            float fontsize=10;
            //writing the document to send to the client
            Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);

            PdfWriter.getInstance(document, res.getOutputStream());

            document.open();

            if(orders == null || orders.isEmpty()){
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
                Order order=orders.get(0);

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

                p = new Paragraph(new Phrase(titleLineSpacing,"  --  --  --  --  --  --  --  --  --  --  ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                p = new Paragraph(new Phrase(titleLineSpacing," ",FontFactory.getFont(FontFactory.HELVETICA, titleFontSize)));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                //document.newPage();


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

                double total=0;
                for(OrderContent c : order.getOrderContent()){
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

            }



            document.close();

            LOGGER.info("Created pdf for order %d.", id_order);
        } catch (DocumentException e) {
            m = new Message(String.format("Cannot create pdf for order %d.", id_order), "E5A5", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        } catch (NumberFormatException e) {
            m = new Message(String.format("Cannot create pdf for order %d. Empty input field", id_order), "E4B1", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        } catch (SQLException e) {
            m = new Message(String.format("Cannot create pdf for order %d.", id_order), "E5A5", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        } catch (Exception e) {
            m = new Message(String.format("Cannot create pdf for order %d. Invalid order", id_order), "E4B2", e.getMessage());
            LOGGER.error(new StringFormattedMessage("Cannot create pdf for order %d.", id_order),e);
        }
    }
}
