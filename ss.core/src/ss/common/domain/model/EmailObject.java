/**
 * 
 */
package ss.common.domain.model;


/**
 * @author roman
 *
 */
public class EmailObject extends DomainObject {

	private String bcc;
	
	private String cc;
	
	private boolean input;
	
	private String receiver;

	/**
	 * @return the bcc
	 */
	public String getBcc() {
		return this.bcc;
	}

	/**
	 * @param bcc the bcc to set
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return the cc
	 */
	public String getCc() {
		return this.cc;
	}

	/**
	 * @param cc the cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * @return the input
	 */
	public boolean isInput() {
		return this.input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(boolean input) {
		this.input = input;
	}

	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return this.receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	
}
