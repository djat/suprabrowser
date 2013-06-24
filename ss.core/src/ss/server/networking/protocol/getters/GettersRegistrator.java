/**
 * 
 */
package ss.server.networking.protocol.getters;

import ss.framework.networking2.Protocol;
import ss.refactor.supraspheredoc.old.unused.GetPrivateMembersHandler;
import ss.refactor.supraspheredoc.old.unused.GetSelfSphereMemberHandler;
import ss.server.networking.AbstractRegistrator;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.protocol.callbacks.RenameContactTypeActionHandler;
/**
 * 
 */
public class GettersRegistrator extends AbstractRegistrator {

	/**
	 * @param peer
	 * @param protocol
	 */
	public GettersRegistrator(DialogsMainPeer peer, Protocol protocol) {
		super(peer, protocol);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.networking.AbstractRegistrator#registerHandlers()
	 */
	@Override
	public void registerHandlers() {
		register(CheckExistingUsernameHandler.class);
		register(CheckForExistingContactHandler.class);
		register(CreateMessageIdOnServerHandler.class);
		register(CreateNewProfileHandler.class);
		register(FindURLSbyTagHandler.class);
		register(GetAllContactsHandler.class);
		register(GetAllKeywordsHandler.class);
		register(GetAllSpheresHandler.class);
		register(GetContactFromLoginHandler.class);
		register(GetEmailInfoHandler.class);
		register(GetExistingQueryHandler.class);
		register(GetInitialPresenceHandler.class);
		register(GetKeywordsWithUniqueHandler.class);
		register(GetMachinePassHandler.class);
		register(GetMachineVerifierForProfileHandler.class);
		register(GetMembersForHandler.class);
		register(GetMyContactHandler.class);
		register(GetPersonalContactsForEmailHandler.class);
		register(GetPersonalContactsForSphereHandler.class);
		register(GetPrivateDomainNamesHandler.class);
		register(GetPrivateMembersHandler.class);
		register(GetRecentBookmarksHandler.class);
		register(GetRecentQueriesHandler.class);
		register(GetSpecificIdHandler.class);
		register(GetSphereDefinitionHandler.class);
		register(GetSphereOrderHandler.class);
		register(GetUserActivityServerHandler.class);
		register(MatchAgainstHistoryForHighlightHandler.class);		
		register(SearchSupraSphereHandler.class);
		register(GetBookmarkAddressesHandler.class);
		register(GetMemberStateHandler.class);
		register(GetMembersStatesHandler.class);
		register(GetCurrentDateTimeCommandHandler.class);
		register(GetEmailsOfPossibleRecipientsHandler.class);
		register(GetSupraSphereDocHandler.class);
		register(GetAllTagsCommandHandler.class);
		register(GetSelfSphereMemberHandler.class);
		register(GetEntireThreadCommandHandler.class);
		register(GetContactMessageIdsHandler.class);
		register(GetInfoOnRelatedKeywordsHandler.class);
		register(GetContactNotesCommandHandler.class);
		register(MatchAgainstOtherHistoryForHighlightHandler.class);
		register(GetAttachmentsCommandHandler.class);
		register(GetAssociatedClubdealsCommandHandler.class);
		register(GetAssociatedFilesCommandHandler.class);
		register(GetAllDistinctContactsCommandHandler.class);
		register(IsAdminCommandHandler.class);
		register(GetAllMessagesCommandHandler.class);
		register(GetVerifyAuthCommandHandler.class);
		register(GetAllAvailableContactsNamesHandler.class);
		register(GetNewAssetsCountCommandHandler.class);
		register(RenameContactTypeActionHandler.class);
		register(GetDraftEmailListToSendHandler.class);
		register(SearchMostRecentMessagesInSphereHandler.class);
		register(GetUserPersonalEmailAddressHandler.class);
		register(GetSphereTypesCommandHandler.class);
		register(UpdateAdminsCommandHandler.class);
		register(GetSpheresByRoleCommandHandler.class);
		register(GetSpheresWithNewAssetsCountCommandHandler.class);
		register(GetContactSpheresForSpecificContactCommandHandler.class);
		register(IsContactLockedHandler.class);
	}

}
