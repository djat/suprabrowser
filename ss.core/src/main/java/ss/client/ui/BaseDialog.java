package ss.client.ui;

import java.awt.Dimension;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.common.ExceptionHandler;
import ss.common.UiUtils;

public class BaseDialog {

	private Display display;

	private Shell parentShell;

	private Shell shell;
	
	private static final String START_UP_TITLE = "BASEDIALOG.DIALOG";
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_BASEDIALOG);

	public BaseDialog() {
		super();
	}

	/**
	 * @return the display
	 */
	protected final Display getDisplay() {
		return this.display;
	}

	/**
	 * @return the parentShell
	 */
	protected final Shell getParentShell() {
		return this.parentShell;
	}

	/**
	 * @return the shell
	 */
	public final Shell getShell() {
		return this.shell;
	}

	/**
	 * Start dialog thread
	 * 
	 * @param parentShell
	 *            parent shell
	 */
	protected void show(final Shell parentShell) {
		try {
			this.display = Display.getDefault();
			this.parentShell = parentShell;
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					BaseDialog.this.dialogLoop();
				}
			});
		} catch (Exception e) {
			ExceptionHandler.handleException(this, e);
		} finally {
			// TODO: check if we require to dispose something
		}
	}
  
	public final void onTop() {
		this.shell.forceActive();
	}

	/**
	 * Dialog loop. Creates dialog and show it.
	 */
	private void dialogLoop() {
		try {

			this.shell = new Shell(BaseDialog.this.display,
					getStartUpDialogStyle());
			Dimension startUpSize = BaseDialog.this.getStartUpDialogSize();
			this.shell.setText(this.getStartUpTitle());
			this.shell.setSize(startUpSize.width, startUpSize.height);
			this.initializeControls();
			this.layoutDialog();
			if ( isOpenAtStartup() ) {
				this.shell.open();
				this.shell.setFocus();
			}
			if(!this.shell.isDisposed())
			{
				this.shell.setFocus();
			}
			while (this.shell != null && !this.shell.isDisposed()) {
				if (!this.display.readAndDispatch()) {
					this.display.sleep();
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(this, e);

		} finally {
			if (this.shell != null) {
				this.shell.dispose();
				this.shell = null;
			}
		}
	}

	/**
	 * @return start up dialog visibility
	 */
	protected boolean isOpenAtStartup() {
		return true;
	}

	/**
	 * Set dialog positon and size
	 */
	protected void layoutDialog() {
		Rectangle childBounds = this.shell.getBounds();
		Rectangle parentBounds = this.parentShell.getBounds();
		int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
		int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
		this.shell.setLocation(x, y);
		this.shell.layout();
	}

	/**
	 * @return start up dialog style
	 */
	protected int getStartUpDialogStyle() {
		return SWT.BORDER | SWT.TITLE | SWT.NORMAL | SWT.RESIZE | SWT.CLOSE;
	}

	/**
	 * @return start up dialog size
	 */
	protected Dimension getStartUpDialogSize() {
		return new Dimension(320, 200);
	}

	/**
	 * Returns dialog title that will be set at start up
	 * 
	 * @return
	 */
	protected String getStartUpTitle() {
		return this.bundle.getString(START_UP_TITLE);
	}

	/**
	 * Add controls to the dialog. Extended dialogs should override this
	 * function to locate controls.
	 */
	protected void initializeControls() {

	}

	/**
	 * Close the dialog
	 */
	public void close() {
		if (this.shell != null) {
			this.shell.close();
			this.shell.dispose();
		}
	}

}
