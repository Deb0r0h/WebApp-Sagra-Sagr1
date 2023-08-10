package it.unipd.dei.sagrone.resource;

/**
 * This class describes a user
 */
public class User {
    /**
     * The identifier of the user.
     */
    private final int id;

    /**
     * The username of the user.
     */
    private final String username;

    /**
     * The password of the user.
     */
    private final String password;

    /**
     * The identifier of the Sagra to which user belongs.
     */
    private final int id_sagra;

    /**
     * TRUE if the user is an admin.
     * FALSE if the user is not an admin.
     */
    private final boolean admin;

    /**
     * Creates a new User object with null id.
     * Suitable for new user insertion, otherwise the appropriate ID must be manually set.
     *
     * @param username
     *            the User's displayed username and login name.
     * @param password
     *            the User's password.
     * @param id_sagra
     *            id of the Sagra the user belongs to.
     * @param admin
     *            TRUE if User is the Sagra admin.
     * @throws IllegalArgumentException if the user provides an invalid username.
     *
     * @throws IllegalArgumentException if th user provides an invalid password.
     *
     * @throws IllegalArgumentException if the user provides an invalid identifier of the Sagra.
     *
     */
    public User(final String username, final String password, final int id_sagra, final boolean admin) throws IllegalArgumentException
    {
        this.id = -1;         //NULL ID since it will NOT be user by the user insertion DAO (pgsql will generate one).
        if(username==null || username.isEmpty() || username.isBlank()) throw new IllegalArgumentException("Invalid username provided!");
        this.username=username;
        if(password==null || password.isEmpty() || password.isBlank()) throw new IllegalArgumentException("Invalid password provided!");
        this.password=password;
        //if(id_sagra==null || id_sagra.isEmpty() || id_sagra.isBlank()) throw new IllegalArgumentException("Invalid id_sagra provided!");  // FOR Integer OBJ
        if(id_sagra < 0) throw new IllegalArgumentException("Invalid id_sagra provided!");                                                  // for int implementation.
        this.id_sagra = id_sagra;
        this.admin=admin;
    }

    /**
     * Creates a new User object indicating an already db-existing user.
     *
     * @param username
     *            the User's displayed username and login name.
     * @param password
     *            the User's password.
     * @param id_sagra
     *            id of the sagra the user belongs to.
     * @param admin
     *            true if User is the sagra administrator.
     * @param id
     *            user identifier (int).
     * @throws IllegalArgumentException if the user provides an invalid username.
     *
     * @throws IllegalArgumentException if the user provides an invalid password.
     *
     * @throws IllegalArgumentException if the user provides an invalid identifier of the Sagra.
     *
     * @throws IllegalArgumentException if the user's identifier is invalid.
     *
     */
    public User(final String username, final String password, final int id_sagra, final boolean admin, final int id) throws IllegalArgumentException
    {
        if(username==null || username.isEmpty() || username.isBlank()) throw new IllegalArgumentException("Invalid username provided!");
        this.username=username;
        if(password==null || password.isEmpty() || password.isBlank()) throw new IllegalArgumentException("Invalid password provided!");
        this.password=password;
        //if(id_sagra==null || id_sagra.isEmpty() || id_sagra.isBlank()) throw new IllegalArgumentException("Invalid id_sagra provided!");  // FOR Integer OBJ
        if(id_sagra < 0) throw new IllegalArgumentException("Invalid id_sagra provided!");                                                  // for int implementation.
        this.id_sagra = id_sagra;
        this.admin=admin;
        //if(id==null || id.isEmpty() || id.isBlank()) throw new IllegalArgumentException("Invalid user id provided!");  // FOR Integer OBJ
        if(id < 0) throw new IllegalArgumentException("Invalid user id provided!");                                      // for int implementation.
        this.id = id;
    }

    /**
     * Creates a new User object used to update an already db-existing user, keeping old password.
     *
     * @param username
     *            the User's displayed username and login name.
     * @param id_sagra
     *            id of the sagra the user belongs to.
     * @param id
     *            user identifier (int).
     * @throws IllegalArgumentException if the user provides an invalid username.
     *
     * @throws IllegalArgumentException if the user provides an invalid identifier of the Sagra.
     *
     * @throws IllegalArgumentException if the user's identifier is invalid.
     */
    public User(final String username, final int id_sagra, final int id) throws IllegalArgumentException
    {
        if(username==null || username.isEmpty() || username.isBlank()) throw new IllegalArgumentException("Invalid username provided!");
        this.username=username;
        this.password=null;
        //if(id_sagra==null || id_sagra.isEmpty() || id_sagra.isBlank()) throw new IllegalArgumentException("Invalid id_sagra provided!");  // FOR Integer OBJ
        if(id_sagra < 0) throw new IllegalArgumentException("Invalid id_sagra provided!");                                                  // for int implementation.
        this.id_sagra = id_sagra;
        this.admin=false;                   //HARDCODED TO PREVENT UPDATING USERS TO ADMINS!
        //if(id==null || id.isEmpty() || id.isBlank()) throw new IllegalArgumentException("Invalid user id provided!");  // FOR Integer OBJ
        if(id < 0) throw new IllegalArgumentException("Invalid user id provided!");                                      // for int implementation.
        this.id = id;
    }

    /**
     * Creates a new User object used to delete an already db-existing user.
     * This object is only used to ensure id and sagra id's correct format.
     *
     * @param id_sagra
     *            id of the sagra the user belongs to.
     * @param id
     *            user identifier (int).
     * @throws IllegalArgumentException if the user provides an invalid identifier of the Sagra.
     *
     * @throws IllegalArgumentException if the user's identifier is invalid.
     */
    public User(final int id_sagra, final int id) throws IllegalArgumentException
    {
        this.username = null;
        this.password = null;
        //if(id_sagra==null || id_sagra.isEmpty() || id_sagra.isBlank()) throw new IllegalArgumentException("Invalid id_sagra provided!");  // FOR Integer OBJ
        if(id_sagra < 0) throw new IllegalArgumentException("Invalid id_sagra provided!");                                                  // for int implementation.
        this.id_sagra = id_sagra;
        this.admin=false;                   //HARDCODED TO PREVENT DELETING ADMINS!
        //if(id==null || id.isEmpty() || id.isBlank()) throw new IllegalArgumentException("Invalid user id provided!");  // FOR Integer OBJ
        if(id < 0) throw new IllegalArgumentException("Invalid user id provided!");                                      // for int implementation.
        this.id = id;
    }

    /**
     * Return the username.
     *
     * @return the username.
     */
    public final String getUsername()
    {
        return username;
    }

    /**
     * Return the identifier of the Sagra.
     *
     * @return the identifier of the Sagra.
     */
    public int getIdSagra() {
        return id_sagra;
    }

    /**
     * Return the password of the user.
     *
     * @return the password of the user.
     */

    public final String getPassword()
    {
        return password;
    }

    /**
     * Return the identifier of the user.
     *
     * @return the identifier of the user.
     */
    public final int getId()
    {
        return id;
    }

    /**
     * Return TRUE if the user is an admin.
     *
     * @return {@code true} if the user is an admin,{@code false} otherwise.
     */
    public final boolean isAdmin()
    {
        return admin;
    }

}