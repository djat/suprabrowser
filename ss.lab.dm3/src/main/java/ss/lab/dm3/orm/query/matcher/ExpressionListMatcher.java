package ss.lab.dm3.orm.query.matcher;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.query.expressions.ExpressionList;
import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.expressions.ExpressionList.Junction;

public abstract class ExpressionListMatcher extends ExpressionMatcher {

	protected final ExpressionMatcher[] matchers;
	
	/**
	 * @param expression
	 */
	ExpressionListMatcher(ExpressionList expression,List<ExpressionMatcher> matchers) {
		super(expression);
		this.matchers = matchers.toArray( new ExpressionMatcher[ matchers.size() ] );
	}

	@Override
	public void collect(MatcherContext context) {
		if ( this.matchers.length == 0 ) {
			// TODO [dg] think about empty expression list
			// this.log.warn("Empty expression list " + this );
			context.add( context.getSource() );
		}
		else if ( this.matchers.length == 1 ) {
			this.matchers[ 0 ].collect( context );
		}
		else {
			collectByMatchers( context );
		}
	}

	public static ExpressionListMatcher create( ExpressionMatcherFactory factory, ExpressionList expressions ) {
		List<ExpressionMatcher> matchers = new ArrayList<ExpressionMatcher>(); 
		for( Expression expression : expressions ) {
			matchers.add( factory.create( expression ) );
		}
		final Junction junction = expressions.getJunction();
		if ( junction == Junction.AND ) {
			return new AndExpressionListMatcher( expressions, matchers );
		}
		else if ( junction == Junction.OR ) {
			return new OrExpressionListMatcher( expressions, matchers );
		}
		else {
			throw new IllegalArgumentException( "Unknow junction " +junction + " in " + expressions );
		}
	}
	
	@Override
	public final boolean match(MappedObject object) {
		if ( this.matchers.length == 0 ) {
			// TODO [dg] think about empty expression list
			// this.log.warn("Empty expression list " + this );
			return true;
		}
		else if ( this.matchers.length == 1 ) {
			return this.matchers[ 0 ].match( object );
		}
		else {
			return matchByMatchers( object );
		}
	}

	/**
	 * @param context
	 */
	protected abstract void collectByMatchers(MatcherContext context);
	
	protected abstract boolean matchByMatchers(MappedObject object);

	public static class AndExpressionListMatcher extends ExpressionListMatcher {

		/**
		 * @param expression
		 * @param matchers
		 */
		AndExpressionListMatcher(ExpressionList expression, List<ExpressionMatcher> matchers) {
			super(expression, matchers);
		}

		/* (non-Javadoc)
		 * @see ss.lab.dm3.orm.query.matcher.ExpressionListMatcher#collectByMatchers(ss.lab.dm3.orm.query.matcher.MatcherContext)
		 */
		@Override
		protected void collectByMatchers(MatcherContext context) {
			if ( this.matchers.length < 2 ) {
				throw new IllegalStateException( "Should be 2 or more matchers in " + this );
			}
			AddMatcherContext addContext = new AddMatcherContext( context );
			this.matchers[ 0 ].collect(addContext);
			if (this.log.isDebugEnabled()) {
				this.log.debug( "At start, collect from first matcher " + addContext );
			}
			for (int n = 1; n < this.matchers.length; n++) {
				ExpressionMatcher matcher = this.matchers[n];
				addContext.replaceSourceByCollectedAndResetCollected();
				matcher.collect( addContext );
			}
			if (this.log.isDebugEnabled()) {
				this.log.debug( "In result has " + addContext );
			}
			context.add( addContext.getCollected() );
		}

		@Override
		public boolean matchByMatchers(MappedObject object) {
			for( ExpressionMatcher matcher : this.matchers ) {
				if ( !matcher.match(object) ) {
					return false;
				}
			}
			return true;
		}
		
	}
	
	public static class OrExpressionListMatcher extends ExpressionListMatcher {

		/**
		 * @param expression
		 * @param matchers
		 */
		OrExpressionListMatcher(ExpressionList expression, List<ExpressionMatcher> matchers) {
			super(expression, matchers);
		}

		/* (non-Javadoc)
		 * @see ss.lab.dm3.orm.query.matcher.ExpressionListMatcher#collectByMatchers(ss.lab.dm3.orm.query.matcher.MatcherContext)
		 */
		@Override
		protected void collectByMatchers(MatcherContext context) {
			for( ExpressionMatcher matcher : this.matchers ) {
				matcher.collect(context);
			}			
		}

		@Override
		public boolean matchByMatchers(MappedObject object) {
			for( ExpressionMatcher matcher : this.matchers ) {
				if ( matcher.match(object) ) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	
}
