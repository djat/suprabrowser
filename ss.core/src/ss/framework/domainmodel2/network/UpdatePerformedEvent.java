/**
 * 
 */
package ss.framework.domainmodel2.network;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.ChangedData;
import ss.framework.networking2.Event;

/**
 *
 */
public final class UpdatePerformedEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8762881799486418612L;

	private final ChangedData changedData;
	
	/**
	 * @param changedData
	 * @param updateId
	 */
	public UpdatePerformedEvent(final ChangedData changedData) {
		super();
		if ( changedData  == null ) {
			throw new ArgumentNullPointerException( "changedData" );
		}
		this.changedData = changedData;
	}

	/**
	 * @return the arg
	 */
	public ChangedData getChangedData() {
		return this.changedData;
	}

}
