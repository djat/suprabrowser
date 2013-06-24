/**
 * 
 */
package ss.framework.domainmodel2.network;

import java.io.Serializable;
import java.util.List;

import ss.framework.domainmodel2.Record;

/**
 *
 */
public final class SelectResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4548494942438574030L;

	private final List<Record> records;

	/**
	 * @param records
	 */
	public SelectResult(final List<Record> records) {
		super();
		this.records = records;
	}

	/**
	 * @return the records
	 */
	public List<Record> getRecords() {
		return this.records;
	}
	
	

}
