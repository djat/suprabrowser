package ss.client.networking;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.event.tagging.ClientEventMethodProcessing;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.client.ui.root.RootTab;
import ss.common.SSProtocolConstants;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.util.NameTranslation;
import ss.util.SessionConstants;

class InsertOperation implements Runnable {


	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InsertOperation.class);
	
	private final DialogsMainCli dmc;
	
	private final Vector<PostponedUpdate> insertQueue; 
	
	/**
	 * @param dmc
	 * @param insertQueue
	 */
	public InsertOperation(final DialogsMainCli dmc, final Vector<PostponedUpdate> insertQueue) {
		super();
		this.dmc = dmc;
		this.insertQueue = insertQueue;
	}

	public void run() {
		try {
			performRun();
		}
		catch( Exception ex ) {
			logger.error( "Insert operation failed", ex );
		}		
		finally {
			this.dmc.notifyInsertOperationEnded( this );
		}
	}

	protected void performRun() throws Exception {
		while (this.insertQueue.size() > 0) {
			try {
				PostponedUpdate update;
				synchronized (this.insertQueue) {
					if (this.insertQueue.size() > 0) {
						update = this.insertQueue.elementAt(0);
						this.insertQueue.removeElementAt(0);
					} else {
						break;
					}
				}
				final Statement statement = update.getStatement();
//				if (logger.isDebugEnabled()) {
//					logger.debug("statement : "+statement);
//				}
				final String typeOfUpdate =  update.getTypeOfUpdate();
				if (typeOfUpdate.equals(SSProtocolConstants.RECALL)) {
					recall(update.getMap(), statement);
				} else if (typeOfUpdate.equals(SSProtocolConstants.VOTE)) {
					vote(update.getMap(), statement);
				}
				else {
					notVoteAndNotRecall(update.getMap(), statement, typeOfUpdate);
				}  
			} catch (Exception e) {
				logger.error( "Cannot process update", e );
			}
		}
	}

	/**
	 * @param update
	 * @param statement
	 */
	private void vote(Hashtable update, final Statement statement) {
		String sphere = (String) update.get(SessionConstants.SPHERE);
		//for (
		//MessagesPane mp_other : this.dmc.getSF().getMessagePanesController().findMessagePanesBySphereId(sphere) ) {
			//TODO: very strange code
			//mp_other.removeThenInsert(statement.getBindedDocument(), true);
			DeliverersManager.INSTANCE.insert(
					DeliverersManager.FACTORY.createReplace(statement.getBindedDocument(), null, sphere, true));
		//}
	}

	/**
	 * @param update
	 * @param statement
	 */
	private void recall(Hashtable update, final Statement statement) {
		String current_sphere = statement.getCurrentSphere();
		if (current_sphere == null) {
			logger.info("current_sphere wasnot set in recall");
		}
		for (MessagesPane mp_other : this.dmc.getSF()
				.getMessagePanesController().findMessagePanesBySphereId(current_sphere)) {
			logger.debug( "recalling message from " + current_sphere );
			mp_other.recall((Document) update.get("document"));
		}
	}

	/**
	 * @param update
	 * @param statement
	 * @param updateType
	 */
	private void notVoteAndNotRecall(Hashtable update,
			final Statement statement, final String updateType) {
		if (statement.isContact()) {
			((RootTab) SupraSphereFrame.INSTANCE.getRootTab())
			.refreshPeopleTables(ContactStatement.wrap(statement
					.getBindedDocument()));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("handling update " + updateType
					+ " for statement " + statement.toShortString());
		}
		final String thread_type = statement.getThreadType();
		String sphere_tab = (String) update.get(SessionConstants.SPHERE);

		boolean external = false;
		boolean foundForReconfirm = false;
		logger.info("spheretab: " + sphere_tab);
		boolean updateProcessed = false;

		for (MessagesPane mp_other : this.dmc.getSF()
				.getMessagePanesController().getAll()) {
			final String queryOnlyString = (String) update
			.get(SessionConstants.QUERY_ONLY);
			final boolean queryOnly = queryOnlyString != null
			&& queryOnlyString.equals("true");

			String sphere_id = (String) mp_other.getRawSession().get(
					SessionConstants.SPHERE_ID2);
			if (external == true) {
				sphere_id = (String) update
				.get(SessionConstants.REMOTE_SPHERE_ID);
			}
			if (sphere_id.equals(sphere_tab)) {
				logger.info("Found it: " + sphere_id);
				final boolean shouldInsert = shouldInsert(update, mp_other,
						queryOnly);
				if (shouldInsert) {
					sphere_id = (String) mp_other.getRawSession().get(
							SessionConstants.SPHERE_ID2);
					updateProcessed = true;
					if (statement.isConfirmedDefined()) {
						if (!statement.getConfirmed()) {							
							foundForReconfirm = deliveryConfirmReceipt(
									statement, updateType,
									foundForReconfirm, mp_other, sphere_id);
						} else {								
							foundForReconfirm = deliveryNormal(statement,
									updateType, foundForReconfirm, mp_other);
						}
					} else {
						foundForReconfirm = deliveryNotSettedUp(statement,
								updateType, foundForReconfirm, mp_other);
					}
				} 
			} else {
				logger.info("did not find it!!!!: " + sphere_id
						+ " Sphere tab: " + sphere_tab);
				if (external) {
					String another = (String) update
					.get(SessionConstants.REMOTE_SPHERE_ID);
					if (another.equals(sphere_tab)) {
						logger.warn("ANOTHER!!!!!!!: " + sphere_id);
					}
				}
			}
		}

		if (updateType.equals(SSProtocolConstants.ONLY_IF_EXISTS)) {
			updateProcessed = true;
		}

		if (updateProcessed == false) {
			if (external == false) {
				insertIfSphereIsNotOpened(update, statement, updateType,
						thread_type, sphere_tab, foundForReconfirm);
			}
		}

	}

	/**
	 * @param update
	 * @param mp_other
	 * @param queryOnly
	 * @param performInsert
	 * @return
	 */
	private boolean shouldInsert(Hashtable update, MessagesPane mp_other, final boolean queryOnly) {
		boolean performInsert = false;
		if (queryOnly) {
			String queryId = (String) mp_other.getRawSession()
					.get(SessionConstants.QUERY_ID);
			if (queryId != null) {
				String updateQueryId = (String) update
						.get(SessionConstants.QUERY_ID);
				if (queryId.equals(updateQueryId)) {
					performInsert = true;
				}
			}
		} else {
			performInsert = true;
		}
		return performInsert;
	}

	/**
	 * @param update
	 * @param statement
	 * @param updateType
	 * @param thread_type
	 * @param sphere_tab
	 * @param foundForReconfirm
	 */
	private void insertIfSphereIsNotOpened(Hashtable update, final Statement statement, final String updateType, final String thread_type, String sphere_tab, boolean foundForReconfirm) {
		if ( (!updateType.equals("false")
			 && !updateType.equals(SSProtocolConstants.VOTE))
				|| foundForReconfirm == false) {
			logger.info("calling open another sphere");
			if (update.get("externalConnection") != null) {
				openSphereAndInsertWithExternalConnection(update, statement, thread_type, sphere_tab);
			} else {
				openSphereAndInsert(statement, updateType, thread_type, sphere_tab);
			}
		}
	}

	/**
	 * @param statement
	 * @param updateType
	 * @param thread_type
	 * @param sphere_tab
	 */
	private void openSphereAndInsert(final Statement statement, final String updateType, final String thread_type, String sphere_tab) {
		if (this.dmc.session == null) {
			logger.warn("Session null");
		}
		if (sphere_tab == null) {
			logger.warn("sphere tab null");
		}
		if (statement.getBindedDocument() == null) {
			logger.warn("doc is null");
		}

		if (thread_type.equals("sphere")) {
			sphere_tab = SphereStatement.wrap(
					statement.getBindedDocument())
					.getSystemName();
		}

		if (updateType.equals("true")) {
			logger.warn("HERES THE DEF: " + statement);

			logger.warn("OPEN ING HERE: " + sphere_tab
					+ " : " + statement + " : "
					+ this.dmc.session);

			openAnotherSphere(this.dmc.session, sphere_tab,
					statement.getBindedDocument(), true,
					true);
		}
	}

	/**
	 * @param update
	 * @param statement
	 * @param thread_type
	 * @param sphere_tab
	 */
	@SuppressWarnings("unchecked")
	private void openSphereAndInsertWithExternalConnection(Hashtable update, final Statement statement, final String thread_type, String sphere_tab) {
		if (((String) update.get("externalConnection"))
				.equals("true")) {
			try {
				logger
						.warn("Put another remot esphere id : "
								+ (String) update
										.get(SessionConstants.REMOTE_SPHERE_ID));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			// String remoteSphereURL =
			// (String)update.get("remoteSphereURL");
			String remoteUsername = (String) update
					.get(SessionConstants.REMOTE_USERNAME);
			// String remoteSphereId =
			// (String)update.get("remoteSphereId");

			Document contactDoc = this.dmc.getSF().client
					.getContactFromLogin(this.dmc.session,
							remoteUsername);
			String cname = NameTranslation
					.createContactNameFromContactDoc(contactDoc);
			String systemName = this.dmc.getSF().client
					.getVerifyAuth().getSystemName(cname);

			Hashtable<String, String> newSession = (Hashtable) this.dmc.session
					.clone();
			// newSession.put("remoteSphereId",remoteSphereId);
			newSession.put(	SessionConstants.LOCAL_SPHERE_ID,	systemName);
			newSession.put(	SessionConstants.EXTERNAL_CONNECTION,"true");

			if (thread_type.equals("sphere")) {
				sphere_tab = SphereStatement.wrap(
						statement.getBindedDocument())
						.getSystemName();
			}
			openAnotherSphere(newSession, sphere_tab,
					statement.getBindedDocument(), true,
					true);
		} else {
			if (thread_type.equals("sphere")) {
				sphere_tab = SphereStatement.wrap(
						statement.getBindedDocument())
						.getSystemName();
				logger.warn("HERES THE DEF: " + statement);
			}

			openAnotherSphere(this.dmc.session, sphere_tab,
					statement.getBindedDocument(), true,
					true);
		}
	}

	/**
	 * @param statement
	 * @param typeOfUpdate 
	 * @param foundForReconfirm
	 * @param mp_other
	 * @return
	 */
	private boolean deliveryNotSettedUp(final Statement statement,
			final String typeOfUpdate , boolean foundForReconfirm,
			MessagesPane mp_other) {
		logger.info("DELIVERY TYPE WAS NOT SET...STILL ADD IT ANYWAY...");
		//mp_other.addToAllMessages(statement.getMessageId(), statement);
		if (typeOfUpdate.equals("true")) {
			if (logger.isDebugEnabled()) {
				logger.debug("insert Simple, current sphere ="+statement.getCurrentSphere());
				
			}
			//mp_other.insertUpdate(statement.getBindedDocument(), true, true, false);
			DeliverersManager.INSTANCE.insert(
	    			DeliverersManager.FACTORY.createSimple(statement.getBindedDocument(), false, true, statement.getCurrentSphere()));
		} else {
			mp_other.addToAllMessages(statement.getMessageId(), statement);
			if (typeOfUpdate.equals(SSProtocolConstants.ONLY_IF_EXISTS)) {
				//mp_other.removeThenInsert((Document) statement
				//		.getBindedDocument().clone(), true);
				if (logger.isDebugEnabled()) {
					logger.debug("create Replace if Exists");
				}
				DeliverersManager.INSTANCE.insert(
						DeliverersManager.FACTORY.createReplace((Document) statement
								.getBindedDocument().clone(), null, statement.getCurrentSphere(), true));

				foundForReconfirm = true;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("just create replace");
				}
				//mp_other.removeThenInsert((Document) statement
				//		.getBindedDocument().clone(), false);
				DeliverersManager.INSTANCE.insert(
						DeliverersManager.FACTORY.createReplace((Document) statement
								.getBindedDocument().clone(), null, statement.getCurrentSphere(), false));

				foundForReconfirm = true;

			}
		}
		return foundForReconfirm;
	}

	/**
	 * @param statement
	 * @param typeOfUpdate 
	 * @param foundForReconfirm
	 * @param mp_other
	 * @return
	 */
	private boolean deliveryNormal(final Statement statement,
			final String typeOfUpdate , boolean foundForReconfirm,
			MessagesPane mp_other) {
		
		logger.info("adding to all messages: " + statement);
		//mp_other.addToAllMessages(statement.getMessageId(), statement);
		if (typeOfUpdate.equals("true")) {
			//mp_other.insertUpdate(statement.getBindedDocument(), true, false,	false);
			
			String currentSphere = ClientEventMethodProcessing.checkMultiLocSphere(statement,(String) mp_other.getRawSession().get("sphere_id"));
			DeliverersManager.INSTANCE.insert(
	    			DeliverersManager.FACTORY.createSimple(statement.getBindedDocument(), true, true, currentSphere));
		} else {
			mp_other.addToAllMessages(statement.getMessageId(), statement);
			if (typeOfUpdate.equals(SSProtocolConstants.ONLY_IF_EXISTS)) {
				//mp_other.removeThenInsert((Document) statement
				//		.getBindedDocument().clone(), true);
				DeliverersManager.INSTANCE.insert(
						DeliverersManager.FACTORY.createReplace((Document) statement
								.getBindedDocument().clone(), null, statement.getCurrentSphere(), true));

				foundForReconfirm = true;
			} else {
				//mp_other.removeThenInsert((Document) statement
				//		.getBindedDocument().clone(), false);
				DeliverersManager.INSTANCE.insert(
						DeliverersManager.FACTORY.createReplace((Document) statement
								.getBindedDocument().clone(), null, statement.getCurrentSphere(), false));

				foundForReconfirm = true;
			}
		}
		return foundForReconfirm;
	}

	/**
	 * @param statement
	 * @param typeOfUpdate 
	 * @param foundForReconfirm
	 * @param mp_other
	 * @param sphere_id
	 * @return
	 */
	private boolean deliveryConfirmReceipt(final Statement statement,
			final String typeOfUpdate , boolean foundForReconfirm,
			MessagesPane mp_other, String sphere_id) {
		
		logger.info("deliveryConfirmReceipt");
		

			String author = statement.getGiver();

			if (statement.getForwardedBy() != null) {
				author = statement.getForwardedBy();
			}
			//mp_other.addToAllMessages(statement.getMessageId(), statement);
			if (!author.equals((String) this.dmc.session
					.get(SessionConstants.REAL_NAME))) {
				if (typeOfUpdate.equals("true")) {
					try {
						final boolean playSound = SupraSphereFrame.INSTANCE.isPlayPopupSound();
						statement.setCurrentSphere(sphere_id);
						PopUpController.INSTANCE.popup(statement
								.getBindedDocument());
						if (playSound) {
							try {
								File file = new File("Blip.wav");
								AudioClip clip = Applet.newAudioClip(file
										.toURL());
								clip.play();
								slipThread();
							} catch (Exception mue) {
								logger.warn( "Popping wav 1",mue);
							}
						}
					} catch (Exception ex) {
						logger.warn( "Popping wav 3",ex );
					}
				}
			}

			if (typeOfUpdate.equals("true")) {
				//mp_other.insertUpdate(statement.getBindedDocument(), true, true, false);
				DeliverersManager.INSTANCE.insert(
		    			DeliverersManager.FACTORY.createSimple(statement.getBindedDocument(), false, false, statement.getCurrentSphere()));
			} else {
				mp_other.addToAllMessages(statement.getMessageId(), statement);
				if (typeOfUpdate.equals(SSProtocolConstants.ONLY_IF_EXISTS)) {
					//mp_other.removeThenInsert((Document) statement
					//		.getBindedDocument().clone(), true);
					DeliverersManager.INSTANCE.insert(
							DeliverersManager.FACTORY.createReplace((Document) statement
									.getBindedDocument().clone(), null, statement.getCurrentSphere(), true));
					foundForReconfirm = true;
				} else {
					foundForReconfirm = true;
					//mp_other.removeThenInsert((Document) statement
					//		.getBindedDocument().clone(), false);
					DeliverersManager.INSTANCE.insert(
							DeliverersManager.FACTORY.createReplace((Document) statement
									.getBindedDocument().clone(), null, statement.getCurrentSphere(), false));

				}
			}
		
		return foundForReconfirm;
	}

	/**
	 * @param newSession
	 * @param sphere_tab
	 * @param bindedDocument
	 * @param b
	 * @param c
	 */
	private void openAnotherSphere(Hashtable session, String sphereId,
			Document doc, boolean openBackground, boolean checkOpening) {
		this.dmc.openAnotherSphere(session, sphereId, doc, openBackground,
				checkOpening);
	}

	/**
	 * 
	 */
	private void slipThread() {
		try {
			Thread.sleep(800);
		} catch (Exception e) {
		}
	}
}
