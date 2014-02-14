/**
 * 
 */
package ss.common.file.vcf;

import net.wimpi.pim.contact.basicimpl.CommunicationsImpl;
import net.wimpi.pim.contact.basicimpl.EmailAddressImpl;
import net.wimpi.pim.contact.basicimpl.PhoneNumberImpl;
import net.wimpi.pim.contact.model.Communications;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.EmailAddress;
import net.wimpi.pim.contact.model.PhoneNumber;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;

/**
 * @author zobo
 * 
 */
public class VCardComunicationsMerger {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardComunicationsMerger.class);

	public final static VCardComunicationsMerger INSTANCE = new VCardComunicationsMerger();

	private VCardComunicationsMerger() {

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public boolean addComunicationsToContactStatement(
			final ContactStatement st, final Communications communications) {
		if (communications == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("communications is null");
			}
			return false;
		}

		try {
			addPhoneInfoToContactStatement(st, communications);
			addAddressToContactStatement(st, communications);
		} catch (Exception ex) {
			logger.error("Error adding comunication info to contact statement",
					ex);
		}

		return true;
	}
	
	public boolean collectComunicationsFromContactStatement(
			final ContactStatement st, final Contact contact) {
		final Communications communications = new CommunicationsImpl();
		try {
			PhoneNumber phone;
			final String mobile = st.getMobile();
			if ( StringUtils.isNotBlank(mobile) ) {
				phone = new PhoneNumberImpl();
				phone.setNumber( mobile );
				phone.setCellular(true);
				phone.setVoice(true);
				communications.addPhoneNumber(phone );
			}
			
			final String work = st.getWorkTelephone();
			if ( StringUtils.isNotBlank(work) ) {
				phone = new PhoneNumberImpl();
				phone.setNumber( work );
				phone.setWork(true);
				phone.setVoice(true);
				communications.addPhoneNumber( phone );
			}
			
			final String home = st.getHomeTelephone();
			if ( StringUtils.isNotBlank(home) ) {
				phone = new PhoneNumberImpl();
				phone.setNumber( home );
				phone.setHome(true);
				phone.setVoice(true);
				communications.addPhoneNumber( phone );
			}
			
			final String fax1 = st.getFax();
			if ( StringUtils.isNotBlank(fax1) ) {
				phone = new PhoneNumberImpl();
				phone.setNumber( fax1 );
				phone.setFax(true);
				communications.addPhoneNumber( phone );
			}

			final String fax2 = st.getFaxSecond();
			if ( StringUtils.isNotBlank(fax2) ) {
				phone = new PhoneNumberImpl();
				phone.setNumber( fax2 );
				phone.setFax(true);
				communications.addPhoneNumber( phone );
			}
			
			EmailAddress email;
			final String addr1 = st.getEmailAddress();
			if ( StringUtils.isNotBlank(addr1) ) {
				email = new EmailAddressImpl();
				email.setAddress(addr1);
				communications.addEmailAddress(email);
			}
			final String addr2 = st.getSecondEmailAddress();
			if ( StringUtils.isNotBlank(addr2) ) {
				email = new EmailAddressImpl();
				email.setAddress(addr2);
				communications.addEmailAddress(email);
			}

			contact.setCommunications( communications );

		} catch (Exception ex) {
			logger.error("Error adding comunication info to contact statement",
					ex);
		}

		return true;
	}

	private void addAddressToContactStatement(final ContactStatement st,
			final Communications communications) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding email addresses information");
		}
		final EmailAddress prefAddress = communications
				.getPreferredEmailAddress();
		if (prefAddress != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Preffered address in avalable, set: "
						+ prefAddress.getAddress());
			}
			st.setEmailAddress(prefAddress.getAddress());
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Preffered address was not specified");
			}
		}
		String address = null;
		final EmailAddress[] addresses = communications.listEmailAddresses();
		if ((addresses != null) && (addresses.length > 0)) {
			if (logger.isDebugEnabled()) {
				for (EmailAddress ea : addresses) {
					logger.debug("EmailAddress: " + ea.getAddress());
				}
			}
			for (EmailAddress ea : addresses) {
				if (ea.isType(EmailAddress.TYPE_INTERNET)) {
					address = ea.getAddress();
					if (logger.isDebugEnabled()) {
						logger.debug("Internet address is setted: " + address);
					}
					addEmailAddress(st, address);
				}
			}
			if (address == null) {
				for (EmailAddress ea : addresses) {
					if (ea.isType(EmailAddress.TYPE_X400)) {
						address = ea.getAddress();
						if (logger.isDebugEnabled()) {
							logger.debug("X400 address is setted: " + address);
						}
						addEmailAddress(st, address);
					}
				}
			}
			if (address == null) {
				for (EmailAddress ea : addresses) {
					address = ea.getAddress();
					if (logger.isDebugEnabled()) {
						logger.debug("Other type address is setted: " + address);
					}
					addEmailAddress(st, address);
				}
			}			
		}
	}
	
	private boolean addEmailAddress( final ContactStatement st, final String address ){
		if ( StringUtils.isBlank(st.getEmailAddress()) ) {
			st.setEmailAddress(address);
			return true;
		}
		if ( StringUtils.isBlank(st.getSecondEmailAddress()) && 
				!st.getEmailAddress().equals(address)) {
			st.setSecondEmailAddress(address);
			return true;
		}
		return false;
	}
	
	private boolean setSinglePhoneNumber(final ContactStatement st,
			final PhoneNumber number){
		if ( number == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("number is null");
			}
			return false;
		}
		if ( number.isCellular() && StringUtils.isBlank(st.getMobile())) {
			st.setMobile(number.getNumber());
			if (logger.isDebugEnabled()) {
				logger.debug("Cellular: " + number.getNumber());
			}
			return true;
		}
		if ( number.isFax() && StringUtils.isBlank(st.getFax()) ) {
			st.setFax(number.getNumber());
			if (logger.isDebugEnabled()) {
				logger.debug("Fax: " + number.getNumber());
			}
			return true;
		} else if ( number.isFax() && StringUtils.isBlank(st.getFaxSecond()) ) {
			st.setFaxSecond(number.getNumber());
			if (logger.isDebugEnabled()) {
				logger.debug("FaxSecond: " + number.getNumber());
			}
			return true;			
		}
		if ( number.isVoice() && number.isWork() && StringUtils.isBlank(st.getWorkTelephone())) {
			st.setWorkTelephone(number.getNumber());
			if (logger.isDebugEnabled()) {
				logger.debug("WORK: " + number.getNumber());
			}
			return true;
		}
		if ( number.isVoice() && number.isHome() && StringUtils.isBlank(st.getHomeTelephone())) {
			st.setHomeTelephone(number.getNumber());
			if (logger.isDebugEnabled()) {
				logger.debug("HOME: " + number.getNumber());
			}
			return true;
		}
		return false;
	}

	private void addPhoneInfoToContactStatement(final ContactStatement st,
			final Communications communications) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding phone information to contact statement");
		}
		final PhoneNumber prefPhone = communications.getPreferredPhoneNumber();
		final PhoneNumber[] allNumbers = communications.listPhoneNumbers();

		setSinglePhoneNumber( st, prefPhone );
		if ( allNumbers != null ) {
			for (PhoneNumber number : allNumbers) {
				setSinglePhoneNumber( st, number );
			}
		}
		
		if (StringUtils.isBlank(st.getHomeTelephone()) && StringUtils.isBlank(st.getWorkTelephone()) 
				&& StringUtils.isBlank(st.getMobile()) && ( allNumbers != null )) {
			for (PhoneNumber number : allNumbers) {
				if ( (number!=null) && (!number.isFax()) ) {
					if (logger.isDebugEnabled()) {
						logger.debug("Due to all voice numbers are blank setting this one as home telephone: " + number.getNumber());
					}
					st.setHomeTelephone(number.getNumber());
				}
			}
		}
	}
		
	private void old_addPhoneInfoToContactStatement(final ContactStatement st,
				final Communications communications) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding phone information to contact statement");
		}
		final PhoneNumber prefPhone = communications.getPreferredPhoneNumber();
		final PhoneNumber[] allFaxes = communications
				.listPhoneNumbersByType(PhoneNumber.TYPE_FAX);
		final PhoneNumber[] allMobile = communications
				.listPhoneNumbersByType(PhoneNumber.TYPE_CELLULAR);
		final PhoneNumber[] allVoice = communications
				.listPhoneNumbersByType(PhoneNumber.TYPE_VOICE);
		final PhoneNumber[] allNumbers = communications.listPhoneNumbers();
		
		String voice1setted = null;
		String voice2setted = null;
		String faxsetted = null;
		String mobilesetted = null;

		if ((allFaxes != null) && (allFaxes.length > 0)) {
			faxsetted = allFaxes[0].getNumber();
			if (logger.isDebugEnabled()) {
				logger.debug("Fax setted: " + faxsetted);
			}
			st.setFax(faxsetted);
		}

		if ((allMobile != null) && (allMobile.length > 0)) {
			mobilesetted = allMobile[0].getNumber();
			if (logger.isDebugEnabled()) {
				logger.debug("Mobile setted: " + mobilesetted);
			}
			st.setMobile(mobilesetted);
		}

		if (prefPhone != null) {
			voice1setted = prefPhone.getNumber();
			if (logger.isDebugEnabled()) {
				logger.debug("Voice1 setted: " + voice1setted);
			}
			st.setWorkTelephone(voice1setted);
		}

		if ((allVoice != null) && (allVoice.length > 0)) {
			String voice1 = allVoice[0].getNumber();
			String voice2 = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Temporary voice1: " + voice1 + "; voice2: "
						+ voice2);
			}
			if (allVoice.length > 1) {
				voice2 = allVoice[1].getNumber();
			}
			if (voice1setted != null) {
				if (voice1setted.equals(voice1)) {
					if (voice2 != null) {
						st.setHomeTelephone(voice2);
						voice2setted = voice2;
					}
				} else {
					st.setHomeTelephone(voice1);
					voice2setted = voice1;
				}
			} else {
				voice1setted = voice1;
				st.setWorkTelephone(voice1setted);
				if (voice2 != null) {
					voice2setted = voice2;
					st.setHomeTelephone(voice2setted);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("After all voicesetted1: " + voice1setted
					+ "; voice2setted: " + voice2setted);
		}

		if ((allNumbers != null) && (allNumbers.length > 0)) {
			if (voice1setted == null) {
				for (PhoneNumber ph : allNumbers) {
					if (!StringUtils.equals(ph.getNumber(), mobilesetted)
							&& !StringUtils.equals(ph.getNumber(), faxsetted)) {
						voice1setted = ph.getNumber();
						if (logger.isDebugEnabled()) {
							logger
									.debug("Voice1 was not specifically setted, setting as: "
											+ voice1setted);
						}
						st.setWorkTelephone(voice1setted);
						break;
					}
				}
			}
			if (voice2setted == null) {
				for (PhoneNumber ph : allNumbers) {
					if (!StringUtils.equals(ph.getNumber(), mobilesetted)
							&& !StringUtils.equals(ph.getNumber(), faxsetted)
							&& !StringUtils
									.equals(ph.getNumber(), voice1setted)) {
						voice2setted = ph.getNumber();
						if (logger.isDebugEnabled()) {
							logger
									.debug("Voice2 was not specifically setted, setting as: "
											+ voice2setted);
						}
						st.setHomeTelephone(voice2setted);
						break;
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("After all: ");
			logger.debug("voice1setted: " + voice1setted);
			logger.debug("voice2setted: " + voice2setted);
			logger.debug("faxsetted: " + faxsetted);
			logger.debug("mobilesetted: " + mobilesetted);
		}
	}
}
