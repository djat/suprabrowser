/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

import ss.server.db.DbUtils;

/**
 * @author dankosedin
 * 
 */
public class InLaw extends BaseWhere {

	private String fieldName;

	private String[] fieldValues;

	public InLaw(String name, String... values) {
		this.fieldName = name;
		this.fieldValues = values;
	}

	public String getWhere() {
		return "(" + this.fieldName + " in (" + getValues() + "))";
	}

	/**
	 * @return
	 */
	private String getValues() {
		String values = null;
		for (String value : this.fieldValues) {
			if (values == null) {
				values = DbUtils.quote(value);
			} else {
				values += "," + DbUtils.quote(value);
			}
		}
		return values;
	}

}
