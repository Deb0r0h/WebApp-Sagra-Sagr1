package it.unipd.dei.sagrone.database;
import it.unipd.dei.sagrone.resource.Category;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns the list of the categories
 */

public final class SearchCategoriesDAO extends AbstractDAO<List<Category>>{
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT name FROM sagrone.category";

    /**
     * Creates a new object for searching the categories.
     * @param con the connection to the database.
     */
    public SearchCategoriesDAO(final Connection con){
        super(con);
    }
    @Override
    public final void doAccess() throws SQLException{

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //the result of the search
        final List<Category> categories = new ArrayList<Category>();

        try {
            pstmt = con.prepareStatement(STATEMENT);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                categories.add(new Category(rs.getString("name")));
            }
            LOGGER.info(new StringFormattedMessage("Categories successfully listed."));
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }
        }

        this.outputParam = categories;
    }


}
