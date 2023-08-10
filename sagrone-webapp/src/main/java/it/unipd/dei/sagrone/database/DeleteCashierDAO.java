package it.unipd.dei.sagrone.database;

import it.unipd.dei.sagrone.resource.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Deletes a cashier from the User table.
 */
public final class DeleteCashierDAO extends AbstractDAO<Integer> {
	/**
	 * SQL statement template to delete a user
	 */
	private static final String STATEMENT = "DELETE FROM sagrone.user WHERE id = ? AND id_sagra = ? AND admin = false";

	/**
	 *	The cashier to be deleted
	 */
	private final User user;

	/**
	 * Creates a new DAO object for deleting an existing cashier of a specific Sagra.
	 *
	 * @param con the connection to be used for accessing the database.
	 * @param user the cashier user to be deleted
	 *
	 * @see it.unipd.dei.sagrone.database.SearchCashierDAO
	 */
	public DeleteCashierDAO(final Connection con, final User user) {
		super(con);
		this.user = user;
	}

	/**
	 * Execution of the SQL statement and removal of the cashier.
	 * The {@link it.unipd.dei.sagrone.database.AbstractDAO#outputParam} is set true if the statement is correctly executed,
	 * false otherwise.<br>
	 * The {@link it.unipd.dei.sagrone.database.AbstractDAO#outputParam} is set in the following way:
	 * <ul>
	 *     <li><b>1</b> if the user was deleted</li>
	 *     <li><b>0</b> if no-one was deleted</li>
	 *     <li><b>-1</b> if more than one user were deleted (unexpected behaviour)</li>
	 * </ul>
	 *
	 * @throws SQLException if some error occurred during query execution
	 */
	@Override
	protected void doAccess() throws SQLException {

		outputParam = -1;
		try (PreparedStatement sql_template = con.prepareStatement(STATEMENT)) {
			sql_template.setInt(1, user.getId());
			sql_template.setInt(2, user.getIdSagra());

			int res = sql_template.executeUpdate();
			assert res < 2;								// Check if more than one user (row) was deleted

			outputParam = res ;						// Optional output parameter if something changed

			LOGGER.info("Cashier number %d successfully deleted", user.getId());
		}catch (AssertionError e){
			// Due to DB constraint on users id, this should not happen
			LOGGER.error("More than one user deleted id: %d - username: %s - id_sagra: %d",
					user.getId(),
					user.getUsername(),
					user.getIdSagra());
			throw new IllegalStateException("More than one user has been deleted");
		}
	}
}
