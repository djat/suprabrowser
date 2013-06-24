/**
 * 
 */
package ss.client.event.messagedeleters;

import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.widgets.Shell;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.ExtendedWarningDialogAdapter;
import ss.client.ui.widgets.warningdialogs.ExtendedWarningDialogListener;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public abstract class AbstractForAllSelectedDeleter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractForAllSelectedDeleter.class);
	
	protected MessagesPane mp;
	
	protected Shell parentShell;
	
	protected Statement statement;
	
	protected AllSelectedDeleteManager manager;
	
	protected final LinkedBlockingQueue<Object> locker = new LinkedBlockingQueue<Object>();
	
	public AbstractForAllSelectedDeleter(MessagesPane mp, AllSelectedDeleteManager manager, Statement statement) {
		this.mp = mp;
		this.parentShell = SupraSphereFrame.INSTANCE.getShell();
		this.statement = statement;
		this.manager = manager;
	}
	
	public void executeDeleting() {
		if (logger.isDebugEnabled()) {
			logger.debug("executeDeliting performed with doc: " + this.statement);
		}
		
		if(!canDeleteAll()) {
			UserMessageDialogCreator.warningDialogWithForAllButton(getParentShell(), getText(),
					getWarningDialogAdapter());
		} else {
			performDeleteAction(this.statement);
			putObjectInLocker();
		}
		takeFromLocker();
	}

	private void takeFromLocker() {
		try {
			this.locker.take();
		} catch (InterruptedException ex) {
			logger.error("error with locker", ex);
		}
	}
	
	
	/**
	 * @return
	 */
	protected abstract boolean canDeleteAll();

	/**
	 * @param statement
	 */
	protected abstract void performDeleteAction(Statement statement);

	private Shell getParentShell() {
		return this.parentShell;
	}
	
	private ExtendedWarningDialogListener getWarningDialogAdapter() {
		return new ExtendedWarningDialogAdapter() {
			@Override
			public void performOK() {
				if (logger.isDebugEnabled()) {
					logger.debug("YES pressed");
				}
				performDeleteAction(AbstractForAllSelectedDeleter.this.statement);
				putObjectInLocker();
			}

			@Override
			public void performNo() {
				if (logger.isDebugEnabled()) {
					logger.debug("NO pressed");
				}
				putObjectInLocker();
			}

			@Override
			public void performYesForAll() {
				if (logger.isDebugEnabled()) {
					logger.debug("Yes for all pressed");
				}
				performDeleteAction(AbstractForAllSelectedDeleter.this.statement);
				cancelAsking();
				putObjectInLocker();
			}

			@Override
			public void performCancel() {
				if (logger.isDebugEnabled()) {
					logger.debug("Cancel pressed");
				}
				cancelDeleting();
				putObjectInLocker();
			}

			@Override
			public void performNoForAll() {
				if (logger.isDebugEnabled()) {
					logger.debug("NO for all pressed");
				}
				cancelDeletingForAllSimilar();
				putObjectInLocker();
			}
		};
	}
	
	protected abstract void cancelAsking();
	
	protected abstract void cancelDeletingForAllSimilar();
	
	protected abstract void cancelDeleting();
	
	protected abstract String getText();

	private void putObjectInLocker() {
		try {
			AbstractForAllSelectedDeleter.this.locker.put(new Object());
		} catch (InterruptedException ex) {
			logger.error("error with locker", ex);
		}
	}
}
