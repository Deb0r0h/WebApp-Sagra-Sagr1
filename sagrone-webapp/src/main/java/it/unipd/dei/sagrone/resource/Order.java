package it.unipd.dei.sagrone.resource;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import it.unipd.dei.sagrone.resource.OrderContent;

/** This class describes an order */

public class Order extends AbstractResource{

    /** The identifier of the order */
    private final int id;

    /** The client's name */
    private final String client_name;

    /** The email of the user that will receive a confirmation for an order */
    private final String email;

    /** The Number of people */
    private final short client_num;

    /** The table number */
    private final String table_number;//NULL take-away

    /** The identifier of the user */
    private final int id_user; //null if not payed

    /** The timestamp of the order */
    private final Timestamp order_time;

    /** The timestamp of the payment */
    private final Timestamp payment_time;

    /** The list of Order Content */
    private final List<OrderContent> order_content;//do not add it into DB

    /** Creates a new Order
     *  @param id
     * 	           the identifier of the order
     *  @param client_name
     * 	             the client's name.
     * 	@param email
     * 	             The email of the user.
     *  @param client_num
     *               The Number of people.
     *  @param table_number
     *               The table number.
     *  @param id_user
     *               The identifier of the user.
     *  @param order_time
     *               The timestamp of the order.
     *  @param payment_time
     *               The timestamp of the payment.
     */
    public Order(final int id, final String client_name, final String email, final short client_num, final String table_number, final int id_user, final Timestamp order_time, final Timestamp payment_time)
    {
        this.id = id;
        this.client_name = client_name;
        this.email = email;
        this.client_num = client_num;
        this.table_number = table_number;
        this.id_user = id_user;
        this.order_time = order_time;
        this.order_content = null;
        this.payment_time=null;
    }

    /** Creates a new Order
     *  @param id
     * 	           the identifier of the order
     *  @param client_name
     * 	             the client's name.
     * 	@param email
     * 	             The email of the user.
     *  @param client_num
     *               The Number of people.
     *  @param table_number
     *               The table number.
     *  @param id_user
     *               The identifier of the user.
     *  @param order_time
     *               The timestamp of the order.
     *  @param payment_time
     *               The timestamp of the payment.
     *  @param order_content
     *               The list of Order-Content.
     */
    public Order(final int id, final String client_name, final String email, final short client_num, final String table_number, final int id_user, final Timestamp order_time, final Timestamp payment_time, final List<OrderContent> order_content)
    {
        this.id = id;
        this.client_name = client_name;
        this.email = email;
        this.client_num = client_num;
        this.table_number = table_number;
        this.id_user = id_user;
        this.order_time = order_time;
        this.order_content = order_content;
        this.payment_time=payment_time;
    }



    /**
     * Return the identifier of the order.
     *
     * @return the identifier of the order.
     */
    public final int getId() {return id;}

    /**
     * Return the client's name of an order.
     *
     * @return the client's name of an order.
     */
    public final String getClientName() {return client_name;}

    /**
     * Return the email of the user.
     *
     * @return the email of the user.
     */
    public final String getEmail() {return email;}

    /**
     * Return the number of people.
     *
     * @return the number of people.
     */
    public final short getClientNum(){return client_num;}

    /**
     * Return the table number.
     *
     * @return the table number.
     */
    public final String getTableNumber(){return table_number;}


    /**
     * Return the identifier of the user.
     *
     * @return the identifier of the user.
     */
    public final int getIdUser(){return id_user;}


    /**
     * Return the timestamp of the order.
     *
     * @return the timestamp of the order.
     */
    public final Timestamp getOrderTime(){return order_time;}


    /**
     * Return the timestamp of the payment.
     *
     * @return the timestamp of the payment.
     */
    public final Timestamp getPaymentTime(){return payment_time;}


    /**
     * Return The list of Order Content.
     *
     * @return The list of Order Content.
     */
    public final List<OrderContent> getOrderContent(){return order_content;}


    /**
     * Creates a JSON representation of the {@code Order}.
     *
     * @param out the stream to which the JSON representation of the {@code Order} has to be written.
     *
     * @throws IOException if something goes wrong while creating the JSON.
     */

    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("order");

        jg.writeStartObject();

        jg.writeNumberField("id", id);

        if(client_name != null) jg.writeStringField("client_name", client_name);

        if(email != null) jg.writeStringField("email", email);

        if(client_num >0) jg.writeNumberField("client_num", client_num);

        if(table_number != null) jg.writeStringField("table_number", table_number);

        if(order_content!=null){
            jg.flush();
            jg.writeFieldName("content");
            jg.writeStartArray();
            jg.flush();
            boolean first=true;
            for(OrderContent c: order_content){
                if(first){
                    c.toJSON(out);
                    first=false;
                }else{
                    jg.flush();
                    jg.writeRaw(',');
                    jg.flush();
                    c.toJSON(out);
                }
            }
            jg.flush();
            jg.writeEndArray();
            jg.flush();
        }


        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }

    /**
     * Creates an {@code Order} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code Order} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static Order fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jId=-1;
        String jClient_name=null;
        String jEmail=null;
        short jClient_num=-1;
        String jTable_number=null;
       List<OrderContent> jOrder_content=null;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"order".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No Order object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no order object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "id":
                            jp.nextToken();
                            jId= jp.getIntValue();
                            break;
                        case "client_name":
                            jp.nextToken();
                            jClient_name = jp.getText();
                            break;
                        case "email":
                            jp.nextToken();
                            jEmail = jp.getText();
                            break;
                        case "client_num":
                            jp.nextToken();
                            jClient_num= jp.getShortValue();
                            break;
                        case "table_number":
                            jp.nextToken();
                            jTable_number = jp.getText();
                            break;
                        case "content":
                            jOrder_content=parseOrderContent(jp);
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse an Order object from JSON.", e);
            throw e;
        }

        return new Order(jId,jClient_name,jEmail,jClient_num,jTable_number,-1,null,null,jOrder_content);
    }


    /**
     * private method that parses a json array of OrderContent.
     *
     * @param jp the json parser to parse the order content
     * @return the list of OrderContent objects parsed from the json
     */
    private static List<OrderContent> parseOrderContent(JsonParser jp) throws IOException {
        List<OrderContent> list =new ArrayList<>();

        while(jp.getCurrentToken() != JsonToken.START_ARRAY){
            // there are no more events
            if (jp.nextToken() == null) {
                LOGGER.error("No OrderContent object found in the stream.");
                throw new EOFException("Unable to parse JSON: no OrderContent object found.");
            }
        }

        while (jp.nextToken() != JsonToken.END_ARRAY ){
            if(jp.getCurrentToken()==JsonToken.START_OBJECT){
                list.add(OrderContent.fromJSON(jp));
            }
        }
        return list;
    }
}