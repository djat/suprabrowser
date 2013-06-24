/**
 * 
 */
package ss.client.ui.email;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import ss.common.ListUtils;
import ss.common.StringUtils;
import ss.domainmodel.configuration.DomainProvider;

/**
 * @author zobo
 * 
 */
public class SpherePossibleEmailsSet {

	private static final String ROUTING_NUMBER_DIVIDER = ".";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpherePossibleEmailsSet.class);

	public static final String PARSE_BRAKER = ",";

	private String singleLineEmails = null;

	private List<String> parsedAddresses = null;

	private boolean updateSingleLineEmails = true;

	/**
	 * Constructs on existing addresses.
	 * 
	 * @param addresses
	 */
	public SpherePossibleEmailsSet(String singleLineEmails) {
		super();
		this.singleLineEmails = singleLineEmails;
		this.updateSingleLineEmails = false;
		this.parsedAddresses = parse(this.singleLineEmails);
	}

	public SpherePossibleEmailsSet(List<String> addresses) {
		this.parsedAddresses = addresses;
	}

	public SpherePossibleEmailsSet() {
		this((List<String>) null);
	}

	/**
	 * @param addresses
	 *            String of addresses divided by PARSE_BRAKER.
	 * @param isCutDomain
	 *            specify is addresses will be added with domain info or without
	 */
	public void addAddresses( final String addresses ) {
		if (StringUtils.isBlank(addresses)) {
			return;
		}
		final List<String> addressesNew = parse(addresses);
		if (this.parsedAddresses == null){
			this.parsedAddresses = new ArrayList<String>();
		}
		List<String> justEmailNames = parse(this.parsedAddresses);
		for (String addressNew : addressesNew) {
			if (!justEmailNames
					.contains(parseSingleAddress(addressNew))) {
				addAddress( addressNew );
				justEmailNames = parse(this.parsedAddresses);
			} else {
				logger.warn("Such email already specified: " + addressNew);
			}
		}
		this.updateSingleLineEmails = true;
	}
	
	public void addAsPrimaryAndOverwriteAddresses( final String address ) {
		if (StringUtils.isBlank(address)) {
			return;
		}
		if (this.parsedAddresses == null){
			this.parsedAddresses = new ArrayList<String>();
		}
		List<String> justEmailNames = parse(this.parsedAddresses);
		if (justEmailNames
				.contains(parseSingleAddress(address))) {
			deleteAddresses(address);
		}
		addAddress( address );
		setPrimaryAlias(address);
		justEmailNames = parse(this.parsedAddresses);
		this.updateSingleLineEmails = true;
	}

	/**
	 * @param address
	 *            Single address.
	 * @param isCutDomain
	 *            specifies if domains should be cutted.
	 */
	private void addAddress( String address ) {
		String first = "";
		if (address.indexOf("<") != -1) {
			first = address.substring(0, address.indexOf('<'));
			first = first.replace('"', ' ').trim();
			if (!first.equals(""))
				first = '"' + first + '"' + " ";
		}
		final String second = parseSingleAddress(address);
		final String total = first + "<" + second + ">";
		this.parsedAddresses.add(total);
	}

	public void deleteAddresses(String addresses) {
		if (StringUtils.isBlank(addresses)) {
			return;
		}
		List<String> addressesToDelete = parse(addresses);
		if ((addressesToDelete == null) || (addressesToDelete.isEmpty()))
			return;
		List<String> justEmailNames = parse(this.parsedAddresses);
		for (String addressToDelete : addressesToDelete) {
			String str = parseSingleAddress(addressToDelete);
			for (Iterator iter = justEmailNames.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				if (element.equals(str)) {
					this.parsedAddresses
							.remove(justEmailNames.indexOf(element));
					justEmailNames = parse(this.parsedAddresses);
				}
			}
		}
		this.updateSingleLineEmails = true;
	}

	public boolean contains(String singleAddress) {
		if (singleAddress == null)
			return false;
		if (this.parsedAddresses == null)
			this.parsedAddresses = new ArrayList<String>();
		List<String> justEmailNames = parse(this.parsedAddresses);
		if (justEmailNames
				.contains(parseSingleAddress(singleAddress))) {
			return true;
		} else {
			return false;
		}
	}

	public static List<String> parse(String addresses) {
		ArrayList<String> array = new ArrayList<String>();
		boolean isCreated = false;
		try {
			StringTokenizer t = new StringTokenizer(addresses, PARSE_BRAKER);
			while (true) {
				String str = t.nextToken();
				array.add(str.trim());
				isCreated = true;
			}
		} catch (Exception e) {
		}
		if (isCreated)
			return array;
		return null;
	}

	/**
	 * Generates ArrayList of just recipient names
	 * 
	 * @param addresses
	 * @return
	 */
	private static List<String> parse(List<String> addresses) {
		ArrayList<String> array = new ArrayList<String>(addresses.size());
		for (String s : addresses) {
			array.add(parseSingleAddress(s));
		}
		return array;
	}

	/**
	 * Generates new ArrayList of email addresses UI Like.
	 * 
	 * @param addresses
	 *            parsed email addresses
	 * @param domain
	 *            domain name
	 * @return
	 */
//	private static List<String> parse(List<String> addresses, String domain) {
//		ArrayList<String> array = new ArrayList<String>(addresses.size());
//		if (domain != null) {
//			for (String s : addresses) {
//				array.add(supplyAddressWithDomain(s, domain));
//			}
//		}
//		return array;
//	}

	public static String parseSingleAddress(String singleAddress) {
		String address = new String(singleAddress);
		if (address.indexOf("<") != -1) {
			int lastChar = address.indexOf('>', address.indexOf('<'));
			address = address.substring(0, lastChar);
			int firstChar = address.indexOf("<");

			address = address.substring(firstChar + 1, address.length());
		}
		return address.toLowerCase().trim();
	}

	public static String parseSingleAddressEvenWithoutDomain(
			String singleAddress) {
		String str = parseSingleAddress(singleAddress);
		if (str.indexOf("@") != -1) {
			int lastChar = str.indexOf('@');
			str = str.substring(0, lastChar);
		}
		return str;
	}

	/**
	 * Only for remote command usage!
	 * @param singleAddress
	 * @param domain
	 */
	public void supplyAllCurrentAddressWithDomain( final String domain ) {
		if ((this.parsedAddresses == null) || this.parsedAddresses.isEmpty()){
			return;
		}
		final List<String> newParsedAddresses = new ArrayList<String>(this.parsedAddresses.size());
		for (String address : this.parsedAddresses){
			if (StringUtils.isBlank(address)){
				continue;
			}
			String newAddress;
			if (address.indexOf('@') != -1){
				newAddress = address;
			} else {
				int index = address.lastIndexOf('>');
				if (index != -1) {
					newAddress = address.substring(0, index) + "@" + domain
							+ ">";
				} else {
					newAddress = address.trim() + "@" + domain;
				}
			}
			newParsedAddresses.add(newAddress);
		}
		this.parsedAddresses = newParsedAddresses;
		this.updateSingleLineEmails = true;
	}

	public static String generateSingleStringEmailAddresses(
			List<String> parsedAddresses) {
		String addresses = "";
		boolean addBraker = false;
		for (String s : parsedAddresses) {
			if (addBraker)
				addresses += PARSE_BRAKER;
			else
				addBraker = true;
			addresses += s;
		}
		return addresses;
	}

	public String getSingleStringEmails() {
		if (this.updateSingleLineEmails) {
			this.singleLineEmails = generateSingleStringEmailAddresses(this.parsedAddresses);
			this.updateSingleLineEmails = false;
		}
		return this.singleLineEmails;
	}

	public List<String> getParsedEmailNames() {
		return parse(this.parsedAddresses);
	}

	public List<String> getParsedEmailAddresses() {
		if (this.parsedAddresses == null)
			this.parsedAddresses = new ArrayList<String>();
		return this.parsedAddresses;
	}
	
	public String get( int index ){
		if ((index >= 0) && (index < getParsedEmailAddresses().size())){
			return getParsedEmailAddresses().get(index);
		}
		return null;
	}
	
	public String getAndDetach( int index ){
		if ((index >= 0) && (index < getParsedEmailAddresses().size())){
			String result = getParsedEmailAddresses().get(index);
			this.updateSingleLineEmails = true;
			getParsedEmailAddresses().remove( index );
			return result;
		}
		return null;
	}

//	/**
//	 * emailName should include domain: test@er.com
//	 */
//	public static String createAddressStringIncludingDomain(String description,
//			String emailName) {
//		String str = "";
//		if (StringUtils.isNotBlank(description)) {
//			str += convertToNamingConventionDescription(description);
//		}
//		str += "<" + convertToNamingConvention(emailName) + ">";
//		return str;
//	}
	
	public static String createAddressString(String description,
			String emailName, String domain) {
		String str = "";
		if ((description != null) && (!description.trim().equals(""))) {
			str += convertToNamingConventionDescription(description);
		}
		str += "<" + convertToNamingConvention(emailName) + "@" + domain + ">";
		return str;
	}
	
	public static String cleanUpAddressString(String description,
			String emailName) {
		String str = "";
		if ((description != null) && (!description.trim().equals(""))) {
			str += convertToNamingConventionDescription(description);
		}
		str += "<" + emailName.trim().toLowerCase() + ">";
		return str;
	}

	public static String createAddressStringOfPersonToPersonShpere(
			String firstLogin, String firstContact, String secondLogin,
			String secondContact, String domain) {
		String name = convertToNamingConvention(firstLogin + "_" + secondLogin);
		String descr = firstContact + " and " + secondContact;
		return createAddressString(descr, name, domain);
	}

	public static String getDescriptionFromAddress(String singleAddress) {
		if (singleAddress.indexOf("<") != -1) {
			String description = singleAddress.substring(0,
					singleAddress.indexOf('<')).trim();
			if (!description.equals("")) {
				return description;
			} else {
				return "";
			}
		}
		return singleAddress;
	}

	public static String convertToNamingConvention(String name) {
		return filterRestrictedCharasters(name.trim(),true).toLowerCase();
	}
	
	public static String convertToNamingConventionDescription(String description){
		return '"' + filterRestrictedCharasters(description.replace('"', ' '), false).trim() + '"' + " ";
	}
	
	private static String filterRestrictedCharasters(String text, boolean isSpaceReplace){
		String returnText = text;
		if (isSpaceReplace){
			returnText = returnText.replace(' ', '_');
		}
		returnText = returnText.replace('&', '_');
		returnText = returnText.replace('<', '_');
		returnText = returnText.replace('>', '_');                                            
		returnText = returnText.replace("'", "_");
		returnText = returnText.replace('"', '_');
		returnText = returnText.replace('!', '_');
		
		returnText = returnText.replace('#', '_');
		returnText = returnText.replace('$', '_');
		returnText = returnText.replace('%', '_');
		returnText = returnText.replace('(', '_');
		returnText = returnText.replace(')', '_');
		returnText = returnText.replace('*', '_');
		returnText = returnText.replace(',', '_');
		
		returnText = returnText.replace('/', '_');
		returnText = returnText.replace(':', '_');
		returnText = returnText.replace(';', '_');
		returnText = returnText.replace('=', '_');
		returnText = returnText.replace('?', '_');
		returnText = returnText.replace('@', '_');
		
		returnText = returnText.replace('[', '_');
		returnText = returnText.replace(']', '_');
		returnText = returnText.replace("\\", "_");
		returnText = returnText.replace('^', '_');
		returnText = returnText.replace('`', '_');
		returnText = returnText.replace('{', '_');
		returnText = returnText.replace('}', '_');
		
		returnText = returnText.replace('|', '_');
		returnText = returnText.replace('~', '_');
		returnText = returnText.replace('.', '_');
		
		return returnText;
	}

	/**
	 * 
	 * @param singleAddress
	 * must contains single @ symbol
	 * @return
	 */
	public static String supplySingleAddressWithRoutingNumber(
			String singleAddress, String routingNumber, boolean addRemove) {
		try {
			StringTokenizer t = new StringTokenizer(singleAddress, "@");
			String first = t.nextToken();
			String second = t.nextToken();
			if (addRemove) {
				first += ROUTING_NUMBER_DIVIDER + routingNumber;
			} else {
				first = first.substring(0, first
						.lastIndexOf(ROUTING_NUMBER_DIVIDER));
			}
			return first + "@" + second;
		} catch (Exception e) {
			logger.warn("Cannot manadge routing number in email");
			return singleAddress;
		}
	}

	public static String supplyAddressesWithRoutingNumber(String addresses,
			String routingNumber, boolean addRemove) {
		List<String> list = parse(addresses);
		if (list == null)
			return "";
		ArrayList<String> listNew = new ArrayList<String>(list.size());
		for (String s : list) {
			listNew.add(supplySingleAddressWithRoutingNumber(s, routingNumber,
					addRemove));
		}
		return generateSingleStringEmailAddresses(listNew);
	}

	public static String getDomainFromSingleAddress(String address) {
		String str = parseSingleAddress(address).toLowerCase().trim();
		if (str.indexOf("@") != -1) {
			try {
				StringTokenizer t = new StringTokenizer(str, "@");
				t.nextToken();
				String second = t.nextToken();
				return second;
			} catch (Exception e) {
				logger.warn("Cannot get domain");
				return null;
			}
		} else {
			logger.warn("Cannot get domain");
			return null;
		}
	}

	public static boolean isValidEmailAlias(String address) {
		if (address.lastIndexOf('@') != -1){
			
		}
		return true;
	}

	/**
	 * @param alias
	 */
	public boolean setPrimaryAlias(String alias) {
		if (alias == null)
			return false;
		if (!this.parsedAddresses.contains(alias))
			return false;
		this.parsedAddresses.remove(alias);
		this.parsedAddresses.add(0, alias);
		this.updateSingleLineEmails = true;
		return true;
	}

	/**
	 * 
	 */
	public void cleanUp() {
		if (logger.isDebugEnabled()) {
			logger.debug("Clean up performed");
		}
		if ((this.parsedAddresses == null) || (this.parsedAddresses.isEmpty())) {
			return;
		}
		try {
			List<String> newParsedAddresses = new ArrayList<String>();

			for (String s : this.parsedAddresses) {
				if (logger.isDebugEnabled()) {
					logger.debug("Checking address: " + s);
				}
				String addr = cleanAddress(s);
				if (isValidAddress(addr)) {
					newParsedAddresses.add(addr);
					if (logger.isDebugEnabled()) {
						logger.debug("Email address is valid: " + addr);
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Email address is not valid: " + addr);
					}
				}
			}
			this.parsedAddresses = newParsedAddresses;
		} catch (Throwable ex) {
			logger.error("Error in processing parsed Addresses", ex);
			this.parsedAddresses = new ArrayList<String>();
		} finally {
			this.updateSingleLineEmails = true;
		}
	}
	
	private static String cleanAddress(String addr){
		String str = addr;
		try {
			if (str.indexOf('>') != -1){
				str = str.substring(0, str.lastIndexOf('>') + 1).trim();
			}
			if (str.indexOf('"') != -1){
				str = str.substring(str.indexOf('\"')).trim();	
			}
		} catch (Exception ex){
			logger.error(ex);
		}
		
		return str;
	}

	/**
	 * @param addr
	 * @return
	 */
	private static boolean isValidAddress(String addr) {
		if (StringUtils.isBlank(addr)){
			return false;
		}
		if (addr.indexOf("@") != -1) {
			try {
				StringTokenizer t = new StringTokenizer(addr, "@");
				String first = t.nextToken();
				String second = t.nextToken();
				if (StringUtils.isBlank(first) || StringUtils.isBlank(second)){
					return false;
				}
				if ((first.indexOf("@") != -1) || (second.indexOf("@") != -1)){
					return false;
				}
				return true;
			} catch (Exception ex){
				logger.error(ex);
				return false;
			}
		} else {
			return false;
		}		
	}

	/**
	 * @param ccText
	 * @return
	 */
	public static String removeComma(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		String toRet = str.trim();
		while (toRet.charAt(toRet.length()-1) == ',') {
			toRet = toRet.substring(0, str.length()-1);
		}
		return toRet;
	}
	
	private static final String LOCALHOST_1 = "localhost";

	private static final String LOCALHOST_2 = "127.0.0.1";
	
	public void cleanUpAddresses() {
		final List<String> addresses = getParsedEmailAddresses();
		if ((addresses == null) || (addresses.isEmpty())) {
			return;
		}
		List<String> result = new ArrayList<String>();
		List<String> domains = DomainProvider.getDomainsList();
		domains.add(LOCALHOST_1);
		domains.add(LOCALHOST_2);
		if ((domains == null) || (domains.isEmpty())) {
			logger.error("Current domains list is null");
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Current domains: "
						+ ListUtils.allValuesToString(domains));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Addresses before clean up: "
					+ ListUtils.allValuesToString(addresses));
		}
		for (String address : addresses) {
			if (notCurrentServer(address, domains)) {
				result.add(address);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Addresses after clean up: "
					+ ListUtils.allValuesToString(result));
		}
		this.parsedAddresses = result;
		this.updateSingleLineEmails = true;
	}
	
	private static boolean notCurrentServer(final String address,
			final List<String> domains) {
		final String domain = getDomainFromSingleAddress(address);
		if (StringUtils.isBlank(domain)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Domain from address: " + address + " is blank");
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Domain from address: " + address + " is " + domain);
		}
		if (domains.contains(domain)) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("Contains in current domains list, returning false");
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger
					.debug("Do not contains in current domains list, returning true");
		}
		return true;
	}
	
	public boolean isEmpty(){
		if ((getParsedEmailAddresses() == null) || (getParsedEmailAddresses().isEmpty())) {
			return true;
		}
		return false;
	}
	
	public static String provideWithDescriptionIfNeeded( final String address, final String description2 ){
		if ( StringUtils.isBlank(address) || StringUtils.isBlank(description2) ) {
			return address;
		}
		String description = null;
		if (address.indexOf("<") != -1) {
			description = address.substring(0, address.indexOf('<'));
		} else if (address.indexOf('"') != -1) {
			int lastIndex = address.lastIndexOf('"');
			description = address.substring(0,lastIndex);
		}
		if ( description != null ) {
			description = description.replace('"', ' ');
		}
		if ( StringUtils.isNotBlank( description ) ) {
			return address;			
		}
		final String result = convertToNamingConventionDescription(description2) +
					"<" + parseSingleAddress( address ) + ">";
		return result;
	}
}
