package it.unipd.dei.sagrone.resource;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

/**
 * Represents the data about a category of products
 */
public class Category extends AbstractResource{
    /**
     * The name of the category.
     */
    private final String name;

    /**
     * Creates a new category.
     *
     * @param name name of the new category.
     */
    public Category(final String name )
    {
        this.name = name;
    }

    /**
     * Returns the name or the category.
     *
     * @return the name of the category.
     */
    public final String getName() {return name;}


    /**
     * Creates a JSON representation of the {@code Category}.
     *
     * @param out the stream to which the JSON representation of the {@code Category} has to be written.
     *
     * @throws IOException if something goes wrong while creating the JSON.
     */
    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("category");

        jg.writeStartObject();

        if(name != null) jg.writeStringField("name", name);

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }
}