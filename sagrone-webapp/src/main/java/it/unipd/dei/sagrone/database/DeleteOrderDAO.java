package it.unipd.dei.sagrone.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Deletes an order from the Order and OrderContent tables.
 */
public final class DeleteOrderDAO extends AbstractDAO {
    /**
     * SQL statement template to delete an order in Order table.
     */
    private static final String STATEMENT_ORDER = "DELETE FROM sagrone.order WHERE id = ?";

    /**
     * Identifier of the order to delete
     */
    private final int id_order;

    /**
     * Creates a new DAO object for deleting an existing order of a specific Sagra.
     *
     * @param con      The connection to be used for accessing the database.
     * @param id_order The identifier of the order to delete.
     */
    public DeleteOrderDAO(final Connection con, final int id_order) {
        super(con);

        this.id_order = id_order;
    }

    /**
     * Execution of the SQL statement and removal of the order.
     * The {@link it.unipd.dei.sagrone.database.AbstractDAO#outputParam} is set true if the statement is correctly executed,
     * false otherwise
     *
     * @throws SQLException if some error occurred during query execution
     */
    @Override
    protected void doAccess() throws SQLException {

        outputParam = false;
        try (PreparedStatement sql_template = con.prepareStatement(STATEMENT_ORDER)) {
            sql_template.setInt(1, id_order);

            int numDeleted = sql_template.executeUpdate();
            if (numDeleted == 1) {
                outputParam = true;
                LOGGER.info("Order [%s] successfully deleted", id_order);
            } else {
                LOGGER.error("Order [%s] not correctly deleted", id_order);
            }
        } catch (SQLException e) {
            LOGGER.error("Error during delete, Order [%s] not correctly deleted", id_order);
        }
    }

}