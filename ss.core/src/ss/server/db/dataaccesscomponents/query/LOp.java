/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public enum LOp {
	equal, less, more,emore,eless;
	public String eval(String name, String value) {
		switch (this) {
		case equal:
			return "(" + name + "=" + value + ")";
		case less:
			return "(" + name + "<" + value + ")";
		case more:
			return "(" + name + ">" + value + ")";
		case eless:
			return "(" + name + "<=" + value + ")";
		case emore:
			return "(" + name + ">=" + value + ")";
		}
		throw new AssertionError("Unknown op: " + this);
	}

}
