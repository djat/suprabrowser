/**
 * 
 */
package ss.common;

import java.util.Hashtable;

import ss.framework.networking2.Command;
import ss.framework.networking2.io.IPacketInformationProvider;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.SessionConstants;

/**
 * 
 */
public final class DmCommand extends Command implements IPacketInformationProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7228207220501548669L;

	public static final String DESIRED_PACKET_ID = "DesiredPacketId";

	private String handlerName;

	private Hashtable data;
	
	/**
	 * @param dmpResponse
	 */
	public DmCommand(DmpResponse dmpResponse) {
		this.handlerName = dmpResponse.getHandlerName(); 
		this.data = dmpResponse.getMap();
	}

	/**
	 * @param orig
	 */
	public DmCommand(Hashtable data) {
		this.handlerName = (String)data.get( SessionConstants.PROTOCOL ); 
		this.data = data;
	}

	/**
	 * @return
	 */
	public String getHandlerName() {
		return this.handlerName;
	}

	/**
	 * @return
	 */
	public Hashtable getData() {
		return this.data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + ", DmCommand: " + getHandlerName() + "; Data: " + MapUtils.valuesWithStringKeysToString( this.data );
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.io.IPacketInformationProvider#isShouldSendNotification()
	 */
	public boolean isShouldSendNotification() {
		return MapUtils.hasValue( this.data, SC.SHOW_PROGRESS, "true" );
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.io.IPacketInformationProvider#getDesiredPacketId()
	 */
	public int getDesiredPacketId() {
		Object value = this.data.get( DESIRED_PACKET_ID );
		if ( value instanceof Integer ) {
			return ((Integer)value).intValue(); 
		}
		else {
			return -1;
		}
	}
	
	
	
}
