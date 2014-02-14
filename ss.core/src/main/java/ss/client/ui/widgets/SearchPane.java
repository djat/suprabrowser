
package ss.client.ui.widgets;

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.event.SearchPaneInputFieldModifyListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.AbstractDockingComponent;
import ss.client.ui.docking.PreviewAreaDocking;
import ss.client.ui.docking.SBrowserDocking;
import ss.common.UiUtils;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

public class SearchPane extends Composite {

	private static ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_WIDGETS_SEARCHPANE);
	
	private static final String FIND = "SEARCHPANE.FIND";
	private static final String NEXT = "SEARCHPANE.NEXT";
	private static final String CLOSE = "SEARCHPANE.CLOSE";
	private static final int SEARCH_PANE_HEIGHT = 34;
	
	private Image closeImage = null; 
	private Image findImage = null; 
	
	private Text inputField;
	private Button nextButton;
	private Button closeButton;
	private Label findLabel;
	
	private AbstractDockingComponent parentDocking;
	static Logger logger = SSLogger.getLogger(SearchPane.class);

	public SearchPane(PreviewAreaDocking docking) {
		super(docking.getMultiContainer(), SWT.BORDER );
		this.parentDocking = docking;
		((PreviewAreaDocking)this.parentDocking).getMultiContainer().addComposite(this);
		layoutComposite();
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SearchPane.this.parentDocking.getBrowser().unhighlightFindResult();	
			}
		});
	}
	
	public SearchPane(SBrowserDocking docking) {
		super(docking.getMultiContainer(), SWT.BORDER);
		this.parentDocking = docking;
		((SBrowserDocking)this.parentDocking).getMultiContainer().addComposite(this);
		layoutComposite();
	}

	private void layoutComposite() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		this.setLayout(layout);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.END;
		data.grabExcessHorizontalSpace = true;
		data.heightHint = SEARCH_PANE_HEIGHT;
		this.setLayoutData(data);

		createCloseItem();
		createFindLabel();
		createInputField();
		createNextButton();
		
		this.inputField.setFocus();
	}
	

	private void createCloseItem() {
		try {
			this.closeImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.CLOSE_ICON).openStream());
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
		this.closeButton = new Button(this, SWT.FLAT);
		this.closeButton.setToolTipText(bundle.getString(CLOSE));
		this.closeButton.setImage(this.closeImage);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		this.closeButton.setLayoutData(data);
		
		this.closeButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				getParentDocking().removeSearchPane();
				getParentDocking().getBrowser().unhighlightFindResult();
			}

			public void widgetDefaultSelected(SelectionEvent event) {

			}
		});
	}

	private void createInputField() {
		this.inputField = new Text(this, SWT.BORDER);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		this.inputField.setLayoutData(data);

		this.inputField.setEnabled(true);
		this.inputField.setEditable(true);
		
		this.inputField.addModifyListener(new SearchPaneInputFieldModifyListener(this));

	}

	private void createFindLabel() {
		this.findLabel = new Label(this, SWT.CENTER);
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		this.findLabel.setLayoutData(data);
		
		this.findLabel.setText(bundle.getString(FIND));
		this.findLabel.setEnabled(true);
	}

	public void createNextButton() {
		try {
			this.findImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.FIND_NEXT_ICON).openStream());
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
				
		this.nextButton = new Button(this, SWT.PUSH);
		this.nextButton.setToolTipText(bundle.getString(NEXT));
		this.nextButton.setImage(this.findImage);
		this.nextButton.setEnabled(false);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		this.nextButton.setLayoutData(data);
		
		this.nextButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						SupraBrowser browser = getParentDocking().getBrowser();
						if(browser==null || browser.isDisposed()) {
							return;
						}
						browser.findHighlightNext(getInputField().getText());
					}
				});	
			}

			public void widgetDefaultSelected(SelectionEvent se) {

			}
		});
	}

	public AbstractDockingComponent getParentDocking() {
		return this.parentDocking;
	}

	public Text getInputField() {
		return this.inputField;
	}

	public Button getFindNextButton() {
		return this.nextButton;
	}
}
