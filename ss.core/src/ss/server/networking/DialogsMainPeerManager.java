/**
 * 
 */
package ss.server.networking;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.MapUtils;
import ss.server.MethodProcessing;

/**
 * 
 */
public class DialogsMainPeerManager {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DialogsMainPeerManager.class);

	/**
	 * Singleton instance
	 */
	public final static DialogsMainPeerManager INSTANCE = new DialogsMainPeerManager();

	private volatile List<DialogsMainPeer> handlers = new ArrayList<DialogsMainPeer>();

	private DialogsMainPeerManager() {
	}

	/**
	 * @return Returns the handlers.
	 */
	public synchronized Iterable<DialogsMainPeer> getHandlers() {
		return this.handlers;
	}

	/**
	 * @param peer
	 */
	public synchronized void register(DialogsMainPeer peer) {
		List<DialogsMainPeer> newHandlers = new ArrayList<DialogsMainPeer>( this.handlers );
		newHandlers.add(peer);
		this.handlers = newHandlers;
	}


	/**
	 * @param peer
	 */
	public synchronized void unregister(DialogsMainPeer peer) {
		List<DialogsMainPeer> newHandlers = new ArrayList<DialogsMainPeer>( this.handlers );
		newHandlers.remove(peer);
		this.handlers = newHandlers;
	}
	
	/**
	 * @param loginSphere
	 * @param contactDoc
	 * @param supraSphereDoc
	 */
	public void updateViryAuthAndSupraSphereDocument(String loginSphere,
			Document contactDoc, Document supraSphereDoc) {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue("protocol", "updateVerify");
		DialogsMainPeer.sendUpdateToAllMembersOfSphere(contactDoc, loginSphere);
		for (DialogsMainPeer handler : getHandlers()) {
			try {
				handler.getVerifyAuth().setSphereDocument(supraSphereDoc);
				dmpResponse.setMapValue("session", handler.getSession());
				dmpResponse.setVerifyAuthValue("verifyAuth", handler
						.getVerifyAuth());
				handler.sendFromQueue(dmpResponse);
			} catch (Exception e) {
				logger.error( "Can't updateViryAuthAndSupraSphereDocument", e);
			}
		}
	}

	/**
	 * @param handlerName
	 */
	public DialogsMainPeer findHandler(String handlerName) {
		if (handlerName == null ) {
			return null;
		}
		for( DialogsMainPeer peer : getHandlers() ) {
			if ( handlerName.equals(peer.getName()) ) {
				return peer;
			}
		}
		return null;
	}

	/**
	 * @param login
	 */
	public boolean isUserOnline(String login) {
		if (login == null) {
			return false;
		}
		for( DialogsMainPeer peer : getHandlers() ) {
			if ( login.equals(peer.getUserLogin()) ) {
				return true;
			}
		}
		return false;
	}


}
