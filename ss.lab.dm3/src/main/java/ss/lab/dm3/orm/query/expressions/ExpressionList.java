package ss.lab.dm3.orm.query.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.orm.query.Expression;

public class ExpressionList extends Expression implements Iterable<Expression> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1593733261881696475L;

	public enum Junction {
		AND( "&&" ),
		OR( "||" );
		
		private final String display;
		
		private Junction(String display) {
			this.display = display;
		}

		@Override
		public String toString() {
			return this.display;
		}
	}
	
	private Junction junction;
	
	private List<Expression> items = new ArrayList<Expression>();

	/**
	 * @param junction
	 */
	public ExpressionList(Junction junction) {
		super();
		this.junction = junction;
	}

	public ExpressionList add( Expression exp ) {
		if ( !exp.isEvaluable() ) {
			throw new IllegalArgumentException( "Can't add not evaluable expression " + exp );
		}
		this.items.add( exp );
		return this;
	}

	public Junction getJunction() {
		return this.junction;
	}

	public Iterator<Expression> iterator() {
		return this.items.iterator();
	}

	public Expression get(int index) {
		return this.items.get(index);
	}

	public int size() {
		return this.items.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Expression expression : this.items) {
			if ( sb.length() > 0 ) {
				sb.append( " " );
				sb.append( getJunction() );
				sb.append( " " );
			}
			sb.append( expression );
		}
		if ( sb.length() > 0 ) {
			sb.insert( 0, "(" );
			sb.append( ")" );
		}
		return sb.toString();
	}
	
	
	
}
