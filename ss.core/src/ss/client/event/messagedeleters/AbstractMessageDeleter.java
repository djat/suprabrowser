/**
 * 
 */
package ss.client.event.messagedeleters;

import org.dom4j.Document;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogAdapter;

/**
 * @author zobo
 * 
 */
public abstract class AbstractMessageDeleter {

	private final MessagesPane mp;

	private boolean closeParent = false;

	private Shell parentShell;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractMessageDeleter.class);

	AbstractMessageDeleter(final MessagesPane mp) {
		this.mp = mp;
		this.parentShell = SupraSphereFrame.INSTANCE.getShell();
	}
	
	public final void executeDeliting(final Document doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("executeDeliting performed with doc: " + doc.asXML());
		}

		UserMessageDialogCreator.warningDeleteMessage(getParentShell(), getText(),
					new WarningDialogAdapter() {
				@Override
				public void performOK() {
					if (logger.isDebugEnabled()) {
						logger.debug("OK pressed");
					}
					performAction(doc);
				}

				@Override
				public void performCancel() {
				}


			}, AbstractMessageDeleter.this.closeParent);
	}

	protected abstract void performAction(Document doc);

	protected abstract String getText();

	protected final MessagesPane getMp() {
		return this.mp;
	}

	protected void setCloseParent(boolean closeParent) {
		this.closeParent = closeParent;
	}
	
	private Shell getParentShell(){
		return this.parentShell;
	}

	protected void setParentShell(Shell parentShell) {
		this.parentShell = parentShell;
	}
}
