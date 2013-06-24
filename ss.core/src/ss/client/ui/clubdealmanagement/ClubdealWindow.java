/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubdealWindow extends ApplicationWindow {

	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_CLUBDEALWINDOW);
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ClubdealWindow.class);
	
	private ClubdealFolder folder;
	
	private final ClubdealManager manager;
	
	private final ChangesDetector detector;
	
	private final DialogsMainCli client;
	
	private static final String CLUBDEAL_WINDOW = "CLUBDEALWINDOW.CLUBDEAL_WINDOW";
	
	/**
	 * @param parentShell
	 */
	public ClubdealWindow(final ClubdealManager manager, final DialogsMainCli client) {
		super(Display.getDefault().getActiveShell());
		this.manager = manager;
		this.client = client;
		this.detector = new ChangesDetector();
	}
	
	public ClubdealWindow(final ClubdealManager manager) {
		this(manager, SupraSphereFrame.INSTANCE.client);
	}

	@Override
	protected void configureShell(final Shell shell) {
		shell.setSize(640, 480);
//		int monitorHeight = SDisplay.display.get().getPrimaryMonitor().getBounds().height;
//		int monitorWidth = SDisplay.display.get().getPrimaryMonitor().getBounds().width;
//		shell.setLocation(monitorWidth/2-320, monitorHeight/2-240);
		shell.setText(bundle.getString(CLUBDEAL_WINDOW));
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		this.folder = new ClubdealFolder(this);
		this.folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final Button cancel = new Button( parent, SWT.PUSH );
		cancel.setText( "Cancel" );
		cancel.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
		cancel.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				safeClose();
			}
			
		});
		parent.layout();
		return parent;
	}

	@Override
	protected ShellListener getShellListener() {
		return new ShellAdapter(){
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = false;
				safeClose();
			}
		};
	}
	
	public void safeClose() {
		if(getChangesDetector().hasChanges()) {
			Dialog dialog = new SavePromptDialog(this);
			dialog.setBlockOnOpen(true);
			dialog.open();
		} else {
			close();
		}	
	}
	
	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MIN;
	}
	
	public ClubdealManager getManager() {
		return this.manager;
	}
	
	public ClubdealFolder getFolder() {
		return this.folder;
	}
	
	public ChangesDetector getChangesDetector() {
		return this.detector;
	}

	public DialogsMainCli getClient() {
		return this.client;
	}
	
	public void refresh() {
		for(CTabItem item : this.folder.getItems()) {
			((AbstractClubdealItem)item).refresh();
		}
	}
}
