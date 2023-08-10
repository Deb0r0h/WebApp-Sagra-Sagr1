package it.unipd.dei.sagrone.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;


/**
 * This class represents the essential features of a Product
 * */
public class Product extends AbstractResource {

    /** The name of a product */
    private final String name;

    /** The id of the Sagra that contains this particular product  */
    private final int id_sagra;

    /** The description of a product */
    private final String description;

    /** The price for a product */
    private final double price;

    /** Specifies whether the product belongs to the bar */
    private final boolean bar;

    /** Specifies whether the product is available  */
    private final boolean available;

    /** Product category */
    private final String category;

    /** Photo of the product */
    private final byte[] photo;

    /** Format of the photo */
    private final String photo_type;


    /**
     *  the path to retrieve the photo using the LoadProductPhotoServlet
     */
    private final String photoPath;


    /**
     * Creates a new product object
     * @param name: name of the product
     * @param id_sagra: sagra id
     * @param description: product description
     * @param price: product price
     * @param bar: specifies whether the product belongs to the bar
     * @param available: specifies whether the product is available
     * @param category: prodoct category
     * @param photo: photo of the product
     * @param photo_type: format of the photo
     */
    public Product(final String name, final int id_sagra, String description, double price, boolean bar, boolean available, final String category, byte[] photo, final String photo_type){

        this.name=name;
        this.id_sagra=id_sagra;
        this.description=description;
        this.price=price;
        this.bar=bar;
        this.available=available;
        this.category=category;
        this.photo=photo;
        this.photo_type=photo_type;
        this.photoPath=null;
    }


    /**
     * Creates a new product object given also the photoPath
     * @param name: name of the product
     * @param id_sagra: sagra id
     * @param description: product description
     * @param price: product price
     * @param bar: specifies whether the product belongs to the bar
     * @param available: specifies whether the product is available
     * @param category: prodoct category
     * @param photo: photo of the product
     * @param photo_type: format of the photo
     * @param photoPath : the path of the photo to retrieve on the server.
     */
    public Product(final String name, final int id_sagra, String description, double price, boolean bar, boolean available, final String category, byte[] photo, final String photo_type, final String photoPath){

        this.name=name;
        this.id_sagra=id_sagra;
        this.description=description;
        this.price=price;
        this.bar=bar;
        this.available=available;
        this.category=category;
        this.photo=photo;
        this.photo_type=photo_type;
        this.photoPath=photoPath;
    }


    /**
     * Return the name of the product
     * @return name
     */
    public final String getName(){ return name;}

    /**
     * Return the sagra id that offers the product
     * @return id_sagra
     */
    public final int getIdSagra(){ return id_sagra;}

    /**
     * Return the product description
     * @return description
     */
    public String getDescription(){ return description;}



    /**
     * Return the product price
     * @return price
     */
    public double getPrice(){ return price;}


    /**
     * Return a boolean that specifies whether the product is offered by the bar
     * @return {@code true} if the {@code Product} is managed by the bar.
     */
    public boolean getBar(){ return bar;}

    /**
     * Returns the value of the bar attribute of a {@code Product}.
     * @return {@code true} if the {@code Product} is managed by the bar.
     */
    public boolean isBar(){ return bar;}

    /**
     * Return a boolean that specifies whether the product is available
     * @return {@code true} if the {@code Product} is available to be ordered.
     */
    public boolean getAvailable(){ return available;}

    /**
     * Returns the value of the available attribute of a {@code Product}.
     * @return {@code true} if the {@code Product} is available to be ordered.
     */
    public boolean isAvailable(){ return available; }

    /**
     * Return the product category
     * @return the {@code Category} of the {@code Product}
     */
    public final String getCategory(){ return category;}

    /**
     * Return the byte array of the product image
     * @return the byte array of the product image
     */
    public byte[] getPhoto(){ return photo;}


    /**
     * Return the photo type
     * @return photo_type
     */
    public final String getPhotoType(){ return photo_type;}


    /**
     * Method that provides the path of the product's photo.
     * @return the path to retrieve the photo of the product.
     */
    public final String getPhotoPath(){ return photoPath;}


    /**
     * Returns {@code true} if the {@code Product} has a photo with a proper MIME media type specified;
     * {@code false} otherwise.
     *
     * @return {@code true} if the {@code Product} has a photo with a proper MIME media type specified;
     *        {@code false} otherwise.
     */
    public final boolean hasPhoto() {
        return photo != null && photo.length > 0 && photo_type != null && !photo_type.isBlank();
    }


    /**
     * Creates a JSON representation of the {@code Product}.
     *
     * @param out the stream to which the JSON representation of the {@code Product} has to be written.
     *
     * @throws IOException if something goes wrong while creating the JSON.
     */

    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("product");

        jg.writeStartObject();

        if(name != null) jg.writeStringField("name", name);

        if(id_sagra >0) jg.writeNumberField("id_sagra", id_sagra);

        if(description!=null) jg.writeStringField("description", description);

        if(price>=0) jg.writeNumberField("price", price);

        jg.writeBooleanField("bar", bar);

        jg.writeBooleanField("available", available);

        if(category!= null) jg.writeStringField("category", category);

        if(this.hasPhoto() && photoPath != null) {
            jg.writeStringField("photo",  photoPath);
        }



        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }

    /**
     * Creates a {@code Product} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code Product} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static Product fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        String jName=null;
        int jId_sagra=-1;
        String jDescription=null;
        double jPrice=-1;
        boolean jBar=false;
        boolean jAvailable=false;
        String jCategory=null;
        String jPhoto = null;
        String jPhoto_type=null;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"product".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No Product object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no product object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "name":
                            jp.nextToken();
                            jName = jp.getText();
                            break;
                        case "id_sagra":
                            jp.nextToken();
                            jId_sagra= jp.getIntValue();
                            break;
                        case "descripton":
                            jp.nextToken();
                            jDescription = jp.getText();
                            break;
                        case "price":
                            jp.nextToken();
                            jPrice= jp.getDoubleValue();
                            break;
                        case "bar":
                            jp.nextToken();
                            jBar= jp.getBooleanValue();
                            break;
                        case "available":
                            jp.nextToken();
                            jAvailable = jp.getBooleanValue();
                            break;
                        case "category":
                            jp.nextToken();
                            jCategory = jp.getText();
                            break;
                        case "photo":
                            jp.nextToken();
                            jPhoto = jp.getText();
                            break;
                        case "photo_type":
                            jp.nextToken();
                            jPhoto_type = jp.getText();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse a Product object from JSON.", e);
            throw e;
        }


        byte[] decodedPhoto= null;
        if(jPhoto != "" && jPhoto != null) decodedPhoto=Base64.getDecoder().decode(jPhoto);
        if(jPhoto_type == "" ) jPhoto_type=null;

        return new Product(jName, jId_sagra, jDescription, jPrice, jBar, jAvailable, jCategory, decodedPhoto, jPhoto_type);
    }



}