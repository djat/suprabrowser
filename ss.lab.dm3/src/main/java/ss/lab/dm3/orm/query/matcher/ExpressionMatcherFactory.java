package ss.lab.dm3.orm.query.matcher;

import ss.lab.dm3.orm.query.expressions.ExpressionList;
import ss.lab.dm3.orm.query.expressions.SimpleExpression;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.query.Expression;

public class ExpressionMatcherFactory {
	
	private final BeanMapper<?extends MappedObject> beanMapper;
	
	/**
	 * @param beanMapper
	 */
	public ExpressionMatcherFactory(BeanMapper<? extends MappedObject> beanMapper) {
		super();
		this.beanMapper = beanMapper;
	}

	/**
	 * @param expression
	 */
	public ExpressionMatcher create(Expression expression) {
		if ( expression instanceof ExpressionList ) {
			ExpressionList expressions = (ExpressionList) expression;
			return ExpressionListMatcher.create( this, expressions );
		}
		else if ( expression instanceof SimpleExpression ) {
			SimpleExpression simpleExpression = (SimpleExpression) expression;
			return SimpleExpressionMatcher.create( this.beanMapper, simpleExpression );
		}
		else {
			throw new IllegalArgumentException( "Unknown expression type " + expression );
		}
	}
	
	public static ExpressionMatcher createMatchAll() {
		return new ExpressionMatcher( null ) {
			@Override
			public boolean match(MappedObject object) {
				return true;
			}			
		};
	}
	
	public static ExpressionMatcher createMatchNothing() {
		return new ExpressionMatcher( null ) {
			@Override
			public boolean match(MappedObject object) {
				return false;
			}			
		};
	}
}
