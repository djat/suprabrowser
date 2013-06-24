/**
 * 
 */
package ss.common.email;

import java.util.ArrayList;
import java.util.List;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.VerifyAuth;
import ss.common.domain.service.ISupraSphereFacade;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.configuration.DomainProvider;

/**
 * @author zobo
 * 
 */
public class EmailAliasesCreator {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailAliasesCreator.class);

	public static List<String> createAddressStringOfPersonToPersonShpere(
			final String firstLogin, final String firstContact,
			final String secondLogin, final String secondContact, final String sphereId, 
			final VerifyAuth auth) {
		final List<String> aliases = new ArrayList<String>();
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("createAddressStringOfPersonToPersonShpere performed, first: " + firstLogin + 
						"," + firstContact + "; second: " + secondLogin + "," + secondContact + "; sphereId: " + sphereId);
			}
			final String firstEmailBoxId = auth.getEmailSphere(firstLogin, firstContact);
			final String secondEmailBoxId = auth.getEmailSphere(secondLogin, secondContact); 
			if (logger.isDebugEnabled()) {
				logger.debug("firstEmailBoxId: " + firstEmailBoxId +", secondEmailBoxId: " + secondEmailBoxId);
			}
			final ISupraSphereFacade supraStatement = auth.getSupraSphere();
			
			final SphereEmail firstSphereEmail = supraStatement
					.getSpheresEmails().getSphereEmailBySphereId(firstEmailBoxId);
			final SphereEmail secondSphereEmail = supraStatement
				.getSpheresEmails().getSphereEmailBySphereId(secondEmailBoxId);

			final List<String> firstDomains = new ArrayList<String>();
			final List<String> secondDomains = new ArrayList<String>();
			final List<String> domains = new ArrayList<String>();
			
			for (String emailAddresses : firstSphereEmail.getEmailNames().getParsedEmailAddresses()){
				if (logger.isDebugEnabled()) {
					logger.debug("Next in firstSphereEmail: " + emailAddresses);
				}
				firstDomains.add(SpherePossibleEmailsSet.getDomainFromSingleAddress(emailAddresses));
			}
			for (String emailAddresses : secondSphereEmail.getEmailNames().getParsedEmailAddresses()){
				if (logger.isDebugEnabled()) {
					logger.debug("Next in secondSphereEmail: " + emailAddresses);
				}
				String domain = SpherePossibleEmailsSet.getDomainFromSingleAddress(emailAddresses);
				secondDomains.add(domain);
				if (firstDomains.contains(domain)){
					domains.add(domain);
				}
			}
			if (domains.isEmpty()){
				if (!firstDomains.isEmpty()){
					domains.add(firstDomains.get(0));
				}
				if (!secondDomains.isEmpty()) {
					domains.add(secondDomains.get(0));
				} 
				if (domains.isEmpty()) {
					domains.add(DomainProvider.getDefaultDomain());
				}
			}
			for (String allowedDomain : domains){
				if (logger.isDebugEnabled()) {
					logger.debug("Next allowedDomain: " + allowedDomain);
				}
				aliases.add(SpherePossibleEmailsSet.createAddressString(
						null, sphereId, allowedDomain));
				aliases.add(SpherePossibleEmailsSet
						.createAddressStringOfPersonToPersonShpere(firstLogin,
								firstContact, secondLogin, secondContact, allowedDomain));
				aliases.add(SpherePossibleEmailsSet
						.createAddressStringOfPersonToPersonShpere(secondLogin,
								secondContact, firstLogin, firstContact, allowedDomain));
			}
			if (logger.isDebugEnabled()) {
				logger.debug("createAddressStringOfPersonToPersonShpere finished successfully");
			}
		} catch (Throwable ex1) {
			logger.error("Error getting sphere owner email alias main domain",
					ex1);
		}
		return aliases;
	}
	
	public static String getDefaultParentDomainName( final String parentSphereId, final ISupraSphereFacade supraStatement ){
		String domain = null;
		try {
			final SphereEmail sphereOwnerEmail = supraStatement.getSpheresEmails().getSphereEmailBySphereId(parentSphereId);
			if (sphereOwnerEmail != null) {
				final List<String> ownerEmails = sphereOwnerEmail.getEmailNames().getParsedEmailAddresses();
				if ((ownerEmails != null)&&(!ownerEmails.isEmpty())){
					domain = SpherePossibleEmailsSet.getDomainFromSingleAddress(ownerEmails.get(0));
				}
			}
		} catch (Throwable ex1){
			logger.error("Error getting sphere owner email alias main domain", ex1);
		}
		if (domain == null){
			domain = DomainProvider.getDefaultDomain();
		}
		return domain;
	}
}
