package ss.lab.dm3.orm.query;

import ss.lab.dm3.orm.query.expressions.ExpressionList;
import ss.lab.dm3.orm.query.expressions.SimpleExpression;
import ss.lab.dm3.orm.query.expressions.ExpressionList.Junction;

/**
 * @author dmitry
 *
 */
public class ExpressionHelper {

	public static ExpressionList and() {
		return new ExpressionList( ExpressionList.Junction.AND );
	}
	
	public static ExpressionList or() {
		return new ExpressionList( ExpressionList.Junction.OR );
	}
	
	public static SimpleExpression ne(String propertyName,Object value) {
		return new SimpleExpression(propertyName, SimpleExpression.Operator.NE, value);
	}
	
	public static SimpleExpression eq(String propertyName,Object value) {
		return new SimpleExpression(propertyName, SimpleExpression.Operator.EQ, value);
	}
	
	public static ExpressionList and( Expression ... expressions ) {
		// Optimized when first expression is AND expression list 
		if ( expressions.length > 0 && 
			 expressions[ 0 ] instanceof ExpressionList &&
			 ((ExpressionList)expressions[ 0 ]).getJunction() == ExpressionList.Junction.AND ) {
			ExpressionList resultList = (ExpressionList) expressions[ 0 ];
			for (int n = 1; n < expressions.length; n++) {
				Expression expression = expressions[n];
				resultList.add( expression );
			}
			return resultList;
		}
		else {
			ExpressionList resultList = and();
			for (Expression expression : expressions) {
				resultList.add(expression);			
			}
			return resultList;
		}
	}
	
	/**
	 * @param expressions
	 * @return
	 */
	public static ExpressionList or( Expression ... expressions ) {
		ExpressionList list = or();
		for (Expression expression : expressions) {
			if ( expression instanceof ExpressionList ) {
				ExpressionList innerList = (ExpressionList) expression;
				// Inline inner or 
				if ( innerList.getJunction() == Junction.OR ) {
					for( Expression innerExpression : innerList ) {
						list.add( innerExpression );
					}
					continue;
				}
				// Empty AND does not matter
				else if ( innerList.getJunction() == Junction.AND && innerList.size() == 0 ) {
					continue;
				}
			}
			list.add(expression);
		}
		return list;
	}
	
}

