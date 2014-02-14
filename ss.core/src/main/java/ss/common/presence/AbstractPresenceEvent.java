package ss.common.presence;

import ss.framework.networking2.Event;

public abstract class AbstractPresenceEvent extends Event {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1259739457444721295L;
	
	private final String sphereId;

	/**
	 * @param sphereId
	 */
	public AbstractPresenceEvent(String sphereId) {
		super();
		this.sphereId = sphereId;
	}

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId;
	}
	
	/**
	 * @return true if server handler should update actvity info 
	 */
	public abstract boolean isActivityUpdate();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " for [SphereId:" + this.sphereId + "]";		
	}	
	
	
};
