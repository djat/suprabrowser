package ss.lab.dm3.persist.backend.hibernate;

import ss.lab.dm3.persist.Query;

public class QueryConvertException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8151628767312684352L;

	/**
	 * @param criteria
	 */
	public QueryConvertException(Query criteria) {
		super( "Can't convert " + criteria );
	}
}
