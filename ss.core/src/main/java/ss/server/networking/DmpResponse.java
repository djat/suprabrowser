/**
 * 
 */
package ss.server.networking;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.ArgumentNullPointerException;
import ss.common.VerifyAuth;
import ss.util.SessionConstants;

/**
 * 
 */
public class DmpResponse {

	private final Hashtable map;

	/**
	 * Construct empty session.
	 */
	public DmpResponse() {
		this(new Hashtable());
	}

	/**
	 * @param map
	 */
	public DmpResponse(final Hashtable map) {
		if ( map == null ) {
			throw new ArgumentNullPointerException( "map" );
		}
		this.map = map;
	}

	/**
	 * @return
	 */
	public Hashtable getMap() {
		return this.map;
	}

	/**
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void setStringValue(String key, String value) {
		if ( value == null ) {
			this.map.remove(key);
		}
		else {
			this.map.put(key, value);
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void setMapValue(String key, Hashtable value) {
		this.map.put(key, value);
	}

	/**
	 * @param key
	 * @param document
	 */
	@SuppressWarnings("unchecked")
	public void setDocumentValue(String key, Document document) {
		if ( document != null ) {
			this.map.put(key, document);
		}
		else {
			this.map.remove(key);
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void setVerifyAuthValue(String key, VerifyAuth value) {
		this.map.put(key, value);
	}

	/**
	 * @return
	 */
	public DmpResponse copy() {
		return new DmpResponse((Hashtable) this.map.clone());
	}

	/**
	 * @param contacts_only
	 * @param contactsOnly
	 */
	@SuppressWarnings("unchecked")
	public void setVectorValue(String key, Vector value) {
		this.map.put(key, value);
	}

	/**
	 * @param is_online
	 * @param isOnline
	 */
	@SuppressWarnings("unchecked")
	public void setBooleanValue(String key, boolean value) {
		this.map.put(key, value ? "true" : "false" );
	}

	/**
	 * @return
	 */
	public String getSphereId() {
		return getStringValue( SessionConstants.SPHERE_ID );
	}

	/**
	 * @return
	 */
	public void setSphereId( String sphereId ) {
		setStringValue( SessionConstants.SPHERE_ID, sphereId );
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String getStringValue(String key) {
		return (String)this.map.get(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean getBooleanValue(String key) {
		String strValue = getStringValue(key);
		return strValue != null && strValue.equals( "true" ); 
	}

	/**
	 * @return
	 */
	public String getHandlerName() {
		return getStringValue( SessionConstants.PROTOCOL );
	}

	/**
	 * @param show_progress
	 * @param largetPacketId
	 */
	@SuppressWarnings("unchecked")
	public void setIntValue(String key, int value) {
		this.map.put(key, value );
	}

	/**
	 * @return
	 */
	public boolean hasKey(String key) {
		return this.map.containsKey(key);
	}

	/**
	 * @return
	 */
	public Object getObject(String key ) {
		return key != null ? this.map.get(key) : null;
	}

	/**
	 * @return
	 */
	public String getProtocolName() {
		return (String) this.map.get(SC.PROTOCOL);
	}
	
	
}
