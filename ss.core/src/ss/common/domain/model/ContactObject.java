/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.collections.LocationCollection;
import ss.common.domain.model.collections.SphereReferencesCollection;
import ss.common.domain.model.collections.TabOrderCollection;
import ss.common.domain.model.collections.WindowPositionColection;

/**
 * @author roman
 *
 */
public class ContactObject extends DomainObject {

	private String lastName;
	
	private String firstName;
	
	private String login;
	
	private String emailAddress;
	
	private String voice1;
	
	private String voice2;
	
	private String mobile;
	
	private String fax;
	
	private String organization;
	
	private String department;
	
	private String url;
	
	private String homeSphere;
	
	private String street;
	
	private String streetCont;
	
	private String city;
	
	private String state;
	
	private String zipCode;
	
	private String country;
	
	private String title;
	
	private String location;
	
	private String timeZone;
	
	private String middleName;
	
	private String namePrefix;
	
	private String nameSuffix;
	
	private final LocationCollection locations = new LocationCollection();
	
	private final SphereReferencesCollection favourites = new SphereReferencesCollection();
	
	private final TabOrderCollection tabOrder = new TabOrderCollection();
	
	private final WindowPositionColection windowPositions = new WindowPositionColection();

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the voice1
	 */
	public String getVoice1() {
		return this.voice1;
	}

	/**
	 * @param voice1 the voice1 to set
	 */
	public void setVoice1(String voice1) {
		this.voice1 = voice1;
	}

	/**
	 * @return the voice2
	 */
	public String getVoice2() {
		return this.voice2;
	}

	/**
	 * @param voice2 the voice2 to set
	 */
	public void setVoice2(String voice2) {
		this.voice2 = voice2;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return this.mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return this.fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return this.organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return this.department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the homeSphere
	 */
	public String getHomeSphere() {
		return this.homeSphere;
	}

	/**
	 * @param homeSphere the homeSphere to set
	 */
	public void setHomeSphere(String homeSphere) {
		this.homeSphere = homeSphere;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @return the streetCont
	 */
	public String getStreetCont() {
		return this.streetCont;
	}

	/**
	 * @param streetCont the streetCont to set
	 */
	public void setStreetCont(String streetCont) {
		this.streetCont = streetCont;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return this.zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return this.timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the namePrefix
	 */
	public String getNamePrefix() {
		return this.namePrefix;
	}

	/**
	 * @param namePrefix the namePrefix to set
	 */
	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	/**
	 * @return the nameSuffix
	 */
	public String getNameSuffix() {
		return this.nameSuffix;
	}

	/**
	 * @param nameSuffix the nameSuffix to set
	 */
	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}

	/**
	 * @return the locations
	 */
	public LocationCollection getLocations() {
		return this.locations;
	}

	/**
	 * @return the favourites
	 */
	public SphereReferencesCollection getFavourites() {
		return this.favourites;
	}

	/**
	 * @return the tabOrder
	 */
	public TabOrderCollection getTabOrder() {
		return this.tabOrder;
	}

	/**
	 * @return the windowPositions
	 */
	public WindowPositionColection getWindowPositions() {
		return this.windowPositions;
	}
}
