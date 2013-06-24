/**
 * 
 */
package ss.smtp.sender;

import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.smtp.defaultforwarding.EmailBody;

/**
 * @author zobo
 *
 */
public class SendingElementRecreateInfo {

	private final EmailAddressesContainer addressesContainer;
	
	private final AttachedFileCollection files;
	
	private final EmailBody emailBody;
	
	private final String header;

	/**
	 * @param addressesContainer
	 * @param files
	 * @param emailBody
	 * @param header
	 */
	public SendingElementRecreateInfo(EmailAddressesContainer addressesContainer, AttachedFileCollection files, EmailBody emailBody, String header) {
		this.addressesContainer = addressesContainer;
		this.files = files;
		this.emailBody = emailBody;
		this.header = header;
	}

	public EmailAddressesContainer getAddressesContainer() {
		return this.addressesContainer;
	}

	public EmailBody getEmailBody() {
		return this.emailBody;
	}

	public AttachedFileCollection getFiles() {
		return this.files;
	}

	public String getHeader() {
		return this.header;
	}

}
