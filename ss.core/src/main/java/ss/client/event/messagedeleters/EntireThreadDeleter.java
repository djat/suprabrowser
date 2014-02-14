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
public class EntireThreadDeleter extends AbstractMessageDeleter{
	
	private ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_EVENT_MESSAGEDELETERS_THREADDELETER);

	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_THREAD = "MESSAGESTREEMOUSELISTENER.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THREAD";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EntireThreadDeleter.class);
	
	public EntireThreadDeleter(MessagesPane mp) { 
		super(mp);
	}
	
	/* (non-Javadoc)
	 * @see ss.client.event.messagedeleters.AbstractMessageDeleter#performAction(java.lang.String)
	 */
	@Override
	protected void performAction(final Document selectedDoc) {
		if (logger.isDebugEnabled()){
			logger.debug("Delete entire thread performed.");
		}
		
		List<Document> docsToRemove = getMp().getMessagesTree().getSelectedThreadDocsInverted();
		
		String sphereId = (String)getMp().getSphereId();
		
		for(Document doc : docsToRemove) {
			SupraSphereFrame.INSTANCE.client.recallMessage(getMp().getRawSession(), doc, sphereId);
		}
	}
	
	/* (non-Javadoc)
	 * @see ss.client.event.messagedeleters.AbstractMessageDeleter#getText()
	 */
	@Override
	protected String getText() {
		return this.bundle.getString(ARE_YOU_SURE_YOU_WANT_TO_DELETE_THREAD);
	}
}
