/**
 * 
 */
package ss.server.debug.commands;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.Document;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.StringUtils;
import ss.common.debug.DebugUtils;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.SphereItem.SphereType;
import ss.domainmodel.configuration.DomainProvider;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 * 
 */
public class ChangeDefaultAliasesForGroupSpheres implements IRemoteCommand {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangeDefaultAliasesForGroupSpheres.class);

	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command ChangeDefaultAliasesForGroupSpheres started");
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
			writer.println("ChangeDefaultAliasesForGroupSpheres started");
			if (logger.isDebugEnabled()) {
				logger.debug("ChangeDefaultAliasesForGroupSpheres started");
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
			for (SphereEmail sphereEmail : spheresemails) {
				try {
					String system_name = sphereEmail.getSphereId();
					SphereItem sphere = tempMember.getSphereBySystemName(system_name);
					if (sphere.getSphereType() != SphereType.GROUP){
						writer.println("Sphere " + system_name + " is not group sphere, skipping");
						continue;
					}
					if (sphere.getDisplayName().contains("Email Box")){
						writer.println("Sphere " + system_name + " is not group sphere, skipping");
						continue;
					}
					if (logger.isDebugEnabled()) {
						logger
								.debug("for sphere: "
										+ system_name);
					}
					writer.println("For sphere: " + system_name);
					perOne(sphereEmail, writer, newSupra, defaultDomain, sphere);
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
					.println("ChangeDefaultAliasesForGroupSpheres finished successfully");
			writer.println();
		} catch (Throwable ex) {
			logger.error("ChangeDefaultAliasesForGroupSpheres failed", ex);
			writer.println("ChangeDefaultAliasesForGroupSpheres failed : "
					+ DebugUtils.getExceptionInfo(ex));
		}
	}

	/**
	 * @param sphereEmail
	 * @param writer
	 * @param xmldb
	 * @param newSupra
	 */
	private void perOne(final SphereEmail sphereEmail, final PrintWriter writer, final SupraSphereStatement newSupra, final String defaultDomain, final SphereItem sphere) {
		final SpherePossibleEmailsSet names = sphereEmail.getEmailNames();
		writer.println("Emails before: " + names.getSingleStringEmails());
		String newSphereName = sphere.getDisplayName();
		String domain = null;
		final List<String> parsedEmailAddresses = names.getParsedEmailAddresses();
		if ((parsedEmailAddresses != null)&&(!parsedEmailAddresses.isEmpty())){
			domain = SpherePossibleEmailsSet.getDomainFromSingleAddress(parsedEmailAddresses.get(0));
		}
		if (StringUtils.isBlank(domain)){
			domain = defaultDomain;
		}
		final String descriptionInAlias = SpherePossibleEmailsSet.convertToNamingConventionDescription(newSphereName);
		final String aliasWithDysplayName = SpherePossibleEmailsSet.createAddressString(descriptionInAlias, 
				SpherePossibleEmailsSet.convertToNamingConvention(newSphereName), domain);
		names.addAsPrimaryAndOverwriteAddresses(aliasWithDysplayName);
		sphereEmail.setEmailNames(names);
		final String emailsLine = names.getSingleStringEmails();
		sphereEmail.setEmailNames(names);
		if (logger.isDebugEnabled()) {
			logger.debug("Next emails: " + emailsLine);
		}
		writer.println("Emails after: " + emailsLine);
		writer.println();
		newSupra.getSpheresEmails().put(sphereEmail);
	}

	private void sendAuthToAll(Document returndoc) {
		DialogsMainPeer.updateVerifyAuthForAll(returndoc);
	}
}
