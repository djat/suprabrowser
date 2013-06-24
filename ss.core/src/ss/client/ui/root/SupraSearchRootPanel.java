/**
 * 
 */
package ss.client.ui.root;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ss.client.event.supramenu.listeners.SupraSearchSelectionListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.LuceneSearchDialog;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.common.ThreadUtils;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
class SupraSearchRootPanel {
	
	private static ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_SUPRASEARCHROOTPANEL);
	
	private static final String DEFAULT_SUPRA_SEARCH_DIALOG_TOOLTIP = "SUPRASEARCHROOTPANEL.DEFAULT_SUPRA_SEARCH_DIALOG_TOOLTIP";

	private static final String SUPRA_SEARCH_DIALOG_TOOLTIP = "SUPRASEARCHROOTPANEL.SUPRA_SEARCH_DIALOG_TOOLTIP";

	private static final String SUPRA_SEARCH_DIALOG = "SUPRASEARCHROOTPANEL.SUPRA_SEARCH_DIALOG";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSearchRootPanel.class);
	
	private final Composite control;
	
	private Text textField;
	
	private static Image image;
	
	SupraSearchRootPanel(Composite parent){
		this.control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.control.setLayout(layout);
		initImages();
		createGUI(this.control);
		if (logger.isDebugEnabled()) {
			logger.debug("Supra Search fast panel created");
		}
	}
	
	void setLayoutData(final Object data){
		this.control.setLayoutData(data);
	}
	
	private void initImages(){
		try {
			image = new Image(SDisplay.display.get(), getClass()
				.getResource(ImagesPaths.SUPRASEARCH).openStream());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void createGUI(final Composite parent){
		GridData data;
		
		this.textField = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.SINGLE);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.textField.setLayoutData(data);
		this.textField.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				if (arg0.keyCode == 13) {
					doFastSearchAction();
				}
			}
		});
		
		final Button fastSearch = new Button(parent, SWT.PUSH);
		fastSearch.setImage(image);
		fastSearch.setToolTipText(bundle.getString(DEFAULT_SUPRA_SEARCH_DIALOG_TOOLTIP));
		data = new GridData(SWT.FILL, SWT.FILL, false, true);
		fastSearch.setLayoutData(data);
		fastSearch.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				doFastSearchAction();
			}
		});
		
		final Button extendedSearch = new Button(parent, SWT.PUSH);
		extendedSearch.setText(bundle.getString(SUPRA_SEARCH_DIALOG));
		extendedSearch.setToolTipText(bundle.getString(SUPRA_SEARCH_DIALOG_TOOLTIP));
		data = new GridData(SWT.FILL, SWT.FILL, false, true);
		extendedSearch.setLayoutData(data);		
		extendedSearch.addSelectionListener(new SupraSearchSelectionListener(SupraSphereFrame.INSTANCE));
	}
	
	private void doFastSearchAction(){
		if (logger.isDebugEnabled()) {
			logger.debug("Fast SupraSearch action performed");
		}
		final String queryText = this.textField.getText();
		Runnable runnable = new Runnable(){
			public void run() {
				LuceneSearchDialog.performDefaultSupraSearch(queryText, false);
			}
		};
		ThreadUtils.startDemon(runnable, "Default supraSearch request");
		this.textField.setText("");
	}
}