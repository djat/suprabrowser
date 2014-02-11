package ss.lab.dm3.orm.query.matcher;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.orm.query.expressions.SimpleExpression;
import ss.lab.dm3.orm.query.expressions.SimpleExpression.Operator;
import ss.lab.dm3.orm.query.index.Index;
import ss.lab.dm3.orm.query.index.IndexHelper;
import ss.lab.dm3.orm.query.index.MatchUtils;

public abstract class SimpleExpressionMatcher extends ExpressionMatcher {

	protected final Property<?> property;
	
	protected final Object expectedValue;
	
	/**
	 * @param beanMapper
	 * @param expression
	 */
	public SimpleExpressionMatcher(SimpleExpression expression, Property<?> property, Object value) {
		super( expression );
		this.property = property;
		this.expectedValue = MatchUtils.getMatchable( value, this.property );
	}


	public static SimpleExpressionMatcher create(BeanMapper<?> beanMapper, SimpleExpression expression) {
		final Operator operator = expression.getOperator();
		final Property<?> property = beanMapper.getProperty( expression.getPropertyName() );
		final Object value = expression.getValue();
		if ( SimpleExpression.Operator.EQ == operator ) {
			return new EqMatcher( expression, property, value );
		}
		else if ( SimpleExpression.Operator.NE == operator ) {
			return new NeMatcher( expression, property, value );	
		}
		else {
			throw new IllegalArgumentException( "Unknown expression operator " + operator + " in " + expression );
		} 
	}
	
	public Property<?> getProperty() {
		return this.property;
	}

	@Override
	public final boolean match( MappedObject object ) {
		return matchValue( MatchUtils.getMatchable( this.property.getValue( object ), this.property ) );
	}
	
	@Override
	public void collect(MatcherContext context) {
		final Iterable<? extends MappedObject> source = context.getSource();
		Index index = IndexHelper.get( source, this.property );
		if ( index != null ) {			
			// Has index, use it! 
			collectByIndex( context, index, source );
		}
		else {
			// Has not index, traverse through all items
			unindexdCollect(context, source);
		}
	}

	protected final void unindexdCollect(MatcherContext context,
			final Iterable<? extends MappedObject> source) {
		for( MappedObject object : source ) {
			if ( match( object ) ) {
				context.add( object );
			}
		}
	}

	/**
	 * @param context
	 * @param index
	 * @param source 
	 */
	protected abstract void collectByIndex(MatcherContext context, Index index, Iterable<? extends MappedObject> source);

	protected abstract boolean matchValue( Object propertyValue );
	
	private static class EqMatcher extends SimpleExpressionMatcher {

		/**
		 * @param property
		 * @param value
		 */
		public EqMatcher(SimpleExpression expression,Property<?> property, Object value) {
			super(expression,property, value);
		}

		/* (non-Javadoc)
		 * @see ss.lab.dm3.orm.query.matcher.SimpleExpressionMatcher#matchProperty(java.lang.Object)
		 */
		@Override
		protected boolean matchValue(Object propertyValue) {
			if ( this.expectedValue == propertyValue ) {
				return true;
			}
			if ( this.expectedValue == null || propertyValue == null ) {
				return false;
			}
			return this.expectedValue.equals( propertyValue );
		}

		@Override
		protected void collectByIndex(MatcherContext context, Index index,
				Iterable<? extends MappedObject> source) {
			index.eq( context, this.expectedValue );
		}
		
	}
	
	public static class NeMatcher extends SimpleExpressionMatcher {

		/**
		 * @param property
		 * @param value
		 */
		public NeMatcher(SimpleExpression expression,Property<?> property, Object value) {
			super(expression,property, value);
		}

		/* (non-Javadoc)
		 * @see ss.lab.dm3.orm.query.matcher.SimpleExpressionMatcher#matchProperty(java.lang.Object)
		 */
		@Override
		protected boolean matchValue(Object propertyValue) {
			if ( this.expectedValue == propertyValue ) {
				return false;
			}
			if ( this.expectedValue == null || propertyValue == null ) {
				return true;
			}
			return !this.expectedValue.equals( propertyValue );
		}

		/* (non-Javadoc)
		 * @see ss.lab.dm3.orm.query.matcher.SimpleExpressionMatcher#collectByIndex(ss.lab.dm3.orm.query.matcher.MatcherContext, ss.lab.dm3.orm.query.index.Index, java.lang.Iterable)
		 */
		@Override
		protected void collectByIndex(MatcherContext context, Index index,
				Iterable<? extends MappedObject> source) {
			unindexdCollect(context, source);
		}


	}

}
