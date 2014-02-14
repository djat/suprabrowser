package ss.domainmodel.configuration;

import ss.framework.entities.xmlentities.XmlEntityObject;

public class ConfigurationValue extends XmlEntityObject {

	private final EmailDomainsList domains = super.bindListProperty(
			new EmailDomainsList(), "email-domains");

	private final ModerationAccessModelList clubdealModerateAccesses = super
			.bindListProperty(new ModerationAccessModelList(),
					"clubdeal-accesses");

	private final ClubdealContactTypeCollection clubdealContactTypes = super
			.bindListProperty(new ClubdealContactTypeCollection(),
					"clubdeal-contact-types");
	
	private final SphereRoleList sphereRoles = super
			.bindListProperty(new SphereRoleList(),
					"sphere-roles");

	/**
	 * Create GlobalSettingsEntity object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ConfigurationValue wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ConfigurationValue.class);
	}

	/**
	 * Create GlobalSettingsEntity object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ConfigurationValue wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ConfigurationValue.class);
	}

	/**
	 * Construct GlobalSettingsEntity
	 */
	public ConfigurationValue() {
		super("GlobalSettingsEntity");
	}

	public EmailDomainsList getDomains() {
		return this.domains;
	}

	public ModerationAccessModelList getClubdealModerateAccesses() {
		return this.clubdealModerateAccesses;
	}

	public ClubdealContactTypeCollection getClubdealContactTypes() {
		return this.clubdealContactTypes;
	}
	
	public SphereRoleList getSphereRoleList() {
		return this.sphereRoles;
	}
	
	public void setSphereRolesList(final SphereRoleList list) {
		this.sphereRoles.clear();
		for(SphereRoleObject roleObject : list) {
			this.sphereRoles.addRole(roleObject);
		}
	}
}
