package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This DAO searches all the cashiers by their username and sagra identifier.
 */
public final class SearchCashierDAO extends AbstractDAO<List<User>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT_ADMINS = "SELECT id, username, password, id_sagra, admin FROM sagrone.user WHERE username LIKE ? AND (id_sagra = ?)";
    private static final String STATEMENT_STRICT = "SELECT id, username, password, id_sagra, admin FROM sagrone.user WHERE username LIKE ? AND (id_sagra = ?) AND (admin = false)";
    private String STATEMENT = null;


    /**
     * The username of the cashier and
     * the identifier of the Sagra.
     */
    private final String username;
    private final int id_sagra;

    /**
     * Creates a new object for searching users. This will return both cashiers and admin accounts.
     *
     * @param con    the connection to the database.
     * @param username the username of the cashier.
     * @param id_sagra the identifier of the sagra.
     * @throws NullPointerException The username cannot be null.
     * @throws NullPointerException The id must be positive.
     */
    public SearchCashierDAO(final Connection con, final String username, final int id_sagra)
    {
        super(con);
        if (username == null) {
            LOGGER.error("The username cannot be null.");
            throw new NullPointerException("The user name cannot be null");}
        if (id_sagra < 0) {
            LOGGER.error("The id can't be non positive");
            throw new NullPointerException("The id can't be non positive");
        }
        this.username = username;
        this.id_sagra = id_sagra;
        STATEMENT = STATEMENT_ADMINS;
    }

    /**
     * Creates a new object for searching users. This will return only cashier accounts.
     *
     * @param con    the connection to the database.
     * @param id_sagra the identifier of the sagra.
     * @throws NullPointerException The username cannot be null.
     * @throws NullPointerException The id must be positive.
     */
    public SearchCashierDAO(final Connection con, final int id_sagra)
    {
        super(con);
        if (id_sagra < 0) {
            LOGGER.error("The id can't be non positive");
            throw new NullPointerException("The id can't be non positive");
        }
        this.username = "%";
        this.id_sagra = id_sagra;
        STATEMENT = STATEMENT_STRICT;
    }

    public final void doAccess() throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // the results of the search
        final List<User> users = new ArrayList<User>();

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setString(1, username);
            pstmt.setInt(2,id_sagra);

            rs = pstmt.executeQuery();
            while(rs.next()){
                users.add(new User(rs.getString("username"), rs.getString("password"),
                        rs.getInt("id_sagra"), rs.getBoolean("admin"), rs.getInt("id")));
            }

            LOGGER.info("Cashiers with username %s and id sagra %d are correctly listed", username, id_sagra);
        }

        finally{
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }

        this.outputParam = users;
    }
}






