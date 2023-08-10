package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Sagra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This DAO accesses the database in order to retrieve sagra with specified Id.
 */
public final class SearchSagraByIdDAO extends AbstractDAO<List<Sagra>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT id, name, description, city, address FROM sagrone.sagra WHERE id = ?";

    /**
     * The id of sagra to retrieve
     */
    private final int id;

    /**
     * Creates a new object for searching sagra with specified Id
     *
     * @param con
     *            the connection to the database.
     * @param id
     *            id of sagra to retrieve
     * @throws NullPointerException the id cannot be a negative number.
     */
    public SearchSagraByIdDAO(final Connection con, final int id){
        super(con);

        if (id < 0) {
            LOGGER.error("The id can't be non positive");
            throw new NullPointerException("The id can't be non positive");
        }

        this.id = id;
    }

    @Override
    public final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final List<Sagra> sagra = new ArrayList<Sagra>();

        try{
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                sagra.add(new Sagra(rs.getInt("id"), rs.getString("name"),
                        rs.getString("city"), rs.getString("address"), rs.getString("description")));
            }

            LOGGER.info("Sagra with id [%d] is correctly searched", id);

        }

        finally{
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }

        this.outputParam = sagra;
    }
}