/**
 * 
 */
package ss.client.event.tagging.gui;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;
import org.eclipse.swt.widgets.Composite;

import ss.client.event.tagging.obtainer.ContactsForTagObtainer;
import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.client.event.tagging.obtainer.ContactsForTagObtainer.ContactForTag;
import ss.client.event.tagging.obtainer.FileForKeywordObtainer.FilesForTag;
import ss.client.networking.protocol.getters.GetSpecificIdCommand;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tree.MessagesTreeActionDispatcher;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.viewers.NewContact;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.FileStatement;
import ss.server.networking.SC;

/**
 * @author zobo
 *
 */
public class ContactsListForTagComposite extends AbstractListForTagComposite {

	public static final String TITLE = "Contacts";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ContactsListForTagComposite.class);
	/**
	 * @param parent
	 * @param style
	 * @param obtainer
	 */
	public ContactsListForTagComposite(Composite parent, int style,
			DataForTagObtainer obtainer) {
		super(parent, style, obtainer);
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.gui.AbstractListForTagComposite#getItemsData()
	 */
	@Override
	protected String[] getItemsData() {
		if ((getObtainer().getList() == null) || 
				getObtainer().getList().isEmpty()) {
			return null;
		}
		String[] items = new String[getObtainer().getList().size()];
		int i = 0;
		for (ContactForTag contact : getObtainer().getList()) {
			items[i++] = contact.getSubject();
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.gui.AbstractListForTagComposite#load(int)
	 */
	@Override
	protected void load(int index) {
		if (logger.isDebugEnabled()){ 
			logger.error("loading contact " + getObtainer().getList().get(index).getSubject());
		}
		final Document doc =  getDocument( getObtainer().getList().get( index ) );
		if ( doc == null ) {
			logger.error("Could not obtain doc for: " + getObtainer().getList().get( index ).toString() );
			UserMessageDialogCreator.error( "Could not get Document" , "Error" );
			return;
		}
		Thread t = new Thread() {
			public void run() {

				final NewContact newUI = new NewContact(
						SupraSphereFrame.INSTANCE.getMainMessagesPane()
								.getMessagesTree().getUserSession()
								.getImeplementationHashtable(),
								SupraSphereFrame.INSTANCE.getMainMessagesPane());

				newUI.fillDoc(doc);
				newUI.createViewToolBar();
				newUI.runEventLoop();

			}
		};
		t.start();
	}
	
	private Document getDocument( final ContactForTag contactForTag ) {
		final GetSpecificIdCommand command = new GetSpecificIdCommand();
		final Hashtable session = new Hashtable();
		session.put(SC.SPHERE_ID, contactForTag.getSphereId());
		command.putSessionArg(session);
		command.putArg(SC.MESSAGE_ID, contactForTag.getMessageId());
		final Document doc = command.execute(SupraSphereFrame.INSTANCE.client, AbstractDocument.class);
		return doc;
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.gui.ITitleContainer#getTitle()
	 */
	public String getTitle() {
		return TITLE;
	}
	
	private ContactsForTagObtainer getObtainer(){
		return this.obtainer.getContacts();
	}
}
