/**
 * 
 */
package ss.client.ui.email;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.ExternalEmailStatement;
import ss.util.ImagesPaths;
import ss.util.TextQuoter;

/**
 * @author zobo
 * 
 */
public class ReplyEmailShell extends EmailCommonShell {

	private static final String RE_STRING = "RE: ";

	private static final String REPLY_EMAIL = "Reply Email";

	public ReplyEmailShell(SupraSphereFrame sF, EmailController controller) {
		super(sF, null, controller);
	}

	public ReplyEmailShell(SupraSphereFrame sF, ExternalEmailStatement email,
			EmailController controller) {
		super(sF, email, controller);
	}

	public ReplyEmailShell(SupraSphereFrame sF,
			ExternalEmailStatement originalEmail, EmailController controller,
			boolean addressable) {
		super(sF, originalEmail, controller, addressable);
	}

	@Override
	protected String getImagePath() {
		return ImagesPaths.EMAIL_REPLY_ICON;
	}

	@Override
	protected String getTitle() {
		return REPLY_EMAIL;
	}

	@Override
	protected void setSubjectFromEmail(ExternalEmailStatement email) {
		setSubjectText(RE_STRING + email.getSubject());
	}

	@Override
	protected void setBodyFromEmail(ExternalEmailStatement email) {
		if (email == null)
			return;
		String text = email.getGiver() + " wrote:\n";
		text += email.getOrigBody();
		setBodyText(text);
	}
	
	@Override
	public String getOrigBody() {
		if (getOriginalEmail() == null) {
			return "";
		}
		String text = getOriginalEmail().getGiver() + " wrote:\n";
		text += TextQuoter.INSTANCE.breakAndMakeQuoted(getOriginalEmail().getOrigBody());
		return text;
	}

	@Override
	protected boolean setFromFromEmail(ExternalEmailStatement email) {
		if (email != null) {
			setFromText(email.getReciever());
			return true;
		}
		return false;
	}

	@Override
	protected boolean setReplyToFromEmail(ExternalEmailStatement email) {
		if (email != null) {
			setReplyTo(email.getReciever());
			return true;
		}
		return false;
	}

	@Override
	protected void setSendToFromEmail(ExternalEmailStatement email) {
		if (email != null)
			setToText(email.getGiver());
	}

	@Override
	protected boolean getIsReply() {
		return true;
	}
}
