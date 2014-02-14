/**
 * 
 */
package ss.smtp.responcetosphere;

/**
 * @author zobo
 *
 */
public class ResponceStringInfo {
	
	public enum ResponceType {
		INFO, WARN, ERROR
	}
	
	private final ResponceType type;
	
	private final String subject;
	
	private final String body;

	public ResponceStringInfo(final ResponceType type, final String subject, final String body) {
		super();
		this.type = type;
		this.subject = subject;
		this.body = body;
	}

	public String getBody() {
		return this.body;
	}

	public String getSubject() {
		return this.subject;
	}

	public ResponceType getType() {
		return this.type;
	}
}
