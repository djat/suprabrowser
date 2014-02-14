/**
 * Jul 3, 2006 : 2:51:56 PM
 */
package ss.client.ui.controllers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ss.client.ui.MessagesPane;
import ss.util.NameTranslation;

/**
 *
 */
public class MessagePanesController {

	private volatile Map<String, MessagesPane> messagePanes = new Hashtable<String, MessagesPane>();
	
	/**
	 * @param sphereId
	 */
	public MessagesPane findFirstMessagePaneBySphereId(String sphereId) {
		if ( sphereId == null ) {
			return null;
		}
		List<MessagesPane> result = findMessagePanesBySphereId( sphereId );
		return result.size() > 0 ? result.get( 0 ) : null;
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public synchronized List<MessagesPane> findMessagePanesBySphereId(String sphereId) {
		final List<MessagesPane> result = new ArrayList<MessagesPane>();
		if ( sphereId != null ) {
			for( MessagesPane messagePane : this.getAll() ) {
				if ( sphereId.equals( messagePane.getSphereId() ) ) {
					result.add(messagePane);
				}
			}
		}
		return result;
	}

	/**
	 * @param sphereId
	 */
	public synchronized int countOfMessagesPanesForSphere(String sphereId) {
		if ( sphereId == null ) {
			return 0;
		}
		int count = 0;
		for( MessagesPane messagePane : this.getAll() ) {
			if ( sphereId.equals( messagePane.getSphereId() ) ) {
				++ count;
			}
		}
		return count;
	}

	/**
	 * @return
	 */
	public synchronized Iterable<MessagesPane> getAll() {
		return this.messagePanes.values();
	}

	/**
	 * @param displayName
	 * @param messagesPane
	 */
	public synchronized void addMessagesPane(String displayName, MessagesPane messagesPane) {
		prepareToModify().put( displayName + "." + messagesPane.getUniqueId(), messagesPane );
	}

	/**
	 * @param sphereId
	 * @param uniqueId
	 */
	public MessagesPane findMessagePane(String sphereId, String uniqueId) {
		for( MessagesPane messagesPane : findMessagePanesBySphereId(sphereId) ) {
			if ( uniqueId != null && uniqueId.equals( messagesPane.getVerbosedSession().getUniqueId() ) ) {
				return messagesPane;
			}
		}
		return null;
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public MessagesPane findFirstMessagePaneBySphereIdAndWithoutQuery(String sphereId) {
		for ( MessagesPane messagePane : findMessagePanesBySphereId( sphereId ) ) {
			final String queryId = NameTranslation.returnQueryId(messagePane.getSphereDefinition());
			if (queryId == null) {
				return messagePane;
			}
		}
		return null;
	}

	/**
	 * @param displayName
	 * @param session
	 */
	public synchronized void removeMessagesPane(String displayName, Hashtable session) {
		prepareToModify().remove( displayName + "." + (String) session.get("unique_id") );
	}

	/**
	 * @return
	 */
	private synchronized Map<String, MessagesPane> prepareToModify() {
		this.messagePanes = new Hashtable<String, MessagesPane>( this.messagePanes );
		return this.messagePanes;
	}
	

}
