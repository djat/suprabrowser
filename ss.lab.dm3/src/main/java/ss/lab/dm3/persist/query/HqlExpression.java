package ss.lab.dm3.persist.query;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.index.MatchUtils;

public class HqlExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1311048925145938881L;

	private final String hql;

	private final Object[] parameters;
	
	/**
	 * @param hql
	 */
	public HqlExpression(String hql, Object[] parameters) {
		super();
		this.hql = hql;
		this.parameters = new Object[ parameters.length ];
		for (int n = 0; n < parameters.length; n++) {
			this.parameters[ n ] = MatchUtils.getSerializable( parameters[n] );
			if ( this.parameters[ n ] == null ) {
				throw new NullPointerException( "Parameter " + n + " is null '" + parameters[n] + "'" );
			}
		}
	}

	public String getHql() {
		return this.hql;
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
		tsb.append( "hql", this.hql );
		tsb.append( "parameters", this.parameters );
		return tsb.toString();
	}

	
	
}