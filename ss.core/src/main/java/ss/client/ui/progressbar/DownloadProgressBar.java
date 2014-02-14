/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui.progressbar;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SDisplay;
import ss.common.UiUtils;
import ss.util.ImagesPaths;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DownloadProgressBar {

	private static final int BAR_DEFAULT_HEIGHT = 65;

	private static final int BAR_MIN_WIDTH = 250;
	
	private static final int BAR_MAX_WIDTH = 500;

	private static String LOADING_SPHERE_PREFIX = "DOWNLOADPROGRESSBAR.LOADING_SPHERE_PREFIX";
	
	private static String LOADING_BLANK_SPHERE_NAME = "DOWNLOADPROGRESSBAR.LOADING_BLANK_SPHERE_NAME";
	
	private static String LOADING_UNKNOWN = "DOWNLOADPROGRESSBAR.LOADING_UNKNOWN";
	
	private final static ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_DOWNLOADPROGRESSBAR);
	
	private Shell progressShell = null;

	private ProgressBar downloadBar = null;

	private String title = null;
	
	private Integer packetId = null;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DownloadProgressBar.class);

	private static final int WITH_OUT_OF_TITLE = 110;

	public DownloadProgressBar(int maximum) {
		createDownloadBar(maximum,  SWT.TOP);
	}

	public DownloadProgressBar() {
		createDownloadBar(-1, SWT.APPLICATION_MODAL | SWT.TOP);

	}

	public DownloadProgressBar(String title) {
		this.title = title;
		createDownloadBar(-1, SWT.APPLICATION_MODAL | SWT.TOP);
	}
	
	public DownloadProgressBar(String title, int style) {
		this.title = title;
		createDownloadBar(-1, style);
	}

	/**
	 * @param dataSize
	 * @param object
	 * @param b
	 */
	public DownloadProgressBar(final int maximum, final String sphereName) {
		if (sphereName != null){
			this.title = sphereName;
		} else {
			this.title = bundle.getString(LOADING_UNKNOWN);
		}
		createDownloadBar(maximum,  SWT.TOP);
	}
	
	public DownloadProgressBar(final int maximum, final String sphereName, final int packetId) {
		this.packetId = new Integer(packetId);
		if (sphereName != null){
			this.title = bundle.getString(LOADING_SPHERE_PREFIX) + " " + sphereName;
		} else {
			this.title = bundle.getString(LOADING_BLANK_SPHERE_NAME);
		}
		createDownloadBar(maximum,  SWT.TOP);
	}

	private void createDownloadBar(final int maximum, final int style) {

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				createDownloadBarBody(maximum, style);
			}
		});
	}

	private void createDownloadBarBody(final int maximum, final int style) {

		this.progressShell = new Shell(SDisplay.display.get(), style);
		this.progressShell.setLayout(new FillLayout());
		initIcons();

		if (this.title != null) {
			setBounds(); 
		} else {
			this.progressShell.setSize(BAR_MIN_WIDTH, BAR_DEFAULT_HEIGHT);
		}
		Monitor primary = SDisplay.display.get().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();

		Rectangle rect = this.progressShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		this.progressShell.setLocation(x, y);
		this.progressShell.forceActive();

		if (maximum < 0) {
			this.downloadBar = new ProgressBar(this.progressShell,
					SWT.INDETERMINATE);
		} else {
			this.downloadBar = new ProgressBar(this.progressShell, SWT.SMOOTH);
			this.downloadBar.setBounds(10, 10, 200, 32);
			this.downloadBar.setMaximum(maximum);
		}

		this.progressShell.layout();
		this.progressShell.setVisible(true);
	}

	/**
	 * 
	 */
	private void setBounds() {
		if (this.progressShell!= null){
			int width = Math.max(BAR_MIN_WIDTH, (this.title.length()*8 + WITH_OUT_OF_TITLE));
			width = Math.min(width, BAR_MAX_WIDTH);
			this.progressShell.setSize(width, BAR_DEFAULT_HEIGHT);
			this.progressShell.setText(this.title);
		}
	}
	
	private void initIcons() {
		try {
			Image image = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.DOWNLOAD_PROGRESS_BAR).openStream());
			this.progressShell.setImage(image);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	public void updateDownloadBar(final int level) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (DownloadProgressBar.this.downloadBar.isDisposed())
					return;
				DownloadProgressBar.this.downloadBar.setSelection(level);

			}
		});
	}

	public void destroyDownloadBar() {
		if (logger.isDebugEnabled()) {
			logger.debug("destroy Download Bar");
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				DownloadProgressBar.this.progressShell.dispose();
			}
		});
	}
	
	public void setTitle(final String title){
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				DownloadProgressBar.this.progressShell.setText(title);
			}
		});
	}

	/**
	 * @param sphereName
	 * @param packetId
	 * @return
	 */
	public boolean changeTitleNotify(String sphereName, int packetId) {
		if (this.packetId == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("PacketId is null");
			}
			return false;
		}
		if (this.packetId.intValue() == packetId){
			if (logger.isDebugEnabled()) {
				logger.debug("Match packetId, Changing title sphere to: " + sphereName);
			}			
			changeSphereNameInTitle(sphereName);
			return true;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("PacketId is different, Id from Bar: " + this.packetId.intValue() + ", and sphereName for packet: " + packetId);
			}	
			return false;
		}
	}
	
	private void changeSphereNameInTitle( final String sphereName ){
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				try {
				final Shell downloadProgressShell = DownloadProgressBar.this.progressShell;
				if (downloadProgressShell != null && !downloadProgressShell.isDisposed() ) {
					DownloadProgressBar.this.title = bundle.getString(LOADING_SPHERE_PREFIX) + " " + sphereName;
					setBounds();
					downloadProgressShell.setText(DownloadProgressBar.this.title);
				}
				} catch (Exception e){
					logger.error("Can't update download progress bar title", e);
				}
			}
		});
	}
}
