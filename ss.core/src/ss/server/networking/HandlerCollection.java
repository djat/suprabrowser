package ss.server.networking;

import java.util.Hashtable;

import ss.common.DmCommand;
import ss.common.ProtocolHandler;
import ss.refactor.supraspheredoc.old.unused.CrossreferenceSpheresHandler;
import ss.refactor.supraspheredoc.old.unused.RegisterSphereWithMembersHandler;
import ss.server.networking.protocol.*;
import ss.server.networking.protocol.obsolete.GetStatsForSphereHandler;

public class HandlerCollection {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HandlerCollection.class);
	
	private final Hashtable<String, ProtocolHandler> handlers;

	private final DialogsMainPeer peer;

	public HandlerCollection(DialogsMainPeer peer) {
		this.peer = peer;
		this.handlers = new Hashtable<String, ProtocolHandler>();
		constructAndRegisterAllHandlers();
	}

	public boolean handle(DmCommand command) {
		String protocol = command.getHandlerName();
		ProtocolHandler handler = getProtocolHandler(protocol);
		logger.info("New packet protocol:" + protocol);
		if (handler != null) {
			handler.handle(command.getData());
			return true;
		} else {
			return false;
		}
	}

	public ProtocolHandler getProtocolHandler(String protocolName) {
		return protocolName != null ? this.handlers.get(protocolName) : null;
	}

	@SuppressWarnings("deprecation")
	private void constructAndRegisterAllHandlers() {
		ProtocolHandler ph;
		ph = new GetForwardResultsHandler();
		registerHandler(ph);
		ph = new ReplaceUsernameInMembershipHandler(this.peer);
		registerHandler(ph);
		ph = new SetEmailForwardingRulesHandler(this.peer);
		registerHandler(ph);
		ph = new GetInfoForHanlder();
		registerHandler(ph);
		ph = new MakeCurrentSphereCoreHandler(this.peer);
		registerHandler(ph);
		ph = new RemoveSphereHandler(this.peer);
		registerHandler(ph);
		ph = new AddChangePassphraseNextLoginHandler(this.peer);
		registerHandler(ph);
		ph = new FindAssetsInSameConceptSetHandler(this.peer);
		registerHandler(ph);
		ph = new SearchSphereHandler(this.peer);
		registerHandler(ph);
		ph = new SendDefinitionMessagesHandler(this.peer);
		registerHandler(ph);
		ph = new CheckForNewVersionsHandler(this.peer);
		registerHandler(ph);
		ph = new StartRemoteBuildHandler();
		registerHandler(ph);
		ph = new AddEventToMessageHandler(this.peer);
		registerHandler(ph);
		ph = new SaveTabOrderToContactHandler(this.peer);
		registerHandler(ph);
		ph = new SaveWindowPositionToContactHandler(this.peer);
		registerHandler(ph);
		ph = new NotifySystemTrayHandler();
		registerHandler(ph);
		ph = new SendPopupNotificationHandler();
		registerHandler(ph);
		ph = new EntitleContactForOneSphereHandler(this.peer);
		registerHandler(ph);
		ph = new EntitleContactForSphereHandler(this.peer);
		registerHandler(ph);
		ph = new AddLocationsToDocHandler(this.peer);
		registerHandler(ph);
		ph = new OpenSphereForMembersHandler(this.peer);
		registerHandler(ph);
		ph = new RegisterMemberHandler(this.peer);
		registerHandler(ph);
		ph = new UpdateDocumentHandler(this.peer);
		registerHandler(ph);
		ph = new SaveMarkForSphereHandler(this.peer);
		registerHandler(ph);
		ph = new VoteDocumentHandler(this.peer);
		registerHandler(ph);
		ph = new UseDocumentHandler(this.peer);
		registerHandler(ph);
		ph = new ReplaceDocHandler(this.peer);
		registerHandler(ph);
		ph = new SaveQueryViewHandler(this.peer);
		registerHandler(ph);
		ph = new AddQueryToContactHandler(this.peer);
		registerHandler(ph);
		ph = new AddInviteToContactHandler(this.peer);
		registerHandler(ph);
		ph = new SendByteRouterInitHandler();
		registerHandler(ph);
		ph = new SendSubListHandler();
		registerHandler(ph);
		ph = new GetSubListHandler(this.peer);
		registerHandler(ph);
		ph = new PublishHandler(this.peer);
		registerHandler(ph);
		ph = new PublishForwardedMessagesHandler(this.peer);
		registerHandler(ph);
		ph = new SetAsSeenAndIndexJustInCaseHandler(this.peer);
		registerHandler(ph);
		ph = new GetAndSendInviteTextHandler();
		registerHandler(ph);
		ph = new MatchAgainstRecentHistoryHandler(this.peer);
		registerHandler(ph);
		ph = new MatchAgainstOtherHistoryHandler(this.peer);
		registerHandler(ph);
		ph = new MatchAgainstHistoryHandler(this.peer);
		registerHandler(ph);
		ph = new SearchForSpecificInIndexHandler(this.peer);
		registerHandler(ph);
		ph = new SendEmailFromServerHandler(this.peer);
		registerHandler(ph);
		ph = new RecallMessageHandler(this.peer);
		registerHandler(ph);
		ph = new GetStatsForSphereHandler(this.peer);
		registerHandler(ph);
		ph = new RebootrpmHandler();
		registerHandler(ph);

		// handler with empty handle
		// TODO remove
		ph = new Check_authHandler();
		// registerHandler(ph);

		// /**
		// * @deprecated
		// */
		ph = new ReplaceMemberHandler();
		registerHandler(ph);
		ph = new CrossreferenceSpheresHandler(this.peer);
		registerHandler(ph);
		ph = new RegisterSphereWithMembersHandler(this.peer);
		registerHandler(ph);
		ph = new SaveUserPrivilegeServerHandler(this.peer);
		registerHandler(ph);
		ph = new UpdateSphereEmailsHandler(this.peer);
		registerHandler(ph);
		ph = new AddSphereToContactFavouritesHandler(this.peer);
		registerHandler(ph);
		ph = new RemoveSphereFromContactFavouritesHandler(this.peer);
		registerHandler(ph);
		ph = new RegisterWorkflowResponseHandler(this.peer);
		registerHandler(ph);
		ph = new UpdateMemberVisibilityServerHandler(this.peer);
		registerHandler(ph);

	}

	public void registerHandler(ProtocolHandler handler) {
		this.handlers.put(handler.getProtocol(), handler);
	}

}
