package it.unipd.dei.sagrone.database;

import java.sql.*;

import it.unipd.dei.sagrone.resource.Order;
import it.unipd.dei.sagrone.resource.OrderContent;
import java.util.List;

/**
 * Insert an order into the tables Order and OrderContent
 */
public final class InsertOrderDAO extends AbstractDAO<Integer>{

    /**
     * The SQL statement to be executed for insert into Order table if the id is unknown
     */
    private static final String STATEMENT_ORDER_WITHOUT_ID = "INSERT INTO sagrone.order (client_name, email, client_num, table_number, id_user, order_time, payment_time) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;";

    /**
     * The SQL statement to be executed for insert into Order table if the id is known
     */
    private static final String STATEMENT_ORDER_WITH_ID = "INSERT INTO sagrone.order (id, client_name, email, client_num, table_number, id_user, order_time, payment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";

    /**
     * The SQL statement to be executed for insert into OrderContent table
     */
    private static final String STATEMENT_ORDERCONTENT = "INSERT INTO sagrone.order_content (id_sagra, id_order, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";

    /**
     *  The SQL statement to be executed to delete an order (and its content) if it already exists.
     *  Due to database construction, all rows, in {@code sagrone.order_content} table, related to the deleted order will be deleted as well
     */
    private static final String STATEMENT_DELETEORDER = "DELETE FROM sagrone.order WHERE id = ?;";

    private final Order order;

    /**
     * Constructor for the class. If an existing order (with a valid id) is passed as parameter,
     * {@link InsertOrderDAO#doAccess()} will try to delete it form the database before the insertion occurs
     * @param con connection to the database
     * @param order Order Object to insert (must contain the list of OrderContent)
     */
    public InsertOrderDAO(final Connection con, final Order order){
        super(con);
        if(order == null){
            LOGGER.error("The Order cannot be null.");
            throw new IllegalStateException("The Order cannot be null.");
        }
        if(order.getOrderContent() == null || order.getOrderContent().isEmpty()){
            LOGGER.error("The content of the order cannot be null or empty.");
            throw new IllegalStateException("The content of the order cannot be null or empty.");
        }
        if(order.getClientNum() < 0){
            LOGGER.error("The number of clients cannot be negative.");
            throw new IllegalStateException("The number of clients cannot be negative.");
        }

        this.order = order;
    }

    /**
     * Order Insert execution.
     * The {@link it.unipd.dei.sagrone.database.AbstractDAO#outputParam} is set to the ID of the created Order if the statement is correctly executed,
     * -1 otherwise
     * @throws SQLException if the execution of the query goes wrong.
     */
    @Override
    public void doAccess() throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null ;
        int id_order = -1;

        // Prepare eventually to rollback changes
        con.setAutoCommit(false);

        try{
            // If an order already exists (but with different dishes), it must be deleted before adding the new one
            if(order.getId() > 0){
                id_order = order.getId();
                pstmt = con.prepareStatement(STATEMENT_DELETEORDER);
                pstmt.setInt(1, order.getId());
                if(pstmt.executeUpdate() > 0)
                    LOGGER.info("Order with id %d correctly removed. A new insertion will follow..", order.getId());

                // Insert a new order with the previous id
                pstmt = con.prepareStatement(STATEMENT_ORDER_WITH_ID);
                pstmt.setInt(1, id_order);
                pstmt.setString(2,order.getClientName());
                pstmt.setString(3,order.getEmail());
                pstmt.setShort(4,order.getClientNum());
                pstmt.setString(5,order.getTableNumber());
                if(order.getIdUser() >= 0)
                    pstmt.setInt(6,order.getIdUser());
                else
                    pstmt.setNull(6, Types.INTEGER);

                pstmt.setTimestamp(7,order.getOrderTime());
                pstmt.setTimestamp(8,order.getPaymentTime());
            }else{
                // Else the id is set automatically by the DB
                pstmt = con.prepareStatement(STATEMENT_ORDER_WITHOUT_ID);
                pstmt.setString(1,order.getClientName());
                pstmt.setString(2,order.getEmail());
                pstmt.setShort(3,order.getClientNum());
                pstmt.setString(4,order.getTableNumber());
                if(order.getIdUser() >= 0)
                    pstmt.setInt(5,order.getIdUser());
                else
                    pstmt.setNull(5, Types.INTEGER);
                pstmt.setTimestamp(6,order.getOrderTime());
                pstmt.setTimestamp(7,order.getPaymentTime());
            }

            rs = pstmt.executeQuery();

            // If the order id is generated by te DB
            if (id_order < 0 && rs.next()) {
                id_order = rs.getInt("id");
            }
            if(id_order == -1){//error
                LOGGER.error("The insert doesn't return the ID");
                throw new IllegalStateException("Problems getting the id.");
            }
            LOGGER.info("The order [%s] has been entered correctly",id_order);

            //create OrderContent elements
            List<OrderContent> order_content_list = order.getOrderContent();

            for (int i = 0; i < order_content_list.size(); i++) {
                OrderContent order_content = order_content_list.get(i);

                pstmt = con.prepareStatement(STATEMENT_ORDERCONTENT);

                pstmt.setInt(1,order_content.getIdSagra());
                pstmt.setInt(2,id_order);
                pstmt.setString(3,order_content.getProductName());
                pstmt.setDouble(4,order_content.getPrice());
                pstmt.setShort(5,order_content.getQuantity());

                pstmt.execute();
                LOGGER.info("The Content number [%s] of Order ID [%s] has been insert correctly",i,id_order);
            }

            con.commit();

        }catch(SQLException e){
            con.rollback();     // If something wrong happens, rollback changes
            throw e;            // And pass the exception to upper caller
        }finally {
            if(rs!=null) rs.close();
            if (pstmt != null){pstmt.close();}
        }

        this.outputParam = id_order;//return the generated ID
    }

}