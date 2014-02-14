/**
 * 
 */
package ss.framework.domainmodel2.network;

import java.io.Serializable;

import ss.framework.domainmodel2.ChangedData;

/**
 *
 */
public class UpdateResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8650283130967500882L;

	private final ChangedData changedData;
	
	
	/**
	 * @param changedData
	 */
	public UpdateResult(final ChangedData changedData) {
		super();
		this.changedData = changedData;
	}


	/**
	 * @return the changedData
	 */
	public ChangedData getChangedData() {
		return this.changedData;
	}



}
