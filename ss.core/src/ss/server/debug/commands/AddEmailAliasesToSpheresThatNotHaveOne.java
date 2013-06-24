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
import ss.common.debug.DebugUtils;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereItemCollection;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.SphereItem.SphereType;
import ss.domainmodel.configuration.DomainProvider;
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
public class AddEmailAliasesToSpheresThatNotHaveOne implements IRemoteCommand {
	
	// NOT FINISHED
	
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangeDefaultAliasesForGroupSpheres.class);

	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command AddEmailAliasesToSpheresThatNotHaveOne started");
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

	private void perform(PrintWriter writer) {
		try {
			writer.println();
			writer.println("AddEmailAliasesToSpheresThatNotHaveOne started");
			if (logger.isDebugEnabled()) {
				logger.debug("AddEmailAliasesToSpheresThatNotHaveOne started");
			}
			writer.println();
			writer.println("Emails after operation:");
			final XMLDB xmldb = new XMLDB();
			final Document supraSphereDocument = Utils.getUtils(xmldb).getSupraSphereDocument();
			final SupraSphereStatement supra = SupraSphereStatement.wrap(supraSphereDocument);
			final SupraSphereStatement newSupra = SupraSphereStatement
					.wrap((Document) supra.getBindedDocument().clone());
			final SphereEmailCollection spheresemails = supra
					.getSpheresEmails();
			if (logger.isDebugEnabled()) {
				logger.debug("Count of sphereEmails objects: "
						+ spheresemails.getCount());
			}
			final SupraSphereMember tempMember = supra.getSupraMembers().get(0);
			final String defaultDomain = DomainProvider.getDefaultDomain();
			SphereItemCollection allSpheres = tempMember.getSpheres();
			for (SphereItem sphere : allSpheres){
				try {
					String system_name = sphere.getSystemName();
					SphereEmail sphereEmail = spheresemails.getSphereEmailBySphereId(system_name);
					if (sphereEmail == null){
						perOne(xmldb, writer, newSupra, defaultDomain, sphere);
					}
				} catch (Throwable ex) {
					writer.println("Error with this sphere");
					writer.println();
					logger.error("", ex);
				}
			}
			
			final Document supraSphereResultDocument = newSupra.getBindedDocument();
			xmldb.replaceDoc(supraSphereResultDocument, supra.getName());
			sendAuthToAll(supraSphereResultDocument);
			writer.println();
			writer
					.println("AddEmailAliasesToSpheresThatNotHaveOne finished successfully");
			writer.println();
		} catch (Throwable ex) {
			logger.error("AddEmailAliasesToSpheresThatNotHaveOne failed", ex);
			writer.println("AddEmailAliasesToSpheresThatNotHaveOne failed : "
					+ DebugUtils.getExceptionInfo(ex));
		}
	}

	/**
	 * @param sphereEmail
	 * @param writer
	 * @param xmldb
	 * @param newSupra
	 * @throws DocumentException 
	 */
	private void perOne(final XMLDB xmldb, final PrintWriter writer, final SupraSphereStatement newSupra, final String defaultDomain, final SphereItem sphere) throws DocumentException {
		final SphereEmail sphereEmail = new SphereEmail();
		if (sphere.getSphereType() == SphereType.GROUP){
			String newSphereName = sphere.getDisplayName();
			String newSphereId = sphere.getSystemName();
			sphereEmail.setSphereId(newSphereId);
			final String descriptionInAlias = SpherePossibleEmailsSet.convertToNamingConventionDescription(newSphereName);
			final String aliasWithSystemName = SpherePossibleEmailsSet.createAddressString(descriptionInAlias, newSphereId, defaultDomain);
			final String aliasWithDysplayName = SpherePossibleEmailsSet.createAddressString(descriptionInAlias, 
					SpherePossibleEmailsSet.convertToNamingConvention(newSphereName), defaultDomain);

			SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(aliasWithDysplayName);
			set.addAddresses(aliasWithSystemName);
			sphereEmail.setEmailNames(set);
			sphereEmail.setEnabled(true);
			sphereEmail.setIsMessageIdAdd(true);
			xmldb.getUtils().addEmailSphereNode(sphereEmail);
			if (logger.isDebugEnabled()) {
				logger.debug("Email aliases for sphere " + newSphereName + " is: " + set.getSingleStringEmails());
			}
		}
		if (sphere.getSphereType() == SphereType.MEMBER){
			
		}
	}

	private void sendAuthToAll(Document returndoc) {
		DialogsMainPeer.updateVerifyAuthForAll(returndoc);
	}
}
