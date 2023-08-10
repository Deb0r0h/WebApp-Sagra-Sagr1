package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * Update an order into the database.
 * <p>
 * Cannnot use it to edit payment information.
 */
public final class UpdateOrderDAO extends AbstractDAO {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "UPDATE sagrone.order SET client_name=?, email=?, client_num=?, table_number=?, order_time=? WHERE id=?";

    /**
     * The order to be updated.
     */
    private final Order order;

    /**
     * Creates a new object to modify
     * @param con the connection to the database.
     * @param order an existing Order object.
     * @throws NullPointerException an order cannot be null.
     */
    public UpdateOrderDAO(final Connection con, final Order order){

        super(con);

        if (order == null) {
            LOGGER.error("The order cannot be null.");
            throw new NullPointerException("The order cannot be null.");
        }

        this.order = order;
    }

    /**
     *
     * @throws SQLException if some error occurred while accessing the database
     */
    @Override
    protected final void doAccess() throws SQLException{

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(STATEMENT);

            pstmt.setString(1, order.getClientName());
            pstmt.setString(2, order.getEmail());
            pstmt.setShort(3, order.getClientNum());
            pstmt.setString(4, order.getTableNumber());
            pstmt.setTimestamp(5, order.getOrderTime());
            pstmt.setInt(6,order.getId());

            pstmt.execute();

            LOGGER.info("Order [%s] edited successfully.",order.getId());

        }finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

}
