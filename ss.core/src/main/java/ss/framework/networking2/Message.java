package ss.framework.networking2;

import java.io.Serializable;

abstract class Message implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7718884222794330304L; // -2780545497577023031L;
	
	private String sendId = null;
	
	/**
	 * @return the id
	 */
	protected final String getSendId() {
		return this.sendId;
	}

	/**
	 * 
	 */
	final void frozeMessage( String sendId ) {
		checkFrozen();
		this.sendId = sendId;
	}

	/**
	 * @return
	 */
	private boolean isFrozen() {
		return this.sendId != null;
	}	
	
	/**
	 * 
	 */
	protected final void checkFrozen() {
		if ( isFrozen() ) {
			throw new IllegalStateException( "Command already frozen." );			
		}
	}	
	
	protected final void clearSendId() {
		this.sendId = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [messageSendId:" + this.sendId + "]";		
	}

	
};
