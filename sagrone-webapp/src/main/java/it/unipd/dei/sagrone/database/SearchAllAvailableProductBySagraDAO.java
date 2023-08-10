
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
 * Searches the products given its id (composed by the identifier of the sagra and name).
 */
public class SearchAllAvailableProductBySagraDAO extends AbstractDAO<List<Product>>{
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT name,id_sagra,description,price,bar,available,category,photo,photo_type FROM sagrone.product WHERE id_sagra= ? AND available=TRUE;";

    /**
     * The id of the sagra
     */
    private final int id_sagra;


    /**
     * Creates a new object for searching available products of a sagra.
     *
     * @param con    the connection to the database.
     * @param id_sagra the id of the sagra.

     */
    public SearchAllAvailableProductBySagraDAO(final Connection con, final int id_sagra) {
        super(con);

        this.id_sagra = id_sagra;
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

            rs=pstmt.executeQuery();

            while(rs.next()){
                products.add(new Product(rs.getString("name"),rs.getInt("id_sagra"),
                        rs.getString("description"), rs.getDouble("price"),
                        rs.getBoolean("bar"), rs.getBoolean("available"),
                        rs.getString("category"), rs.getBytes("photo"), rs.getString("photo_type")));
            }

            LOGGER.info("Product(s) with id_sagra %d ", id_sagra);

        }finally{
            if(rs!=null) rs.close();

            if(pstmt!=null) pstmt.close();
        }

        this.outputParam=products;
    }
}
