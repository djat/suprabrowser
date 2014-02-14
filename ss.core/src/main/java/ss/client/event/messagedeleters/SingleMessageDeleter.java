/**
 * 
 */
package ss.client.event.messagedeleters;

import java.util.ResourceBundle;

import org.dom4j.Document;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public class SingleMessageDeleter extends AbstractMessageDeleter {

	private ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_EVENT_MESSAGEDELETERS_SINGLEMESSAGEDELETER);

	private String text;
	
	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE = "MESSAGESTREEMOUSELISTENER.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SingleMessageDeleter.class);

	public SingleMessageDeleter(MessagesPane mp, boolean isCloseParentShell, String differentText, Shell parentShell) {
		super(mp);
		setCloseParent(isCloseParentShell);
		if (differentText == null){
			this.text = this.bundle.getString(ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE);
		} else {
			this.text = differentText;
		}
		logger.debug("text : "+this.text);
		if (parentShell != null){
			setParentShell(parentShell);
		}
	}
	
	public SingleMessageDeleter(MessagesPane mp, boolean isCloseParentShell) {
		this(mp, isCloseParentShell, null, null);
	}
	
	public SingleMessageDeleter(MessagesPane mp) {
		this(mp, false, null, null);
	}

	/* (non-Javadoc)
	 * @see ss.client.event.messagedeleters.AbstractMessageDeleter#performAction(java.lang.String)
	 */
	@Override
	protected void performAction(final Document doc) {
		if (logger.isDebugEnabled()){
			logger.debug("Delete single message performed.");
		}
		String sphereId = (String)getMp().getSphereId();
		getMp().client.recallMessage(getMp().getRawSession(), doc, sphereId);
	}

	@Override
	protected String getText() {
		return this.text;
	}
}
