/**
 * 
 */
package ss.framework.networking2.keepalive;

import ss.framework.networking2.Event;

public final class KeepAlivePingEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7964983105622368813L;
	
	private final String displayInformation;

	/**
	 * @param displayInformation
	 */
	public KeepAlivePingEvent(final String displayInformation) {
		super();
		this.displayInformation = displayInformation;
	}

	/**
	 * @return the displayInformation
	 */
	public String getDisplayInformation() {
		return this.displayInformation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " KeepAlivePingEvent[" + getDisplayInformation() + "]";
	}
	
	
	
	
}
