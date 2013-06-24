/**
 * 
 */
package ss.server.debug.commands;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.EmailDomain;
import ss.domainmodel.configuration.EmailDomainsList;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

/**
 * @author zobo
 * 
 */
public class ProvideAllEmailAliasesWithDomains implements IRemoteCommand {

	private static final String[] DOMAINS = { "suprasecure.com", "atabike.com" };

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ProvideAllEmailAliasesWithDomains.class);

	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command ProvideAllEmailAliasesWithDomains started");
		}
		final StringWriter responce = new StringWriter();
		final PrintWriter writer = new PrintWriter(responce);
		perform(writer);
		writer.flush();
		final String toReturn = responce.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Command finished, responce: " + toReturn);
		}
		return toReturn;
	}

	private void perform(final PrintWriter writer) {
		try {
			writer.println();
			writer.println("ProvideAllEmailAliasesWithDomains started");
			if (logger.isDebugEnabled()) {
				logger.debug("ProvideAllEmailAliasesWithDomains started");
			}
			writer.println();
			setDomains(writer);
			writer.println();
			writer.println("Emails after operation:");
			DialogsMainPeer peer = null;
			for (DialogsMainPeer p : DialogsMainPeerManager.INSTANCE
					.getHandlers()) {
				peer = p;
				break;
			}
			if (peer == null) {
				logger.error("No Dialog Main Peers on server!");
				writer.println("No Dialog Main Peers on server!");
				return;
			}
			final XMLDB xmldb = peer.getXmldb();
			final VerifyAuth auth = xmldb.getVerifyAuth();
			final ISupraSphereFacade supra = auth.getSupraSphere();
			final ISupraSphereFacade newSupra = supra.duplicate();
			final SphereEmailCollection spheresemails = supra
					.getSpheresEmails();
			if (logger.isDebugEnabled()) {
				logger.debug("Count of sphereEmails objects: " + spheresemails.getCount());
			}
			for (SphereEmail sphereEmail : spheresemails) {
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("for sphere: " + sphereEmail.getSphereId());
					}
					perOne(sphereEmail, DOMAINS[0], writer, xmldb, newSupra);
				} catch (Throwable ex) {
					logger.error("", ex);
				}
			}
			peer.getXmldb().replaceDoc(newSupra.getBindedDocumentForSaveToDb(), supra.getName());
			sendAuthToAll(Utils.getUtils(peer).getSupraSphereDocument());
			writer.println();
			writer
					.println("ProvideAllEmailAliasesWithDomains finished successfully");
			writer.println();
		} catch (Throwable ex) {
			logger.error("ProvideAllEmailAliasesWithDomains failed", ex);
			writer.println("ProvideAllEmailAliasesWithDomains failed : "
					+ ex.toString());
		}
	}

	/**
	 * 
	 */
	private void setDomains(final PrintWriter writer) {
		ConfigurationValue configuration = SsDomain.CONFIGURATION
				.getMainConfigurationValue();
		EmailDomainsList domains = configuration.getDomains();
		EmailDomain domain;
		for (String s : DOMAINS) {
			domain = new EmailDomain();
			domain.setDomain(s);
			domains.put(domain);
		}
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
		String domainsString = "";
		for (String domainName : DOMAINS) {
			domainsString += "  " + domainName;
		}
		writer.println("Email domains setted: " + domainsString);
		if (logger.isDebugEnabled()) {
			logger.debug("Email domains setted: " + domainsString);
		}
	}

	private void perOne(final SphereEmail sphereEmail, final String domain,
			final PrintWriter writer, final XMLDB xmldb, final ISupraSphereFacade newSupra)
			throws DocumentException {
		final SpherePossibleEmailsSet names = sphereEmail.getEmailNames();
		names.supplyAllCurrentAddressWithDomain(domain);
		final String emailsLine = names.getSingleStringEmails();
		sphereEmail.setEmailNames(names);
		if (logger.isDebugEnabled()) {
			logger.debug("Next emails: " + emailsLine);
		}
		writer.println("Emails: " + emailsLine);
		newSupra.getSpheresEmails().put(sphereEmail);
	}

	private void sendAuthToAll(Document returndoc) {
		DialogsMainPeer.updateVerifyAuthForAll(returndoc);
	}
}
