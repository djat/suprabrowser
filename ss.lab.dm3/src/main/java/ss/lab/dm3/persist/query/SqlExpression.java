package ss.lab.dm3.persist.query;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.index.MatchUtils;

public class SqlExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 831472613659441720L;
	
	private String sql;

	private final Object[] parameters;
	
	/**
	 * @param sql
	 */
	public SqlExpression(String sql, Object[] parameters) {
		super();
		this.sql = sql;		
		this.parameters = new Object[ parameters.length ];
		for (int n = 0; n < parameters.length; n++) {
			this.parameters[ n ] = MatchUtils.getSerializable( parameters[n] );
			if ( this.parameters[ n ] == null ) {
				throw new NullPointerException( "Parameter " + n + " is null '" + parameters[n] + "'" );
			}
		}
	}

	public String getSql() {
		return this.sql;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return
	 */
	public Object[] getParameters() {
		return this.parameters;
	}

	@Override
	public boolean isEvaluable() {
		return false;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "sql", this.sql );
		tsb.append( "parameters", this.parameters );
		return tsb.toString();
	}

}
