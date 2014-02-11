package ss.lab.dm3.persist.query;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.query.Expression;

/**
 * @author dmitry
 *
 */
public class LuceneExpression extends Expression {

	private static final long serialVersionUID = -2506521294792559958L;
	
	private final String text;

	public LuceneExpression(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return this.text;
	}
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "text", this.text );
		return tsb.toString();
	}
	
	@Override
	public boolean isEvaluable() {
		return false;
	}

}
