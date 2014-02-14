/**
 * 
 */
package ss.client.ui.email;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.mail.internet.InternetAddress;

/**
 * @author zobo
 *
 */
public class SendListBuilder {

	private final Hashtable<String, SendList> hostToSendList = new Hashtable<String, SendList>();
	
	public void addAddresses( List<InternetAddress> addresses ) {
		if ( addresses == null ) {
			return;
		}
		for(InternetAddress address : addresses ) {
			final String addressHost = SpherePossibleEmailsSet.getDomainFromSingleAddress(address.toString());
			SendList addressSendList = this.hostToSendList.get( addressHost );
			if ( addressSendList == null ) {
				addressSendList = new SendList( addressHost );
				this.hostToSendList.put( addressSendList.getHost(), addressSendList );
			}
			addressSendList.add( address );
		}
	}

	/**
	 * @return
	 */
	public Collection<SendList> getResult() {
		return this.hostToSendList.values();
	}
}
