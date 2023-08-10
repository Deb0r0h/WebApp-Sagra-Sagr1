package it.unipd.dei.sagrone.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Represents the content of an order.
 */
public class OrderContent extends AbstractResource {

    /** The identifier of the sagra */
    private final int id_sagra;

    /** The identifier of the order */
    private final int id_order;

    /** The name of the product */
    private final String product_name;

    /** The price of the product (at order time) */
    private final double price;

    /** The quantity of the product */
    private  final short quantity;

    /**
     * Creates a new OrderContent object.
     *
     * @param id_sagra
     *            The Id of the sagra with which to associate the order.
     * @param id_order
     *            The order's Id.
     * @param product_name
     *            the product's name (at the ordination time).
     * @param price
     *            the product's price (at the ordination time).
     * @param quantity
     *            the quantity of the selected product.
     */
    public OrderContent(final int id_sagra, final int id_order, final String product_name, final double price, short quantity)
    {
        this.id_sagra = id_sagra;
        this.id_order = id_order;
        this.product_name = product_name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Return the identifier of the sagra.
     *
     * @return the identifier of the sagra.
     */
    public final int getIdSagra() {
        return id_sagra;
    }

    /**
     * Return the identifier of the order.
     *
     * @return the identifier of the order.
     */
    public final int getIdOrder() {
        return id_order;
    }

    /**
     * Return the name of the product.
     *
     * @return the name of the product.
     */
    public final String getProductName() {
        return product_name;
    }

    /**
     * Return the price of the product (at order time).
     *
     * @return the price of the product (at order time).
     */
    public final double getPrice() {
        return price;
    }

    /**
     * Return the quantity of the product.
     *
     * @return the quantity of the product.
     */
    public final short getQuantity() {
        return quantity;
    }


    /**
     * Performs the actual writing of JSON.
     *
     * @param out the stream to which the JSON representation of the {@code Resource} has to be written.
     * @throws Exception if something goes wrong during writing.
     */
    @Override
    protected void writeJSON(OutputStream out) throws Exception {
        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("orderContent");

        jg.writeStartObject();

        jg.writeNumberField("id_sagra", id_sagra);

        jg.writeNumberField("id_order", id_order);

        if(product_name != null) jg.writeStringField("product_name", product_name);

        jg.writeNumberField("price", price);

        jg.writeNumberField("quantity", quantity);

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }



    /**
     * Creates a {@code OrderContent} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code OrderContent} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static OrderContent fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jId_sagra=-1;
        int jId_order=-1;
        String jProduct_name=null;
        double jPrice=-1;
        short jQuantity=-1;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"orderContent".equals(jp.getCurrentName())) {

                LOGGER.info("qui");
                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No OrderContent object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no OrderContent object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "id_sagra":
                            jp.nextToken();
                            jId_sagra= jp.getIntValue();
                            break;
                        case "id_order":
                            jp.nextToken();
                            jId_order= jp.getIntValue();
                            break;
                        case "product_name":
                            jp.nextToken();
                            jProduct_name = jp.getText();
                            break;
                        case "price":
                            jp.nextToken();
                            jPrice= jp.getDoubleValue();
                            break;
                        case "quantity":
                            jp.nextToken();
                            jQuantity= jp.getShortValue();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse an OrderContent object from JSON.", e);
            throw e;
        }

        return new OrderContent(jId_sagra, jId_order, jProduct_name,jPrice,jQuantity);
    }

    /**
     * Creates a {@code OrderContent} from its JSON representation given the parser.
     *
     * @param jp the json parser to parse the order content
     *
     * @return the {@code OrderContent} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static OrderContent fromJSON(final JsonParser jp) throws IOException  {

        // the fields read from JSON
        int jId_sagra=-1;
        int jId_order=-1;
        String jProduct_name=null;
        double jPrice=-1;
        short jQuantity=-1;

        try {

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"orderContent".equals(jp.getCurrentName())) {


                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No OrderContent object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no OrderContent object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "id_sagra":
                            jp.nextToken();
                            jId_sagra= jp.getIntValue();
                            break;
                        case "id_order":
                            jp.nextToken();
                            jId_order= jp.getIntValue();
                            break;
                        case "product_name":
                            jp.nextToken();
                            jProduct_name = jp.getText();
                            break;
                        case "price":
                            jp.nextToken();
                            jPrice= jp.getDoubleValue();
                            break;
                        case "quantity":
                            jp.nextToken();
                            jQuantity= jp.getShortValue();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse an OrderContent object from JSON.", e);
            throw e;
        }

        return new OrderContent(jId_sagra, jId_order, jProduct_name,jPrice,jQuantity);
    }
}