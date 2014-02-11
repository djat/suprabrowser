package ss.lab.dm3.orm.query;

import java.io.Serializable;

public class Expression implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1920791931242031009L;
	
	/**
	 * Returns true if this expression can be evaluated via matcher
	 * @return
	 */
	public boolean isEvaluable() {
		return true;
	}
	
}
