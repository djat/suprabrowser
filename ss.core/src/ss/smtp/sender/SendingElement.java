/**
 * 
 */
package ss.smtp.sender;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;

import ss.client.ui.email.SendList;
import ss.smtp.SMailMessage;

/**
 * @author zobo
 * 
 */
public class SendingElement {
	
	public enum SendingElementMode {
		FORWARDED, CREATED, POSTPONED
	}
	
	private final SendingElementRecreateInfo recreateInfo;
	
	private final SMailMessage smessage;

	private final Session sendsession;

	private final SendList sendList;

	private final List<String> mxHosts;
	
	private final String messageId;
	
	private final String sphereId;
	
	private SendingElementMode mode;

	public SendingElement(final SMailMessage smessage,
			final Session sendsession, final SendList sendList,
			final List<String> mxHosts, final String messageId, final String sphereId, final SendingElementMode mode, final SendingElementRecreateInfo recreateInfo) {
		super();
		this.smessage = smessage;
		this.sendsession = sendsession;
		this.sendList = sendList;
		this.mxHosts = mxHosts;
		this.messageId = messageId;
		this.sphereId = sphereId;
		this.mode = mode;
		this.recreateInfo = recreateInfo;
	}

	/**
	 * @return the sendList
	 */
	public SendList getSendList() {
		return this.sendList;
	}

	/**
	 * @return the sendsession
	 */
	public Session getSendsession() {
		return this.sendsession;
	}

	/**
	 * @return the smessage
	 */
	public SMailMessage getSmessage() {
		return this.smessage;
	}

	/**
	 * @return
	 */
	public List<String> getMXHosts() {
		return this.mxHosts;
	}

	@Override
	public String toString() {
		try {
			return this.smessage.getReplyTo()[0].toString();
		} catch (MessagingException ex) {
			return "MessagingException";
		}
	}

	public String getMessageId() {
		return this.messageId;
	}

	public SendingElementMode getMode() {
		return this.mode;
	}

	public void setMode(SendingElementMode mode) {
		this.mode = mode;
	}

	public String getSphereId() {
		return this.sphereId;
	}

	public SendingElementRecreateInfo getRecreateInfo() {
		return this.recreateInfo;
	}
}
