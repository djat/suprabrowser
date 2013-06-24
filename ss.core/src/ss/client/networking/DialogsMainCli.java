package ss.client.networking;

/*
 * This is the heart of the client networking. It's called DialogsMainCli more for historic reasons. It provides most of the methods that the client engages
 * with the server, "DialogsMainPeer.java". SimplestClient.java is used for some connections that do not require a persistant connection, like transferring
 * files, but most other operations use this. The way the networking model works is similar to RMI, I think. Basically, it requests an exclusive lock on the
 * outputstream while it sends a reqest to the server. It listens for responses, and when its response to the request comes back, it will return the method to
 * the client. This is indicated with the "protocol" variable in the "update" Hashtable. It's very important to remove certain information sent, such as the
 * "passphrase". This shouldn't even be stored in the session Hashtable variable anymore, but it was for a while. Instead, as a part of the zero knowledge
 * password protocol, it creates a M1 variable out of its passphrase which should match the M1 on the server. The server stores this in the handler header
 * information on the server, such that it requires a proof of verification upon every method sent by the client.
 *
 *
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.AbstractDocument;
import org.eclipse.swt.widgets.Display;

import ss.client.networking.protocol.CheckForNewVersionsHandler;
import ss.client.networking.protocol.FindAssetsInSameConceptSetHandler;
import ss.client.networking.protocol.GetInfoForHandler;
import ss.client.networking.protocol.GetSubListHandler;
import ss.client.networking.protocol.MatchAgainstHistoryHandler;
import ss.client.networking.protocol.NotifySystemTrayHandler;
import ss.client.networking.protocol.RecallMessageHandler;
import ss.client.networking.protocol.SearchForSpecificInIndexHandler;
import ss.client.networking.protocol.SearchSupraSphere;
import ss.client.networking.protocol.SendByteRouterInitHandler;
import ss.client.networking.protocol.SendPopupNotificationHandler;
import ss.client.networking.protocol.UpdateDocumentHandler;
import ss.client.networking.protocol.UseDocumentHandler;
import ss.client.networking.protocol.VoteDocumentHandler;
import ss.client.networking.protocol.actions.AddChangePassphraseNextLoginHandler;
import ss.client.networking.protocol.actions.AddEventToMessageHandler;
import ss.client.networking.protocol.actions.AddInviteToContactHandler;
import ss.client.networking.protocol.actions.AddLocationsToDocHandler;
import ss.client.networking.protocol.actions.AddQueryToContactHandler;
import ss.client.networking.protocol.actions.AddSphereToFavouritesHandler;
import ss.client.networking.protocol.actions.DeleteSpheresAction;
import ss.client.networking.protocol.actions.EntitleContactForOneSphereHandler;
import ss.client.networking.protocol.actions.EntitleContactForSphereHandler;
import ss.client.networking.protocol.actions.ForwardMessagesSubTreeAction;
import ss.client.networking.protocol.actions.GetAndSendInviteTextHandler;
import ss.client.networking.protocol.actions.GetRecentBookmarks;
import ss.client.networking.protocol.actions.GetRecentQueries;
import ss.client.networking.protocol.actions.LockContactAction;
import ss.client.networking.protocol.actions.MakeCurrentSphereCoreHandler;
import ss.client.networking.protocol.actions.MatchAgainstOtherHistoryHandler;
import ss.client.networking.protocol.actions.MatchAgainstRecentHistoryHandler;
import ss.client.networking.protocol.actions.OpenSphereForMembersHandler;
import ss.client.networking.protocol.actions.PublishFileAction;
import ss.client.networking.protocol.actions.PublishHandler;
import ss.client.networking.protocol.actions.PublishMessageAction;
import ss.client.networking.protocol.actions.RecallContactActionCommand;
import ss.client.networking.protocol.actions.RecallFileFromSphereAction;
import ss.client.networking.protocol.actions.RegisterMemberHandler;
import ss.client.networking.protocol.actions.RemoveSphereFromFavouritesHandler;
import ss.client.networking.protocol.actions.RemoveSphereHandler;
import ss.client.networking.protocol.actions.RenameContactTypeAction;
import ss.client.networking.protocol.actions.ReplaceDocHandler;
import ss.client.networking.protocol.actions.ReplaceUsernameInMembershipHandler;
import ss.client.networking.protocol.actions.SaveClubDealsCommand;
import ss.client.networking.protocol.actions.SaveMarkForSphereHandler;
import ss.client.networking.protocol.actions.SaveQueryViewHandler;
import ss.client.networking.protocol.actions.SaveTabOrderToContactHandler;
import ss.client.networking.protocol.actions.SaveUserPrivilegeClientHandler;
import ss.client.networking.protocol.actions.SaveWindowPositionToContactHandler;
import ss.client.networking.protocol.actions.SearchP2PAction;
import ss.client.networking.protocol.actions.SearchPrivateSphereAction;
import ss.client.networking.protocol.actions.SearchSphereHandler;
import ss.client.networking.protocol.actions.SendDefinitionMessagesHandler;
import ss.client.networking.protocol.actions.SendEmailFromServerHandler;
import ss.client.networking.protocol.actions.SetAsSeenAndIndexJustInCaseHandler;
import ss.client.networking.protocol.actions.SetEmailForwardingRulesHandler;
import ss.client.networking.protocol.actions.SphereRoleRenameAction;
import ss.client.networking.protocol.actions.StartRemoteBuildHandler;
import ss.client.networking.protocol.actions.TagActionCommand;
import ss.client.networking.protocol.actions.UnlockContactAction;
import ss.client.networking.protocol.actions.UpdateClubdealVisibilityAction;
import ss.client.networking.protocol.actions.UpdateMemberVisibilityClientHandler;
import ss.client.networking.protocol.actions.UpdateSphereEmailsHandler;
import ss.client.networking.protocol.callbacks.CallbackRegistrator;
import ss.client.networking.protocol.getters.AbstractGetterCommand;
import ss.client.networking.protocol.getters.CheckExistingUsernameCommand;
import ss.client.networking.protocol.getters.CheckForExistingContactCommand;
import ss.client.networking.protocol.getters.CreateMessageIdOnServerCommand;
import ss.client.networking.protocol.getters.CreateNewProfileCommand;
import ss.client.networking.protocol.getters.FindURLSbyTagCommand;
import ss.client.networking.protocol.getters.GetAllContactsCommand;
import ss.client.networking.protocol.getters.GetAllDistinctContactsCommand;
import ss.client.networking.protocol.getters.GetAllKeywordsCommand;
import ss.client.networking.protocol.getters.GetAllMessagesCommand;
import ss.client.networking.protocol.getters.GetAllSpheresCommand;
import ss.client.networking.protocol.getters.GetAllTagsCommand;
import ss.client.networking.protocol.getters.GetAllVisibleContactsNames;
import ss.client.networking.protocol.getters.GetAssociatedClubdealsCommand;
import ss.client.networking.protocol.getters.GetAssociatedFilesCommand;
import ss.client.networking.protocol.getters.GetAttachmentsCommand;
import ss.client.networking.protocol.getters.GetContactFromLoginCommand;
import ss.client.networking.protocol.getters.GetContactMessageIdsCommand;
import ss.client.networking.protocol.getters.GetContactNotesCommand;
import ss.client.networking.protocol.getters.GetCurrentDateTimeCommand;
import ss.client.networking.protocol.getters.GetEmailInfoCommand;
import ss.client.networking.protocol.getters.GetEmailsOfPossibleRecipientsCommand;
import ss.client.networking.protocol.getters.GetEntireThreadCommand;
import ss.client.networking.protocol.getters.GetExistingQueryCommand;
import ss.client.networking.protocol.getters.GetInitialPresenceCommand;
import ss.client.networking.protocol.getters.GetKeywordsWithUniqueCommand;
import ss.client.networking.protocol.getters.GetMachinePassCommand;
import ss.client.networking.protocol.getters.GetMachineVerifierForProfileCommand;
import ss.client.networking.protocol.getters.GetMemberStateCommand;
import ss.client.networking.protocol.getters.GetMembersForCommand;
import ss.client.networking.protocol.getters.GetMembersStatesCommand;
import ss.client.networking.protocol.getters.GetMyContactCommand;
import ss.client.networking.protocol.getters.GetNewAssetsCountCommand;
import ss.client.networking.protocol.getters.GetPersonalContactsForEmailCommand;
import ss.client.networking.protocol.getters.GetPersonalContactsForSphereCommand;
import ss.client.networking.protocol.getters.GetPrivateDomainNamesCommand;
import ss.client.networking.protocol.getters.GetSpecificIdCommand;
import ss.client.networking.protocol.getters.GetSphereDefinitionCommand;
import ss.client.networking.protocol.getters.GetSphereOrderCommand;
import ss.client.networking.protocol.getters.GetSpheresByRoleCommand;
import ss.client.networking.protocol.getters.GetSupraSphereDocCommand;
import ss.client.networking.protocol.getters.GetUserActivityCommand;
import ss.client.networking.protocol.getters.GetVerifyAuthCommand;
import ss.client.networking.protocol.getters.IsAdminCommand;
import ss.client.networking.protocol.getters.IsContactLockedCommand;
import ss.client.networking.protocol.getters.MatchAgainstHistoryForHighlightCommand;
import ss.client.networking.protocol.getters.MatchAgainstOtherHistoryForHighlightCommand;
import ss.client.networking.protocol.getters.SearchSupraSphereCommand;
import ss.client.networking2.ClientProtocolManager;
import ss.client.preferences.ForwardingController;
import ss.client.preferences.PreferencesAdminController;
import ss.client.preferences.PreferencesChecker;
import ss.client.preferences.PreferencesController;
import ss.client.ui.ExitDialog;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.email.AttachedFile;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.progressbar.DownloadProgressBar;
import ss.client.ui.root.RootTab;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.memberaccess.SphereMemberBundle;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.tempComponents.KeywordSearchResult;
import ss.client.ui.tempComponents.SupraSearchControlPanel;
import ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer;
import ss.common.DmCommand;
import ss.common.ProtocolHandler;
import ss.common.ProtocolUtil;
import ss.common.SSProtocolConstants;
import ss.common.SearchCriteria;
import ss.common.SearchSettings;
import ss.common.SphereDefinitionCreator;
import ss.common.SphereReferenceList;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.common.VerifyAuth;
import ss.common.XmlDocumentUtils;
import ss.common.build.AntBuilder;
import ss.common.networking2.ProtocolUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.FileStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.UserActivity;
import ss.domainmodel.clubdeals.ClubDeal;
import ss.domainmodel.clubdeals.ClubDealUtils;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.framework.entities.xmlentities.XmlEntityUtils;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.ProtocolLifetimeAdapter;
import ss.framework.networking2.ProtocolLifetimeEvent;
import ss.framework.networking2.ReplyObjectHandler;
import ss.framework.networking2.VoidCommandHandler;
import ss.framework.networking2.io.PacketHeader;
import ss.framework.networking2.io.PacketLoadingListener;
import ss.global.SSLogger;
import ss.refactor.supraspheredoc.old.unused.GetSelfSphereMemberCommand;
import ss.server.networking.SC;
import ss.util.LocationUtils;
import ss.util.SessionConstants;

/**
 * Description of the Class
 * 
 * @author david
 * @created September 23, 2003
 */
public class DialogsMainCli {

	protected final Object insertOperatiopnSync = new Object();
	
	private final List<DialogMainClientCommandListener> commandListeners = new ArrayList<DialogMainClientCommandListener>();
	
	private DownloadProgressBar dpb = null;

	private DownloadProgressBar indDPB = null;		

	private MessagesPane mP = null;
	
	private SupraSphereFrame sF = null;

	private volatile VerifyAuth verifyAuth = null;

	// TODO encapsulate this field
	public Hashtable session = new Hashtable();

	private static Random tableIdGenerator = new Random();

	private static final Logger logger = SSLogger
			.getLogger(DialogsMainCli.class);

	private PreferencesController preferencesController = null;
	
	private ForwardingController forwardingController = null;
	
	private PreferencesChecker preferencesChecker = null;
	
	private PreferencesAdminController preferencesAdminController = new PreferencesAdminController();

	protected final HandlerCollection handlers;

	protected Vector<PostponedUpdate> insertQueue = new Vector<PostponedUpdate>();

	private InsertOperation insertOperation = null;
	
	private final Protocol protocol;
	
	private final DmCommandHandler dmCommandHandler = new DmCommandHandler();
	
	private final PacketLoadingObserver packetLoadingObserver = new PacketLoadingObserver();
	
	private final ProtocolLifetimeObserver protocolLifetimeObserver = new ProtocolLifetimeObserver();
	
	public DialogsMainCli(Hashtable session, DataInputStream cdatain,
			DataOutputStream cdataout) {
		this.session = session;
		this.protocol = new Protocol( cdatain, cdataout, ProtocolUtils.generateProtocolDisplayName( "DMC", (String) session.get( SC.USERNAME )) );
		this.protocol.registerHandler( this.dmCommandHandler );
		CallbackRegistrator callbackRegistrator = new CallbackRegistrator( this, this.protocol );
		callbackRegistrator.registerHandlers();
		this.protocol.addProtocolListener( this.protocolLifetimeObserver);
		this.protocol.addPacketLoadingListener( this.packetLoadingObserver );
		this.handlers = createHandlerCollection();
		this.protocol.start(ClientProtocolManager.INSTANCE);
	}
	
	public HandlerCollection createHandlerCollection() {
		return new HandlerCollection(this);
	}

	/**
	 * Description of the Method
	 */
	public void setSupraSphereFrame(SupraSphereFrame sF) {
		this.sF = sF;
	}

	public void setSession(Hashtable session) {
		this.session = session;

	}

	/**
	 * Gets the hashOf attribute of the DialogsMainCli object
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return The hashOf value
	 */

	/**
	 * Sets the mP attribute of the DialogsMainCli object
	 * 
	 * @param mp
	 *            The new mP value
	 */
	// TODO never used, so mP always null. initialize or inline?
	public void setMP(MessagesPane mp) {
		this.mP = mp;

	}

	public MessagesPane getMP() {
		return this.mP;
	}

	/**
	 * @deprecated
	 * 
	 * @param session
	 */
	public void setLastLogin(Hashtable session) {
		try {
			final File file = LocationUtils.getLastLoginFile();
			final Document doc = XmlDocumentUtils.load(file);
			if (doc.getRootElement().element("last_login") == null) {
				doc.getRootElement().addElement("last_login").addAttribute(
						"username", (String) session.get("contact_name"))
						.addAttribute("passphrase",
								(String) session.get("verifier"));
				XmlDocumentUtils.save(file, doc);
			}
		} catch (Exception e) {

		}
	}

	
	/**
	 * @param command
	 */
	private void handleCommand(DmCommand command) {
		try {					
			if (!DialogsMainCli.this.handlers.handle(command)) {
				logger.error("Unrecognized command " + command);
			}
			for(DialogMainClientCommandListener listener : this.commandListeners) {
				listener.commandReceived(command);
			}
			this.commandListeners.clear();
		} catch (Exception exc) {
			logger.error("general exception occurred", exc);
			//TODO: Think about additional processing
		}
	}
	
	public void addCommandListener(final DialogMainClientCommandListener commandListener) {
		this.commandListeners.add(commandListener);
	}
	
	public void removeCommandListener(final DialogMainClientCommandListener commandListener) {
		this.commandListeners.remove(commandListener);
	}

	/**
	 * Gets the running attribute of the DialogsMainCli object
	 * 
	 * @deprecated
	 * 
	 * @return The running value
	 */
	public boolean isRunning() {
		boolean running = true;
		return running;
	}


	/**
	 * Gets the messagesNode attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 */

	/**
	 * Gets the anotherSphere attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param buildDoc
	 *            Description of the Parameter
	 */
	public void saveTabOrderToContact(final Hashtable session, Document buildDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SAVE_TAB_ORDER_TO_CONTACT);
		((SaveTabOrderToContactHandler) ph).saveTabOrderToContact(session,
				buildDoc);
	}

	public void addSphereToFavourites(final Hashtable session, Document buildDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ADD_SPHERE_TO_FAVOURITES);
		((AddSphereToFavouritesHandler) ph).addSphereToFavourites(session,
				buildDoc);
	}

	public void removeSphereFromFavourites(String id) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.REMOVE_FROM_FAVORITES);
		((RemoveSphereFromFavouritesHandler) ph).removeSphereFromFavourites(
				(Hashtable)this.session.clone(), id);
	}

	public void saveWindowPositionToContact(final Hashtable session,
			Document buildDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SAVE_WINDOW_POSITION_TO_CONTACT);
		((SaveWindowPositionToContactHandler) ph).saveWindowPositionToContact(
				session, buildDoc);
	}

	/**
	 * Save user privilege. Currenty works only for SetDeliveryOptionPrivilege
	 * 
	 * @category NOT FINAL VERSION, WILL BE CHANGE IN FUTURE
	 */
	public void saveUserPrivilege(String userLogin, String userPermission) {
		if (!this.verifyAuth.getPrivilegesManager()
				.getSetDeliveryOptionPrivilege()
				.canModifyPermissionForOtherUsers()) {
			logger.warn("Access denied");
			return;
		}
		SaveUserPrivilegeClientHandler ph = (SaveUserPrivilegeClientHandler) this.handlers
				.getProtocolHandler(SSProtocolConstants.SAVE_USER_PRIVILEGS);
		ph.saveUserPrivileges(userLogin, userPermission);
	}

	public void notifySystemTray(final Hashtable session, Vector memberList,
			Document doc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.NOTIFY_SYSTEM_TRAY);
		((NotifySystemTrayHandler) ph).notifySystemTray(session, memberList,
				doc);
	}

	public void sendPopupNotification(final Hashtable session,
			Vector memberList, Document doc) {

		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEND_POPUP_NOTIFICATION);
		((SendPopupNotificationHandler) ph).sendPopupNotification(session,
				memberList, doc);
	}

	public void removeSphere(final Hashtable session, Document doc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.REMOVE_SPHERE);
		((RemoveSphereHandler) ph).removeSphere(session, doc);
	}

	/**
	 * Gets the sphereDefinition attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The sphereDefinition value
	 */
	public Document getCurrentSphereDefinition(final Hashtable session) {
		GetSphereDefinitionCommand command = new GetSphereDefinitionCommand();
		command.putArg( SC.SPHERE_ID, (String)session.get( SC.SPHERE_ID ) );
		command.putArg( SC.SUPRA_SPHERE, (String)session.get( SC.SUPRA_SPHERE ) );
		return command.execute(this, AbstractDocument.class);
	}

	/**
	 * Gets the sphereDefinition
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The sphereDefinition value
	 */
	public Document getSphereDefinition(String systemSphereName) {
		GetSphereDefinitionCommand command = new GetSphereDefinitionCommand();
		command.putArg(SC.SPHERE_ID, systemSphereName );
		command.putArg(SC.SUPRA_SPHERE, systemSphereName);//systemSphereName );
		command.putArg(SC.CREATE_SPHERE_DEFINITION_IF_NO_DEFINITION_FOUND, false ); // Was true but true will create definitions for p2p spheres
		return command.execute(this, AbstractDocument.class);
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Document> getAllKeywords() {
		GetAllTagsCommand command = new GetAllTagsCommand();
		return command.execute(this, Vector.class);
	}

	/**
	 * Gets the anotherSphere attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 */

	/**
	 * Issues a search of a single sphere
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param sphere_definition
	 *            Description of the Parameter
	 */
	public void searchSphere(Hashtable session, Document sphere_definition,
			String openBackground) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEARCH_SPHERE);
		((SearchSphereHandler) ph).searchSphere(session, sphere_definition,
				openBackground);
	}
	
	public void searchPrivateSphere(Hashtable session, MemberReference member) {
		SearchPrivateSphereAction action = new SearchPrivateSphereAction(session, member);
		action.beginExecute( this );
	}
	
	/**
	 * @param newSession
	 * @param member
	 * @param systemName
	 */
	public void searchP2PSphere(Hashtable newSession, MemberReference member, String systemName) {
		SearchP2PAction action = new SearchP2PAction(newSession, member, systemName);
		action.beginExecute( this );
	}

	/**
	 * Issues a search of a single sphere
	 * 
	 */
	public void searchSphere(String sphereId, String messageId, String keywords,
			String openBackground) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEARCH_SPHERE);
		((SearchSphereHandler) ph).searchSphere(sphereId, messageId,keywords,
				openBackground);
	}

	/**
	 * DUPLICATE OF searchSupraSphere
	 * 
	 * @deprecated
	 * @param session
	 * @param sphere_definition
	 * @param openBackground
	 */
	@SuppressWarnings("unchecked")
	public void supraSearch(Hashtable session, // Document sphere_definition,
			String openBackground, SearchCriteria criteria) {

		// what does addPending do?
		// sF.addPending((String) session.get("sphere_id"));

		try {
			Hashtable toSend = (Hashtable) session.clone();
			// TODO migrate from ProtocolUtil to ProtocolHandler and
			// SessionConstants
			Hashtable search = new Hashtable();
			search.put(ProtocolUtil.COMMAND, SSProtocolConstants.SEARCH_SPHERE);
			search.put(ProtocolUtil.OPEN_BACKGROUND, openBackground);
			search.put(ProtocolUtil.SESSION, toSend);

			// search.put(ProtocolUtil.SPHERE_TYPE, session.get("sphere_type"));
			// search.put(ProtocolUtil.SPHERE_DEFINITION, sphere_definition);
			search.put(ProtocolUtil.CRITERIA, criteria);

			sendFromQueue(search);
		} catch (NullPointerException exc) {
			logger.error("session or sendFromQueue throw a NPE", exc);
		}
	}

	public void showPage(String queryId, String pageId, SupraBrowser browser, SupraSearchControlPanel panel,String sQuery) {
		SearchSupraSphere searchSupraSphere = new SearchSupraSphere( this, sQuery, null, false, false );
		searchSupraSphere.showPageofSearchSupraSphere(queryId, pageId, browser, panel );
	}

	/**
	 * Gets the sphereCore attribute of the DialogsMainCli object
	 * 
	 * @param userSession
	 *            Description of the Parameter
	 * @param buildOrder
	 *            Description of the Parameter
	 */

	public Hashtable getSession() {

		return this.session;
	}

	/**
	 * Gets the sphereOrder attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The sphereOrder value
	 */
	public Document getSphereOrder(Hashtable session) {
		GetSphereOrderCommand command = new GetSphereOrderCommand();
		command.putSessionArg(session);
		return command.execute(this, AbstractDocument.class);
	}

	public void getAndSendInviteText(Hashtable session, Document contactDoc,
			String fromDomain, String fromEmail) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.GET_AND_SEND_INVITE_TEXT);
		((GetAndSendInviteTextHandler) ph).getAndSendInviteText(session,
				contactDoc, fromDomain, fromEmail);
	}

	/**
	 * Gets the emailAddress attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param contact_name
	 *            Description of the Parameter
	 * @return The emailAddress value
	 */
	public Hashtable getEmailInfo(Hashtable session, String contact_name) {
		GetEmailInfoCommand command = new GetEmailInfoCommand();
		command.putSessionArg(session);
		command.putArg( SC.CONTACT_NAME, contact_name);
		return command.execute(this, Hashtable.class );
	}

	/**
	 * @deprecated
	 * @param session
	 * @param sphereDefinition
	 * @param currentPage
	 */
	@SuppressWarnings("unchecked")
	public void getForwardResults(Hashtable session, Document sphereDefinition,
			String currentPage) {

		try {
			Hashtable toSend = (Hashtable) session.clone();

			Hashtable test = new Hashtable();

			test.put(SessionConstants.PROTOCOL,
					SSProtocolConstants.GET_FORWARD_RESULTS);
			test.put(SessionConstants.SPHERE_DEFINITION, sphereDefinition);

			test.put(SessionConstants.CURRENT_PAGE, currentPage);

			test.put(SessionConstants.SESSION, toSend);

			sendFromQueue(test);
			/*
			 * test = new Hashtable(); test.put("protocol","change_presence");
			 * obout = new ObjectOutputStream(new
			 * BufferedOutputStream(cdataout)); obout.writeObject(test);
			 */
			// mP.setDefaultDelivery();
		} catch (NullPointerException npe) {
			logger.error(npe);
		}

	}

	public void replaceUsernameInMembership(Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.REPLACE_USERNAME_IN_MEMBERSHIP);
		((ReplaceUsernameInMembershipHandler) ph).replaceUsernameInMembership(
				session, oldUsername, newUsername, newSalt, newVerifier);
	}

	public Vector getRecentQueries(Hashtable session, String homeSphereId,
			String homeMessageId, String localSphereId) {
		GetRecentQueries getRecentQueriesHandler = new GetRecentQueries( this ); 
		return getRecentQueriesHandler.getRecentQueries(session, homeSphereId, homeMessageId, localSphereId);
	}

	@SuppressWarnings("unchecked")
	public Vector<Document> getAllKeyWords(String filter, Hashtable session) {
		GetAllKeywordsCommand command = new GetAllKeywordsCommand();
		command.putArg( SC.FILTER, filter );
		command.putArg( SC.SPHERE_ID2, (String)session.get( SC.SPHERE_ID) );
		command.putArg( SC.USERNAME, (String)session.get( SC.USERNAME) );
		return command.execute(this, Vector.class);
	}

	@SuppressWarnings("unchecked")
	public Vector<String> findURLSbyTag(String uniqueId, Hashtable session) {
		FindURLSbyTagCommand command = new FindURLSbyTagCommand();
		command.putArg(SC.UNIQUE_ID2, uniqueId);
		String sphere = (String) session.get(SessionConstants.SPHERE_ID2);
		command.putArg(SC.SPHERE_ID2, sphere );
		return command.execute(this, Vector.class );
	}

	public Document getSpecificId(Hashtable session, String messageId) {
		GetSpecificIdCommand command = new GetSpecificIdCommand();
		command.putSessionArg(session);
		command.putArg(SC.MESSAGE_ID, messageId);
		return command.execute(this, AbstractDocument.class);
	}
	
	public void saveClubDealsData( final Document data ) {
		SaveClubDealsCommand command = new SaveClubDealsCommand(data);
		command.beginExecute(this);
	}

	public boolean checkExistingUsername(Hashtable session, String username) {
		CheckExistingUsernameCommand command = new CheckExistingUsernameCommand();
		command.putArg(SC.USERNAME, username );
		return command.execute( this, Boolean.class );		
	}
	
	public Document getContactFromLogin(Hashtable session, String loginName) {
		GetContactFromLoginCommand command = new GetContactFromLoginCommand();
		command.putArg(SC.USERNAME,  loginName );
		return command.execute(this, AbstractDocument.class );
	}
	
	public Document getContactFromContactName(Hashtable session, String contactName) {
		GetContactFromLoginCommand command = new GetContactFromLoginCommand();
		command.putArg(SC.REAL_NAME,  contactName );
		return command.execute(this, AbstractDocument.class );
	}

	/*
	 * 
	 * currentSphere
	 * 
	 * 
	 */

	public Document getKeywordsWithUnique(String uniqueId,
			String currentSphere) {
		GetKeywordsWithUniqueCommand command = new GetKeywordsWithUniqueCommand();
		command.putArg( SC.UNIQUE_ID2, uniqueId);
		if(StringUtils.isNotBlank(currentSphere)) {
			command.putArg( SC.CURRENT_SPHERE, currentSphere);
		}
		return command.execute( this, AbstractDocument.class );
	}

	public Hashtable createMessageIdOnServer(Hashtable session) {
		CreateMessageIdOnServerCommand command = new CreateMessageIdOnServerCommand();
		return command.execute(this, Hashtable.class);
	}

	public Vector<Document> getRecentBookmarks(Hashtable session, String homeSphereId,
			String homeMessageId, String localSphereId) {
		GetRecentBookmarks getRecentBookmarksHandler = new GetRecentBookmarks( this );
		return getRecentBookmarksHandler.getRecentBookmarks(session,
				homeSphereId, homeMessageId, localSphereId);
	}

	/**
	 * @deprecated
	 * 
	 * Description of the Method
	 * 
	 */
	public void createSWT() {
		String os = System.getProperty("os.name");
		if (!os.startsWith("Mac")) {
			// System.out.println("Created SWT in client");
			// so = new SOptionPane(mP,session);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param sendDoc
	 *            Description of the Parameter
	 */
	public void publishTerse(final Hashtable session,
			final org.dom4j.Document sendDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.PUBLISH);
		((PublishHandler) ph).publishTerse(session, sendDoc);
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param filename
	 *            Description of the Parameter
	 * @param sendDoc
	 *            Description of the Parameter
	 */
	public void updateDocument(final Hashtable session, final String filename,
			final org.dom4j.Document sendDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.UPDATE_DOCUMENT);
		((UpdateDocumentHandler) ph).updateDocument(session, filename, sendDoc);
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param filename
	 *            Description of the Parameter
	 * @param sendDoc
	 *            Description of the Parameter
	 */

	public String entitleContactForSphere(final Hashtable session,
			final Document sendDoc, final String sphereType) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ENTITLE_CONTACT_FOR_SPHERE);
		return ((EntitleContactForSphereHandler) ph).entitleContactForSphere(
				session, sendDoc, sphereType);
	}

	// For the memberDoc, set the channel active for the personalSphere of the
	// member that was in the channel
	public void entitleContactForOneSphere(final Hashtable session,
			final Document contactDoc, final Document memberDoc,
			String existingMemberLogin, String existingMemberContact) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ENTITLE_CONTACT_FOR_ONE_SPHERE);
		((EntitleContactForOneSphereHandler) ph).entitleContactForOneSphere(
				session, contactDoc, memberDoc, existingMemberLogin,
				existingMemberContact);
	}

	public void addEventToMessage(final Hashtable session, String messageId,
			Element event) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ADD_EVENT_TO_MESSAGE);
		((AddEventToMessageHandler) ph).addEventToMessage(session, messageId,
				event);

	}

	public Document checkForExistingContact(final Hashtable session,
			final Document sendDoc) {
		CheckForExistingContactCommand commmand = new CheckForExistingContactCommand();
		commmand.putArg( SessionConstants.DOCUMENT, sendDoc);
		return commmand.execute( this, AbstractDocument.class );
	}

	public void voteDocument(final Hashtable session, final String filename,
			final org.dom4j.Document sendDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.VOTE_DOCUMENT);
		((VoteDocumentHandler) ph).voteDocument(session, filename, sendDoc);
	}

	public void useDocument(final Hashtable session,
			final org.dom4j.Document sendDoc, String increment) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.USE_DOCUMENT);
		((UseDocumentHandler) ph).useDocument(session, sendDoc, increment);
	}

	public void sendEmailFromServer(Hashtable session,
			EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, StringBuffer sb, String subject,
			String replySphere) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEND_EMAIL_FROM_SERVER);
		((SendEmailFromServerHandler) ph).sendEmailFromServer(session,
				addressesContainer, files, sb, subject, replySphere);
	}

//	/**
//	 * @deprecated
//	 * 
//	 * @param session
//	 */
//	public void sendRebootOrder(final Hashtable session) {
//		ProtocolHandler ph = this.handlers
//				.getProtocolHandler(SSProtocolConstants.REBOOT);
//		((RebootHandler) ph).sendRebootOrder(session);
//	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param sendDoc
	 *            Description of the Parameter
	 */
	public void replaceDoc(final Hashtable session,
			final org.dom4j.Document sendDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.REPLACE_DOC);
		((ReplaceDocHandler) ph).replaceDoc(session, sendDoc);
	}

	// Creates the correlation between the document and second keyword that is
	// being tagged....

	public void saveQueryView(final Hashtable session,
			org.dom4j.Document sendDoc, Element keywordElement) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SAVE_QUERY_VIEW);
		((SaveQueryViewHandler) ph).saveQueryView(session, sendDoc,
				keywordElement);
	}

	public void addQueryToContact(final Hashtable session,
			Element keywordElement) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ADD_QUERY_TO_CONTACT);
		((AddQueryToContactHandler) ph).addQueryToContact(session,
				keywordElement);
	}

	public void addInviteToContact(final Hashtable session,
			String contactMessageId, String inviteSphereId,
			String inviteSphereName, String inviteSphereType) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ADD_INVITE_TO_CONTACT);
		((AddInviteToContactHandler) ph).addInviteToContact(session,
				contactMessageId, inviteSphereId, inviteSphereName,
				inviteSphereType);
	}


	/**
	 * Invite username is the contact that is inviting, username is the username
	 * of the contact that is being registered
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param contact_name
	 *            Description of the Parameter
	 * @param login_name
	 *            Description of the Parameter
	 * @param sphere_core
	 *            Description of the Parameter
	 */

	public void registerMember(Hashtable session, String supraSphere,
			Document contactDoc, String inviteUsername, String inviteContact,
			String sphereName, String sphereId, String realName,
			String username, String inviteSphereType) {
		logger.debug("registerMember " + sphereName + realName + username);
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.REGISTER_MEMBER);
		((RegisterMemberHandler) ph).registerMember(session, supraSphere,
				contactDoc, inviteUsername, inviteContact, sphereName,
				sphereId, realName, username, inviteSphereType);
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param filename
	 *            Description of the Parameter
	 * @param sendDoc
	 *            Description of the Parameter
	 */
	public void publishMessageAsComposite(Hashtable session, String filename,
			org.dom4j.Document sendDoc) {
		// TODO implement
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param filename
	 *            Description of the Parameter
	 * @param sendDoc
	 *            Description of the Parameter
	 */
	public void publishWithout(Hashtable session, String filename,
			org.dom4j.Document sendDoc) {
		// TODO implement
	}

	/**
	 * Gets the membersFor attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The membersFor value
	 */

	public Vector getAllContacts(Hashtable session, String sphereId) {
		GetAllContactsCommand command = new GetAllContactsCommand();
		command.putArg(SC.SPHERE_ID2, sphereId );
		return command.execute(this, Vector.class );
	}

	@SuppressWarnings("unchecked")
	public Vector<Document> getMembersFor(Hashtable session) {
		GetMembersForCommand command = new GetMembersForCommand();
		command.putSessionArg(session);
		return command.execute(this, Vector.class);
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String,String> getPersonalContactsForSphere(Hashtable session) {
		GetPersonalContactsForSphereCommand command = new GetPersonalContactsForSphereCommand();
		command.putSessionArg(session);
		return command.execute(this, Hashtable.class);
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<String, String> getPersonalContactsForEmail(Hashtable session) {
		GetPersonalContactsForEmailCommand command = new GetPersonalContactsForEmailCommand();
		command.putSessionArg(session);
		return command.execute(this, Hashtable.class);
	}

	/**
	 * Gets the allSpheres attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The allSpheres value
	 */
	@SuppressWarnings("unchecked")
	public Vector<Document> getAllSpheres() {
		GetAllSpheresCommand command = new GetAllSpheresCommand();
		return command.execute(this, Vector.class, AbstractGetterCommand.LONG_DM_TIMEOUT );
	}

	/**
	 * Gets the initialPresence attribute of the DialogsMainCli object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The initialPresence value
	 */
	@SuppressWarnings("unchecked")
	public Vector<String> getInitialPresence(Hashtable session) {
		GetInitialPresenceCommand command = new GetInitialPresenceCommand();
		command.putSessionArg(session);
		return command.execute(this, Vector.class);
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param members
	 *            Description of the Parameter
	 * @param system_name
	 *            Description of the Parameter
	 * @param display_name
	 *            Description of the Parameter
	 */
	public void openSphereForMembers(Hashtable session, Document doc,
			Vector members, String system_name, String display_name) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.OPEN_SPHERE_FOR_MEMBERS);
		((OpenSphereForMembersHandler) ph).openSphereForMembers(session, doc,
				members, system_name, display_name);
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param doc
	 *            Description of the Parameter
	 * @param sphere
	 *            Description of the Parameter
	 */
	public void recallMessage(Hashtable session, Document doc, String sphere) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.RECALL_MESSAGE);
		((RecallMessageHandler) ph).recallMessage(session, doc, sphere);
	}


	
	/**
	 * Description of the Method
	 * 
	 * @param orig
	 *            Description of the Parameter
	 */
	@SuppressWarnings("unchecked")
	public void sendFromQueue(Hashtable orig) {
		DmCommand dmCommand = new DmCommand( orig );
		if ( logger.isDebugEnabled() ) {
			logger.debug("Sending " + dmCommand.getHandlerName() /*+ " called by " + DebugUtils.getCurrentStackTrace()*/ );
		}
		dmCommand.beginExecute(this.protocol);
	}

	/**
	 * Description of the Method
	 * 
	 * @param forqueue
	 *            Description of the Parameter
	 * @param invoke
	 *            Description of the Parameter
	 * 
	 * Offloads the incoming messages into a queue, that will process and insert
	 * the messages into the proper MessagesPane inside the sF.messagePanes
	 * Hashtable
	 */
	public void callInsert(PostponedUpdate forqueue) {
		synchronized (this.insertQueue) {
			this.insertQueue.addElement(forqueue);
		}
		startInsertOperationIfRequire();
	}
	
	/**
	 * Converts a serializable object to a byte array.
	 * 
	 * @param object
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception IOException
	 *                Description of the Exception
	 */
	@SuppressWarnings("unused")
	private byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(object);
		return baos.toByteArray();
	}

	/**
	 * Converts a byte array to a serializable object.
	 * 
	 * @param bytes
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception IOException
	 *                Description of the Exception
	 * @exception ClassNotFoundException
	 *                Description of the Exception
	 */
	@SuppressWarnings("unused")
	private Object bytesToObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream is = new ObjectInputStream(bais);
		return is.readObject();
	}

	public synchronized long getNextTableId() {

		return Math.abs(tableIdGenerator.nextLong());

	}

	/**
	 * This method will create an element <change_passphrase_next_login/>
	 * associated with the contact doc so that the next time that user logs in,
	 * it will have to change their username and passphrase
	 * 
	 * 
	 * @param session
	 * @param login
	 * 
	 * 
	 * 
	 */
	public void addChangePassphraseNextLogin(Hashtable session, String login) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ADD_CHANGE_PASSPHRASE_NEXT_LOGIN);
		((AddChangePassphraseNextLoginHandler) ph)
				.addChangePassphraseNextLogin(session, login);
	}

	/**
	 * 
	 * @param session
	 * @param sphereId
	 * @param doc
	 * @param openBackground
	 * @param checkOpening - set true if is needed to check for opening in preferences
	 */
	@SuppressWarnings("unchecked")
	public void openAnotherSphere(Hashtable session, String sphereId,
			Document doc, boolean openBackground, boolean checkOpening) {

		if (checkOpening){
			if (!(getPreferencesChecker().isNewMessageShouldOpenTab(sphereId))){
				return;
			}
		}
		
		Hashtable newSession = (Hashtable) session.clone();
		newSession.put(SessionConstants.SPHERE_ID2, sphereId);
		Document sphereDefinition = null;

		String sphereType = this.verifyAuth.getSphereType(sphereId);
		newSession.put(SessionConstants.SPHERE_TYPE, sphereType);

		if (doc != null) {
			newSession.put(SessionConstants.FIRST_DOC, doc);
		}

		SphereDefinitionCreator sdc = new SphereDefinitionCreator();
		sphereDefinition = sdc.createDefinition(this.verifyAuth
				.getDisplayName(sphereId), sphereId);
		newSession.put(SessionConstants.SPHERE_DEFINITION, sphereDefinition);

		searchSphere(newSession, sphereDefinition, String
				.valueOf(openBackground));

	}

	public void restartWithAnt(final String autologin) {
		logger.warn( "restartWithAnt" );
		beginClose();
		Thread t = new Thread() {
			public void run() {				
				try {
					Runtime.getRuntime().exec(
							"java -jar supra."
									+ (String) DialogsMainCli.this.session
											.get(SessionConstants.PORT)
									+ ".jar client "
									+ (String) DialogsMainCli.this.session
											.get(SessionConstants.PORT));
				} catch (Exception e) {
				}

				try {

					DialogsMainCli.this.sF.closeFromWithin();

				} catch (NullPointerException npe) {
					System.exit(0);
				}
				Display.getCurrent().dispose();
				System.exit(0);
			}
		};
		t.start();
	}

	public void restartOnly(final boolean autoLogin) {
		logger.warn("will restart only with autologin as : " + autoLogin);
		beginClose();
		Thread t = new Thread() {
			public void run() {
				

				AntBuilder build = new AntBuilder();
				build.runOnly(DialogsMainCli.this.sF, autoLogin);
			}
		};
		t.start();

	}

	/**
	 * @return
	 */
	public SupraSphereFrame getSupraSphereFrame() {
		return this.sF;
	}

	/**
	 * @param dpb2
	 */
	public void setActiveProgressBar(DownloadProgressBar dpb2) {
		this.indDPB = dpb2;
	}

	/**
	 * @param session2
	 * @return
	 */
	public Document getMyContact(Hashtable session2) {
		GetMyContactCommand command = new GetMyContactCommand();
		command.putSessionArg(session2);
		return command.execute(this, AbstractDocument.class );
	}

	/**
	 * @param session2
	 * @param text
	 * @return
	 */
	public Document getExistingQuery(Hashtable session2, String queryText, String otherSphere) {
		//TODO: warn  isSupraQuery is unused
		GetExistingQueryCommand command = new GetExistingQueryCommand();
		command.putSessionArg(session2);
		command.putArg(SC.QUERY_TEXT, queryText);
		command.putArg(SC.SPHERE_ID, otherSphere);				
		return command.execute(this,AbstractDocument.class);
	}

	/**
	 * @param session2
	 * @param unique
	 */
	public void findAssetsInSameConceptSet(Hashtable session2, String uniqueId,
			String messageId, String keywordSphereId, Vector messageIdsToExclude) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.FIND_ASSETS_IN_SAME_CONCEPT_SET);
		((FindAssetsInSameConceptSetHandler) ph).findAssetsInSameConceptSet(
				session2, uniqueId, messageId, keywordSphereId,
				messageIdsToExclude);
	}

	public Hashtable matchAgainstHistoryForHighlight(Hashtable session2) {
		MatchAgainstHistoryForHighlightCommand command = new MatchAgainstHistoryForHighlightCommand();
		command.putSessionArg(session2);
		return command.execute(this, Hashtable.class);
	}
	
	public Hashtable matchAgainstOtherHistoryForHighlight(Hashtable session2, ResearchComponentDataContainer data) {
		MatchAgainstOtherHistoryForHighlightCommand command = new MatchAgainstOtherHistoryForHighlightCommand();
		command.putSessionArg(session2);
		command.setData(data);
		return command.execute(this, Hashtable.class);
	}

	public void matchAgainstHistory(Hashtable session2, Vector docsToMatch) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.MATCH_AGAINST_HISTORY);
		((MatchAgainstHistoryHandler) ph).matchAgainstHistory(session2,
				docsToMatch);
	}

	/**
	 * @param createDoc
	 */
	public void setAsSeenAndIndexJustInCase(Hashtable session2,
			Document createDoc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SET_AS_SEEN_AND_INDEX_JUST_IN_CASE);
		((SetAsSeenAndIndexJustInCaseHandler) ph).setAsSeenAndIndexJustInCase(
				session2, createDoc);
	}

	/**
	 * @param newSession
	 * @param new_definition
	 */
	public void searchForSpecificInIndex(Hashtable session2,
			Document new_definition, Vector docsToMatch) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEARCH_FOR_SPECIFIC_IN_INDEX);
		((SearchForSpecificInIndexHandler) ph).searchForSpecificInIndex(
				session2, new_definition, docsToMatch);
	}

	public void setEmailForwardingRules(Hashtable session2,
			String targetSphereId, String emailAddresses, String enabled) {

		if (this.verifyAuth.getPrivilegesManager()
				.getSetDeliveryOptionPrivilege().hasModifyPermissionForSphere(
						targetSphereId)) {
			ProtocolHandler ph = this.handlers
					.getProtocolHandler(SSProtocolConstants.SET_EMAIL_FORWARDING_RULES);
			((SetEmailForwardingRulesHandler) ph).setEmailForwardingRules(
					session2, targetSphereId, emailAddresses, enabled);
		}

	}
	
	@SuppressWarnings("unchecked")
	public void forwardMessagesSubTree( List<String> sphereList, List<Document> docList) {
		ForwardMessagesSubTreeAction command = new ForwardMessagesSubTreeAction();
		Hashtable update = new Hashtable();
		update.put(SessionConstants.SPHERE_LIST, sphereList);
		update.put(SessionConstants.DOC_LIST, docList);
		update.put(SessionConstants.SESSION, this.session.clone());
		command.putSessionArg(update);
		command.beginExecute( this );
	}

	public void getSubList(Hashtable session2, Document doc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.GET_SUB_LIST);
		((GetSubListHandler) ph).getSubList(session2, doc);
	}

	public Hashtable createNewProfile(Hashtable session2, String profileName) {
		//TODO: profileName is UNUSED!
		CreateNewProfileCommand command = new CreateNewProfileCommand();
		command.putSessionArg( session2 );
		return command.execute(this, Hashtable.class );
	}

	public void startRemoteBuild(Hashtable session2, Document doc,
			String cliSerServant, String restart) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.START_REMOTE_BUILD);
		((StartRemoteBuildHandler) ph).startRemoteBuild(session2, doc,
				cliSerServant, restart);
	}

	public void sendByteRouterInit(Hashtable session2, Document doc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEND_BYTE_ROUTER_INIT);
		((SendByteRouterInitHandler) ph).sendByteRouterInit(session2, doc);
	}

	public void getInfoFor(Hashtable session2, Document doc) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.GET_INFO_FOR);
		((GetInfoForHandler) ph).getInfoFor(session2, doc);
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String,String> getMachineVerifierForProfile(Hashtable session2,
			String inviteURL) {
		// TODO inviteURL is unused
		GetMachineVerifierForProfileCommand command = new GetMachineVerifierForProfileCommand();
		command.putSessionArg(session2);
		return command.execute(this, Hashtable.class );

	}

	public String getMachinePass(Hashtable session2, String inviteURL) {
		//TODO warn inviteURL is not used
		GetMachinePassCommand command = new GetMachinePassCommand();
		command.putSessionArg(session2);
		return command.execute(this, String.class);
	}

	public void matchAgainstOtherHistory(Hashtable session2, Vector docsToMatch) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.MATCH_AGAINST_OTHER_HISTORY);
		((MatchAgainstOtherHistoryHandler) ph).matchAgainstOtherHistory(
				session2, docsToMatch);
	}

	public void saveMarkForSphere(Hashtable session2, String localOrGlobal) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SAVE_MARK_FOR_SPHERE);
		((SaveMarkForSphereHandler) ph).saveMarkForSphere(session2,
				localOrGlobal);
	}

	public void addLocationsToDoc(Hashtable session2, Document document,
			String newSphereId, String newSphereName, String newMessageId) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.ADD_LOCATIONS_TO_DOC);
		((AddLocationsToDocHandler) ph).addLocationsToDoc(session2, document,
				newSphereId, newSphereName, newMessageId);
	}

	public boolean isOnlyOpenSphere(Hashtable session) {
		final String sphereId = (String) session.get(SessionConstants.SPHERE_ID2);
		int times = this.sF.getMessagePanesController().countOfMessagesPanesForSphere( sphereId );
		return times <= 1;
	}

	public void makeCurrentSphereCore(Hashtable session2, String login) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.MAKE_CURRENT_SPHERE_CORE);
		((MakeCurrentSphereCoreHandler) ph).makeCurrentSphereCore(session2,
				login);
	}

	public void matchAgainstRecentHistory(Hashtable session2, Vector docsToMatch) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.MATCH_AGAINST_RECENT_HISTORY);
		((MatchAgainstRecentHistoryHandler) ph).matchAgainstRecentHistory(
				session2, docsToMatch);
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getPrivateDomainNames(String filter, Hashtable session) {
		GetPrivateDomainNamesCommand command = new GetPrivateDomainNamesCommand();
		command.putArg(SC.FILTER, filter );
		command.putSessionArg(session);
		return command.execute(this, Vector.class);
	}

	/**
	 * @return Returns the sF.
	 */
	public SupraSphereFrame getSF() {
		return this.sF;
	}

	/**
	 * @param verifyAuth
	 *            The verifyAuth to set.
	 */
	public synchronized void setVerifyAuth(VerifyAuth verifyAuth) {
		if (logger.isDebugEnabled()) {
			logger.debug("Updating verify auth.");
		}		
		this.verifyAuth = verifyAuth;
	}

	/**
	 * 
	 */
	public void fireVerifyAuthChanged() {
		if(SupraSphereFrame.INSTANCE==null) {
			return;
		}
		if(SupraSphereFrame.INSTANCE.getRootTab()==null) {
			return;
		}
		((RootTab)SupraSphereFrame.INSTANCE.getRootTab()).getHierarchyComponent().refreshSphereTree();
	}

	/**
	 * @return Returns the verifyAuth.
	 */
	public synchronized VerifyAuth getVerifyAuth() {
		// if (this.verifyAuth == null) {
		// logger.info("when in connectiona nd returning, it is null");
		// }
		return this.verifyAuth;
	}

	/**
	 * @param verifyAuth
	 *            The verifyAuth to set if this.verifyAuth is null.
	 */
	public synchronized void setVeryfyAuthIfNull(VerifyAuth verifyAuth) {
		if (this.verifyAuth == null) {
			setVerifyAuth(verifyAuth);
		}
	}
	
	/**
	 * Send reques to server to recevie start up spheres
	 */
	public void sendDefinitionMessage() {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.SEND_DEFINITION_MESSAGES);
		((SendDefinitionMessagesHandler) ph)
				.sendDefinitionMessage(this.session);

		try {
			this.getSF()
					.setTitle(
							(String) this.session
								.get(SessionConstants.SUPRA_SPHERE)
								+ " : "
								+ (String) this.session
										.get(SessionConstants.REAL_NAME));
			this.getSF().getWelcomeScreen().closeFromWithin();
		} catch (Throwable ex) {
			logger.error("Error while setting supraSphereFrame title");
		}
	}

	public void checkForNewVersion(Document versionsDoc, VerifyAuth verifyAuth) {
		logger.info("Will check for new versions now: " + versionsDoc.asXML());
		this.setVeryfyAuthIfNull(verifyAuth);
		// TODO: What should it do?
		// this.setCheckingForNewVersions(true);
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.CHECK_FOR_NEW_VERSIONS);
		((CheckForNewVersionsHandler) ph).checkForNewVersion(this.session,
				versionsDoc);
	}

	/**
	 * @param loginName
	 */
	public UserActivity getUserActivity(String sphereId, String loginName) {
		GetUserActivityCommand command = new GetUserActivityCommand( sphereId, loginName );
		Document returnDocument = command.execute(this, AbstractDocument.class);
		return returnDocument != null ? UserActivity.wrap(returnDocument ) : null;		
	}

	public void saveNewSpheresEmails(SphereEmail sphereEmail) {
		ProtocolHandler ph = this.handlers
				.getProtocolHandler(SSProtocolConstants.UPDATE_SPHERES_EMAILS);
		((UpdateSphereEmailsHandler) ph).handleUpdateSphereEmails(sphereEmail);
	}
	
	/**
	 * @return the preferencesController
	 */
	public PreferencesController getPreferencesController() {
		if (this.preferencesController == null){
			this.preferencesController = new PreferencesController();
			this.preferencesController.init((String) this.session.get("username"));
		}
		return this.preferencesController;
	}
	
	public ForwardingController getForwardingController() {
		if (this.forwardingController == null){
			this.forwardingController = new ForwardingController((String) this.session.get("username"));
		}
		return this.forwardingController;
	}
	
	public PreferencesChecker getPreferencesChecker(){
		if (this.preferencesChecker == null){
			String contact = (String) this.session.get("real_name");
			String login = (String) this.session.get("username");
			this.preferencesChecker = new PreferencesChecker(login,
					getVerifyAuth().isAdmin(contact, login));
		}
		return this.preferencesChecker;
	}

	/**
	 * @return the preferencesAdminController
	 */
	public PreferencesAdminController getPreferencesAdminController() {
		return this.preferencesAdminController;
	}

	public void updateMemberVisibility( List<SphereMemberBundle> added, List<SphereMemberBundle> removed ) {
		ProtocolHandler ph = this.handlers.getProtocolHandler(SSProtocolConstants.UPDATE_MEMBER_VISIBILITY);
		((UpdateMemberVisibilityClientHandler) ph).saveSphereMemberVisibility(added,removed);
	}

	/**
	 * 
	 */
	void notifyInsertOperationEnded( InsertOperation caller ) {
		synchronized( this.insertOperatiopnSync ) {
			if ( caller != this.insertOperation ) {
				logger.error( "Notify received from unknown insert operation." );
			}
			this.insertOperation = null;
			if ( this.insertQueue.size() > 0 ) {
				startInsertOperationIfRequire();
			}
		}
	}
	
	/**
	 * 
	 */
	protected void startInsertOperationIfRequire() {
		synchronized( this.insertOperatiopnSync ) {
			if ( this.insertOperation != null ) {
				return;
			}
			this.insertOperation = new InsertOperation( this, this.insertQueue );
			try {
				UiUtils.swtBeginInvoke(this.insertOperation);
				// Absolutely necessary, don't mess with
			} catch (Exception e) {
				this.insertOperation = null;
				this.mP.logException("invokeLater in callException", e);
			}	
		}
		
	}

	/**
	 * @return 
	 * 
	 */
	public String getLogin() {
		return (String) this.session.get(SessionConstants.USERNAME);
	}
	
	public String getContact() {
		return (String) this.session.get(SessionConstants.REAL_NAME);
	}
	
	public boolean isLoginSphere(String sphereId){
		if (sphereId == null)
			return false;
		String loginSphere = getVerifyAuth().getLoginSphere(getLogin());
		if (loginSphere == null)
			return false;
		return loginSphere.equals(sphereId);
	}

	/**
	 * 
	 */
	public void beginClose() {
		logger.warn( "DMC begin Close" );
		this.protocol.removeProtocolListener(this.protocolLifetimeObserver);
		this.protocol.removePacketLoadingListener(this.packetLoadingObserver);
		this.protocol.beginClose();
	}

	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return this.protocol.isValid();
	}

	private void protocolBeingClosed() {
		UiUtils.swtBeginInvoke(new Runnable(){

			public void run() {
				new ExitDialog(DialogsMainCli.this.sF, "Connection to server lost");	
			}
			
		});			
	}
	

	/**
	 * @return the protocol
	 */
	public Protocol getProtocol() {
		return this.protocol;
	};	
	
	class DmCommandHandler extends VoidCommandHandler<DmCommand> {
		/**
		 * @param messageClass
		 */
		public DmCommandHandler() {
			super(DmCommand.class);
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.VoidCommandHandler#execute(ss.common.networking2.Command)
		 */
		@Override
		protected void execute(DmCommand command) throws CommandHandleException {
			handleCommand(command);
		}

	}

	class PacketLoadingObserver implements PacketLoadingListener {

		/* (non-Javadoc)
		 * @see ss.common.networking2.io.PacketLoadingListener#beginPacket(ss.common.networking2.io.PacketHeader)
		 */
		public void beginPacket(PacketHeader header) {
			logger.info( "begin packet " + header  );
			if(!DialogsMainCli.this.getClass().equals(DialogsMainCli.class)) {
				return;
			}
			DialogsMainCli.this.dpb = new DownloadProgressBar(header.getDataSize(), 
					SphereOpenManager.INSTANCE.getSphereNameWhichLoading(header.getId()), header.getId());
			if (DialogsMainCli.this.indDPB != null) {
				DialogsMainCli.this.indDPB.destroyDownloadBar();
				DialogsMainCli.this.indDPB = null;
			}
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.io.PacketLoadingListener#endPacket(ss.common.networking2.io.PacketHeader)
		 */
		public void endPacket(PacketHeader header) {
			if(!DialogsMainCli.this.getClass().equals(DialogsMainCli.class)) {
				return;
			}
			DialogsMainCli.this.dpb.destroyDownloadBar();
			
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.io.PacketLoadingListener#packetDataProgress(ss.common.networking2.io.PacketHeader, int)
		 */
		public void packetDataProgress(PacketHeader header, int loadedBytesCount) {
			if(!DialogsMainCli.this.getClass().equals(DialogsMainCli.class)) {
				return;
			}
			DialogsMainCli.this.dpb.updateDownloadBar(loadedBytesCount);
		}
		
	}

	class ProtocolLifetimeObserver extends ProtocolLifetimeAdapter {

		/* (non-Javadoc)
		 * @see ss.common.networking2.ProtocolLifetimeAdapter#beginClose(ss.common.networking2.ProtocolLifetimeEvent)
		 */
		@Override
		public void beginClose(ProtocolLifetimeEvent e) {
			protocolBeingClosed();
		}
		
	}

	/**
	 * @param loginreturnObjectClass
	 * @return
	 */
	public boolean isMemberOnline(String login) {
		GetMemberStateCommand command = new GetMemberStateCommand();
		command.putArg(SessionConstants.USERNAME, login);
		return command.execute(this, Boolean.class).booleanValue();
	}
	
	public void queryMembersStatesForUi( List<String> contactNames, ReplyObjectHandler<Hashtable<String, Boolean>> replyHandler ) {
		GetMembersStatesCommand command = new GetMembersStatesCommand( contactNames );
		command.beginExecuteForUi( this, replyHandler, AbstractGetterCommand.LONG_DM_TIMEOUT );
	}
	
	public void queryEmailsOfPossibleRecipientsForUi( final String sphereId, ReplyObjectHandler<ArrayList<String>> replyHandler ) {
		final GetEmailsOfPossibleRecipientsCommand command = new GetEmailsOfPossibleRecipientsCommand();
		command.setLookupSphere(sphereId);
		command.beginExecuteForUi( this, replyHandler, AbstractGetterCommand.LONG_DM_TIMEOUT );
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String, Boolean> getMembersStates( List<String> contactNames ) {
		GetMembersStatesCommand command = new GetMembersStatesCommand( contactNames );
		return (Hashtable<String, Boolean>) command.execute(this, Hashtable.class );
	}
	
	/**
	 * @return
	 */
	public Date getCurrentDateTime() {
		GetCurrentDateTimeCommand command = new GetCurrentDateTimeCommand();
		return command.execute(this, Date.class);
	}

	public DownloadProgressBar getProgressBar() {
		return this.dpb;
	}

	/**
	 * @param session2
	 * @param files
	 * @param bindedDocument
	 */
	public void sendMessageFromServer(final Hashtable session2, final AttachedFileCollection files, final Document bindedDocument) {
		Hashtable toSend = (Hashtable) session2.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		PublishMessageAction publish = new PublishMessageAction();
		publish.putSessionArg(toSend);
		publish.putArg(SessionConstants.TO_EMAIL_ATTACHED_FILES, files);
		publish.putArg(SessionConstants.DOCUMENT, bindedDocument);
		publish.beginExecute(this);
	}

	/**
	 * @return 
	 * 
	 */
	public Hashtable<MemberReference, Boolean> getAllMembersStates() {
		final List<String> allContactNames = new ArrayList<String>();
		final List<SupraSphereMember> allMembers = this.verifyAuth.getAllMembers();
		for( SupraSphereMember member : allMembers ) {
			allContactNames.add( member.getContactName() );			
		}
		Hashtable<String, Boolean> states = getMembersStates(allContactNames);
		final Hashtable<MemberReference, Boolean> allMembersStates = new Hashtable<MemberReference, Boolean>();
		for( SupraSphereMember member : allMembers ) {
			Boolean state = states.get( member.getContactName() );
			if ( state == null ) {
				logger.warn( "State is null for " + member );
			}
			allMembersStates.put( member, state != null ? state : false);
		}
		return allMembersStates;
	}

	/**
	 * @param spheresToDelete
	 */
	public void deleteSpheres(List<ManagedSphere> spheresToDelete) {
		DeleteSpheresAction command = new DeleteSpheresAction();
		command.putSessionArg(this.getSession());
		command.setSpheresList( spheresToDelete );
		command.beginExecute(this);
	}
	
	public void deleteSpheresTotal(List<ManagedSphere> spheresToDelete) {
		DeleteSpheresAction command = new DeleteSpheresAction();
		command.putSessionArg(this.getSession());
		command.setSpheresList( spheresToDelete );
		command.setRemoveTotally(true);
		command.beginExecute(this);
	}

	public void searchSupraSphere(String keywords, Query query, boolean inSameTab, boolean isAnonymous) {
		SearchSupraSphere searchSupraSphere = new SearchSupraSphere( this, keywords, query, inSameTab, isAnonymous );
		searchSupraSphere.searchSupraSphere();
	}

	/**
	 * @return
	 */
	public Document getSupraSphereDoc() {
		GetSupraSphereDocCommand command = new GetSupraSphereDocCommand();
		return command.execute(this, AbstractDocument.class );
	}
	
	public SupraSphereMember getSelfSphereMember() {
		AbstractDocument sphereMemberDoc = new GetSelfSphereMemberCommand().execute( this, AbstractDocument.class );
		return sphereMemberDoc != null ? XmlEntityUtils.safeWrap( sphereMemberDoc, SupraSphereMember.class ) : null;
	}

	public void showKeywordSearchResultFlyer(SupraBrowser mb, String keyword) {
		KeywordSearchResult.addFlyer(mb, keyword);
	}
	
	public void showKeywordSearchResult(SupraBrowser mb, String keyword) {
		KeywordSearchResult.loadResultInBrowser(mb, keyword);
	}

	public AbstractDocument searchTagged(SearchSettings settings) {
		SearchSupraSphereCommand command = new SearchSupraSphereCommand();
		command.putArg(SessionConstants.SEARCH_SETTINGS, settings);
		return command.execute(this, AbstractDocument.class);
	}

	
	@SuppressWarnings("unchecked")
	public Vector getEntireThread(final Hashtable session, final String messageId) {
		GetEntireThreadCommand command = new GetEntireThreadCommand();
		command.putSessionArg(session);
		command.putArg(SessionConstants.MESSAGE_ID, messageId);
		return command.execute(this, Vector.class);
	}

	/**
	 * @return
	 */
	public AbstractDocument getContactMessageIds(final String contactName) {
		GetContactMessageIdsCommand command = new GetContactMessageIdsCommand();
		command.putArg(SessionConstants.CONTACT_NAME, contactName);
		return command.execute(this, AbstractDocument.class);
	}

	/**
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vector<Document> getContactNotes(String name) {
		GetContactNotesCommand command = new GetContactNotesCommand();
		command.putArg(SessionConstants.CONTACT_NAME, name);
		return command.execute(this, Vector.class);
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Document> getAttachments(final String sphereId, final String messageId) {
		GetAttachmentsCommand command = new GetAttachmentsCommand();
		command.putArg(SessionConstants.SPHERE_ID2, sphereId);
		command.putArg(SessionConstants.MESSAGE_ID, messageId);
		return command.execute(this, Vector.class);
	}

	/**
	 * @param cd
	 * @return
	 */
	public Vector<Document> getAssociatedFilesForClubdeal(ClubDeal cd) {
		return getAssociatedFilesForClubdeal(cd.getSystemName());
	}
	
	public Vector<Document> getAssociatedFilesForClubdeal(String clubdealId) {
		GetAssociatedFilesCommand command = new GetAssociatedFilesCommand();
		command.putArg(SessionConstants.SPHERE_ID2, clubdealId);
		return command.execute(this, Vector.class);
	}
	
	public Vector<String> getAssotiatedClubdeald(FileStatement file) {
		GetAssociatedClubdealsCommand command = new GetAssociatedClubdealsCommand();
		command.putArg(SessionConstants.DATA_ID, file.getDataId());
		return command.execute(this, Vector.class);
	}

	/**
	 * @param removed
	 * @param dataId
	 */
	public void recallFile(List<String> removed, String dataId) {
		if(removed.size()<1) {
			return;
		}
		RecallFileFromSphereAction action = new RecallFileFromSphereAction();
		Hashtable update = new Hashtable();
		update.put(SessionConstants.SPHERE_LIST, removed);
		update.put(SessionConstants.DATA_ID, dataId);
		action.putSessionArg(update);
		action.beginExecute(this);
	}
	
	public TreeSet<ContactStatement> getDistinctContactDocs() {
		GetAllDistinctContactsCommand command = new GetAllDistinctContactsCommand();
		HashSet<Document> contactDocs = command.execute(this, HashSet.class);
		TreeSet<ContactStatement> contacts = new TreeSet<ContactStatement>();
		for(Document doc : contactDocs) {
			contacts.add(ContactStatement.wrap(doc));
		}
		return contacts;
	}

	/**
	 * @param session2
	 * @param bindedDocument
	 * @param clubdealId
	 */
	public void recallContactMessage(String clubdealId, ContactStatement contact) {
		RecallContactActionCommand command = new RecallContactActionCommand();
		command.putArg(SessionConstants.CONTACT_DOC, contact.getBindedDocument());
		command.putArg(SessionConstants.SPHERE_ID2, clubdealId);
		command.beginExecute(this);
	}

	/**
	 * @return
	 */
	public Collection<? extends ContactStatement> getAvailableContactDocs() {
		TreeSet<ContactStatement> contacts = new TreeSet<ContactStatement>();
		Hashtable sessionCopy = (Hashtable)this.session.clone();
		for(String sphereId : getVerifyAuth().getAvailableGroupSpheresId()) {
			Vector<Document> contactDocs = getAllContacts(this.session, sphereId);
			for(Document doc : contactDocs) {
				contacts.add(ContactStatement.wrap(doc));
			}
		}
		for(SphereReference sphere : getVerifyAuth().getAllAvailablePrivateSpheres((String)this.session.get(SessionConstants.USERNAME))) {
			Vector<Document> contactDocs = getAllContacts(this.session, sphere.getSystemName());
			for(Document doc : contactDocs) {
				contacts.add(ContactStatement.wrap(doc));
			}
		}
		for(ClubdealWithContactsObject clubdeal : ClubDealUtils.INSTANCE.getAllAvaliableClubdealsForUser(this)) {
			for(ContactStatement contact : clubdeal.getContacts()) {
				contacts.add(contact);
			}
		}
		return contacts;
	}
	
	public boolean isAdmin() {
		IsAdminCommand command = new IsAdminCommand();
		command.putSessionArg(this.session);
		return command.execute(this, Boolean.class);
	}
	
	public Hashtable getAllMessages(final String sphereId) {
		GetAllMessagesCommand command = new GetAllMessagesCommand();
		command.putArg(SessionConstants.SPHERE_ID2, sphereId);
		command.putSessionArg(this.session);
		return command.execute(this, Hashtable.class);
	}
	
	public void setUpVerifyAuth() {
		GetVerifyAuthCommand command = new GetVerifyAuthCommand();
		command.putArg(SessionConstants.USERNAME, getLogin());
		VerifyAuth verifyAuth = command.execute(this, VerifyAuth.class);
		setVerifyAuth(verifyAuth);
	}

	/**
	 * @param added
	 * @param removed
	 */
	public void updateClubdealVisibilityForMember(final ClubDeal clubdeal, final String contactName, final boolean added) {
		UpdateClubdealVisibilityAction action = new UpdateClubdealVisibilityAction();
		action.putArg(SessionConstants.SPHERE_DEFINITION, clubdeal.getBindedDocument());
		action.putArg(SessionConstants.CONTACT_NAME, contactName);
		action.putArg(SessionConstants.ADDED_REMOVED, added);
		action.beginExecute(this);
	}
	
	public List<String> getAllVisibleContactNames(){
		GetAllVisibleContactsNames command = new GetAllVisibleContactsNames();
		return command.execute(this, ArrayList.class);
	}

	/**
	 * @param cd
	 */
	public void updateClubdealVisibilityForAdmin(ClubDeal cd) {
		updateClubdealVisibilityForMember(cd, getVerifyAuth().getAdminName(), true);
	}
	
	public int getNewAssetsCount(final String sphereId) {
		GetNewAssetsCountCommand command = new GetNewAssetsCountCommand();
		command.putArg(SessionConstants.SPHERE_ID2, sphereId);
		command.putArg(SessionConstants.REAL_NAME, (String)this.session.get(SessionConstants.REAL_NAME));
		return command.execute(this, Integer.class);
	}

	/**
	 * @param oldName
	 * @param newName
	 */
	public boolean renameContactType(final String oldName, final String newName) {
		RenameContactTypeAction action = new RenameContactTypeAction();
		action.putArg(SessionConstants.OLD_NAME, oldName);
		action.putArg(SessionConstants.NEW_NAME, newName);
		try {
			return action.execute(this.protocol, Boolean.class);
		} catch (CommandExecuteException ex) {
			return false;
		}
	}
	
	public void publishFile(final AttachedFile attachedFile, final String sphereId, final String messageId, final String subject, final String body) {
		PublishFileAction action = new PublishFileAction();
		action.putArg(SessionConstants.SUBJECT, StringUtils.getTrimmedString(subject));
		action.putArg(SessionConstants.BODY, StringUtils.getTrimmedString(subject));
		action.putArg(SessionConstants.MESSAGE_ID, messageId);
		
		AttachedFileCollection collection = new AttachedFileCollection();
		collection.add(attachedFile);
		action.putArg(SessionConstants.FILES, collection);
		
		Hashtable session = (Hashtable)getSession().clone();
		session.put(SessionConstants.SPHERE_ID, sphereId);
		action.putSessionArg(session);
		action.beginExecute(this);
	}

	/**
	 * @param string
	 */
	public void addPendingToSupraSphereFrame(String sphere) {
		getSF().getPendingSpheres().addPending(sphere);
	}

	/**
	 * @param roleObject
	 */
	public void handleSphereRoleRemoved(SphereRoleObject roleObject) {
		handleSphereRoleRenamed(roleObject, SphereRoleObject.getDefaultName());
	}
	
	public void handleSphereRoleRenamed(SphereRoleObject roleObject, final String replacement) {
		SphereRoleRenameAction action = new SphereRoleRenameAction();
		action.putSphereRole(roleObject);
		action.putReplacement(replacement);
		action.beginExecute(this);
	}

	/**
	 * @return
	 */
	public SphereReferenceList getAllSpheresByType(final String keyword, final Vector<String> requiredTypes) {
		GetSpheresByRoleCommand command = new GetSpheresByRoleCommand();
		command.setContactName(getContact());
		command.setKeyword(keyword);
		command.setSphereRoleList(requiredTypes);
		return command.execute(this, SphereReferenceList.class);
	}

	/**
	 * @param parentDoc
	 * @param sphereId
	 * @param keywordText
	 */
	public void doTagAction(Document parentDoc, String sphereId,
			String keywordText) {
		TagActionCommand tagCommand = new TagActionCommand(parentDoc, sphereId, keywordText);
		tagCommand.putSessionArg(getSession());
		tagCommand.beginExecute(this);
	}
	
	public void lockContact(final String username) {
		if(StringUtils.isBlank(username)) {
			logger.error("Contact Null Pointer!!!");
			return;
		}
		LockContactAction action = new LockContactAction(username);
		action.beginExecute(this);
	}
	
	public void unlockContact(final String username) {
		if(StringUtils.isBlank(username)) {
			logger.error("Contact Null Pointer!!!");
			return;
		}
		UnlockContactAction action = new UnlockContactAction(username);
		action.beginExecute(this);
	}

	/**
	 * @param login
	 * @return
	 */
	public boolean isContactLocked(String login) {
		IsContactLockedCommand command = new IsContactLockedCommand(login);
		return command.execute(this, Boolean.class);
	}

}