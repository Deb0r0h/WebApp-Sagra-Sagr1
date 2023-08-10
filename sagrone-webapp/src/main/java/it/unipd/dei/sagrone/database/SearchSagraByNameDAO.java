package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Sagra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This DAO accesses the database in order to retrieve all sagras containing the researched pattern in the name.
 * If the pattern to search for is an empty string, all sagras are selected.
 */
public final class SearchSagraByNameDAO extends AbstractDAO<List<Sagra>>{

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT_PATTERN = "SELECT id, name, description, city, address FROM sagrone.sagra WHERE name ILIKE ?";
    private static final String STATEMENT_ALL = "SELECT id, name, description, city, address FROM sagrone.sagra";

    /**
     * The pattern to retrieve in the name of sagras
     */
    private final String name;

    /**
     * Creates a new object for searching sagras containing a precise pattern in the name
     *
     * @param con
     *            the connection to the database.
     * @param name
     *            pattern to retrieve in the name of sagras
     * @throws NullPointerException The name of the sagra cannot be null.
     */
    public SearchSagraByNameDAO(final Connection con, final String name){
        super(con);

        if (name == null) {
            LOGGER.error("The name can't be null.");
            throw new NullPointerException("The name can't be null.");
        }

        this.name = name;
    }

    @Override
    public final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final List<Sagra> sagras = new ArrayList<Sagra>();

        try{
            if(name.isEmpty()) {
                pstmt = con.prepareStatement(STATEMENT_ALL);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    sagras.add(new Sagra(rs.getInt("id"), rs.getString("name"), rs.getString("city"),
                            rs.getString("address"), rs.getString("description")));
                }

                LOGGER.info("All sagras are correctly listed");
            }

            else{
                String pattern = "%" + name + "%";

                pstmt = con.prepareStatement(STATEMENT_PATTERN);
                pstmt.setString(1, pattern);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    sagras.add(new Sagra(rs.getInt("id"), rs.getString("name"), rs.getString("city"),
                            rs.getString("address"), rs.getString("description")));
                }

                LOGGER.info("Sagras containing pattern [%s] are correctly listed", name);
            }
        }

        finally{
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }

        this.outputParam = sagras;
    }
}
