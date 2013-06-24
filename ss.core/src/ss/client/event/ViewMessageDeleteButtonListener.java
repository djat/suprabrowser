/**
 * 
 */
package ss.client.event;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.event.messagedeleters.SingleMessageDeleter;
import ss.client.ui.viewers.ViewMessageSWT;
import ss.global.SSLogger;

/**
 * @author roman
 * 
 */
public class ViewMessageDeleteButtonListener implements SelectionListener {

	ViewMessageSWT vm;

	private static final Logger logger = SSLogger
			.getLogger(ViewMessageDeleteButtonListener.class);

	public ViewMessageDeleteButtonListener(ViewMessageSWT vm) {
		this.vm = vm;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent arg0) {
		Thread t = new Thread() {
			ViewMessageSWT vm = ViewMessageDeleteButtonListener.this.vm;

			@SuppressWarnings("unchecked")
			public void run() {
				try {
					(new SingleMessageDeleter(this.vm.getMessagesPane(), true))
							.executeDeliting(this.vm.getViewDoc());
					/*
					 * UserMessageDialogCreator.warningDeleteMessage(this.vm.getBundle().getString(ViewMessageSWT
					 * .ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE) ,
					 * this.vm.getMessagesPane().client , this.vm.getSession() ,
					 * this.vm.getViewDoc());
					 */
				} catch (NullPointerException npe) {
					logger.error(npe.getMessage(), npe);
				}
			}
		};
		t.start();
	}

}
