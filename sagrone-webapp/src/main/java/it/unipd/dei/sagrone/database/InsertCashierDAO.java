package it.unipd.dei.sagrone.database;

import static org.apache.logging.log4j.core.util.NameUtil.md5;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import it.unipd.dei.sagrone.resource.User;

/**
 * insert a cashier into the User table
 */
public final class InsertCashierDAO extends AbstractDAO{

    private static final String STATEMENT= "INSERT INTO sagrone.user (username, password, id_sagra, admin) VALUES(?, ?, ?, ?)";

    private final User user;

    /**
     *  Constructor for the class
     * @param con connection to the database
     * @param user User Object to be insert
     */
    public InsertCashierDAO(final Connection con, final User user){
        super(con);
        this.user=user;
    }

    public final void doAccess() throws SQLException {
        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setString(1,user.getUsername());
            pstmt.setString(2,md5(user.getPassword()));
            pstmt.setInt(3,user.getIdSagra());
            pstmt.setBoolean(4, user.isAdmin());
            pstmt.execute();
            LOGGER.info("The user " +user.getId()+ " has been entered correctly");
        } finally {

                if (pstmt != null){
                    pstmt.close();
                }
            }
    }

}
