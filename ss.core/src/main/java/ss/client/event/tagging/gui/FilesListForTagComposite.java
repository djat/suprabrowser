/**
 * 
 */
package ss.client.event.tagging.gui;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;
import org.eclipse.swt.widgets.Composite;

import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.client.event.tagging.obtainer.FileForKeywordObtainer;
import ss.client.event.tagging.obtainer.FileForKeywordObtainer.FilesForTag;
import ss.client.networking.protocol.getters.GetSpecificIdCommand;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.FileStatement;
import ss.server.networking.SC;


/**
 * @author zobo
 *
 */
public class FilesListForTagComposite extends AbstractListForTagComposite {

	public static final String TITLE = "Files";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FilesListForTagComposite.class);
	
	/**
	 * @param parent
	 * @param style
	 * @param obtainer
	 */
	public FilesListForTagComposite(Composite parent, int style,
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
		for (FilesForTag file : getObtainer().getList()) {
			items[i++] = file.getSubject();
		}
		return items;
	}

	public String getTitle() {
		return TITLE;
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.gui.AbstractListForTagComposite#load(int)
	 */
	@Override
	protected void load(int index) {
		final Document doc = getDocument( getObtainer().getList().get( index ) );
		if ( doc == null ) {
			logger.error("Could not obtain doc for: " + getObtainer().getList().get( index ).toString() );
			UserMessageDialogCreator.error( "Could not get Document" , "Error" );
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("loading file " + getObtainer().getList().get(index).getSubject());
		}
		final NewBinarySWT nb = new NewBinarySWT(SupraSphereFrame.INSTANCE.getMainMessagesPane().getMessagesTree().getUserSession().getImeplementationHashtable(),
					SupraSphereFrame.INSTANCE.getMainMessagesPane(), null,
				false);
		nb.fillDoc(FileStatement.wrap( doc ));
		nb.addFillButtons();
	}
	
	private Document getDocument( final FilesForTag fileForTag ) {
		final GetSpecificIdCommand command = new GetSpecificIdCommand();
		final Hashtable session = new Hashtable();
		session.put(SC.SPHERE_ID, fileForTag.getSphereId());
		command.putSessionArg(session);
		command.putArg(SC.MESSAGE_ID, fileForTag.getMessageId());
		final Document doc = command.execute(SupraSphereFrame.INSTANCE.client, AbstractDocument.class);
		return doc;
	}

	private FileForKeywordObtainer getObtainer(){
		return this.obtainer.getFiles();
	}
}
