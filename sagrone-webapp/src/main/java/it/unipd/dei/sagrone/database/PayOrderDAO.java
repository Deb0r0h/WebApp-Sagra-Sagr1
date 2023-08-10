
package it.unipd.dei.sagrone.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Sets an order as paid by inserting the user id and the payment timestamp.
 */
public class PayOrderDAO extends AbstractDAO{
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "UPDATE sagrone.order SET id_user=?, payment_time=? WHERE id=? ;";

    /**
     * The id of the order
     */
    private final int id_order;

    /**
     * The id of the user
     */
    private final int id_user;

    /**
     * The payment time
     */
    private final Timestamp payment_time;

    /**
     * Creates a new object for setting an order as paid by specifying the user performing the payment and the payment time.
     *
     * @param con the connection to the database.
     * @param id_order the id of the order to pay.
     * @param id_user the id of the user performing the payment.
     * @param payment_time the payment time.
     * @throws NullPointerException The payment time cannot be null.
     *
     */
    public PayOrderDAO(Connection con, final int id_order, final int id_user, final Timestamp payment_time) {
        super(con);

        if (payment_time == null) {
            LOGGER.error("Payment time cannot be null");
            throw new NullPointerException("Payment time cannot be null");
        }

        this.id_order = id_order;
        this.id_user = id_user;
        this.payment_time = payment_time;
    }

    @Override
    protected void doAccess() throws SQLException {
        PreparedStatement pstmt=null;

        try{
            pstmt= con.prepareStatement(STATEMENT);
            pstmt.setInt(1,id_user);
            pstmt.setTimestamp(2, payment_time);
            pstmt.setInt(3,id_order);

            int res= pstmt.executeUpdate();

            if(res>=2) throw new SQLException("Wrongly executed update.");

            LOGGER.info("Order %d successfully updated", id_order);
        }finally{
            if(pstmt!=null) pstmt.close();
        }

    }
}
