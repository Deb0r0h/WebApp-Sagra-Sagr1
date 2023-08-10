package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * Insert a product into the table Product
 */
public final class InsertProductDAO extends AbstractDAO
{
    /**
     * The SQL statement to be executed for insert into Product table
     */
    private static final String STATEMENT = "INSERT INTO sagrone.product (name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES (?,?,?,?,?,?,?,?,?)";

    /**
     * @param product Product Object to insert
     */
    private final Product product;


    /**
     * Constructor for the class.
     * @param con connection to the database.
     * @param product product that is inserted in the database.
     * @throws NullPointerException The product cannot be null.
     */
    public InsertProductDAO(final Connection con, final Product product)
    {
        super(con);

        if(product == null)
        {
            LOGGER.error("The product is not valid");
            throw new NullPointerException("The product is not valid");
        }

        this.product = product;
    }

    /**
     *
     * Product Insert execution.
     * @throws SQLException if some error occurred during query execution
     */
    @Override
    protected void doAccess() throws SQLException
    {
        PreparedStatement pstmt = null;
        try
        {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setString(1,product.getName());
            pstmt.setInt(2,product.getIdSagra());
            pstmt.setString(3,product.getDescription());
            pstmt.setDouble(4,product.getPrice());
            pstmt.setBoolean(5,product.getBar());
            pstmt.setBoolean(6,product.getAvailable());
            pstmt.setString(7,product.getCategory());
            pstmt.setBytes(8,product.getPhoto());
            pstmt.setString(9,product.getPhotoType());


            pstmt.execute();
            LOGGER.info("The product "+product.getName()+" has been insert correctly");
        }
        finally
        {
            if(pstmt != null)
                pstmt.close();
        }
    }
}
