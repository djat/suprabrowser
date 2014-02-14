/**
 * 
 */
package ss.client.ui.clubdealmanagement.contact;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.networking.protocol.actions.ChangeContactAction;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;

/**
 * @author zobo
 *
 */
public class ChangeUserContactName extends Dialog {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangeUserContactName.class);
	
	private ContactStatement contact = null;

	private Text newFirstName;

	private Text newLastName;

	public ChangeUserContactName(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label labelLogin = new Label(comp, SWT.CENTER);
		labelLogin.setText("New first name: ");
		labelLogin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		this.newFirstName = new Text( comp, SWT.SINGLE | SWT.BORDER );
		this.newFirstName.setText("");
		this.newFirstName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPassword = new Label(comp, SWT.CENTER);
		labelPassword.setText("New last name: ");
		labelPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		this.newLastName = new Text( comp, SWT.SINGLE | SWT.BORDER );
		this.newLastName.setText("");
		this.newLastName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		return comp;
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		final Rectangle parentBounds = getShell().getBounds();
		final Point currentSize = getInitialSize();
		int x = parentBounds.x + ( parentBounds.width / 2 - currentSize.x / 2 );
		int y = parentBounds.y + ( parentBounds.height / 2 - currentSize.y / 2 );
		return new Point( x, y );
	}

	@Override
	protected Point getInitialSize() {
		return new Point( 400, 140 );
	}

	@Override
	protected void okPressed() {
		final String newFirstName = getFirstName();
		final String newLastName = getLastName();
		if (StringUtils.isBlank( newFirstName )) {
			UserMessageDialogCreator.warning("New first name can not be blank", "Wrong data");
			return;
		}
		if (StringUtils.isBlank( newLastName )) {
			UserMessageDialogCreator.warning("New last name can not be blank", "Wrong data");
			return;
		}
		final String contactName = newFirstName.trim() + " " + newLastName.trim();
		if ( StringUtils.isNotBlank(SupraSphereFrame.INSTANCE.client.getVerifyAuth().getLoginForContact(contactName))) {
			UserMessageDialogCreator.warning("User already exists with such contact name: " + contactName, "Wrong data");
			return;
		}
		ChangeContactAction action = new ChangeContactAction();
		action.setOldContactName(getContact().getContactNameByFirstAndLastNames());
		action.setNewContactNameFirstName(newFirstName);
		action.setNewContactNameLastName(newLastName);
		action.beginExecute(SupraSphereFrame.INSTANCE.client);
		super.okPressed();
	}

	@Override
	public int open() {
		if ( getContact() == null ) {
			logger.error("Contact can not be null");
			throw new NullPointerException("Contact can not be null");
		}
		return super.open();
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Change user contact name");
	}

	public void setContact(ContactStatement contact) {
		this.contact = contact;
	}

	private ContactStatement getContact() {
		return this.contact;
	}

	private String getFirstName() {
		return this.newFirstName.getText();
	}

	private String getLastName() {
		return this.newLastName.getText();
	}
}
