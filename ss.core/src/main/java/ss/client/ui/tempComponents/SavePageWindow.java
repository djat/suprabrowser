/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.interfaces.ISphereListOwner;
import ss.client.ui.typeahead.TypeAheadManager;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.global.SSLogger;
import ss.rss.RSSParser;

/**
 * @author roman
 *
 */
public class SavePageWindow extends Dialog implements ISphereListOwner {

	private String address;
	
	private String desiredSubject;

	private Text subjectField;

	private String currentSphere;
	
	private SpheresCollectionByTypeObject sphereOwner;
	
	@SuppressWarnings("unused")
	private SphereListComponent sphereList;

	private Button saveButton;

	private Text tagField;
	
	private static final String SAVE = "SAVEPAGEWINDOW.SAVE";
	private static final String CANCEL = "SAVEPAGEWINDOW.CANCEL";
	private static final String ADDRESS = "SAVEPAGEWINDOW.ADDRESS";
	private static final String SUBJECT = "SAVEPAGEWINDOW.SUBJECT";
	private static final String NEW_BOOKMARK = "SAVEPAGEWINDOW.NEW_BOOKMARK";

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger
			.getLogger(SavePageWindow.class);

	private static ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_SAVEPAGEWINDOW);

	private SavePageWindow(final String address, final String subject, SpheresCollectionByTypeObject sphereOwner) {
		super((Shell)null);
		this.sphereOwner = sphereOwner;
		this.address = address;
		this.desiredSubject = subject;
	}

	@Override
	protected int getShellStyle() {
		return SWT.RESIZE | SWT.TITLE | SWT.CLOSE;
	}

	public String getAddress() {
		return this.address;
	}

	@Override
	protected Control createContents(Composite parent) {
		createTextFieldComposite(parent);
		createSphereListComposite(parent);
		createSaveButton(parent);
		setFocusToSubjectField();
		return parent;
	}

	/**
	 * @param parent
	 */
	private void createSaveButton(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		buttonComposite.setLayoutData(data);
		buttonComposite.setLayout(new GridLayout(2, true));

		this.saveButton = new Button(buttonComposite, SWT.PUSH);
		this.saveButton.setText(bundle.getString(SAVE));
		this.saveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				publishBookmark();
				getShell().dispose();
			}
		});
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		this.saveButton.setLayoutData(data);

		Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText(bundle.getString(CANCEL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getShell().dispose();
			}
		});
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		cancelButton.setLayoutData(data);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void publishBookmark() {
		Hashtable sessionCopy = (Hashtable) SupraSphereFrame.INSTANCE.client.session.clone();
		sessionCopy.put("sphere_id", getChoosedSphereId());
		logger.debug("choosed:"+this.currentSphere);
		logger.debug("choosed:"+getChoosedSphereId());
		
		CreateBookmarkAction.saveAsBookmark(getSubject(),
				getAddress(), getTag(), DeliveryFactory.INSTANCE.getDeliveryTypeNormal(getChoosedSphereId()), sessionCopy);
	}

	/**
	 * @return
	 */
	private String getTag() {		
		return this.tagField.getText();
	}

	private String getSubject() {
		return this.subjectField.getText();
	}

	/**
	 * @return
	 */
	private String getChoosedSphereId() {
		return SupraSphereFrame.INSTANCE.client.getVerifyAuth().getSystemName(this.currentSphere);
	}

	public void setFocusToSubjectField() {
		this.subjectField.setFocus();
	}

	private void createSphereListComposite(Composite parent) {
		this.sphereList = new SphereListComponent(parent, this);
		this.sphereList.addSphereListChangedListener(new SpheresListChangeListener() {
			public void sphereChanged(SphereChangeEvent event) {
				boolean hasSpheres = event.source.getSpheresCount()>0;
				SavePageWindow.this.saveButton.setEnabled(hasSpheres);
			}			
		});
	}

	/**
	 * @param parent
	 */
	private void createTextFieldComposite(Composite parent) {
		Composite textComposite = new Composite(parent, SWT.BORDER);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		textComposite.setLayoutData(data);

		textComposite.setLayout(new GridLayout(2, false));

		Label subjectLabel = new Label(textComposite, SWT.LEFT);
		subjectLabel.setText(bundle.getString(SUBJECT));
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.FILL;
		subjectLabel.setLayoutData(data);

		this.subjectField = new Text(textComposite, SWT.BORDER | SWT.SINGLE);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.subjectField.setLayoutData(data);
		this.subjectField.setText( StringUtils.isBlank( this.desiredSubject ) ? RSSParser.getTitleFromURL(this.address) : this.desiredSubject );
		
		Label tagLabel = new Label(textComposite, SWT.LEFT);
		tagLabel.setText("Tag: ");
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.FILL;
		tagLabel.setLayoutData(data);

		this.tagField = new Text(textComposite, SWT.BORDER | SWT.SINGLE);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.tagField.setLayoutData(data);
		TypeAheadManager.INSTANCE.addKeywordAutoComplete(this.tagField);


		Label addressLabel = new Label(textComposite, SWT.LEFT);
		addressLabel.setText(bundle.getString(ADDRESS));
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.FILL;
		addressLabel.setLayoutData(data);

		Label url = new Label(textComposite, SWT.LEFT);
		url.setText(this.address);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.FILL;
		url.setLayoutData(data);

	}

	public static void showDialog(final SupraBrowser browser, final SpheresCollectionByTypeObject sphereOwner) {
		final String address = UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				return browser.getUrl();
			}
		});
		showDialog( address, sphereOwner);
	}
	
	public static void showDialog( final String address, final SpheresCollectionByTypeObject sphereOwner ) {
		showDialog(address, null, sphereOwner);
	}
	
	public static void showDialog( final String address, final String subject, final SpheresCollectionByTypeObject sphereOwner ) {
		if ( sphereOwner == null ) {
			logger.error("sphereOwner is null");
			return;
		}
		if (StringUtils.isBlank( address )) {
			logger.error("address is blank");
			return;
		}
		final SavePageWindow spw = new SavePageWindow( address, subject, sphereOwner );
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				spw.open();
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setSize(640, 480);
		shell.setLayout(new GridLayout());
		shell.setText(bundle.getString(NEW_BOOKMARK));
	}

	/**
	 * @param text
	 */
	public void setCurrent(String text) {
		logger.debug("new current sphere: "+text);
		this.currentSphere = text;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.interfaces.ISphereListOwner#getSphereOwner()
	 */
	public SpheresCollectionByTypeObject getSphereOwner() {
		return this.sphereOwner;
	}
}
