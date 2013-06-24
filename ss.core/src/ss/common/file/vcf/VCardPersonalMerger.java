/**
 * 
 */
package ss.common.file.vcf;

import net.wimpi.pim.contact.basicimpl.PersonalIdentityImpl;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.PersonalIdentity;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;

/**
 * @author zobo
 * 
 */
public class VCardPersonalMerger {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardPersonalMerger.class);

	public static final VCardPersonalMerger INSTANCE = new VCardPersonalMerger();

	private VCardPersonalMerger() {

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * @param st
	 * @param personalIdentity
	 */
	public boolean addPersonalToContactStatement(final ContactStatement st,
			final PersonalIdentity personal) {
		if (personal == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("personalIdentity is null");
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding personalIdentity info to contact statement");
		}
		
		try {

		final String firstname =  personal.getFirstname();
		final String lastname = personal.getLastname();
		String additionalName = null;
		final String suffixes = StringUtils.asOneString(personal.listSuffixes());
		final String preffixes = StringUtils.asOneString(personal.listPrefixes());
		
		st.setFirstName(firstname);
		st.setLastName(lastname);
		st.setSubject(firstname + " " + lastname);
		if (personal.getAdditionalNameCount() > 0) {
			additionalName = personal.getAdditionalName(0);
			st.setMiddleName(additionalName);
		}
		st.setNamePrefix(preffixes);
		st.setNameSuffix(suffixes);
		if (logger.isDebugEnabled()) {
			logger.debug("Presonal Info added:");
			logger.debug("firstname: " + firstname);
			logger.debug("lastname: " + lastname);
			logger.debug("additionalName: " + additionalName);
			logger.debug("suffixes: " + suffixes);
			logger.debug("preffixes: " + preffixes);
		}
		} catch (Exception ex) {
			logger.error("Error adding Personal info to contact statement",
					ex);
		}

		return true;
	}
	
	public boolean collectPersonalFromContactStatement(final ContactStatement st,
			final Contact contact) {
		final PersonalIdentity personal = new PersonalIdentityImpl();
		if (logger.isDebugEnabled()) {
			logger.debug("Collecting personalIdentity from contact statement");
		}
		try {
		personal.setFirstname( StringUtils.getNotNullString(st.getFirstName()) );
		personal.setLastname( StringUtils.getNotNullString(st.getLastName()) );
		personal.addAdditionalName( StringUtils.getNotNullString(st.getMiddleName()) );
		personal.addSuffix( StringUtils.getNotNullString(st.getNameSuffix()));
		personal.addPrefix( StringUtils.getNotNullString(st.getNamePrefix()));
		
		contact.setPersonalIdentity(personal);
		} catch (Exception ex) {
			logger.error("Error adding Personal info to contact statement",
					ex);
		}
		return true;
	}
}
