package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Updates a product into the database.
 * This is meant to be used by the event admin to change a product's information.
 */
public final class UpdateProductDAO extends AbstractDAO {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "UPDATE sagrone.product SET name=?, description=?, price=?, bar=?, available=?, category=?, photo=?, photo_type=? WHERE name=? AND id_sagra=?";

    /**
     * The product to be updated
     */
    private final Product product;
    /**
     * Identifier of the product to delete
     */
    private final String oldName;

    /**
     * Identifier of the sagra where the product is served
     */
    private final int oldIdSagra;

    /**
     * Creates a new object to modify the price and the availability of the product.
     * @param con: the connection to the database.
     * @param product: an existing Product object.
     * @param oldName: previous name of the product.
     * @param oldIdSagra: the id of the sagra where we are changing the product.
     * @throws NullPointerException the product cannot be null.
     */
    public UpdateProductDAO(final Connection con, final Product product,final String oldName, final int oldIdSagra){

        super(con);

        if (product == null) {
            LOGGER.error("The product cannot be null.");
            throw new NullPointerException("The product cannot be null.");
        }

        this.product = product;
        this.oldName=oldName;
        this.oldIdSagra =oldIdSagra;
    }

    /**
     *
     * @throws SQLException if some error occurred while accessing the database.
     */
    protected final void doAccess() throws SQLException{

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(STATEMENT);

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setBoolean(4, product.getBar());
            pstmt.setBoolean(5, product.getAvailable());
            pstmt.setString(6,product.getCategory());
            pstmt.setBytes(7,product.getPhoto());
            pstmt.setString(8,product.getPhotoType());
            pstmt.setString(9, oldName);
            pstmt.setInt(10, oldIdSagra);

            pstmt.execute();

            LOGGER.info("Product %s edited successfully.", product.getName());

        }finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

}
