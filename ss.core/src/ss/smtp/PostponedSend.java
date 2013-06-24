/**
 * 
 */
package ss.smtp;

import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;

/**
 * @author zobo
 * 
 */
public class PostponedSend {

	private String uniqueId;

	private EmailAddressesContainer addressesContainer;

	private AttachedFileCollection attachedFiles;

	private String subject;

	private String replySphere;

	private StringBuffer text;

	/**
	 * @return the addressesContainer
	 */
	public EmailAddressesContainer getAddressesContainer() {
		return this.addressesContainer;
	}

	/**
	 * @param addressesContainer
	 *            the addressesContainer to set
	 */
	public void setAddressesContainer(EmailAddressesContainer addressesContainer) {
		this.addressesContainer = addressesContainer;
	}

	/**
	 * @return the files
	 */
	public AttachedFileCollection getAttachedFiles() {
		return this.attachedFiles;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setAttachedFiles(AttachedFileCollection files) {
		this.attachedFiles = files;
	}

	/**
	 * @return the replySphere
	 */
	public String getReplySphere() {
		return this.replySphere;
	}

	/**
	 * @param replySphere
	 *            the replySphere to set
	 */
	public void setReplySphere(String replySphere) {
		this.replySphere = replySphere;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the text
	 */
	public StringBuffer getText() {
		return this.text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(StringBuffer text) {
		this.text = text;
	}

	/**
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		return this.uniqueId;
	}

	/**
	 * @param uniqueId
	 *            the uniqueId to set
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

}
