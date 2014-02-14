/**
 * 
 */
package ss.client.networking.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;

import ss.common.MapUtils;
import ss.framework.networking2.Command;
import ss.server.networking.SC;

/**
 *
 */
public abstract class AbstractDialogMainCommand extends Command {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2186914798473541675L;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractDialogMainCommand.class);
	
	private final Hashtable<String,Serializable> args = new Hashtable<String,Serializable>();

	/**
	 * @param key
	 * @param value
	 */
	public void putArg(String key, Document value) {
		putArg( key, (Serializable) value );		
	}

	/**
	 * @param key
	 * @param value
	 */
	public void putArg(String key, String value) {
		putArg( key, (Serializable) value );
	}



	/**
	 * @param key	
	 * @param value
	 */
	public void putArg(String key, boolean value) {
		putArg( key, value ? "true" : "false" );
	}
	/**
	 * @param session
	 */
	public void putSessionArg(Hashtable session) {
		// TODO: remove pasphrase ? 
		putArg( SC.SESSION, session );		
	}

	/**
	 * @param key
	 * @param value
	 */
	public void putArg(String key, Serializable value) {
		if ( value != null ) {
			if ( this.args.containsKey(key) ) {
				logger.error( "Key " + key + " already has value "+ getObjectArg(key) );
			}
			this.args.put(key, value);
		}
	}
	

	/**
	 * @param key
	 * @return
	 */
	public Serializable getObjectArg(String key ) {
		return this.args.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String getStringArg(String key) {
		return (String)getObjectArg(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public Document getDocumentArg(String key) {
		return (Document)getObjectArg(key);
	}
	
	/**
	 * @return
	 */
	public Hashtable getSessionArg() {
		return (Hashtable)getObjectArg( SC.SESSION );
	}
	
	/**
	 * @param key
	 * @return
	 */
	public boolean getBooleanArg(String key) {
		String value = getStringArg(key);
		return value != null && value.equals( "true" );
	}
	
	protected synchronized List getLazyList( String key ) {
		ArrayList list = (ArrayList) getObjectArg( key );
		if ( list == null ) {
			list  = new ArrayList();
			putArg( key, list );
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "Command " );
		sb.append( super.toString() );
		sb.append( ": " );
		sb.append( MapUtils.valuesWithStringKeysToString( this.args ) );
		return sb.toString();
	}

	
}

