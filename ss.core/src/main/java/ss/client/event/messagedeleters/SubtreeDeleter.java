/**
 * 
 */
package ss.client.event.messagedeleters;

import java.util.List;
import java.util.ResourceBundle;

import org.dom4j.Document;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class SubtreeDeleter extends AbstractMessageDeleter{
	
	private ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_EVENT_MESSAGEDELETERS_SUBTREEDELETER);
	
	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_SUBTREE = "MESSAGESTREEMOUSELISTENER.ARE_YOU_SURE_YOU_WANT_TO_DELETE_SUBTREE";
	
	private final String text;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SubtreeDeleter.class);
	
	
	public SubtreeDeleter(MessagesPane mp) { 
		this(mp, null);
	}
	
	public SubtreeDeleter(MessagesPane mp, String prefferedText) { 
		super(mp);
		if(prefferedText != null) {
			this.text = prefferedText;
		} else {
			this.text = this.bundle.getString(ARE_YOU_SURE_YOU_WANT_TO_DELETE_SUBTREE); 
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.event.messagedeleters.AbstractMessageDeleter#performAction(java.lang.String)
	 */
	@Override
	protected void performAction(final Document notUsedDoc) {
		if (logger.isDebugEnabled()){
			logger.debug("Delete subtree of messages performed.");
		}
		
		List<Document> docsToRemove = getMp().getMessagesTree().getSelectedInvertedSubtree();
		
		String sphereId = getMp().getSphereId();
		
		for(Document doc : docsToRemove) {
			SupraSphereFrame.INSTANCE.client.recallMessage(getMp().getRawSession(), doc, sphereId);
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.event.messagedeleters.AbstractMessageDeleter#getText()
	 */
	@Override
	protected String getText() {
		return this.text;
	}
}
