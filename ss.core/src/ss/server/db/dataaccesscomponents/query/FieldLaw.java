/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

import ss.server.db.DbUtils;

/**
 * @author dankosedin
 * 
 */
public class FieldLaw extends BaseWhere {

	private String fieldValue;

	private String fieldName;

	private LOp op;

	public FieldLaw(String value, String name) {
		this(value, name, LOp.equal);
	}

	public FieldLaw(String value, String name, LOp op) {
		this.fieldValue = value;
		this.fieldName = name;
		this.op = op;
	}

	public String getWhere() {
		return this.op.eval(this.fieldName, DbUtils.quote(this.fieldValue));
	}

}
