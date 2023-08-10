
package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Searches the products given its id (composed by the identifier of the sagra and name).
 */
public class SearchProductByIdDAO extends AbstractDAO<Product> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT name,id_sagra,description,price,bar,available,category,photo,photo_type FROM sagrone.product WHERE id_sagra= ? AND name = ?;";

    /**
     * The id of the sagra
     */
    private final int id_sagra;

    /**
     * The name of the product
     */
    private final String name;

    /**
     *
     * Creates a new object for searching products by id.
     *
     * @param con the connection to the database
     * @param id_sagra the id of the sagra of the product
     * @param name name of the product
     * @throws NullPointerException The Id of the sagra cannot be null.
     */
    public SearchProductByIdDAO(final Connection con, final int id_sagra, final String name) {
        super(con);

        if (name == null) {
            LOGGER.error("The category cannot be null.");
            throw new NullPointerException("the name of the product cannot be null");
        }

        this.id_sagra = id_sagra;
        this.name = name;
    }

    @Override
    public void doAccess() throws Exception {
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        //the returned product
        Product p=null;

        try{
            pstmt= con.prepareStatement(STATEMENT);
            pstmt.setInt(1,id_sagra);
            pstmt.setString(2, name);

            rs=pstmt.executeQuery();

            if(rs.next()){
                p=new Product(rs.getString("name"),rs.getInt("id_sagra"),
                        rs.getString("description"), rs.getDouble("price"),
                        rs.getBoolean("bar"), rs.getBoolean("available"),
                        rs.getString("category"), rs.getBytes("photo"), rs.getString("photo_type"));
            }


            LOGGER.info("Product with id_sagra %d and of name %s successfully retrieved", id_sagra, name);
        }finally{
            if(rs!=null) rs.close();

            if(pstmt!=null) pstmt.close();
        }

        this.outputParam=p;
    }
}
