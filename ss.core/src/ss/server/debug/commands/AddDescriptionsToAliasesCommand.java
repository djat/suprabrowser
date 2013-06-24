/**
 * 
 */
package ss.server.debug.commands;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.SSProtocolConstants;
import ss.common.debug.DebugUtils;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereItem;
import ss.domainmodel.configuration.DomainProvider;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.server.debug.ssrepair.Context;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

/**
 * @author zobo
 * 
 */
public class AddDescriptionsToAliasesCommand implements IRemoteCommand {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AddDescriptionsToAliasesCommand.class);

	Context repairerContext;

	public String evaluate(RemoteCommandContext context) throws Exception {
		try {
			return performOperation();
		} catch (Throwable ex) {
			logger
					.error(
							"Exception while adding descriptions to sphers email aliases",
							ex);
			return "Exception while adding descriptions to sphers email aliases: "
					+ DebugUtils.getExceptionInfo(ex);
		}
	}

	private String performOperation() throws DocumentException {
		DialogsMainPeer peer = null;
		for (DialogsMainPeer p : DialogsMainPeerManager.INSTANCE.getHandlers()) {
			peer = p;
			break;
		}
		if (peer == null) {
			logger.error("No Dialog Main Peers on server!");
			return "No Dialog Main Peers on server!";
		}

		List<SphereItem> spheres = peer.getXmldb().getVerifyAuth()
				.getAllGroupSpheres();
		if ((spheres == null) || (spheres.isEmpty())) {
			logger
					.warn("No group spheres to perform adding descriptions to email aliases");
			return "No group spheres to perform adding descriptions to email aliases";
		}

		SphereEmailCollection sphereEmails = peer.getXmldb().getVerifyAuth()
				.getSpheresEmails();

		StringWriter responce = new StringWriter();
		PrintWriter writer = new PrintWriter(responce);
		writer.println("Next aliases now fixed:");
		for (SphereItem sphere : spheres) {
			performPerSphere(sphere, peer.getXmldb(), sphereEmails, writer);
		}
		writer.flush();
		String toReturn = responce.toString();
		sendAuthToAll(Utils.getUtils(peer).getSupraSphereDocument());
		if (logger.isDebugEnabled()) {
			logger
					.debug("Alias fixing finished, returning string: "
							+ toReturn);
		}
		return toReturn;
	}

	private void performPerSphere(final SphereItem sphere, final XMLDB xmldb,
			final SphereEmailCollection sphereEmails, final PrintWriter writer)
			throws DocumentException {
		SphereEmail sphereEmail = sphereEmails
				.getSphereEmailBySphereId(sphere.getSystemName());
		if (sphereEmail == null) {
			writer.println("No aliases for sphere: " + sphere.getDisplayName()
					+ ", alias will be created");
			try {
				sphereEmail = new SphereEmail();
				sphereEmail.setSphereId(sphere.getSystemName());
				String description = SpherePossibleEmailsSet.convertToNamingConventionDescription(sphere.getDisplayName());
				String aliasWithSystemName = SpherePossibleEmailsSet.createAddressString(description, 
						sphere.getSystemName(), DomainProvider.getDefaultDomain());
				String aliasWithDysplayName = SpherePossibleEmailsSet.createAddressString(description, 
						SpherePossibleEmailsSet.convertToNamingConvention(sphere.getDisplayName()), DomainProvider.getDefaultDomain());
				SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(aliasWithSystemName);
				set.addAddresses(aliasWithDysplayName);
				sphereEmail.setEmailNames(set);
				sphereEmail.setEnabled(true);
				sphereEmail.setIsMessageIdAdd(true);
				xmldb.getUtils().addEmailSphereNode(sphereEmail);
				if (logger.isDebugEnabled()) {
					logger.debug("Email aliases for sphere " + sphere.getDisplayName() + " is: " + set.getSingleStringEmails());
				}
				writer.println("Aliases created: " + aliasWithSystemName + ", " + aliasWithDysplayName);
			} catch (Exception ex) {
				logger.error("Cannot create email aliases for sphere: " + sphere.getDisplayName(), ex);
			}
		} else {
			final SpherePossibleEmailsSet names = sphereEmail.getEmailNames();
			names.deleteAddresses(sphere.getSystemName());
			String aliasWithDysplayName = SpherePossibleEmailsSet
					.createAddressString(SpherePossibleEmailsSet
							.convertToNamingConventionDescription(sphere
									.getDisplayName()), sphere.getSystemName(), DomainProvider.getDefaultDomain());
			names.addAddresses(aliasWithDysplayName);
			names.setPrimaryAlias(aliasWithDysplayName);
			sphereEmail.setEmailNames(names);
			writer.println("Alias fixed: " + aliasWithDysplayName);
			xmldb.getUtils().addEmailSphereNode(sphereEmail);
			if (logger.isDebugEnabled()) {
				logger.debug("Alias fixed: " + aliasWithDysplayName
						+ " for sphere: " + sphere.getDisplayName());
			}
		}
	}

	private void sendAuthToAll(Document returndoc) {
		DialogsMainPeer.updateVerifyAuthForAll(returndoc);
	}
}
