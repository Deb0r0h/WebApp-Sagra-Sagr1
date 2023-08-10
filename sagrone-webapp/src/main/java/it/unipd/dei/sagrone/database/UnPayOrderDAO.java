package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Data object to mark an order as not paid if erroneously it was marked so
 */
public class UnPayOrderDAO extends AbstractDAO<Integer>{
    /**
     * SQL statement to be executed to mark an order as unpaid
     */
    private static final String STATEMENT = "UPDATE sagrone.order SET id_user = NULL, payment_time = NULL WHERE id = ?";

    /**
     *	The id of the order to flag as not paid
     */
    private final int id_order;

    /**
     * Let the user set an order as unpaid.
     * @param conn the connection to the database
     * @param id_order id of the order that has to be set as unpaid
     */
    public UnPayOrderDAO(final Connection conn, final int id_order){
        super(conn);
        this.id_order = id_order;
    }

    /**
     * Order update execution.
     * The {@link it.unipd.dei.sagrone.database.AbstractDAO#outputParam} is set in the following way:
     * <ul>
     *     <li><b>1</b> if the order was correctly modified</li>
     *     <li><b>0</b> if no order was modified</li>
     *     <li><b>-1</b> if more than one order were modified</li>
     * </ul>
     * @throws SQLException if the execution of the query goes wrong.
     */
    @Override
    protected void doAccess() throws SQLException {
        outputParam = -1;
        String logMessage;
        try (PreparedStatement sql_template = con.prepareStatement(STATEMENT)) {
            sql_template.setInt(1,id_order);

            int res = sql_template.executeUpdate();
            assert res < 2;                         // Check if more than one order (row) has been updated

            outputParam = res;

            logMessage = res == 1 ? "Order %d successfully set as unpaid" : "No order %d was set as unpaid";

            LOGGER.info(logMessage, id_order);
        }catch (AssertionError err){
            outputParam = -1;                       // An optional error code is given as output result
            LOGGER.error("Two or more order set as unpaid. Order ID: %d",
                    id_order);
        }
    }
}
