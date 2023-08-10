package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.apache.logging.log4j.core.util.NameUtil.md5;

/**
 * Updates a cashier profile into the database.
 * This is meant to be used by the event admin to change a cashier account's information.
 */
public final class UpdateCashierDAO extends AbstractDAO{
    /**
     * The SQL statement to be executed in case of both username and password editing.
     */
    //USER in the DB saved as (id SERIAL, user VARCHAR NOT NULL, password CHAR, id_sagra INT NOT NULL, admin BOOL NOT NULL)
    private static final String STATEMENT_P = "UPDATE sagrone.user SET username=?, password=? WHERE id=? AND id_sagra=?";
    /**
     * The SQL statement to be executed in case of username editing only.
     */
    private static final String STATEMENT_NP = "UPDATE sagrone.user SET username=? WHERE id=? AND id_sagra=?";


    /**
     /**
     * The user to be updated
     */
    private final User user;
    /**
     * Var where to save new password
     */
    private final String password;

    /**
     * Creates a new object for editing an existing user's credentials.
     *
     * @param con
     *            the connection to the database.
     * @param user
     *            User object corresponding to an existing user (id-identified) with new attributes.
     * @throws NullPointerException the user cannot be null.
     */
    public UpdateCashierDAO(final Connection con, final User user) {
        super(con);

        if (user == null) {
            LOGGER.error("The user cannot be null.");
            throw new NullPointerException("The user cannot be null.");
        }

        this.user = user;
        this.password = user.getPassword(); //warning: no null check, since password CAN be null (username update case).
    }

    /**
     * Execution of the SQL statement updating the cashier.
     *
     * @throws SQLException if some error occurred during query execution
     */
    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        // USER in the DB saved as (id SERIAL, user VARCHAR NOT NULL, password CHAR, id_sagra INT NOT NULL, admin BOOL NOT NULL)
        // Reading from a User object ensures values to be valid (valid username, password either valid or null etc.)
        try {
            if(password!=null){   //request with pw change.
                pstmt = con.prepareStatement(STATEMENT_P);
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, md5(password));
                pstmt.setInt(3, user.getId());
                pstmt.setInt(4, user.getIdSagra());
                pstmt.execute();
            } else {            //request without pw change
                pstmt = con.prepareStatement(STATEMENT_NP);
                pstmt.setString(1, user.getUsername());
                pstmt.setInt(2, user.getId());
                pstmt.setInt(3, user.getIdSagra());
                pstmt.execute();
            }
            LOGGER.info("User %d edited successfully.", user.getId());
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }

    }
}
