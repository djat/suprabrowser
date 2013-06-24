/**
 * 
 */
package ss.client.ui.sphereopen;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereStatement;

/**
 * @author zobo
 * 
 */
public class SphereManagerRequestor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereManagerRequestor.class);

	public static void request(final String sphereId, final DialogsMainCli client) {
		
		Thread t = new Thread(){

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					final SupraSphereFrame sF = SupraSphereFrame.INSTANCE;

					final Document doc = client.getSphereDefinition(sphereId);

					if (doc == null) {
						logger.error("Cannot find sphere definition for sphereId: " + sphereId + ", sphere not opened");
						SphereOpenManager.INSTANCE.responceSphereFailed(sphereId);
						return;
					}

					final SphereStatement sphereSt = SphereStatement.wrap(doc);

					final String systemName = sphereSt.getSystemName();
					
					if (sF != null) {
						sF.tabbedPane.putSphereToTabQueue(sphereId);
					}

					Hashtable newSession = null;
					if (sF != null) {
						try {
						newSession = (Hashtable) sF.getRegisteredSession(client.getVerifyAuth().getSupraSphereName(),
							"DialogsMainCli").clone();
						}
						catch( NullPointerException ex ) {
							logger.error( "Can't get session", ex);
						}
					}
					if ( newSession == null ) {
						newSession =  (Hashtable)client.session.clone();
					}

					newSession.put("sphere_id", systemName);

					String sphereType = client.getVerifyAuth().getSphereType(systemName);
					newSession.put("sphere_type", sphereType);

					client.searchSphere(newSession, doc, "false");
				} catch (Throwable ex) {
					logger.error("Loading sphere for sphereId: " + sphereId + " failed", ex);
					SphereOpenManager.INSTANCE.responceSphereFailed(sphereId);
				}
			}
			
		};
		ThreadUtils.startDemon( t, "Sphere request" );
	}
	
	public static void request(final String sphereId) {
		request(sphereId, SupraSphereFrame.INSTANCE.client);
	}

	static void show(final String sphereId) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				final SupraSphereFrame supraSphereFrame = SupraSphereFrame.INSTANCE;
				String displayName = supraSphereFrame.client.getVerifyAuth().getDisplayName(sphereId);
				supraSphereFrame.tabbedPane.selectTabByTitle(displayName);
			}
		});
	}

	/**
	 * @param contactname
	 */
	static void requestUser(final String contactname) {
		final String systemName = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getSystemName(contactname);
		requestUser(contactname, systemName);
	}
	
	/**
	 * @param contactName
	 * @param sphereId
	 */
	@SuppressWarnings("unchecked")
	static void requestUser(final String contactname, final String systemName) {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Hashtable newSession = (Hashtable) SupraSphereFrame.INSTANCE.getRegisteredSession(
							(String)SupraSphereFrame.INSTANCE.client.getSession().get("supra_sphere"),
							"DialogsMainCli").clone();
					newSession.put("sphere_id", systemName);
					logger.info("searchSphere");
					SupraSphereFrame.INSTANCE.client.searchSphere(newSession, null, "false");
				} catch (Throwable ex) {
					logger.error("Loading sphere for user: " + contactname + " failed", ex);
					SphereOpenManager.INSTANCE.responceSphereFailed(systemName);
				}
			}
		};
		ThreadUtils.startDemon(runnable, "User request");
	}
	
	static void requestUserPrivate(final MemberReference member) {
		final DialogsMainCli client = SupraSphereFrame.INSTANCE.client;
		Runnable runnable = new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Hashtable newSession = (Hashtable) SupraSphereFrame.INSTANCE
						.getRegisteredSession(
								(String) client.getSession().get("supra_sphere"),
								"DialogsMainCli").clone();
				logger.info("search private sphere : "+member.getContactName());
				client.searchPrivateSphere(newSession, member);
			}
		};
		ThreadUtils.startDemon(runnable, "User private sphere request");
	}
	
	static void requestP2P(final MemberReference member, final String systemName) {
		final DialogsMainCli client = SupraSphereFrame.INSTANCE.client;
		Runnable runnable = new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				try {
				Hashtable newSession = (Hashtable) SupraSphereFrame.INSTANCE
						.getRegisteredSession(
								(String) client.getSession().get("supra_sphere"),
								"DialogsMainCli").clone();
				client.searchP2PSphere(newSession, member, systemName);
				} catch (Throwable ex) {
					logger.error("Loading sphere for member: " + member.getContactName() + " failed", ex);
					//SphereOpenManager.INSTANCE.responceSphereFailed(systemName);
				}
			}
		};
		ThreadUtils.startDemon(runnable, "P2P sphere request");
	}
}
