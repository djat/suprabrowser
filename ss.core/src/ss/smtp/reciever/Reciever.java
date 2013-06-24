/**
 * 
 */
package ss.smtp.reciever;

import java.io.Serializable;

/**
 * @author zobo
 *
 */
public class Reciever implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8228136853822664463L;

	private String responseId;
	
	private String recipientsSphere;
	
	private String emailAdress;

	public Reciever(String recipientsSphere, String responseId, String emailAdress) {
		super();
		this.responseId = responseId;
		this.recipientsSphere = recipientsSphere;
		this.emailAdress = emailAdress;
	}
	
	public Reciever(){
	}

	/**
	 * @return the emailAdress
	 */
	public String getEmailAdress() {
		return this.emailAdress;
	}

	/**
	 * @param emailAdress the emailAdress to set
	 */
	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}

	/**
	 * @return the recipientsSphere
	 */
	public String getRecipientsSphere() {
		return this.recipientsSphere;
	}

	/**
	 * @param recipientsSphere the recipientsSphere to set
	 */
	public void setRecipientsSphere(String recipientsSphere) {
		this.recipientsSphere = recipientsSphere;
	}

	/**
	 * @return the responseId
	 */
	public String getResponseId() {
		return this.responseId;
	}

	/**
	 * @param responseId the responseId to set
	 */
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
}
