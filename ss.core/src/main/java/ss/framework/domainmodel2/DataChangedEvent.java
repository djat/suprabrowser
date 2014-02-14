/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public final class DataChangedEvent {

	private final ChangedData changedData;
	
	
	/**
	 * @param changedData
	 * @param updateId
	 * @param externalUpdate
	 */
	public DataChangedEvent(ChangedData changedData, boolean externalUpdate) {
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
