package it.unipd.dei.sagrone.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Deletes a product from the Product table.
 */
public final class DeleteProductDAO extends AbstractDAO {
    /**
     * SQL statement template to delete a product
     */
    private static final String STATEMENT = "DELETE FROM sagrone.product WHERE name = ? AND id_sagra = ? ";

    /**
     * Identifier of the product to delete
     */
    private final String name;

    /**
     * Identifier of the sagra where the product is served
     */
    private final int id_sagra;

    /**
     * Creates a new DAO object for deleting an existing product of a specific Sagra.
     *
     * @param con    The connection to be used for accessing the database.
     * @param name The name of the product to delete.
     * @param id_sagra The ID of the Sagra the product belongs to
     */
    public DeleteProductDAO(final Connection con, final String name, final int id_sagra) {
        super(con);

        this.name = name;
        this.id_sagra = id_sagra;
    }

    /**
     * Execution of the SQL statement and removal of the cashier.
     * The {@link it.unipd.dei.sagrone.database.AbstractDAO#outputParam} is set true if the statement is correctly executed,
     * false otherwise
     *
     * @throws SQLException if some error occurred during query execution
     */

    @Override
    protected void doAccess() throws SQLException {

        outputParam = false;
        try (PreparedStatement sql_template = con.prepareStatement(STATEMENT)) {
            sql_template.setString(1, name);
            sql_template.setInt(2, id_sagra);

            // The output parameter is modified only for a double check
            outputParam = sql_template.executeUpdate() == 1;

            LOGGER.info("Product named %s successfully deleted", name);
        }
    }


}
