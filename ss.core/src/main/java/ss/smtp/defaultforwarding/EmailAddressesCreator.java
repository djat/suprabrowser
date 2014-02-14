/**
 * 
 */
package ss.smtp.defaultforwarding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.Statement;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesUser;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesUserSphere;
import ss.domainmodel.preferences.emailforwarding.CommonEmailForwardingPreferences.ForwardingModes;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesSphere.SphereForwardingModes;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.SC;
import ss.server.networking.protocol.getters.GetUserPersonalEmailAddressHandler;

/**
 * @author zobo
 * 
 */
public class EmailAddressesCreator {

	private static final String DO_NOT_REPLY = "DO_NOT_REPLY@";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailAddressesCreator.class);

	public static EmailAddressesContainer create(final String sphereId,
			final String messageId, final String giver,
			final DialogsMainPeer peer,
			final ForsedForwardingData forsedForwardingData, final Statement statement) {

		if (logger.isDebugEnabled()) {
			logger.debug("Create Email Adresses for forwarding performed");
			logger.debug("SphereId: " + sphereId + ", messageId: " + messageId
					+ ", Giver: " + giver);
		}

		
		SpherePossibleEmailsSet set = new SpherePossibleEmailsSet();

		final VerifyAuth auth = peer.getVerifyAuth();

		if (forsedForwardingData == null) {
			if ( !ThreadListConversationResolver.addSpecificThreadConversation( sphereId, set, statement, peer ) ) {
				try {
					addSphereEmails(sphereId, set, peer.getXmldb());
				} catch (Exception ex) {
					logger.error("Error in adding sphere emails",ex);
				}

				try {
					addUsersEmails(sphereId, set, auth);
				} catch (Exception ex) {
					logger.error("Error in adding user emails",ex);
				}
			}

			set.cleanUpAddresses();

			if ( set.isEmpty() ) {
				if (logger.isDebugEnabled()) {
					logger.debug("No forwarding needed");
				}
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug(set.getSingleStringEmails());
			}

			String from = getFromPerUser(giver, auth, peer.getSession());
			String fromForSphere = getFrom(sphereId, auth, peer.getSession());
			if (from == null) {
				if (logger.isDebugEnabled()) {
					logger
							.debug("From for giver is null, setting from from current sphere email aliases");
				}
				from = fromForSphere;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("From is: " + from);
			}
			String replyTo = getReplyTo(fromForSphere, messageId, from);
			if (logger.isDebugEnabled()) {
				logger.debug("ReplyTo is: " + replyTo);
			}

			String sendTo = set.getAndDetach(0);
			if (logger.isDebugEnabled()) {
				logger.debug("SendTo is: " + sendTo);
			}

			if ((sendTo != null) && (from != null) && (replyTo != null)
					&& (set != null)) {
				EmailAddressesContainer container = new EmailAddressesContainer(
						sendTo, from, replyTo, set.getSingleStringEmails(), "");
				if (logger.isDebugEnabled()) {
					logger.debug("Emails addresses successfully created");
				}
				return container;
			} else {
				logger.error("One of required parameters for email is null");
				return null;
			}
		} else {
			if (!forsedForwardingData.getInfo().isAddressListEmpty()) {
				set = new SpherePossibleEmailsSet( forsedForwardingData.getInfo().getReciepientAddressesList() );
			} else {
				try {
					if ( forsedForwardingData.isAddMembers() ) {
						set.addAddresses(getMembersEmails(sphereId, peer.getXmldb()));
					}
				} catch (Exception ex) {
					logger.error("Error in add members",ex);
				}
				try {
					if ( forsedForwardingData.isAddContacts() ) {
						set.addAddresses(getContactEmails(sphereId, peer.getXmldb()));
					}
				} catch (Exception ex) {
					logger.error("Error in add contacts",ex);
				}
			}

			String userPersonalEmail = GetUserPersonalEmailAddressHandler.evaluateImpl(peer.getUserLogin(), sphereId, peer);
			if ( StringUtils.isNotBlank(userPersonalEmail) ) {
				userPersonalEmail = SpherePossibleEmailsSet.provideWithDescriptionIfNeeded(userPersonalEmail, peer.getUserContactName());
				set.addAddresses( userPersonalEmail );
			}
			set.cleanUpAddresses();

			if ( set.isEmpty() ) {
				if (logger.isDebugEnabled()) {
					logger.debug("No forwarding needed");
				}
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug(set.getSingleStringEmails());
			}

			String from = getFromPerUser(giver, auth, peer.getSession());
			String fromForSphere = getFrom(sphereId, auth, peer.getSession());
			if (from == null) {
				if (logger.isDebugEnabled()) {
					logger
							.debug("From for giver is null, setting from from current sphere email aliases");
				}
				from = fromForSphere;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("From is: " + from);
			}
			String replyTo = getReplyTo(fromForSphere, messageId, from);
			if (logger.isDebugEnabled()) {
				logger.debug("ReplyTo is: " + replyTo);
			}

			String sendTo = set.getAndDetach(0);
			if (logger.isDebugEnabled()) {
				logger.debug("SendTo is: " + sendTo);
			}

			if ((sendTo != null) && ((from != null)||StringUtils.isNotBlank(userPersonalEmail)) && (replyTo != null)
					&& (set != null)) {
				String cc = "";
				String bcc = "";
				if (forsedForwardingData.isAllAddressesAs_BCC_insteadOf_CC()) {
					bcc = set.getSingleStringEmails();
				} else {
					cc = set.getSingleStringEmails();
				}
				EmailAddressesContainer container = new EmailAddressesContainer(
						sendTo, StringUtils.isNotBlank(userPersonalEmail) ? userPersonalEmail : from, 
						replyTo, cc, bcc);
				if (logger.isDebugEnabled()) {
					logger.debug("Emails addresses successfully created");
				}
				return container;
			} else {
				logger.error("One of required parameters for email is null");
				return null;
			}
		}
	}

	private static void addUsersEmails(String sphereId,
			SpherePossibleEmailsSet set, VerifyAuth auth) {
		List<MemberReference> members = auth.getMembersForSphere(sphereId);

		for (MemberReference member : members) {
			try {
				String login = member.getLoginName();
				if (logger.isDebugEnabled()) {
					logger.debug("Delivering for user: " + login
							+ ", and sphere: " + sphereId);
				}
				EmailForwardingPreferencesUserSphere sphereUserPref = SsDomain.INVITED_MEMBER_HELPER
						.getInvitedMemberPreferences(sphereId, login)
						.getEmailForwardingPreferences();
				if (sphereUserPref.isSetted()) {
					if (logger.isDebugEnabled()) {
						logger.debug("for sphere is setted");
					}
					if (isForwarding(login, sphereUserPref.getMode())) {
						set.addAddresses(sphereUserPref.getEmails());
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("for sphere is not setted");
					}
					EmailForwardingPreferencesUser emailPref = SsDomain.MEMBER_HELPER
							.getMemberPreferences(login)
							.getEmailForwardingPreferences();
					if (isForwarding(login, emailPref.getMode())) {
						set.addAddresses(emailPref.getEmails());
					}
				}
			} catch (Exception ex) {
				logger.error("addUsersEmails failed", ex);
			}
		}
	}

	private static boolean isForwarding(String login, ForwardingModes mode) {
		if (logger.isDebugEnabled()) {
			logger.debug("sending mode for " + login + ": " + mode);
		}
		if (mode == ForwardingModes.OFF) {
			return false;
		}
		if (mode == ForwardingModes.FORCED) {
			return true;
		}
		if (mode == ForwardingModes.AUTOMATIC) {
			boolean online = DialogsMainPeerManager.INSTANCE
					.isUserOnline(login);
			if (logger.isDebugEnabled()) {
				logger.debug("Checking is user: " + login + " online: "
						+ online);
			}
			return !online;
		}
		return false;
	}

	private static void addSphereEmails(String sphereId,
			SpherePossibleEmailsSet set, XMLDB xmldb) {
		SphereOwnPreferences pref = SsDomain.SPHERE_HELPER
				.getSpherePreferences(sphereId);
		SphereForwardingModes mode = pref.getEmailForwardingPreferences()
				.getMode();
		boolean addAdditionalEmails = pref.getEmailForwardingPreferences()
				.getAdditional();
		if (mode == SphereForwardingModes.OFF) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("SphereForwardingMode for sphere is OFF, returning");
			}
			return;
		}
		if (mode == SphereForwardingModes.CONTACTS) {
			set.addAddresses(getContactEmails(sphereId, xmldb));
			if (addAdditionalEmails) {
				set.addAddresses(pref.getEmailForwardingPreferences()
						.getEmails());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("SphereForwardingMode for sphere is CONTACTS");
			}
		}
		if (mode == SphereForwardingModes.MEMBERS) {
			set.addAddresses(getMembersEmails(sphereId, xmldb));
			if (addAdditionalEmails) {
				set.addAddresses(pref.getEmailForwardingPreferences()
						.getEmails());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("SphereForwardingMode for sphere is MEMBERS");
			}
		}
		if (mode == SphereForwardingModes.ADDITIONAL) {
			set.addAddresses(pref.getEmailForwardingPreferences().getEmails());
			if (logger.isDebugEnabled()) {
				logger.debug("SphereForwardingMode for sphere is ADDITIONAL");
			}
		}
	}

	/**
	 * @param from
	 * @return
	 */
	public static String getReplyTo(String from, String messageId, String fromForUser) {
		if ( from == null ) {
			return fromForUser;
		}
		if (from.startsWith(DO_NOT_REPLY)) {
			return "";
		}
		return SpherePossibleEmailsSet.supplySingleAddressWithRoutingNumber(
				from, messageId, true);
	}

	/**
	 * @param session
	 * @return
	 */
	public static String getFrom(String sphereId, VerifyAuth auth,
			Hashtable session) {
		SphereEmail currentSphereEmail = auth.getSpheresEmails()
				.getSphereEmailBySphereId(sphereId);
		if (currentSphereEmail == null) {
			logger.error("Current Sphere Email is null");
			return null;
		}
		String domain = auth.getDomain();
		if ((domain == null) || (domain.equals("$loginAddress")))
			domain = (String) session.get(SC.ADDRESS);

		List<String> emails = currentSphereEmail.getEmailNames()
				.getParsedEmailAddresses();
		if ((emails == null) || (emails.isEmpty())) {
			return DO_NOT_REPLY + domain;
		}
		String from = emails.get(0);
		if (logger.isDebugEnabled()) {
			logger.debug("From: " + from);
		}
		return from;
	}

	public static String getFromPerUser(String contactName, VerifyAuth auth,
			Hashtable session) {
		String from = null;
		try {
			final String login = auth.getLoginForContact(contactName);
			if (StringUtils.isBlank(login)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Returning from as : " + contactName
							+ ", assuming this external email forwarding");
				}
				return contactName;
			}
			String userEmailBoxId = auth.getEmailSphere(login, contactName);
			SphereEmail currentSphereEmail = auth.getSpheresEmails()
					.getSphereEmailBySphereId(userEmailBoxId);
			if (currentSphereEmail == null) {
				logger.error("Current Sphere Email is null");
				return null;
			}
			String domain = auth.getDomain();
			if ((domain == null) || (domain.equals("$loginAddress")))
				domain = (String) session.get(SC.ADDRESS);

			List<String> emails = currentSphereEmail.getEmailNames()
					.getParsedEmailAddresses();
			if ((emails == null) || (emails.isEmpty())) {
				return DO_NOT_REPLY + domain;
			}
			from = emails.get(0);
			if (logger.isDebugEnabled()) {
				logger.debug("From: " + from);
			}
		} catch (Throwable e) {
			logger.error("Error taking from per user", e);
		}
		return from;
	}

	public static String getMembersEmails(String sphereId, XMLDB xmldb) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting MembersEmails");
		}
		Collection<String> emails = xmldb.getEmailsForMembersInSphere(sphereId);
		if ((emails == null) || (emails.isEmpty())) {
			return null;
		}
		SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
				new ArrayList<String>(emails));
		return set.getSingleStringEmails();
	}

	public static String getContactEmails(String sphereId, XMLDB xmldb) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting ContactEmails");
		}
		Collection<String> emails = xmldb
				.getEmailsForContactsInSphere(sphereId);
		if ((emails == null) || (emails.isEmpty())) {
			return null;
		}
		SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
				new ArrayList<String>(emails));
		return set.getSingleStringEmails();
	}

	// private static String getAllContactsEmails(String sphereId, XMLDB xmldb)
	// {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Getting All Contacts Emails on server");
	// }
	// Collection<String> emails = xmldb.getEmailsForAllContacts();
	// SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
	// new ArrayList<String>(emails));
	// return set.getSingleStringEmails();
	// }
}
