/**
 * 
 */
package ss.common.file;

/**
 * @author zobo
 * 
 */
public class ParentStatementData {

	private String body;

	private String subject;

	public ParentStatementData(final String body, final String subject) {
		this.body = body;
		this.subject = subject;
	}

	public String getBody() {
		return this.body;
	}

	public String getSubject() {
		return this.subject;
	}
}
