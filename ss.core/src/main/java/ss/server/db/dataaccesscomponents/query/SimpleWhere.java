/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;


/**
 * @author dankosedin
 * 
 */
public class SimpleWhere extends BaseWhere {

	private String where;

	public SimpleWhere(String where) {
		this.where = where;
	}

	public String getWhere() {

		return "(" + this.where + ")";
	}

}
