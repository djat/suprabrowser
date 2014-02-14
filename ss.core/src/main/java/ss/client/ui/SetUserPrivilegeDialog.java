package ss.client.ui;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.models.userprivileges.SetUserPrivilegeDataModel;
import ss.common.privileges.Permission;
import ss.global.SSLogger;

public class SetUserPrivilegeDialog extends BaseDialog  {

	private final static String TITLE = "SETUSERPRIVILEGE.TITLE";
	private final static String OK = "SETUSERPRIVILEGE.OK";
	private final static String PRIVILEDGE = "SETUSERPRIVILEGE.PRIVILEDGE";
	private final static String USER_NAME = "SETUSERPRIVILEGE.USER_NAME";
	
    @SuppressWarnings("unused")
	private final static Logger logger = SSLogger.getLogger(SetUserPrivilegeDialog.class);    
    private final ResourceBundle bundle = LocalizationLinks.getBundle(this.getClass());
    
    private Combo cmbPrivilege;
   
    private SetUserPrivilegeDataModel dataModel; 
	/**
	 * Start dialog thread 
	 * @param parentShell parent shell 
	 */
    public void show(final Shell parentShell, SetUserPrivilegeDataModel dataModel ) {
    	this.dataModel = dataModel;
    	super.show( parentShell );	
    }

	/* (non-Javadoc)
	 * @see ss.client.ui.BaseDialog#initializeControls()
	 */
	@Override
	protected void initializeControls() {
		super.initializeControls();
		
		
		 GridLayout layout = new GridLayout();
		 layout.numColumns = 2;
		 this.getShell().setLayout(layout);
		 
		 Label lblUserNameInfo = new Label( this.getShell(), SWT.LEFT
				 | SWT.SINGLE );
		 lblUserNameInfo.setText( this.bundle.getString(USER_NAME) );
		 
		 Text txtUserName = new Text( this.getShell(), SWT.LEFT
				 | SWT.SINGLE );
		 GridData txtUserNameLayoutData = new GridData();
		 txtUserNameLayoutData.horizontalAlignment = GridData.FILL;
		 txtUserNameLayoutData.grabExcessHorizontalSpace = true;
		  txtUserName.setLayoutData(txtUserNameLayoutData);
		 txtUserName.setText( this.dataModel.getUserContactName() );

		 Label lblPrivilegeInfo = new Label( this.getShell(), SWT.LEFT
				 | SWT.SINGLE );
		 lblPrivilegeInfo.setText( this.bundle.getString(PRIVILEDGE) );
		 
		 this.cmbPrivilege = new Combo( this.getShell(), SWT.DROP_DOWN );
		 for (Permission privilege : this.dataModel.getAllAvaliableUserPermissions()) {
			 this.cmbPrivilege.add( privilege.getDisplayName() );
		 } 
		 this.cmbPrivilege.select( this.dataModel.getUserPermissionSelectionIndex() );
		 
		 GridData cmbPrivilegeLayoutData = new GridData();
		 cmbPrivilegeLayoutData.horizontalAlignment = GridData.FILL;
		 cmbPrivilegeLayoutData.grabExcessHorizontalSpace = true;
		 this.cmbPrivilege.setLayoutData(cmbPrivilegeLayoutData); 
		 
		 Button btnOk = new Button( this.getShell(), SWT.PUSH );
		 btnOk.setText( this.bundle.getString(OK) );
		 btnOk.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				SetUserPrivilegeDialog.this.saveUserPrivilege(); 
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected( e );				
			}
			 
		 });
		 		 
		
	}

	
	
	/* (non-Javadoc)
	 * @see ss.client.ui.BaseDialog#getStartUpTitle()
	 */
	@Override
	protected String getStartUpTitle() {
		return this.bundle.getString( TITLE );
	}

	private void saveUserPrivilege() {
		this.dataModel.saveUserPermission();
		int index = this.cmbPrivilege.getSelectionIndex();
		if ( index >= 0 ) {
			Permission userPrivilege = this.dataModel.getAllAvaliableUserPermissions().get( index );
			this.dataModel.setUserPermission( userPrivilege );
			this.dataModel.saveUserPermission();
		}		
		super.close();
	}
    
}
