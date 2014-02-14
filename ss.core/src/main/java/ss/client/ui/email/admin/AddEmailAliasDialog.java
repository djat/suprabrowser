/**
 * 
 */
package ss.client.ui.email.admin;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.BaseDialog;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.domainmodel.configuration.DomainProvider;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class AddEmailAliasDialog extends BaseDialog {

	/**
	 * 
	 */
	private static final String ADD_EMAIL_ALIAS = "ADDEMAILALIASDIALOG.ADD_EMAIL_ALIAS";

	private static final String ENTER_NEW_ALIAS_FOR_SPHERE = "ADDEMAILALIASDIALOG.ENTER_NEW_ALIAS_FOR_SPHERE";
	
	private static final String ENTER_NEW_DESCRIPTION_FROM = "ADDEMAILALIASDIALOG.ENTER_NEW_DESCRIPTION_FROM";
	
	private static final String DOMAIN_FOR_ALIAS = "ADDEMAILALIASDIALOG.DOMAIN_FOR_ALIAS";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AddEmailAliasDialog.class);

	private Button ok;

	private Button cancel;

	private EmailAliasesComposite parentDialog;

	private Text newAliasText;
	
	private Text newAliasDescriptionText;

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_EMAIL_ADMIN_ADDEMAILALIASDIALOG);

	private Combo domainsList;

	public AddEmailAliasDialog(EmailAliasesComposite parentDialog) {
		this.parentDialog = parentDialog;
	}

	@Override
	protected void initializeControls() {
		super.initializeControls();

		initIcons();

		GridLayout gridLayout = new GridLayout(2, false);
		GridData data;
		gridLayout.numColumns = 2;
		getShell().setLayout(gridLayout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		Label label;
		label = new Label(getShell(), SWT.LEFT);
		label.setText(this.bundle.getString(ENTER_NEW_ALIAS_FOR_SPHERE));
		label.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 2;
		this.newAliasText = new Text(getShell(), SWT.LEFT | SWT.SINGLE
				| SWT.BORDER);
		this.newAliasText.setText("");
		this.newAliasText.setLayoutData(data);
		
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 1;
		label = new Label(getShell(), SWT.LEFT);
		label.setText(this.bundle.getString(DOMAIN_FOR_ALIAS));
		label.setLayoutData(data);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 1;
		this.domainsList = new Combo(getShell(), SWT.DROP_DOWN | SWT.READ_ONLY);
		this.domainsList.setItems( DomainProvider.getDomains() );
		this.domainsList.select(0);
		this.domainsList.setLayoutData(data);
		
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		label = new Label(getShell(), SWT.LEFT);
		label.setText(this.bundle.getString(ENTER_NEW_DESCRIPTION_FROM));
		label.setLayoutData(data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		this.newAliasDescriptionText = new Text(getShell(), SWT.LEFT | SWT.SINGLE
				| SWT.BORDER);
		this.newAliasDescriptionText.setText("");
		this.newAliasDescriptionText.setLayoutData(data);

		Composite toolBar = new Composite(getShell(), SWT.NONE);
		toolBar.setLayout(new GridLayout(2, false));

		this.ok = new Button(toolBar, SWT.PUSH);
		this.ok.setText("OK");
		addOkActionListener();

		this.cancel = new Button(toolBar, SWT.PUSH);
		this.cancel.setText("Cancel");
		addCancelActionListener();

		data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.horizontalSpan = 4;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		toolBar.setLayoutData(data);
	}

	@Override
	protected Dimension getStartUpDialogSize() {
		return new Dimension(400, 215);
	}

	@Override
	protected String getStartUpTitle() {
		return this.bundle.getString(ADD_EMAIL_ALIAS);
	}

	private void addCancelActionListener() {

		this.cancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				AddEmailAliasDialog.this.close();
			}
		});
	}

	private void addOkActionListener() {

		this.ok.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				okPerformed();
			}
		});
	}

	protected void okPerformed() {
		String alias = this.newAliasText.getText();
		if ( StringUtils.isBlank(alias) ) {
			UserMessageDialogCreator.error("Cannot add blank alias");
			return;
		}
		if (!this.parentDialog.addAliases(this.newAliasText.getText(), this.newAliasDescriptionText.getText(), this.domainsList.getText())) {
			return;
		}
		this.parentDialog.getDetector().setChanged(true);
		close();
	}

	private void initIcons() {
		try {
			Image image = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_IN_ICON).openStream());
			getShell().setImage(image);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void show(final Shell parentShell) {
		super.show(parentShell);
	}
}
