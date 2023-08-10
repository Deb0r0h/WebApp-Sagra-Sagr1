
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
 * Returns the list of the categories for which there is at least one available product.
 */

public final class SearchCategoriesWithAvailableProductsDAO extends AbstractDAO<List<Category>>{
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT c.name FROM sagrone.category AS c INNER JOIN sagrone.product as p ON c.name=p.category WHERE p.id_sagra=? AND p.available=TRUE GROUP BY c.name HAVING count(p.id_sagra)>0; ";

    /**
     * The id of the sagra
     */
    private final int id_sagra;

    /**
     * Creates a new object for searching the categories.
     *
     * @param con the connection to the database.
     * @param id_sagra the id of the sagra related to the products.
     */
    public SearchCategoriesWithAvailableProductsDAO(final Connection con, final int id_sagra){
        super(con);
        this.id_sagra=id_sagra;
    }
    @Override
    public final void doAccess() throws SQLException{

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //the result of the search
        final List<Category> categories = new ArrayList<Category>();

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1,id_sagra);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                categories.add(new Category(rs.getString("name")));
            }
            LOGGER.info(new StringFormattedMessage("Categories with available products for sagra %d successfully listed.",id_sagra));
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

