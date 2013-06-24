/**
 * 
 */
package ss.client.ui.email;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.FileStatement;
import ss.util.ImagesPaths;
import ss.util.TextQuoter;

/**
 * @author zobo
 * 
 */
public class ForwardEmailShell extends EmailCommonShell {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForwardEmailShell.class);

	private static final String LINE_DIVIDER = "<br>";

	private static final String FWD_STRING = "Fwd: ";

	private static final String FORWARD_EMAIL = "Forward Email";
	
	/**
	 * @param sF
	 * @param originalEmail
	 */
	public ForwardEmailShell(SupraSphereFrame sF,
			ExternalEmailStatement originalEmail, EmailController controller) {
		super(sF, originalEmail, controller);
		setForwardingFiles();
	}

	public ForwardEmailShell(SupraSphereFrame sF, EmailController controller) {
		super(sF, null, controller);
		setForwardingFiles();
	}

	public ForwardEmailShell(SupraSphereFrame sF,
			ExternalEmailStatement originalEmail, EmailController controller,
			boolean addressable) {
		super(sF, originalEmail, controller, addressable);
		setForwardingFiles();
	}

	/**
	 * 
	 */
	private void setForwardingFiles() {
		Runnable runnable = new Runnable() {
			public void run() {
				String messageId = getOriginalEmail().getMessageId();
				String sphereId = getOriginalEmail().getCurrentSphere();
				Vector<Document> fileDocs = SupraSphereFrame.INSTANCE.client.getAttachments(sphereId, messageId);
				for(Document fileDoc : fileDocs) {
					FileStatement file = FileStatement.wrap(fileDoc);
					getAttachFileComponent().attachNewForwardingFile(file);
				}
			}
		};
		runnable.run();
	}

	@Override
	protected String getImagePath() {
		return ImagesPaths.EMAIL_FORWARD_ICON;
	}

	@Override
	protected String getTitle() {
		return FORWARD_EMAIL;
	}

	@Override
	protected void setSendToFromEmail(ExternalEmailStatement email) {
	}

	@Override
	protected void setSubjectFromEmail(ExternalEmailStatement email) {
		setSubjectText(FWD_STRING + email.getSubject());
	}

	@Override
	protected void setBodyFromEmail(ExternalEmailStatement email) {
		if (email == null)
			return;
		String text = "---------- Forwarded message ----------" + LINE_DIVIDER;
		text += "From: " + email.getGiver() + LINE_DIVIDER;
		text += "Date: " + email.getMoment() + LINE_DIVIDER;
		text += "Subject: " + email.getSubject() + LINE_DIVIDER;
		text += "To: " + email.getReciever() + LINE_DIVIDER + LINE_DIVIDER;
		text += email.getBody();
		setTextToEditor(text);
	}

	@Override
	protected boolean getIsReply() {
		return true;
	}

	
	@Override
	public String getOrigBody() {
		String text = "---------- Forwarded message ----------" + LINE_DIVIDER;
		text += "From: " + getOriginalEmail().getGiver() + LINE_DIVIDER;
		text += "Date: " + getOriginalEmail().getMoment() + LINE_DIVIDER;
		text += "Subject: " + getOriginalEmail().getSubject() + LINE_DIVIDER;
		text += "To: " + getOriginalEmail().getReciever() + LINE_DIVIDER + LINE_DIVIDER;
		
		text += TextQuoter.INSTANCE.breakAndMakeQuoted(getOriginalEmail().getOrigBody());
		
		return text;
	}
}
