package ss.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

@ss.refactor.Refactoring(classify=SupraSphereRefactor.class)
public class DmpFilter {

	/**
	 * @param handler
	 * @return
	 */
	public static Iterable<DialogsMainPeer> filter(Iterable<DialogsMainPeer> handlers,
			Condition condition) {
		List<DialogsMainPeer> filteredPeers = new ArrayList<DialogsMainPeer>();
		for( DialogsMainPeer handler : handlers ) {
			if ( condition.macth( handler ) ) {
				filteredPeers.add( handler );
			}
		}
		return filteredPeers;
	}

	/**
	 * @param handlers
	 * @param sphereId
	 * @return
	 */
	public static Iterable<DialogsMainPeer> filter(Iterable<DialogsMainPeer> handlers,
			String sphereId) {
		return filter( handlers, new Condition( sphereId ) );
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public static Iterable<DialogsMainPeer> filter(String sphereId) {
		return filter( getAllHandlers(), sphereId );
	}

	/**
	 * @param filteredHandlers
	 * @param session
	 * @return
	 */
	@Refactoring( classify=SupraSphereRefactor.class, message="Very strange functionality, because filter can returns only two values: handlers or empty list")
	public static Iterable<DialogsMainPeer> filter(Iterable<DialogsMainPeer> handlers,
			Hashtable session) {
		String sphereId = (String) session.get( SC.SPHERE_ID );
		String memberLogin = (String) session.get( SC.USERNAME );
		Iterator<DialogsMainPeer> iterator = handlers.iterator();
		if ( iterator.hasNext() ) {
			DialogsMainPeer firstPeer = iterator.next();
			if ( firstPeer.getVerifyAuth().isSphereEnabledForMember(sphereId, memberLogin) ) {
				return handlers;
			}
			else {
				return Collections.emptyList();
			}
		}
		else {
			return handlers;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public static Iterable<DialogsMainPeer> filterOrAdmin(String sphereId) {
		return filter( getAllHandlers(), new Condition( sphereId, true ) );
	}

	/**
	 * @param dmpResponse
	 */
	public static void sendToAll(DmpResponse dmpResponse) {
		for( DialogsMainPeer peer : getAllHandlers() ) {
			peer.sendFromQueue(dmpResponse);
		}
	}

	/**
	 * @return
	 */
	private static Iterable<DialogsMainPeer> getAllHandlers() {
		return DialogsMainPeerManager.INSTANCE.getHandlers();
	}

	/**
	 * @param dmpResponse
	 * @param locSphereId
	 */
	public static void sendToMembers(DmpResponse dmpResponse, String sphereId) {
		for( DialogsMainPeer peer : filter(sphereId) ) {
			peer.sendFromQueue(dmpResponse);
		}
	}
	
	static class Condition {
	
		private final String sphereId;
		
		private final boolean forceForAdmin;

		/**
		 * @param sphereId
		 * @param forceForAdmin
		 */
		public Condition(String sphereId) {
			this( sphereId, false );		
		}
		
		/**
		 * @param sphereId
		 * @param forceForAdmin
		 */
		public Condition(String sphereId, boolean forceForAdmin) {
			super();
			this.sphereId = sphereId;
			this.forceForAdmin = forceForAdmin;
		}
		
		public boolean macth( DialogsMainPeer handler ) {
			VerifyAuth verifyAuth = handler.getVerifyAuth();
			if ( verifyAuth == null ) {
				throw new NullPointerException( "Invalid handler " + handler );
			}
			if ( this.forceForAdmin ) {
				if ( verifyAuth.isAdmin() ) {
					return true;
				}
			}
			String login = verifyAuth.getUserSession().getUserLogin();
			return verifyAuth.isSphereEnabledForMember(this.sphereId,login);
			
		}
	}

}
