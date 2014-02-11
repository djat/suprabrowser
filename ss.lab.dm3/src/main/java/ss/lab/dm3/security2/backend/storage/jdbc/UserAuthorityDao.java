package ss.lab.dm3.security2.backend.storage.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.backend.storage.jdbc.dao.AbstractDao;
import ss.lab.dm3.security2.backend.storage.jdbc.dao.DaoException;

public class UserAuthorityDao extends AbstractDao {

	private static final String INSERT_USER_AUTHORITY = "INSERT INTO dm3s_user_authority (user_id,authority_id) VALUES (?,?)";
	private static final String DELETE_USER_AUTHORITY = "DELETE FROM dm3s_user_authority WHERE user_id = ? AND authority_id = ?";
	private static final String SELECT_USER_AUTHORITY = "SELECT (authority_id) FROM dm3s_user_authority WHERE user_id = ?";

	/**
	 * @param id
	 * @param authority
	 */
	public void addUserAuthority(Long userId, Authority authority) {
		try {
			PreparedStatement ps = prepareStatement( INSERT_USER_AUTHORITY );
			ps.setLong( 1, userId );
			ps.setLong( 2, authority.getId() );
			ps.execute();
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't add user authority. UserId " + userId + " authority " + authority , ex );
		}
	}

	public void removeUserAuthority(Long userId, Authority authority) {
		try {
			PreparedStatement ps = prepareStatement( DELETE_USER_AUTHORITY );
			ps.setLong( 1, userId );
			ps.setLong( 2, authority.getId() );
			ps.execute();
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't add user authority. UserId " + userId + " authority " + authority , ex );
		}
	}
	
	public Set<Authority> getUserAuthorities(Long userId) {
		Set<Authority> authorities = new HashSet<Authority>();
		try {
			final PreparedStatement ps = prepareStatement( SELECT_USER_AUTHORITY );
			ps.setLong( 1, userId );
			final ResultSet rs = ps.executeQuery();
			addToConsume(rs);
			while( rs.next() ) {
				Long authorityId = rs.getLong( 1 );
				Authority authority = resolveAuthority( authorityId );
				authorities.add( authority );
			}
			return authorities;
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't add user authority. UserId " + userId, ex );
		}
	}

	/**
	 * @param authorityId
	 * @return
	 */
	private Authority resolveAuthority(Long authorityId) {
		return Authority.getBuitin( authorityId );
	}
	
}
