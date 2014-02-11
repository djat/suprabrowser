package ss.lab.dm3.orm.query.trash;

import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.ext.Condition;
import ss.lab.dm3.orm.MappedObject;

public class ConditionBuilder<T extends MappedObject> {

	public enum Operator {
		EQUAL,
		GREATE,
		LESS,
		LIKE,
		NOT_EQ
	}
	
	// private Class<T> beanClass;
	
	/**
	 * @param class1
	 */
	public ConditionBuilder(Class<T> beanClass) {
		// this.beanClass = beanClass;
	}

	
	public ConditionBuilder<T> setCondition( Condition condition ) {
		return null;
	}
	
	public Expression and( Expression ... expressions ) {
		return null;
	}
	
	/**
	 * @return
	 */
	public ConditionBuilder<T> and( String property, Operator operator, Object value ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	/**
	 * @return
	 */
	public Expression or( Expression ... expressions ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected Expression eq(String string, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Expression ne(String string, Object otherValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Condition build() {
		return null;
	}
	
}
