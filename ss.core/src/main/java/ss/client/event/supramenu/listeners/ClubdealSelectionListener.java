/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.clubdealmanagement.ClubdealManager;
import ss.client.ui.clubdealmanagement.ClubdealWindow;
import ss.client.ui.progressbar.DownloadProgressBar;
import ss.common.ThreadUtils;
import ss.common.UiUtils;

/**
 * @author roman
 *
 */
public class ClubdealSelectionListener extends SelectionAdapter {

	//private final static Logger logger = SSLogger.getLogger(ClubdealSelectionListener.class);
	
	private DownloadProgressBar bar = null;
	
	private ClubdealManager manager = null;
	
	public ClubdealSelectionListener() {
		
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		Runnable runnable = new Runnable() {
			public void run() {
				setUpHelper();
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						ClubdealWindow cw = new ClubdealWindow(ClubdealSelectionListener.this.manager);
						cw.setBlockOnOpen(true);
						cw.open();
					}
				});
			}
		};
		ThreadUtils.start(runnable);
	}
	
	private void setUpHelper() {
		if(SupraSphereFrame.INSTANCE!=null) {
			this.bar = new DownloadProgressBar("Loading Contact Management", SWT.TOP);
		}
		this.manager = new ClubdealManager();
		this.manager.setUp();
		this.bar.destroyDownloadBar();
	}
}
