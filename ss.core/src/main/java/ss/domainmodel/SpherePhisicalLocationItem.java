/**
 * 
 */
package ss.domainmodel;

import ss.common.StringUtils;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 *
 */
public class SpherePhisicalLocationItem extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "spherelocation";
	
	private final ISimpleEntityProperty email = super
		.createAttributeProperty( "email/@value" );
	
	private final ISimpleEntityProperty telephone = super
		.createAttributeProperty( "telephone/@value" );
	
	private final ISimpleEntityProperty state = super
	.createAttributeProperty( "state/@value" );
	
	private final ISimpleEntityProperty fax = super
	.createAttributeProperty( "fax/@value" );
	
	private final ISimpleEntityProperty region = super
		.createAttributeProperty( "region/@value" );
	
	private final ISimpleEntityProperty city = super
		.createAttributeProperty( "city/@value" );
	
	private final ISimpleEntityProperty country = super
		.createAttributeProperty( "country/@value" );

	private final ISimpleEntityProperty street = super
		.createAttributeProperty( "street/@value" );
	
	private final ISimpleEntityProperty streetcont = super
		.createAttributeProperty( "streetcont/@value" );
	
	private final ISimpleEntityProperty address = super
		.createAttributeProperty( "address/@value" );
	
	private final ISimpleEntityProperty zipcode = super
		.createAttributeProperty( "zipcode/@value" );

	private final ISimpleEntityProperty description = super
		.createAttributeProperty( "description/@value" );
	
	public SpherePhisicalLocationItem() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}

	public String getAddress() {
		return this.address.getValue();
	}

	public String getTelephone() {
		return this.telephone.getValue();
	}

	public String getRegion() {
		return this.region.getValue();
	}

	public void setAddress( final String value ) {
		this.address.setValue(value);
	}

	public void setTelephone( final String value ) {
		this.telephone.setValue(value);
	}

	public void setRegion( final String value ) {
		this.region.setValue(value);
	}

	public String getEmail() {
		return this.email.getValue();
	}

	public void setEmail(String value) {
		this.email.setValue(value);
	}

	public String getFax() {
		return this.fax.getValue();
	}

	public void setFax(String value) {
		this.fax.setValue(value);
	}

	public String getCity() {
		return this.city.getValue();
	}

	public void setCity(String value) {
		this.city.setValue(value);
	}

	public String getCountry() {
		return this.country.getValue();
	}

	public void setCountry(String value) {
		this.country.setValue(value);
	}

	public String getStreet() {
		return this.street.getValue();
	}

	public void setStreet(String value) {
		this.street.setValue(value);
	}

	public String getStreetcont() {
		return this.streetcont.getValue();
	}

	public void setStreetcont(String value) {
		this.streetcont.setValue(value);
	}

	public String getZipcode() {
		return this.zipcode.getValue();
	}

	public void setZipcode(String value) {
		this.zipcode.setValue(value);
	}

	public String getDescription() {
		return this.description.getValue();
	}

	public void setDescription(String value) {
		this.description.setValue(value);
	}
	
	public void setState(final String state) {
		this.state.setValue(state);
	}
	
	public String getState() {
		return this.state.getValue();
	}

	/**
	 * @param locationItem
	 */
	public void copyAll(final SpherePhisicalLocationItem locationItem) {
		setDescription(locationItem.getDescription());
		setAddress(locationItem.getAddress());
		setCity(locationItem.getCity());
		setCountry(locationItem.getCountry());
		setFax(locationItem.getFax());
		setEmail(locationItem.getEmail());
		setStreet(locationItem.getStreet());
		setStreetcont(locationItem.getStreetcont());
		setRegion(locationItem.getRegion());
		setTelephone(locationItem.getTelephone());
		setZipcode(locationItem.getZipcode());
		setState(locationItem.getState());
	}
	
	public boolean isSame(final SpherePhisicalLocationItem locationItem) {
		if(!StringUtils.getTrimmedString(getDescription()).equals(StringUtils.getTrimmedString(locationItem.getDescription()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getAddress()).equals(StringUtils.getTrimmedString(locationItem.getAddress()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getCountry()).equals(StringUtils.getTrimmedString(locationItem.getCountry()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getCity()).equals(StringUtils.getTrimmedString(locationItem.getCity()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getEmail()).equals(StringUtils.getTrimmedString(locationItem.getEmail()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getFax()).equals(StringUtils.getTrimmedString(locationItem.getFax()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getRegion()).equals(StringUtils.getTrimmedString(locationItem.getRegion()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getStreet()).equals(StringUtils.getTrimmedString(locationItem.getStreet()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getStreetcont()).equals(StringUtils.getTrimmedString(locationItem.getStreetcont()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getTelephone()).equals(StringUtils.getTrimmedString(locationItem.getTelephone()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getZipcode()).equals(StringUtils.getTrimmedString(locationItem.getZipcode()))) {
			return false;
		}
		if(!StringUtils.getTrimmedString(getState()).equals(StringUtils.getTrimmedString(locationItem.getState()))) {
			return false;
		}
		return true;
	}
}
