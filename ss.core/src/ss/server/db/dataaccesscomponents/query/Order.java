/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public class Order {
	private static final String DESC = " DESC ";

	private static final String ORDER_BY = " ORDER BY ";

	private String field;

	private boolean desc = false;

	public Order(String field) {
		this(field, false);
	}

	public Order(String field, boolean desc) {
		this.field = field;
		this.desc = desc;
	}

	public String getOrder() {
		if (this.field != null) {
			return ORDER_BY + this.field + ((this.desc) ? DESC : "");
		} else {
			return null;
		}
	}

}
