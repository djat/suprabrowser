/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.workflow.PollDelivery;

/**
 * @author roman
 *
 */
public class PollConfigureDialog extends ConfigureDeliveryDialog {

	private PollDelivery poll;
	private Text percentText;
	
	/**
	 * @param editComposite
	 * @param typeDelivery
	 */
	public PollConfigureDialog(PollDelivery poll, EditDeliveryPreferencesComposite editComposite) {
		super(editComposite);
		this.poll = poll;
		this.oldName = poll.getDisplayName();
	}

	
	@Override
	protected Control createContents(Composite parent) {
		super.createContents(parent);
		createPercentText(parent, new Double(this.poll.getPercent()).toString());
		this.nameText.setEditable(true);
		return parent;
	}
	
	
	private void createPercentText(Composite parent, String startPercent) {
		Label label  = new Label(this.nameComp, SWT.LEFT);
		label.setText(bundle.getString(REQUIRED_PERCENTAGE));
		
		this.percentText = new Text(this.nameComp, SWT.SINGLE | SWT.BORDER);
		this.percentText.setText(startPercent);
		this.percentText.setLayoutData(new GridData(GridData.BEGINNING));
		this.percentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.percentText.setTextLimit(5);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		shell.setText(bundle.getString(CONFIGURE_DIALOG));
		shell.setLayout(new GridLayout());
		shell.setSize(480, 160);
	}
	
	@Override
	protected boolean saveNewDeliveryName() {
		if(this.nameText.getText()!=null && this.editComposite.checkAlreadyExist(this.nameText.getText(), this.poll)) {
			UserMessageDialogCreator.error(bundle.getString(THIS_DELIVERY_NAME_IS_ALREADY_IN_USE));
		} else if(this.nameText.getText()!=null && !this.nameText.getText().trim().equals("")) {
			this.poll.setDisplayName(this.nameText.getText());
			return true;
		} else {
			UserMessageDialogCreator.error(bundle.getString(INVALID_DELIVERY_NAME));
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.preferences.delivery.ConfigureDeliveryDialog#saveNewPercentValue()
	 */
	@Override
	protected boolean saveNewPercentValue() {
		try {
			double newPercent = Double.parseDouble(this.percentText.getText());
			if(newPercent>=0 && newPercent<=100) {
				this.poll.setPercent(newPercent);
				return true;
			}
		} catch (Exception ex) {
			UserMessageDialogCreator.error(bundle.getString(INVALID_PERCENTAGE));
			return false;
		}
		UserMessageDialogCreator.error(bundle.getString(INVALID_PERCENTAGE));
		return false;
	}
}
