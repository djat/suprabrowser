package ss.lab.dm3.security2.backend.storage.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ss.lab.dm3.security2.UserDetails;
import ss.lab.dm3.security2.backend.storage.jdbc.dao.AbstractDao;
import ss.lab.dm3.security2.backend.storage.jdbc.dao.DaoException;

public class UserDetailsDao extends AbstractDao {

	public static final String DELETE_USER_BY_NAME = "DELETE from dm3s_user WHERE name=?";
	public static final String SELECT_USER_BY_NAME = "SELECT id,name,description FROM dm3s_user WHERE name=?";
	private static final String INSERT_USER_WITH_NAME = "INSERT INTO dm3s_user (name) VALUES (?)";
	
	public UserDetails find( String accountName ) {
		try {
			PreparedStatement st = super.prepareStatement(SELECT_USER_BY_NAME);
			st.setString( 1, accountName );
			final ResultSet rs = st.executeQuery();
			addToConsume( rs );
			if ( rs.next() ) {
				final long userId = rs.getLong( 1 );
				// rs.getString( 3 ); not used yet
				return new UserDetails( userId, rs.getString( 2 ) ); 
			}
			else {
				return null;
			}
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't find user by '" + accountName + "'", ex );
		}
	}
	
	public UserDetails get( String accountName ) {
		UserDetails userDetails = find( accountName );
		if ( userDetails == null ) {
			throw new DaoException( "Account not found '" + accountName + "'" );
		}
		return userDetails;
	}

	public void delete(String accountName ) {
		try {
			PreparedStatement st = prepareStatement( DELETE_USER_BY_NAME );
			st.setString( 1, accountName );
			st.execute();
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't delete user by '" + accountName + "'", ex );
		}
	}
	
	public UserDetails create(String accountName ) {
		try {
			PreparedStatement st = prepareStatement( INSERT_USER_WITH_NAME );
			st.setString( 1, accountName );
			st.execute();
			return get(accountName);
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't delete user by '" + accountName + "'", ex );
		}
	}	
	
}
