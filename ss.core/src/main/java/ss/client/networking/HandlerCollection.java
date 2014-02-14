/**
 * Jul 4, 2006 : 5:06:47 PM
 */
package ss.client.networking;

import java.util.Hashtable;

import ss.client.networking.protocol.BootstrapCompleteHandler;
import ss.client.networking.protocol.ChangePassphraseNextLoginHandler;
import ss.client.networking.protocol.CheckForNewVersionsHandler;
import ss.client.networking.protocol.FindAssetsInSameConceptSetHandler;
import ss.client.networking.protocol.GetInfoForHandler;
import ss.client.networking.protocol.GetSubListHandler;
import ss.client.networking.protocol.InviteCompleteHandler;
import ss.client.networking.protocol.MatchAgainstHistoryHandler;
import ss.client.networking.protocol.NotifySystemTrayHandler;
import ss.client.networking.protocol.PromptPassphraseChangeHandler;
import ss.client.networking.protocol.RecallMessageHandler;
import ss.client.networking.protocol.ReceiveResultsFromXMLSearchHandler;
import ss.client.networking.protocol.RefreshPresenceHandler;
import ss.client.networking.protocol.SearchForSpecificInIndexHandler;
import ss.client.networking.protocol.SendByteRouterInitHandler;
import ss.client.networking.protocol.SendPopupNotificationHandler;
import ss.client.networking.protocol.SendSubListHandler;
import ss.client.networking.protocol.SphereAccessDeniedMessageHandler;
import ss.client.networking.protocol.UpdateDocumentHandler;
import ss.client.networking.protocol.UpdateHandler;
import ss.client.networking.protocol.UpdateSphereDefinitionHandler;
import ss.client.networking.protocol.UpdateVerifyHandler;
import ss.client.networking.protocol.UpdateVerifySphereDocumentHandler;
import ss.client.networking.protocol.UseDocumentHandler;
import ss.client.networking.protocol.VoteDocumentHandler;
import ss.client.networking.protocol.actions.AddChangePassphraseNextLoginHandler;
import ss.client.networking.protocol.actions.AddEventToMessageHandler;
import ss.client.networking.protocol.actions.AddInviteToContactHandler;
import ss.client.networking.protocol.actions.AddLocationsToDocHandler;
import ss.client.networking.protocol.actions.AddQueryToContactHandler;
import ss.client.networking.protocol.actions.AddSphereToFavouritesHandler;
import ss.client.networking.protocol.actions.EntitleContactForOneSphereHandler;
import ss.client.networking.protocol.actions.EntitleContactForSphereHandler;
import ss.client.networking.protocol.actions.GetAndSendInviteTextHandler;
import ss.client.networking.protocol.actions.MakeCurrentSphereCoreHandler;
import ss.client.networking.protocol.actions.MatchAgainstOtherHistoryHandler;
import ss.client.networking.protocol.actions.MatchAgainstRecentHistoryHandler;
import ss.client.networking.protocol.actions.OpenSphereForMembersHandler;
import ss.client.networking.protocol.actions.PublishHandler;
import ss.client.networking.protocol.actions.RegisterMemberHandler;
import ss.client.networking.protocol.actions.RemoveSphereFromFavouritesHandler;
import ss.client.networking.protocol.actions.RemoveSphereHandler;
import ss.client.networking.protocol.actions.ReplaceDocHandler;
import ss.client.networking.protocol.actions.ReplaceUsernameInMembershipHandler;
import ss.client.networking.protocol.actions.SaveMarkForSphereHandler;
import ss.client.networking.protocol.actions.SaveQueryViewHandler;
import ss.client.networking.protocol.actions.SaveTabOrderToContactHandler;
import ss.client.networking.protocol.actions.SaveUserPrivilegeClientHandler;
import ss.client.networking.protocol.actions.SaveWindowPositionToContactHandler;
import ss.client.networking.protocol.actions.SearchSphereHandler;
import ss.client.networking.protocol.actions.SendDefinitionMessagesHandler;
import ss.client.networking.protocol.actions.SendEmailFromServerHandler;
import ss.client.networking.protocol.actions.SetAsSeenAndIndexJustInCaseHandler;
import ss.client.networking.protocol.actions.SetEmailForwardingRulesHandler;
import ss.client.networking.protocol.actions.StartRemoteBuildHandler;
import ss.client.networking.protocol.actions.UpdateMemberVisibilityClientHandler;
import ss.client.networking.protocol.actions.UpdateSphereDefaultDeliveryHandler;
import ss.client.networking.protocol.actions.UpdateSphereEmailsHandler;
import ss.client.networking.protocol.obosolete.RebootHandler;
import ss.client.networking.protocol.obosolete.UpdateContactDocHandle;
import ss.common.DmCommand;
import ss.common.ProtocolHandler;

/**
 * @author dankosedin
 * 
 */
public class HandlerCollection {

	protected Hashtable<String, ProtocolHandler> handlers;

	protected DialogsMainCli cli;

	public HandlerCollection(DialogsMainCli cli) {
		this.cli = cli;
		this.handlers = new Hashtable<String, ProtocolHandler>();
		constructAndRegisterAllHandlers();
	}

	public boolean handle(DmCommand comm) {
		final String handlerName = comm.getHandlerName();
		ProtocolHandler handler = getProtocolHandler(handlerName);
		if (handler != null) {
			handler.handle(comm.getData());
			return true;
		} else {
			return false;
		}
	}

	public ProtocolHandler getProtocolHandler(String handlerName) {
		return handlerName != null ? this.handlers.get(handlerName) : null;
	}
	
	@SuppressWarnings("deprecation")
	protected void constructAndRegisterAllHandlers() {
		ProtocolHandler handler = new VoteDocumentHandler(this.cli);
		registerHandler(handler);
		handler = new RecallMessageHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateSphereDefinitionHandler(this.cli);
		registerHandler(handler);
		handler = new GetInfoForHandler(this.cli);
		registerHandler(handler);
		handler = new SendByteRouterInitHandler(this.cli);
		registerHandler(handler);
		handler = new FindAssetsInSameConceptSetHandler(this.cli);
		registerHandler(handler);
		handler = new GetSubListHandler(this.cli);
		registerHandler(handler);
		handler = new SendSubListHandler(this.cli);
		registerHandler(handler);
		handler = new MatchAgainstHistoryHandler(this.cli);
		registerHandler(handler);
		handler = new SearchForSpecificInIndexHandler(this.cli);
		registerHandler(handler);
		handler = new InviteCompleteHandler(this.cli);
		registerHandler(handler);
		handler = new BootstrapCompleteHandler(this.cli);
		registerHandler(handler);
		handler = new CheckForNewVersionsHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateVerifyHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateVerifySphereDocumentHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateContactDocHandle(this.cli);
		registerHandler(handler);
		handler = new RefreshPresenceHandler(this.cli);
		registerHandler(handler);
		handler = new PromptPassphraseChangeHandler(this.cli);
		registerHandler(handler);
		handler = new ChangePassphraseNextLoginHandler(this.cli);
		registerHandler(handler);
		//
		// handler = new GetPersonalContactsHandler(this.cli);
		// registerHandler(handler);
		//
		//
		handler = new NotifySystemTrayHandler(this.cli);
		registerHandler(handler);
		handler = new SendPopupNotificationHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateHandler(this.cli);
		registerHandler(handler);
		handler = new RebootHandler(this.cli);
		registerHandler(handler);
		handler = new ReceiveResultsFromXMLSearchHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateDocumentHandler(this.cli);
		registerHandler(handler);
		// some usefull not used handlers
		handler = new GetAndSendInviteTextHandler(this.cli);
		registerHandler(handler);
		handler = new UseDocumentHandler(this.cli);
		registerHandler(handler);
		handler = new OpenSphereForMembersHandler(this.cli);
		registerHandler(handler);
		// to-server-only Handlers
		handler = new SetEmailForwardingRulesHandler(this.cli);
		registerHandler(handler);
		handler = new MakeCurrentSphereCoreHandler(this.cli);
		registerHandler(handler);
		handler = new RemoveSphereHandler(this.cli);
		registerHandler(handler);
		handler = new SearchSphereHandler(this.cli);
		registerHandler(handler);
		handler = new SendDefinitionMessagesHandler(this.cli);
		registerHandler(handler);
		handler = new StartRemoteBuildHandler(this.cli);
		registerHandler(handler);
		handler = new AddChangePassphraseNextLoginHandler(this.cli);
		registerHandler(handler);
		handler = new AddEventToMessageHandler(this.cli);
		registerHandler(handler);
		handler = new SaveTabOrderToContactHandler(this.cli);
		registerHandler(handler);
		handler = new SaveWindowPositionToContactHandler(this.cli);
		registerHandler(handler);
		handler = new EntitleContactForOneSphereHandler(this.cli);
		registerHandler(handler);
		handler = new EntitleContactForSphereHandler(this.cli);
		registerHandler(handler);
		handler = new AddLocationsToDocHandler(this.cli);
		registerHandler(handler);
		handler = new RegisterMemberHandler(this.cli);
		registerHandler(handler);
		handler = new SaveMarkForSphereHandler(this.cli);
		registerHandler(handler);
		handler = new ReplaceDocHandler(this.cli);
		registerHandler(handler);
		handler = new SaveQueryViewHandler(this.cli);
		registerHandler(handler);
		handler = new AddQueryToContactHandler(this.cli);
		registerHandler(handler);
		handler = new AddInviteToContactHandler(this.cli);
		registerHandler(handler);
		handler = new PublishHandler(this.cli);
		registerHandler(handler);
		handler = new SetAsSeenAndIndexJustInCaseHandler(this.cli);
		registerHandler(handler);
		handler = new MatchAgainstRecentHistoryHandler(this.cli);
		registerHandler(handler);
		handler = new MatchAgainstOtherHistoryHandler(this.cli);
		registerHandler(handler);
		handler = new SendEmailFromServerHandler(this.cli);
		registerHandler(handler);
		handler = new ReplaceUsernameInMembershipHandler(this.cli);
		registerHandler(handler);
		handler = new SaveUserPrivilegeClientHandler(this.cli);
		registerHandler(handler);
		handler = new UpdateSphereEmailsHandler(this.cli);
		registerHandler(handler);
		handler = new AddSphereToFavouritesHandler(this.cli);
		registerHandler(handler);
		handler = new RemoveSphereFromFavouritesHandler(this.cli);
		registerHandler(handler);
		//TODO check before usage commit
//		handler = new UpdateRolesModelHandler(this.cli);
//		registerHandler(handler);
//		handler = new GetRolesModelDocumentHandler(this.cli);
//		registerHandler(handler);
//		handler = new GetResultMessageHandler(this.cli);
//		registerHandler(handler);
		handler = new UpdateMemberVisibilityClientHandler( this.cli );
		registerHandler(handler);
		handler = new UpdateSphereDefaultDeliveryHandler( this.cli );
		registerHandler(handler);
		handler = new SphereAccessDeniedMessageHandler( this.cli );
		registerHandler(handler);

	}

	public void registerHandler(ProtocolHandler handler) {
		this.handlers.put(handler.getProtocol(), handler);
	}

}
