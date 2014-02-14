package ss.common;

import java.util.Hashtable;

/**
 * Command defines all commands within the SupraSphere network
 * 
 * @author sbitteker
 * 
 */
public class Command {

	private String protocol;

	private Hashtable update;

	public Command(String protocol, Hashtable update) {
		this.protocol = protocol;
		this.update = update;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public Hashtable getUpdate() {
		return this.update;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		sb.append( this.protocol );
		sb.append( "]. Values: " );
		sb.append( MapUtils.valuesWithStringKeysToString( this.update ) );
		return sb.toString();
	}

	/*
	 * public String public String
	 * 
	 * 
	 * else if (protocol.equals("check_auth")) { } else if
	 * (protocol.equals("getContactFromLogin")) { } else if
	 * (protocol.equals("getKeywordsWithUnique")) { } else if
	 * (protocol.equals("getInfoFor")) { } else if
	 * (protocol.equals("checkExistingUsername")) { } else if
	 * (protocol.equals("getPrivateMembers")) { } else if
	 * (protocol.equals("makeCurrentSphereCore")) { } else if
	 * (protocol.equals("removeSphere")) { else if
	 * (protocol.equals("addChangePassphraseNextLogin")) { } else if
	 * (protocol.equals("getExistingQuery")) { } else if
	 * (protocol.equals("findAssetsInSameConceptSet")) { } else if
	 * (protocol.equals("createMessageIdOnServer")) { } else if
	 * (protocol.equals("getAllSpheres")) { } else if
	 * (protocol.equals("searchSphere")) { // was getAnotherDefinition } else if
	 * (protocol.equals("sendDefinitionMessages")) { } else if
	 * (protocol.equals("checkForNewVersions")) { } else if
	 * (protocol.equals("getRecentQueries")) { } else if
	 * (protocol.equals("getRecentBookmarks")) { } else if
	 * (protocol.equals("startRemoteBuild")) { } else if
	 * (protocol.equals("get_sub_presence")) { } else if
	 * (protocol.equals("getMemberPresence")) { } else if
	 * (protocol.equals("addEventToMessage")) { } else if
	 * (protocol.equals("saveTabOrderToContact")) { } else if
	 * (protocol.equals("saveWindowPositionToContact")) { } else if
	 * (protocol.equals("notifySystemTray")) { } else if
	 * (protocol.equals("sendPopupNotification")) { } else if
	 * (protocol.equals("replaceMember")) { } else if
	 * (protocol.equals("getSphereOrder")) { } else if
	 * (protocol.equals("crossreferenceSpheres")) { } else if
	 * (protocol.equals("checkForExistingContact")) { } else if
	 * (protocol.equals("entitleContactForOneSphere")) { } else if
	 * (protocol.equals("entitleContactForSphere")) { } else if
	 * (protocol.equals("registerSphereWithMembers")) { else if
	 * (protocol.equals("addLocationsToDoc")) { } else if
	 * (protocol.equals("openSphereForMembers")) { } else if
	 * (protocol.equals("getSphereDefinition")) { } else if
	 * (protocol.equals("get_initial_presence")) { } else if
	 * (protocol.equals("registerMember")) { } else if
	 * (protocol.equals("update_document")) { else if
	 * (protocol.equals("saveMarkForSphere")) { else if
	 * (protocol.equals("voteDocument")) { } else if
	 * (protocol.equals("useDocument")) { } else if
	 * (protocol.equals("replaceDoc")) { } else if
	 * (protocol.equals("saveQueryView")) { } else if
	 * (protocol.equals("addQueryToContact")) { } else if
	 * (protocol.equals("addInviteToContact")) { } else if
	 * (protocol.equals("createNewProfile")) { } else if
	 * (protocol.equals("sendByteRouterInit")) { } else if
	 * (protocol.equals("sendSubList")) { } else if
	 * (protocol.equals("getSubList")) { else if
	 * (protocol.equals("getEntireThread")) { else if
	 * (protocol.equals("publish")) { } else if
	 * (protocol.equals("getMachinePass")) { } else if
	 * (protocol.equals("getMachineVerifierForProfile")) { } else if
	 * (protocol.equals("setAsSeenAndIndexJustInCase")) { } else if
	 * (protocol.equals("getAndSendInviteText")) { } else if
	 * (protocol.equals("matchAgainstRecentHistory")) { } else if
	 * (protocol.equals("matchAgainstOtherHistory")) { } else if
	 * (protocol.equals("matchAgainstHistory")) { } else if
	 * (protocol.equals("searchForSpecificInIndex")) { } else if
	 * (protocol.equals("sendEmailFromServer")) { } else if
	 * (protocol.equals("recallMessage")) { } else if
	 * (protocol.equals("getMyContact")) { } else if
	 * (protocol.equals("getStatsForSphere")) { } else if
	 * (protocol.equals("getPersonalContactsForSphere")) { } else if
	 * (protocol.equals("getPersonalContactsForEmail")) { } else if
	 * (protocol.equals("getAllContacts")) { } else if
	 * (protocol.equals("get_members_for")) { } else if
	 * (protocol.equals("reboot")) { } else if (protocol.equals("getEmailInfo")) { }
	 * else if (protocol.equals("remove_handler")) { } else if
	 * (protocol.equals("keepAlivePing"))
	 */

}
