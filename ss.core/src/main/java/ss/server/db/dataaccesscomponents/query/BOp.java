/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public enum BOp {
	AND, OR;

	private static final String _OR = " OR ";

	private static final String _AND = " AND ";

	public String eval(IWhere first, IWhere second) {
		switch (this) {
		case AND:
			return first.getWhere() + _AND + second.getWhere();
		case OR:
			return first.getWhere() + _OR + second.getWhere();

		}
		throw new AssertionError("Unknown op: " + this);
	}
}
