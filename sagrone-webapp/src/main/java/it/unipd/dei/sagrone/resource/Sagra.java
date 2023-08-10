package it.unipd.dei.sagrone.resource;

/** This class represents the essential features of a "Sagra"*/
public class Sagra {

    /** The identifier of the Sagra */
    private final int id;

    /** The name of the Sagra */
    private final String name;

    /** The city where the Sagra is held */
    private final String city;

    /** The address of the Sagra */
    private final String address;

    /** A brief description of the Sagra */
    private final String description;

    /** Creates a new Sagra
     *  @param id
     * 	           the identifier of the Sagra
     *  @param name
     * 	             the name of the Sagra.
     * 	@param city
     * 	             the city of the Sagra.
     * 	@param address
     * 	             the address of the Sagra.
     *  @param description
     *               the description of the sagra.

     */
    public Sagra(final int id, final String name, final String city, final String address, final String description)
    {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;

    }

    /** Creates a new Sagra
     *  @param id
     * 	           the identifier of the Sagra
     *  @param name
     * 	             the name of the Sagra.

     */
    public Sagra(final int id, final String name)
    {
        this.id = id;
        this.name = name;
        this.city = null;
        this.address = null;
        this.description = null;
    }

    /** Creates a new Sagra
     *  @param id
     * 	           the identifier of the Sagra
     *  @param name
     * 	             the name of the Sagra.
     * 	@param city
     * 	             the city of the Sagra.

     */
    public Sagra(final int id, final String name, final String city)
    {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = null;
        this.description = null;
    }

    /** Creates a new Sagra
     *  @param id
     * 	           the identifier of the Sagra
     *  @param name
     * 	             the name of the Sagra.
     * 	@param city
     * 	             the city of the Sagra.

     *  @param description
     *               the description of the sagra.

     */
    public Sagra(final int id, final String name, final String city, final String description )
    {
        this.id = id;
        this.name = name;
        this.city = city;
        this.description = description;
        this.address = null;
    }

    /**
     * Returns the identifier of the Sagra.
     *
     * @return the identifier of the Sagra.
     */
    public final int getId() {return id;}

    /**
     * Return the name of the Sagra.
     *
     * @return the name of the Sagra.
     */
    public final String getName() {return name;}

    /**
     * Return the city of the Sagra.
     *
     * @return the city of the Sagra.
     */
    public final String getCity() {return city;}

    /**
     * Return the address of the Sagra.
     *
     * @return the address of the Sagra.
     */
    public final String getAddress() {return address;}

    /**
     * Return a brief description of the Sagra.
     *
     * @return a brief description of the Sagra.
     */
    public final String getDescription() {return description;}

}