/**
 * 
 */
package ss.common.file.vcf;

import java.util.Iterator;

import net.wimpi.pim.contact.basicimpl.AddressImpl;
import net.wimpi.pim.contact.model.Address;
import net.wimpi.pim.contact.model.Contact;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;

/**
 * @author zobo
 *
 */
public class VCardAddressMerger {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardAddressMerger.class);
	
	public final static VCardAddressMerger INSTANCE = new VCardAddressMerger();
	
	private VCardAddressMerger(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public boolean addAddressToContactStatement( final ContactStatement st, final Contact contact){
		if ( st == null ) {
			logger.error("Statement is null");
			return false;
		}
		if ( contact == null ) {
			logger.error("contact is null");
			return false;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Address classification: " + contact.getAccessClassification());
		}
		
		final Address preferredAddress = contact.getPreferredAddress();
		if ( addAddress(st, preferredAddress) ) {
			return true;
		}
		if ( contact.getAddresses() != null ) {
			for (Iterator iterator = contact.getAddresses(); iterator.hasNext();) {
				Address address = (Address)iterator.next();
				if ( addAddress(st, address) ) {
					return true;
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("iterator for addresses is null");
			}
		}
		return false;
	}
	
	public boolean collectAddressFromContactStatement( final ContactStatement st, final Contact contact){
		if ( st == null ) {
			logger.error("Statement is null");
			return false;
		}
		if ( contact == null ) {
			logger.error("contact is null");
			return false;
		}
		
		final Address address = new AddressImpl();
		address.setCountry( StringUtils.getNotNullString(st.getCountry()) );
		address.setCity( StringUtils.getNotNullString(st.getCity()) );
		address.setStreet( StringUtils.getNotNullString(st.getStreet()) );
		address.setPostalCode( StringUtils.getNotNullString(st.getZipCode()) );
		address.setRegion( StringUtils.getNotNullString(st.getState()) );
		contact.addAddress( address );
		return true;
	}
	
	private boolean addAddress(  final ContactStatement st, final Address address ){
		if ( address == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("address is null");
			}
			return false;
		}
		final String city = address.getCity();
		final String country = address.getCountry();
		final String postalCode = address.getPostalCode();
		final String region = address.getRegion();
		final String street = address.getStreet();
		final String UID = address.getUID();
		final String extended = address.getExtended();
		final String postBox = address.getPostBox();
		final String label = address.getLabel();
		if (logger.isDebugEnabled()) {
			logger.debug("city: " + city);
			logger.debug("country: " + country);
			logger.debug("postalCode: " + postalCode);
			logger.debug("region: " + region);
			logger.debug("street: " + street);
			logger.debug("UID: " + UID);
			logger.debug("extended: " + extended);
			logger.debug("postBox: " + postBox);
			logger.debug("label: " + label);
		}
		st.setCountry( StringUtils.getNotNullString(country) );
		st.setStreet( StringUtils.getNotNullString(street) );
		st.setZipCode( StringUtils.getNotNullString(postalCode) );
		st.setCity( StringUtils.getNotNullString(city) );
		return true;
	}
}
