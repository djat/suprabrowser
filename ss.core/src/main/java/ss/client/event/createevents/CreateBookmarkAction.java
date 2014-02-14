/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.event.SendCreateAction;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewWeblink;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.rss.RSSParser;
import ss.util.ImagesPaths;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class CreateBookmarkAction extends CreateAbstractAction {

	private static Image image; 

	public static final String BOOKMARK_TITLE = "Bookmark";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateBookmarkAction.class);

	private Hashtable session = null;
	/**
	 * 
	 */
	public CreateBookmarkAction(Hashtable session) {
		super();
		this.session = session;
		try {
			image = new Image(Display.getDefault(),getClass().getResource(
					ImagesPaths.BOOKMARK).openStream());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void performImpl() {
		final String sendText = SupraSphereFrame.INSTANCE.getSendText();
		final AbstractDelivery delivery = SupraSphereFrame.INSTANCE.getDefaultDelivery(this.session);

		final Hashtable send_session = (Hashtable) this.session.clone();
		
		final MessagesPane mp = (MessagesPane) SupraSphereFrame.INSTANCE.tabbedPane
		.getSelectedMessagesPane();
		if ( mp == null ) {
			logger.error( "Selected messages pane is null" );
			return;
		}

		final Document lastSelectedDoc = (mp.getLastSelectedDoc() == null) ? null : ((Document) mp.getLastSelectedDoc().clone());

		boolean isReply = SupraSphereFrame.INSTANCE.isReplyChecked(); 

		new NewWeblink(send_session, mp,
					lastSelectedDoc, delivery, isReply, sendText, null);
		super.performImpl();
	}

	public String getName() {
		return BOOKMARK_TITLE;
	}

	public Image getImage() {
		return image;
	}

	@SuppressWarnings("unchecked")
	public static void saveAsBookmark(final String desiredSubject, final String sendText, final String tagtext, AbstractDelivery delivery, Hashtable session) {
		String saveText = sendText;
		
		SendCreateAction.clearTextAndChecks(session);

		saveText = VariousUtils.convertToFullURL(saveText);

		final String text = saveText;

		final String url = text;

		Hashtable temp = (Hashtable) SupraSphereFrame.INSTANCE.getRegisteredSession(
				(String) session.get("supra_sphere"),
		"DialogsMainCli");
		String sessionId = (String) temp.get("session");
		session.put("session", sessionId);
		
		String subjectString = (desiredSubject!=null) ? desiredSubject : getDefaultSubject(text, url); 
		
		final NewWeblink nw = new NewWeblink(session, null, DeliveryFactory.INSTANCE.getDeliveryTypeByDeliveryClass(delivery.getClass()), url, subjectString, null);
		nw.setTagText(tagtext);
		nw.doPublishAction();
	}

	private static String getDefaultSubject(final String text, final String url) {
		String mainTitle = null;
		if (mainTitle == null) {
			mainTitle = RSSParser.getTitleFromURL(text);

		}

		final String finalMainTitle = mainTitle;
		if (finalMainTitle != null) {
			return finalMainTitle;
		} 
		return url;	
	}

}
