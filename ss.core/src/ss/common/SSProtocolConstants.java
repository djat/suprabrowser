package ss.common;

public interface SSProtocolConstants {

	// checked - some questions(session).
	public static final String ADD_CHANGE_PASSPHRASE_NEXT_LOGIN = "addChangePassphraseNextLogin";

	// checked
	public static final String ADD_EVENT_TO_MESSAGE = "addEventToMessage";

	// checked
	public static final String ADD_INVITE_TO_CONTACT = "addInviteToContact";

	// checked - sq(NEW_MESSAGE_ID)
	public static final String ADD_LOCATIONS_TO_DOC = "addLocationsToDoc";

	// checked - server part need some refactorings
	public static final String REFRESH_PRESENCE = "refreshPresence";
	
	// checked
	public static final String ADD_QUERY_TO_CONTACT = "addQueryToContact";
	
	// checked
	public static final String ADD_SPHERE_TO_FAVOURITES = "addSphereToFavourites";

	// checked - sq(VERIFY_AUTH)
	public static final String BOOTSTRAP_COMPLETE = "bootstrapComplete";

	// checked
	public static final String CHECK_FOR_NEW_VERSIONS = "checkForNewVersions";

	// checked - sq(session)
	public static final String ENTITLE_CONTACT_FOR_ONE_SPHERE = "entitleContactForOneSphere";

	// checked
	public static final String ENTITLE_CONTACT_FOR_SPHERE = "entitleContactForSphere";

	// checked
	public static final String FIND_ASSETS_IN_SAME_CONCEPT_SET = "findAssetsInSameConceptSet";

	// checked
	public static final String GET_AND_SEND_INVITE_TEXT = "getAndSendInviteText";

	// checked
	public static final String GET_ENTIRE_THREAD = "getEntireThread";

//	// checked
//	public static final String GET_MEMBER_PRESENCE = "getMemberPresence";

	
	// checked - sq(MESSAGE_ID_SERVER)
	public static final String GET_SUB_LIST = "getSubList";

//	// chacked
//	public static final String GET_SUB_PRESENCE = "getSubPresence";


	// checked
	public static final String INVITE_COMPLETE = "inviteComplete";

	// checked
	public static final String MAKE_CURRENT_SPHERE_CORE = "makeCurrentSphereCore";

	// checked
	public static final String MATCH_AGAINST_HISTORY = "matchAgainstHistory";

	// checked
	public static final String MATCH_AGAINST_OTHER_HISTORY = "matchAgainstOtherHistory";

	// checked
	public static final String MATCH_AGAINST_RECENT_HISTORY = "matchAgainstRecentHistory";

	// checked
	public static final String NOTIFY_SYSTEM_TRAY = "notifySystemTray";

	// checked
	public static final String OPEN_SPHERE_FOR_MEMBERS = "openSphereForMembers";

	// checked
	public static final String PROMPT_PASSPHRASE_CHANGE = "promptPassphraseChange";

	// checked
	public static final String PUBLISH = "publish";

	// checked
	public static final String RECALL_MESSAGE = "recallMessage";

	// NOT checked - sendDefinitionMessage problem
	public static final String RECEIVE_RESULTS_FROM_XMLSEARCH = "receiveResultsFromXMLSearch";

	// checked
	public static final String REGISTER_MEMBER = "registerMember";
	
	public static final String REGISTER_WORKFLOW_RESPONSE = "registerWorkflowResponse";

	//	checked
	public static final String REMOVE_FROM_FAVORITES = "removeFromFavourites";

	// checked
	public static final String REMOVE_SPHERE = "removeSphere";

	// checked
	public static final String REPLACE_DOC = "replaceDoc";

	public static final String REPLACE_USERNAME_IN_MEMBERSHIP = "replaceUsernameInMembership";

	// checked
	public static final String SAVE_MARK_FOR_SPHERE = "saveMarkForSphere";

	// checked
	public static final String SAVE_QUERY_VIEW = "saveQueryView";

	// checked
	public static final String SAVE_TAB_ORDER_TO_CONTACT = "saveTabOrderToContact";

	// checked
	public static final String SAVE_WINDOW_POSITION_TO_CONTACT = "saveWindowPositionToContact";

	// checked
	public static final String SEARCH_FOR_SPECIFIC_IN_INDEX = "searchForSpecificInIndex";

	// checked - sq(IS_SUPRA_QUERY)
	public static final String SEARCH_SPHERE = "searchSphere";

	// checked
	public static final String SEND_BYTE_ROUTER_INIT = "sendByteRouterInit";

	// checked
	public static final String SEND_DEFINITION_MESSAGES = "sendDefinitionMessages";

	// checked - sq(session)
	public static final String SEND_EMAIL_FROM_SERVER = "sendEmailFromServer";

	// checked
	public static final String SEND_POPUP_NOTIFICATION = "sendPopupNotification";

	// checked
	public static final String SEND_SUB_LIST = "sendSubList";

	// checked - sq(session)
	public static final String SET_AS_SEEN_AND_INDEX_JUST_IN_CASE = "setAsSeenAndIndexJustInCase";

	// checked
	public static final String SET_EMAIL_FORWARDING_RULES = "setEmailForwardingRules";

	// checked
	public static final String START_REMOTE_BUILD = "startRemoteBuild";


	// checked - some refactoring needed
	public static final String UPDATE = "update";

	// checked - sended only by CROSSREFERENCE_SPHERES and
	// REGISTER_SPHERE_WITH_MEMBERS.
	// only server handler.
	/**
	 * @deprecated
	 */
	public static final String UPDATE_AUTH = "update_auth";

	// checked - client->server ok server->client need to be refactored
	// also Client send FILE_NAME. server ignore it
	public static final String UPDATE_DOCUMENT = "update_document";
	
	// checked
	public static final String UPDATE_SPHERE_DEFINITION = "updateSphereDefinition";

	// checked
	public static final String UPDATE_VERIFY = "updateVerify";

	// checked
	public static final String UPDATE_VERIFY_SPHERE_DOCUMENT = "updateVerifySphereDocument";

	// checked Empty client handle();
	public static final String USE_DOCUMENT = "useDocument";

	// checked - used for callInsert only
	public static final String VOTE = "vote";
	
	//used for callInsert
	public static final String RECALL = "recall";

	// checked - Client send FILE_NAME. server ignore it
	public static final String VOTE_DOCUMENT = "voteDocument";

	// checked
	public static final String SAVE_USER_PRIVILEGS = "setUserPrivilegsHandler";
	
    public static final String UPDATE_SPHERES_EMAILS = "UpdateSpheresEmailsHandler";

	public static final String UPDATE_MEMBER_VISIBILITY = "UpdateMemberVisibility";

	public static final String ONLY_IF_EXISTS = "onlyIfExists";;
	
	public static final String FORCE_ONLY_IF_EXISTS = "forceOnlyIfExists";

	public static final String UPDATE_DEFAULT_DELIVERY = "updateDefaultDelivery";;
	
	/// Obsolete contants {{
	
	// checked - SupraServer and Supra client need to be refactored
	public static final String CHANGE_PASSPHRASE_NEXT_LOGIN = "changePassphraseNextLogin";

	// emty
	/**
	 * @deprecated
	 */
	public static final String CHECK_AUTH = "check_auth";

	// checked
	public static final String CHECK_FOR_EXISTING_CONTACT = "checkForExistingContact";

	// checked - sq(session,ENABLED_DOC)
	/**
	 * @deprecated
	 */
	public static final String CROSSREFERENCE_SPHERES = "crossreferenceSpheres";

	/**
	 * @deprecated
	 */
	public static final String GET_FORWARD_RESULTS = "getForwardResults";

	// checked
	/**
	 * @deprecated
	 */
	public static final String GET_MODEL_OPTIONS = "get_model_options";

	// checked
	/**
	 * @deprecated
	 */
	public static final String GET_MODEL_THRESHOLD = "get_model_threshold";
	
	// bugged - shaking of DOC only.
	public static final String GET_INFO_FOR = "getInfoFor";
	
	// checked - sq(CREATE_DEFINITION,SPHERE_ID also server send it to all
	// user sessions, but only one of them intrested in this.)
	public static final String GET_SPHERE_DEFINITION = "getSphereDefinition";

//	 checked - sq(SYSTEM_NAME). Handlers never work
	/**
	 * @deprecated
	 */
	public static final String GET_STATS_FOR_SPHERE = "getStatsForSphere";


	// checked - client part never recieve it.
	/**
	 * @deprecated
	 */
	public static final String REBOOT = "reboot";
	
	// checked
	/**
	 * @deprecated
	 */
	public static final String REBOOTRPM = "rebootrpm";
	
//	 checked -sq(session)
	/**
	 * @deprecated
	 */
	public static final String REGISTER_SPHERE_WITH_MEMBERS = "registerSphereWithMembers";

	// checked - sq(session)
	/**
	 * @deprecated
	 */
	public static final String REPLACE_MEMBER = "replaceMember";
	
	// checked
	/**
	 * @deprecated
	 */
	public static final String SEND_DEFINITION_ALL_MESSAGES = "sendDefinitionAllMessages";

	// checked
	/**
	 * @deprecated
	 */
	public static final String START_REPLIES = "start_replies";
	
	// checked - server never send this
	/**
	 * @deprecated
	 */
	public static final String UPDATE_CONTACT_DOC = "updateContactDoc";

	public static final String PUBLISH_FORWARDED = "publishForwarded";

	public static final String ACCESS_DENIED = "access denied";
	
	/// }}} Obsolete contants
	
}
