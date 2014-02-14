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

import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;

/**
 * @author zobo
 *
 */
public class PromoteToUserDialog extends Dialog {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PromoteToUserDialog.class);
	
	private ContactStatement contact = null;
	
	private ContactToMemberConverter converter = null;

	private Text fieldLogin;

	private Text fieldPassword;
	
	/**
	 * @param parentShell
	 */
	public PromoteToUserDialog( final Shell parentShell ) {
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
		labelLogin.setText("Login: ");
		labelLogin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		this.fieldLogin = new Text( comp, SWT.SINGLE | SWT.BORDER );
		this.fieldLogin.setText("");
		this.fieldLogin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPassword = new Label(comp, SWT.CENTER);
		labelPassword.setText("Password: ");
		labelPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		this.fieldPassword = new Text( comp, SWT.SINGLE | SWT.BORDER );
		this.fieldPassword.setText("");
		this.fieldPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

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
		final String login = getLogin();
		final String pass = getPassword();
		if (StringUtils.isBlank( login )) {
			UserMessageDialogCreator.warning("Login can not be blank", "Wrong data");
			return;
		}
		if (StringUtils.isBlank( pass )) {
			UserMessageDialogCreator.warning("Password can not be blank", "Wrong data");
			return;
		}
		final String result = this.converter.promoteContactToUser( getContact(), login, pass );
		if ( result != null ) {
			UserMessageDialogCreator.warning(result, "Error occured");
			return;
		}
		super.okPressed();
	}

	@Override
	public int open() {
		if ( getContact() == null ) {
			logger.error("Contact can not be null");
			throw new NullPointerException("Contact can not be null");
		}
		if ( getConverter() == null ) {
			logger.error("Converter can not be null");
			throw new NullPointerException("Converter can not be null");
		}
		return super.open();
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Make member");
	}

	public void setContact(ContactStatement contact) {
		this.contact = contact;
	}

	private ContactStatement getContact() {
		return this.contact;
	}

	private ContactToMemberConverter getConverter() {
		return this.converter;
	}

	public void setConverter(ContactToMemberConverter converter) {
		this.converter = converter;
	}

	private String getLogin() {
		return this.fieldLogin.getText();
	}

	private String getPassword() {
		return this.fieldPassword.getText();
	}
}
