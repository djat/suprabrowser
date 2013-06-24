/**
 * 
 */
package ss.client.ui.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import ss.common.ListUtils;

/**
 * @author zobo
 *
 */
public class SendList {

	private final String host;
	
	private final List<Address> addresses = new ArrayList<Address>();
	
	public SendList(final String host) {
		super();
		this.host = host;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * @return
	 */
	public Address[] getAddresses() {
		return this.addresses.toArray( new Address[ this.addresses.size() ] );
	}
	

	/**
	 * @param address
	 */
	public void add(InternetAddress address) {
		this.addresses.add(address);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SendList { host: " + this.host + ", addresses " + ListUtils.valuesToString(this.addresses);
	}
	
	public String getSingleLineAddresses(){
		return ListUtils.valuesToString(this.addresses);
	}
	
}
