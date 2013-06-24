/**
 * 
 */
package ss.client.ui.preferences.delivery;


import java.util.ResourceBundle;

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

import ss.client.localization.LocalizationLinks;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ConfigureDeliveryDialog extends Dialog {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger
			.getLogger(ConfigureDeliveryDialog.class);

	protected EditDeliveryPreferencesComposite editComposite;

	protected Composite buttonPane;

	protected Composite nameComp;

	protected Text nameText;

	protected String oldName;

	protected static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_DELIVERY_CONFIGUREDELIVERYDIALOG);

	protected static final String REQUIRED_PERCENTAGE = "CONFIGUREDELIVERYDIALOG.REQUIRED_PERCENTAGE";

	protected static final String CONFIGURE_DIALOG = "CONFIGUREDELIVERYDIALOG.CONFIGURE_DIALOG";

	protected static final String INVALID_DELIVERY_NAME = "CONFIGUREDELIVERYDIALOG.INVALID_DELIVERY_NAME";

	protected static final String THIS_DELIVERY_NAME_IS_ALREADY_IN_USE = "CONFIGUREDELIVERYDIALOG.THIS_NAME_IS_ALREADY_IN_USE";

	protected static final String INVALID_PERCENTAGE = "CONFIGUREDELIVERYDIALOG.INVALID_PERCENTAGE";

	protected static final String CONTACT_NAME = "CONFIGUREDELIVERYDIALOG.CONTACT_NAME";

	protected static final String ROLE = "CONFIGUREDELIVERYDIALOG.ROLE";

	protected static final String DELIVERY_NAME = "CONFIGUREDELIVERYDIALOG.DELIVERY_NAME";

	protected static final String SAVE = "CONFIGUREDELIVERYDIALOG.SAVE";

	protected static final String CANCEL = "CONFIGUREDELIVERYDIALOG.CANCEL";

	/**
	 * @param arg0
	 */
	protected ConfigureDeliveryDialog(
			EditDeliveryPreferencesComposite editComposite) {
		super(editComposite.getShell());
		this.editComposite = editComposite;
	}

	@Override
	protected Control createContents(Composite parent) {
		createNameText(parent);
		createButtonPane(parent);
		return parent;
	}

	/**
	 * 
	 */
	private void createNameText(Composite parent) {
		this.nameComp = new Composite(parent, SWT.NONE);
		this.nameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.nameComp.setLayout(new GridLayout(2, false));

		Label label = new Label(this.nameComp, SWT.LEFT);
		label.setText(bundle.getString(DELIVERY_NAME));

		this.nameText = new Text(this.nameComp, SWT.SINGLE | SWT.BORDER);
		this.nameText.setText(this.editComposite.getSelectedName());
		this.nameText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.nameText.setEditable(false);
	}

	private void createButtonPane(Composite parent) {
		this.buttonPane = new Composite(parent, SWT.NONE);
		this.buttonPane.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.buttonPane.setLayout(new GridLayout(2, false));

		Button saveButton = new Button(this.buttonPane, SWT.PUSH);
		saveButton.setText(bundle.getString(SAVE));
		saveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				savePressed();
			}
		});

		Button cancelButton = new Button(this.buttonPane, SWT.PUSH);
		cancelButton.setText(bundle.getString(CANCEL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				cancelPressed();
			}
		});
	}

	protected void cancelPressed() {
		dispose();
	}

	private void savePressed() {
		boolean isSavedName = saveNewDeliveryName();
		boolean isSavedPercent = saveNewPercentValue();
		if (isSavedName && isSavedPercent) {
			this.editComposite.refreshTableNames();
			this.editComposite.refreshDefaultDeliveryCombo(this.oldName,
					this.nameText.getText());
			this.editComposite.getApplyButton().setEnabled(true);
			this.editComposite.getDetector().setChanged(true);
			dispose();
		}
	}

	/**
	 * @return
	 */
	protected boolean saveNewPercentValue() {
		return true;
	}

	public void dispose() {
		getShell().dispose();
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setText(bundle.getString(CONFIGURE_DIALOG));
		shell.setLayout(new GridLayout());
		shell.setSize(480, 120);
	}

	public static void showDialog(EditDeliveryPreferencesComposite editComposite) {
		AbstractDelivery delivery = editComposite.getSelectedDelivery();
		if (delivery != null) {
			ConfigureDeliveryDialog dialog = delivery
					.createConfigurationDialog(editComposite);
			if (dialog != null) {
				dialog.open();
				dialog.setBlockOnOpen(true);
			}
		}
	}

	protected boolean saveNewDeliveryName() {
		return true;
	}
}
