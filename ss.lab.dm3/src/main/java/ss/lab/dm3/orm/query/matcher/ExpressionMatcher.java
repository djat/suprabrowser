package ss.lab.dm3.orm.query.matcher;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.query.Expression;

public abstract class ExpressionMatcher {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Expression expression;
	
	/**
	 * @param expression
	 */
	public ExpressionMatcher(Expression expression) {
		super();
		this.expression = expression;
	}

	public void collect( MatcherContext context ) {
		for( MappedObject object : context.getSource() ) {
			if ( match( object ) ) {
				context.add( object );
			}
		}
	}
	
	public abstract boolean match( MappedObject object );

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "expression", this.expression );
		return tsb.toString();
	}

	public Expression getExpression() {
		return this.expression;
	}


	
}
