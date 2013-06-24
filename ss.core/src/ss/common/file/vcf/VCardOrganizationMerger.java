/**
 * 
 */
package ss.common.file.vcf;

import net.wimpi.pim.contact.basicimpl.OrganizationImpl;
import net.wimpi.pim.contact.basicimpl.OrganizationalIdentityImpl;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.Organization;
import net.wimpi.pim.contact.model.OrganizationalIdentity;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.clubdeals.ClubDealUtils;
import ss.domainmodel.configuration.ClubdealContactType;

/**
 * @author zobo
 * 
 */
public class VCardOrganizationMerger {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardOrganizationMerger.class);

	public static final VCardOrganizationMerger INSTANCE = new VCardOrganizationMerger();

	private VCardOrganizationMerger() {

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * @param st
	 * @param organizationalIdentity
	 */
	public boolean addOrganizationToContactStatement(final ContactStatement st,
			final OrganizationalIdentity organizationalIdentity) {
		if (organizationalIdentity == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("organizationalIdentity is null");
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding organization info to contact statement");
		}

		try {
			String role = organizationalIdentity.getRole();
			if ( StringUtils.isNotBlank( role ) && 
					(!ClubDealUtils.getAllContactTypes().contains( role ))){ 
				logger.error("Type : \"" + role + "\" is not allowed, will be set to: " + ClubdealContactType.getDefaultName());
				role = ClubdealContactType.getDefaultName();
			}
			st.setRole( (role != null) ? role : "" );
			st.setTitle( (organizationalIdentity.getTitle() != null) ? organizationalIdentity.getTitle() : "");
			Organization oprSpecific = organizationalIdentity.getOrganization();
			if (oprSpecific != null) {
				final String organizationName = oprSpecific.getName();
				st.setOrganization(organizationName);
				final String departmentsList = StringUtils
						.asOneString(oprSpecific.listUnits());
				st.setDepartment(departmentsList);
				if (logger.isDebugEnabled()) {
					logger.debug("organizationName: " + organizationName);
					logger.debug("departmentsList: " + departmentsList);
				}
			}
		} catch (Exception ex) {
			logger.error("Error adding Organization info to contact statement",
					ex);
			return false;
		}

		return true;
	}
	
	public boolean collectOrganizationFromContactStatement( final ContactStatement st,
			final Contact contact ){
		final OrganizationalIdentity organizationalIdentity = new OrganizationalIdentityImpl();
		if (logger.isDebugEnabled()) {
			logger.debug("Collecting organization info from contact statement");
		}
		try {
			
			organizationalIdentity.setRole( StringUtils.getNotNullString(st.getRole()) );
			organizationalIdentity.setTitle( StringUtils.getNotNullString(st.getMessageTitle()) );
			
			Organization oprSpecific = new OrganizationImpl();
			oprSpecific.setName( StringUtils.getNotNullString(st.getOrganization()) );
			oprSpecific.addUnit( StringUtils.getNotNullString(st.getDepartment()) );
			organizationalIdentity.setOrganization(oprSpecific);
			
			contact.setOrganizationalIdentity(organizationalIdentity);
		} catch (Exception ex) {
			logger.error("Error adding Organization info to contact statement",
					ex);
			return false;
		}

		return true;
	}

}
