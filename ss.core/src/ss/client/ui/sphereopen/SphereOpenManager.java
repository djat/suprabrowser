/**
 * 
 */
package ss.client.ui.sphereopen;

import java.util.Hashtable;
import java.util.LinkedList;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.common.ThreadUtils;
import ss.domainmodel.MemberReference;

/**
 * @author zobo
 * 
 */
public class SphereOpenManager {

	private class SphereManagerSpheres {
		int count = 0;
		boolean openedByMessage;
	}

	private static class User {
		String contactName;
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereOpenManager.class);

	public static final SphereOpenManager INSTANCE = new SphereOpenManager();

	private Hashtable<String, SphereManagerSpheres> spheres;

	private Hashtable<String, User> users;

	private Runnable manager;
	
	//private final SphereStateController controller;

	@SuppressWarnings("unused")
	private volatile boolean loading = false;

	//private volatile boolean asking = false;

	private volatile boolean blockerator = true;

	private LinkedList<SphereCreationContext> recieved;

	private LinkedList<String> requested;

	//private volatile String loadingSphereName = null;
	
	private final SphereNamesLoadingController loadingSphereNamesController;

	private SphereOpenManager() {
		logger.info("Sphere loader manager started");
		//this.controller = new SphereStateController();
		this.loadingSphereNamesController = new SphereNamesLoadingController();
		this.spheres = new Hashtable<String, SphereManagerSpheres>();
		this.users = new Hashtable<String, User>();
		this.recieved = new LinkedList<SphereCreationContext>();
		this.requested = new LinkedList<String>();
		this.manager = new Runnable() {

			public void run() {
				while (true) {
					try {
						processNext();
						Thread.sleep(50);
					} catch (InterruptedException ex) {
						logger.error( "Sphere open manager interrupted", ex);
					}
				}
			}

		};
		ThreadUtils.startDemon(this.manager, SphereOpenManager.class);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@SuppressWarnings("unused")
	private void processNextNew() {
		synchronized (this.recieved) {
			if (!this.recieved.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("recieved not empty, processing");
				}
				processRecieve(this.recieved.poll());
			}
		}
		synchronized (this.requested) {
				if ((!this.requested.isEmpty()) && ((!this.blockerator))) {
					if (logger.isDebugEnabled()) {
						logger.debug("requested not empty, processing");
					}
					processRequest(this.requested.poll());
				}
		}
	}

	private void processNext() {
		synchronized (this.recieved) {
			if ( true /*!this.loading*/ ) {
				if (!this.recieved.isEmpty()) {
					if (logger.isDebugEnabled()) {
						logger.debug("recieved not empty, processing");
					}
					this.loading = true;
					processRecieve(this.recieved.poll());
				} else {
					synchronized (this.requested) {
						if ((!this.blockerator)) {
							if (!this.requested.isEmpty()) {
								if (logger.isDebugEnabled()) {
									logger
											.debug("requested not empty, processing");
								}
								processRequest(this.requested.poll());
							}
						}
					}
				}
			}
		}
	}

	public void recieve(final SphereCreationContext context) {
		//logger.info(MapUtils.allValuesToString(data));
		synchronized (this.recieved) {
			this.recieved.offer(context);
			if (logger.isDebugEnabled()) {
				logger.debug("recieve called, number in line: "
						+ this.recieved.size());
			}
		}
	}

	public void request(final String sphereId) {
		requestBody(sphereId, false);
	}

	public void requestPeek(final String sphereId) {
		requestBody(sphereId, true);
	}

	private void requestBody(final String sphereId, final boolean peek) {
		if (isOpening(sphereId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Showing opening sphere: " + sphereId
						+ ", look is already opened?");
			}
			if ((isOpened(sphereId)) && (!peek)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Yes, showing already opened sphere: "
							+ sphereId);
				}
				synchronized (this.users) {
					this.users.remove(sphereId);
				}
				SphereManagerRequestor.show(sphereId);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No, has not opened yet: " + sphereId);
				}
			}
		} else {
			setOpening(sphereId, peek);
			synchronized (this.requested) {
				this.requested.offer(sphereId);
				if (logger.isDebugEnabled()) {
					logger.debug("requested called, number in line: "
							+ this.requested.size());
				}
			}
		}
	}

	private void processRequest(final String sphereId) {
		if (logger.isDebugEnabled()) {
			logger.debug("request process called for " + sphereId);
		}
		synchronized (this.users) {
			User user = this.users.get(sphereId);
			if (user == null) {
				final String type = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getSphereType(sphereId);
				if (logger.isDebugEnabled()) {
					logger.debug("Prerequested user is null, cheking type for sphereId: " + sphereId + ", is: " + type);
				}
				if ((type != null)&&(type.equals("member"))) {
					if (logger.isDebugEnabled()) {
						logger.debug("Type is member, requesting user sphere");
					}
					final String contactName = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getDisplayName(sphereId);
					SphereManagerRequestor.requestUser(contactName, sphereId);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Is not member, group sphere, requesting it");
					}
					SphereManagerRequestor.request(sphereId);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Prerequested user is not null, requesting");
				}
				this.users.remove(sphereId);
				SphereManagerRequestor.requestUser(user.contactName);
			}
		}
	}

	private void processRecieve(final SphereCreationContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("processRecieve called");
		}
		SphereManagerCreator.create(context);
	}

	private boolean isOpening(final String sphereId) {
		synchronized (this.spheres) {
			SphereManagerSpheres s = this.spheres.get(sphereId);
			if (s == null) {
				return false;
			}
			return true;
		}
	}

	private boolean isOpened(final String sphereId) {
		synchronized (this.spheres) {
			SphereManagerSpheres s = this.spheres.get(sphereId);
			if (s == null) {
				return false;
			}
			return s.count > 0;
		}
	}

	private void setOpening(final String sphereId, final boolean opendedByMessage) {
		synchronized (this.spheres) {
			getOrCreate(sphereId, opendedByMessage);
		}
	}

	private SphereManagerSpheres getOrCreate(final String sphereId, final boolean opendedByMessage) {
		SphereManagerSpheres s = this.spheres.get(sphereId);
		if (s == null) {
			s = new SphereManagerSpheres();
			s.openedByMessage = opendedByMessage;
			this.spheres.put(sphereId, s);
		}
		return s;
	}

	private void check(final SphereManagerSpheres s, final String sphereId) {
		if (s.count <= 0) {
			this.spheres.remove(sphereId);
		}
	}

	public void register(final MessagesPane pane) {
		synchronized (this.spheres) {
			String sphereId = pane.getSystemName();
			if (logger.isDebugEnabled()) {
				logger.debug("Registering sphere " + sphereId);
			}
			SphereManagerSpheres s = getOrCreate(sphereId, false);
			s.count++;
			this.loading = false;
			this.blockerator = false;
			DeliverersManager.INSTANCE.register(pane);
		}
	}

	public void responceSphereDenied(String sphereId) {
		synchronized (this.spheres) {
			this.loading = false;
			this.blockerator = false;
			this.spheres.remove(sphereId);
		}
	}
	
	public void responceSphereFailed(String sphereId) {
		synchronized (this.spheres) {
			this.loading = false;
			this.blockerator = false;
			this.spheres.remove(sphereId);
		}
	}

	public void unregister(final MessagesPane pane) {
		synchronized (this.spheres) {
			String sphereId = pane.getSystemName();
			if (logger.isDebugEnabled()) {
				logger.debug("Registering sphere " + sphereId);
			}
			SphereManagerSpheres s = getOrCreate(sphereId, false);
			s.count--;
			check(s, sphereId);
			DeliverersManager.INSTANCE.unregister(pane);
		}
	}

	/**
	 * @param memberContactName
	 * @param listenerSWT
	 * @param isOnline
	 */
	public void requestUser(final String memberContactName) {
			if (logger.isDebugEnabled()) {
				logger.debug("User sphere requested for contact: "
						+ memberContactName);
			}

			final String sphereId = SupraSphereFrame.INSTANCE.client
					.getVerifyAuth().getSystemName(memberContactName);

			if (sphereId == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("SphereId of current user is null, returning...");
				}
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("SphereId of current user is: " + sphereId);
			}
			boolean isRequest = false;
			synchronized (this.users) {
				if (this.users.get(sphereId) == null) {
					User user = new User();
					user.contactName = memberContactName;
					this.users.put(sphereId, user);
					isRequest = true;
				}
			}
			if (isRequest) {
				request(sphereId);
			}
	}

	/**
	 * @param memberContactName
	 * @param listenerSWT
	 * @param isOnline
	 */
	public void requestUserPrivate(final MemberReference member) {
		SphereManagerRequestor.requestUserPrivate(member);
	}

	/**
	 * @param memberContactName
	 * @param listenerSWT
	 * @param isOnline
	 */
	public void requestP2P(final MemberReference member, final String systemName) {
		SphereManagerRequestor.requestP2P(member, systemName);
	}

	/**
	 * @return
	 */
	public synchronized String getSphereNameWhichLoading( final int packetId ) {
		return this.loadingSphereNamesController.getSphereName(packetId);
	}
	
	public synchronized void setSphereNameWhichLoading( final String sphereName, final int packetId ) {
		this.loadingSphereNamesController.setSphereName(sphereName, packetId);
	}
	
	public synchronized boolean isSphereWasRequested(final String sphereId){
		if (logger.isDebugEnabled()) {
			logger.debug("isSphereWasRequested performed for " + sphereId);
		}
		if (sphereId == null){
			logger.error("sphereId is null, returning false anyway");
			return false;
		}
		synchronized (this.spheres) {
			 SphereManagerSpheres s = this.spheres.get(sphereId);
			 if (s == null) {
				 if (logger.isDebugEnabled()) {
					logger.debug("returning false, sphere was opened not manually");
				}
				 return false;
			 } else {
				 boolean toRet = !(s.openedByMessage);
				 if (logger.isDebugEnabled()) {
					logger.debug("returning: " + toRet);
				}
				 return toRet;
			 }
		}
	}
}
