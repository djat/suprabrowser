/*
 * Created on Jan 11, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.smtp;

import javax.mail.internet.*;
import javax.mail.*;

public class SMailMessage extends MimeMessage {

	private String header = null;

	public SMailMessage(Session session) {
		super(session);
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void updateHeaders() throws MessagingException {
		super.updateHeaders();
		setHeader("Message-ID", this.header);
	}

}
