/**
 * 
 */
package ss.smtp.defaultforwarding;

/**
 * @author zobo
 *
 */
public class EmailBody {
	
	private String subject;
	
	private String body;

	/**
	 * @return the body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public EmailBody(String subject, String body) {
		super();
		this.subject = subject;
		this.body = body;
	}
}
