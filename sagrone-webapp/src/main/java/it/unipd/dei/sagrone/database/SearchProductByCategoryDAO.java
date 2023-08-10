

package it.unipd.dei.sagrone.database;


import it.unipd.dei.sagrone.resource.Category;
import it.unipd.dei.sagrone.resource.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches all the products of a given category for a given sagra.
 */
public final class SearchProductByCategoryDAO extends AbstractDAO<List<Product>> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT name,id_sagra,description,price,bar,available,category,photo,photo_type FROM sagrone.product WHERE id_sagra= ? AND category = ? AND (available=? OR available=?);";

    /**
     * The id of the sagra
     */
    private final int id_sagra;

    /**
     * The availability of the products
     */
    private final boolean available;

    /**
     *  The category of products to retrieve
     */
    private final Category category;

    /**
     * Creates a new object for searching products by category.
     * If availability is set to true then only products that are available for the customer are searched, if availability is
     * set to false then all the products are retrieved.
     *
     * @param con    the connection to the database.
     * @param id_sagra the id of the sagra.
     * @param category the category of products to look for.
     * @param available the availability of the products
     */
    public SearchProductByCategoryDAO(final Connection con, final int id_sagra, final Category category, final boolean available) {
        super(con);

        //input validation
        if (category == null) {
            LOGGER.error("The category cannot be null.");
            throw new NullPointerException("The category cannot be null.");
        }

        this.available=available;
        this.id_sagra = id_sagra;
        this.category = category;
    }

    @Override
    protected void doAccess() throws SQLException {
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        //results of the search
        final List<Product> products= new ArrayList<>();

        try{
            pstmt= con.prepareStatement(STATEMENT);
            pstmt.setInt(1,id_sagra);
            pstmt.setString(2, category.getName());
            pstmt.setBoolean(3,true);
            if(available){
                pstmt.setBoolean(4,true);
            }else{
                pstmt.setBoolean(4,false);
            }

            rs=pstmt.executeQuery();

            while(rs.next()){
                products.add(new Product(rs.getString("name"),rs.getInt("id_sagra"),
                        rs.getString("description"), rs.getDouble("price"),
                        rs.getBoolean("bar"), rs.getBoolean("available"),
                        rs.getString("category"), rs.getBytes("photo"), rs.getString("photo_type")));
            }

            LOGGER.info("Product(s) with id_sagra %d and of category %s successfully listed", id_sagra, category.getName());

        }finally{
            if(rs!=null) rs.close();

            if(pstmt!=null) pstmt.close();
        }

        this.outputParam=products;
    }
}
