package ss.lab.dm3.orm.query.expressions;

import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.index.MatchUtils;

public class SimpleExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5975137487714989015L;

	public enum Operator {
		EQ( "==" ),
		NE( "!=" );

		private final String display;
		
		private Operator(String display) {
			this.display = display;
		}

		@Override
		public String toString() {
			return this.display;
		}
		
	};
	
	private String propertyName;
	
	private Operator operator;
	
	private Object value;

	/**
	 * @param propertyName
	 * @param operator
	 * @param value
	 */
	public SimpleExpression(String propertyName, Operator operator, Object value) {
		super();
		this.propertyName = propertyName;
		this.operator = operator;
		this.value = MatchUtils.getSerializable( value );
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public Operator getOperator() {
		return this.operator;
	}

	public Object getValue() {
		return this.value;
	}

	@Override
	public String toString() {				
		return getPropertyName() + " " + getOperator() + " '" + getValue() + "'";
	}
	
}
