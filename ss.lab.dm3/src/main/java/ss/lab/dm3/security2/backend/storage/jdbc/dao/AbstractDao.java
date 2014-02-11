package ss.lab.dm3.security2.backend.storage.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;


public class AbstractDao {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private DaoProvider provider;
	
	private final Set<Statement> stToConsume = new HashSet<Statement>();
	private final Set<ResultSet> rsToConsume = new HashSet<ResultSet>();
	
	/**
	 * @param deleteUserByName
	 * @throws SQLException 
	 */
	protected  PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.provider.getConnection().prepareStatement(sql);
	}

	/**
	 * @param rs
	 */
	protected void addToConsume(ResultSet rs) {
		this.rsToConsume.add( rs );
	}
	
	public DaoProvider getProvider() {
		return this.provider;
	}

	void setProvider(DaoProvider provider) {
		this.provider = provider;
	}

	void dispose() {
		for( ResultSet rs : this.rsToConsume ) {
			try {
				rs.close();
			}
			catch (SQLException ex) {
				this.log.error( "Can't close result set " + rs, ex );
			}
		}
		this.rsToConsume.clear();
		
		for( Statement statement : this.stToConsume  ) {
			try {
				statement.close();
			}
			catch (SQLException ex) {
				this.log.error( "Can't close statement " + statement, ex );
			}
		}
		this.stToConsume.clear();		
	}
	
}
