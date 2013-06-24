package ss.domainmodel;

import java.util.Comparator;

import ss.common.StringUtils;
import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.domainmodel.preferences.UserSpherePreferencesCollection;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class ContactStatement extends Statement implements Comparable<ContactStatement> {

	public static final String EMPTY_OWNER = "-";
	
	private final ISimpleEntityProperty lastName = super
		.createAttributeProperty("last_name/@value");

	private final ISimpleEntityProperty firstName = super
		.createAttributeProperty("first_name/@value");
	
	private final ISimpleEntityProperty middleName = super
	.createAttributeProperty("middle_name/@value");
	
	private final ISimpleEntityProperty namePrefix = super
	.createAttributeProperty("name_prefix/@value");
	
	private final ISimpleEntityProperty nameSuffix = super
	.createAttributeProperty("name_suffix/@value");

	private final ISimpleEntityProperty login = super
		.createAttributeProperty("login/@value");

	private final ISimpleEntityProperty emailAddress = super
		.createAttributeProperty("email_address/@value");
	
	private final ISimpleEntityProperty secondEmailAddress = super
	.createAttributeProperty("second_email_address/@value");

	private final ISimpleEntityProperty voice1 = super
		.createAttributeProperty("voice1/@value");

	private final ISimpleEntityProperty voice2 = super
		.createAttributeProperty("voice2/@value");

	private final ISimpleEntityProperty mobile = super
		.createAttributeProperty("mobile/@value");

	private final ISimpleEntityProperty fax= super
		.createAttributeProperty("fax/@value");
	
	private final ISimpleEntityProperty faxSecond= super
	.createAttributeProperty("faxsecond/@value");

	private final ISimpleEntityProperty URL = super
		.createAttributeProperty("URL/@value");

	private final ISimpleEntityProperty homeSphere = super
		.createAttributeProperty("home_sphere/@value");

	private final ISimpleEntityProperty street = super
		.createAttributeProperty("street/@value");

	private final ISimpleEntityProperty streetCont = super
		.createAttributeProperty("street_cont/@value");

	private final ISimpleEntityProperty city = super
		.createAttributeProperty("city/@value");

	private final ISimpleEntityProperty state = super
		.createAttributeProperty("state/@value");

	private final ISimpleEntityProperty zipCode = super
		.createAttributeProperty("zip_code/@value");

	private final ISimpleEntityProperty country = super
		.createAttributeProperty("country/@value");
	
	private final ISimpleEntityProperty location = super
	.createAttributeProperty("location/@value");
	
	private final ISimpleEntityProperty timeZone = super
	.createAttributeProperty("timeZone/@value");

	private final ISimpleEntityProperty department = super
		.createAttributeProperty("department/@value");

	private final ISimpleEntityProperty title = super
		.createAttributeProperty("title/@value");
	
	private final ISimpleEntityProperty role = super
	.createAttributeProperty("role/@value");
	
	private final ISimpleEntityProperty account = super
	.createAttributeProperty("account/@value");
	
	private final ISimpleEntityProperty ownerContact = super
	.createAttributeProperty("ownercontact/@value");

	private final ISimpleEntityProperty organization = super
		.createAttributeProperty("organization/@value");

	private final ISimpleEntityProperty origBody = super
		.createAttributeProperty("body/@orig_body");
	
	private final OrderCollection buildOrder = super
		.bindListProperty( new OrderCollection(), "build_order" );
	
	private final FavouritesCollection favourites = super
	.bindListProperty( new FavouritesCollection(), "favourites" );
	
	private final WindowPositionCollection winPosition = super
		.bindListProperty( new WindowPositionCollection(), "window_position" );

	private final ISimpleEntityProperty reciprocalLogin = 
		super.createAttributeProperty("reciprocal_login/@value");
	
	private final UserSpherePreferencesCollection spheresPreferences = super
	.bindListProperty(new UserSpherePreferencesCollection(), "spherePreferences" );
	
	private final IComplexEntityProperty<UserPersonalPreferences> globalPreferences = super.createComplexProperty("globalPreferences", UserPersonalPreferences.class);
	
	public ContactStatement() {
		super("contact");
	}

	/**
	 * Create Contact object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ContactStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ContactStatement.class);
	}

	/**
	 * Create Contact object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ContactStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ContactStatement.class);
	}

	/**
	 * Gets the contact last name
	 */
	public final String getLastName() {
		return this.lastName.getValueOrEmpty();
	}

	/**
	 * Sets the contact last name
	 */
	public final void setLastName(String value) {
		this.lastName.setValue(value);
	}

	/**
	 * Gets the contact first name
	 */
	public final String getFirstName() {
		return this.firstName.getValueOrEmpty();
	}

	/**
	 * Sets the contact first name
	 */
	public final void setFirstName(String value) {
		this.firstName.setValue(value);
	}
	
	/**
	 * Gets the contact first name
	 */
	public final String getMiddleName() {
		return this.middleName.getValueOrEmpty();
	}

	/**
	 * Sets the contact first name
	 */
	public final void setMiddleName(String value) {
		this.middleName.setValue(value);
	}
	
	/**
	 * Gets the contact name prefix
	 */
	public final String getNamePrefix() {
		return this.namePrefix.getValueOrDefault("Mr.");
	}

	/**
	 * Sets the contact name prefix
	 */
	public final void setNamePrefix(String value) {
		this.namePrefix.setValue(value);
	}
	
	/**
	 * Gets the contact name suffix
	 */
	public final String getNameSuffix() {
		return this.nameSuffix.getValueOrEmpty();
	}

	/**
	 * Sets the contact name suffix
	 */
	public final void setNameSuffix(String value) {
		this.nameSuffix.setValue(value);
	}

	/**
	 * Gets the contact login
	 */
	public final String getLogin() {
		return this.login.getValue();
	}

	/**
	 * Sets the contact login
	 */
	public final void setLogin(String value) {
		this.login.setValue(value);
	}

	/**
	 * Gets the contact email address
	 */
	public final String getEmailAddress() {
		return this.emailAddress.getValue();
	}

	/**
	 * Sets the contact email address
	 */
	public final void setEmailAddress(String value) {
		this.emailAddress.setValue(value);
	}
	
	/**
	 * Gets the contact second email address
	 */
	public final String getSecondEmailAddress() {
		return this.secondEmailAddress.getValue();
	}

	/**
	 * Sets the second contact email address
	 */
	public final void setSecondEmailAddress(String value) {
		this.secondEmailAddress.setValue(value);
	}

	/**
	 * Gets the contact voice1
	 */
	public final String getWorkTelephone() {
		return this.voice1.getValue();
	}

	/**
	 * Sets the contact voice1
	 */
	public final void setWorkTelephone(String value) {
		this.voice1.setValue(value);
	}

	/**
	 * Gets the contact voice2
	 */
	public final String getHomeTelephone() {
		return this.voice2.getValue();
	}

	/**
	 * Sets the contact voice2
	 */
	public final void setHomeTelephone(String value) {
		this.voice2.setValue(value);
	}

	/**
	 * Gets the contact mobile
	 */
	public final String getMobile() {
		return this.mobile.getValue();
	}

	/**
	 * Sets the contact mobile
	 */
	public final void setMobile(String value) {
		this.mobile.setValue(value);
	}

	/**
	 * Gets the contact fax
	 */
	public final String getFax() {
		return this.fax.getValue();
	}

	/**
	 * Sets the contact fax
	 */
	public final void setFax(String value) {
		this.fax.setValue(value);
	}
	
	/**
	 * Gets the contact faxSecond
	 */
	public final String getFaxSecond() {
		return this.faxSecond.getValue();
	}

	/**
	 * Sets the contact faxSecond
	 */
	public final void setFaxSecond(String value) {
		this.faxSecond.setValue(value);
	}

	/**
	 * Gets the contact URL
	 */
	public final String getURL() {
		return this.URL.getValue();
	}

	/**
	 * Sets the contact URL
	 */
	public final void setURL(String value) {
		this.URL.setValue(value);
	}

	/**
	 * Gets the contact home sphere
	 */
	public final String getHomeSphere() {
		return this.homeSphere.getValue();
	}

	/**
	 * Sets the contact home sphere
	 */
	public final void setHomeSphere(String value) {
		this.homeSphere.setValue(value);
	}


	/**
	 * Gets the contact street
	 */
	public final String getStreet() {
		return this.street.getValue();
	}

	/**
	 * Sets the contact street
	 */
	public final void setStreet(String value) {
		this.street.setValue(value);
	}

	/**
	 * Gets the contact street_cont
	 */
	public final String getStreetCont() {
		return this.streetCont.getValue();
	}

	/**
	 * Sets the contact street_cont
	 */
	public final void setStreetCont(String value) {
		this.streetCont.setValue(value);
	}

	/**
	 * Gets the contact city
	 */
	public final String getCity() {
		return this.city.getValue();
	}

	/**
	 * Sets the contact city
	 */
	public final void setCity(String value) {
		this.city.setValue(value);
	}

	/**
	 * Gets the contact state
	 */
	public final String getState() {
		return this.state.getValue();
	}

	/**
	 * Sets the contact state
	 */
	public final void setState(String value) {
		this.state.setValue(value);
	}

	/**
	 * Gets the contact country
	 */
	public final String getCountry() {
		return this.country.getValue();
	}

	/**
	 * Sets the contact country
	 */
	public final void setCountry(String value) {
		this.country.setValue(value);
	}
	
	/**
	 * Gets the contact location
	 */
	public final String getLocation() {
		return this.location.getValue();
	}

	/**
	 * Sets the contact location
	 */
	public final void setLocation(String value) {
		this.location.setValue(value);
	}
	
	/**
	 * Gets the contact time zone
	 */
	public final String getTimeZone() {
		return this.timeZone.getValueOrDefault("+0");
	}

	/**
	 * Sets the contact time zone
	 */
	public final void setTimeZone(String value) {
		this.timeZone.setValue(value);
	}

	/**
	 * Gets the contact zip code
	 */
	public final String getZipCode() {
		return this.zipCode.getValue();
	}

	/**
	 * Sets the contact zip code
	 */
	public final void setZipCode(String value) {
		this.zipCode.setValue(value);
	}

	/**
	 * Gets the contact department
	 */
	public final String getDepartment() {
		return this.department.getValue();
	}

	/**
	 * Sets the contact department
	 */
	public final void setDepartment(String value) {
		this.department.setValue(value);
	}

	/**
	 * Gets the contact title
	 */
	@Override
	public final String getMessageTitle() {
		return this.title.getValue();
	}

	/**
	 * Sets the contact title
	 */
	public final void setTitle(String value) {
		this.title.setValue(value);
	}
	
	/**
	 * Gets the contact role
	 */
	public final String getRole() {
		return (StringUtils.isNotBlank( this.role.getValue() ) ?
				this.role.getValue() : MemberReference.NO_TYPE );
	}

	/**
	 * Sets the contact role
	 */
	public final void setRole(String value) {
		this.role.setValue(value);
	}

	/**
	 * Gets the contact organization
	 */
	public final String getOrganization() {
		return this.organization.getValue();
	}

	/**
	 * Sets the contact organization
	 */
	public final void setOrganization(String value) {
		this.organization.setValue(value);
	}

	/**
	 * Gets the contact original body
	 */
	@Override
	public final String getOrigBody() {
		return this.origBody.getValue();
	}

	/**
	 * Sets the contact original body
	 */
	@Override
	public final void setOrigBody(String value) {
		this.origBody.setValue(value);
	}
	
	public OrderCollection getBuildOrder() {
		return this.buildOrder;
	}
	
	public int getOrderCount() {
		return this.buildOrder.getCount();
	}
	
	public FavouritesCollection getFavourites() {
		return this.favourites;
	}
	
	public int getFavouritesCount() {
		return this.favourites.getCount();
	}
	
	public WindowPositionCollection getWindowPositionCollection() {
		return this.winPosition;
	}
	
	public int getWindowPositionCount() {
		return this.winPosition.getCount();
	}
	
	public void clearBuildOrder() {
		this.buildOrder.removeAll();
	}
	
	public void addSphereToFavourites(FavouriteSphere fs) {
		this.favourites.add(fs);
	}
	
	public void removeSphereFromFavourites(FavouriteSphere fs) {
		this.favourites.remove(fs);
	}
	
	public String getFullContactName(){
		final StringBuilder sb = new StringBuilder();
		addToSB( sb, getNamePrefix() );
		addToSB( sb, getFirstName() );
		addToSB( sb, getMiddleName() );
		addToSB( sb, getLastName() );
		return sb.toString();		
	}
	
	private void addToSB( final StringBuilder sb, final String str ){
		if ( StringUtils.isBlank(str) ) {
			return;
		}
		if ( sb.length() > 0 ) {
			sb.append( " " );			 
		}
		sb.append( str );
	}
	
	public static Comparator<ContactStatement> getComparatorForContactStatements(){
		final Comparator<ContactStatement> comparator = new Comparator<ContactStatement>(){
			public int compare( final ContactStatement c1, final ContactStatement c2 ) {
				if ( (c1 == null) && (c2 == null) ) {
					return 0;
				}
				if ( c1 == null ) {
					return -1;
				}
				if ( c2 == null ) {
					return 1;
				}
				final int result = StringUtils.safeCompare(
						c1.getLastName(), c2.getLastName());
				if ( result != 0 ) {
					return result;
				} else {
					return StringUtils.safeCompare(
							c1.getFirstName(), c2.getFirstName());
				}
			}
		};
		return comparator;		
	}
	
	public static Comparator<String> getComparatorForContactNames(){
		final Comparator<String> comparator = new Comparator<String>(){
			public int compare( final String contactName1, final String contactName2 ) {
				if ( (contactName1 == null) && (contactName2 == null) ) {
					return 0;
				}
				if ( contactName1 == null ) {
					return -1;
				}
				if ( contactName2 == null ) {
					return 1;
				}
				final int result = StringUtils.safeCompare(
						getLastName(contactName1), getLastName(contactName2));
				if ( result != 0 ) {
					return result;
				} else {
					return StringUtils.safeCompare(contactName1, contactName2);
				}
			}
		};
		return comparator;
	}
	
	private static String getLastName( final String contactName ){
		final String c = contactName.trim();
		final int index = c.lastIndexOf(' ');
		return ( index < 0 ) ? c : c.substring( index + 1 );
	}
	
	/**
	 * Evaluates contact name by first and last name
	 */
	public String getContactNameByFirstAndLastNames() {
		StringBuilder sb = new StringBuilder();
		sb.append( getFirstName() );
		final String lastName = getLastName();
		if ( sb.length() > 0 &&
			 lastName != null &&
			 lastName.length() > 0) {
			sb.append( " " );			 
		}
		sb.append( lastName );
		return sb.toString();
	}

	public String getReciprocalLogin() {
		return this.reciprocalLogin.getValue();
	}

	/**
	 * @return the spheresPreferences
	 */
	public UserSpherePreferencesCollection getSpheresPreferences() {
		return this.spheresPreferences;
	}

	/**
	 * @return the globalPreferences
	 */
	public UserPersonalPreferences getGlobalPreferences() {
		return this.globalPreferences.getValue();
	}

	public int compareTo(ContactStatement o) {
		return getContactNameByFirstAndLastNames().compareTo(o.getContactNameByFirstAndLastNames());
	}

	public String getAccount() {
		return this.account.getValueOrEmpty();
	}

	public void setAccount( final String value ) {
		this.account.setValue( value );
	}

	public String getOwnerContact() {
		return this.ownerContact.getValueOrEmpty();
	}

	public void setOwnerContact( final String ownerContact) {
		this.ownerContact.setValue( StringUtils.getTrimmedString( ownerContact ) );
	}

	/**
	 * @param newContactSt
	 */
	public void copyFields(ContactStatement newContactSt) {
		setAccount( newContactSt.getAccount() );
		setBody( newContactSt.getBody() );
		setCity( newContactSt.getCity() );
		setCountry( newContactSt.getCountry() );
		setDepartment( newContactSt.getDepartment() );
		setEmailAddress( newContactSt.getEmailAddress() );
		setFax( newContactSt.getFax() );
		setSecondEmailAddress( newContactSt.getSecondEmailAddress() );
		setFaxSecond( newContactSt.getFaxSecond() );
		setFirstName( newContactSt.getFirstName() );
		setHomeTelephone( newContactSt.getHomeTelephone() );
		setLastName( newContactSt.getLastName() );
		setLocation( newContactSt.getLocation() );
		setMiddleName( newContactSt.getMiddleName() );
		setMobile( newContactSt.getMobile() );
		setNamePrefix( newContactSt.getNamePrefix() );
		setNameSuffix( newContactSt.getNameSuffix() );
		setOrganization( newContactSt.getOrganization() );
		setOrigBody( newContactSt.getOrigBody() );
		setOwnerContact( newContactSt.getOwnerContact() );
		//setRole(value); - not setted
		setState( newContactSt.getState() );
		setStreet( newContactSt.getStreet() );
		setStreetCont( newContactSt.getStreetCont() );
		setSubject( newContactSt.getContactNameByFirstAndLastNames() );
		setTitle( newContactSt.getMessageTitle() );
		setURL( newContactSt.getURL() );
		setWorkTelephone( newContactSt.getWorkTelephone() );
		setZipCode( newContactSt.getZipCode() );
	}
}