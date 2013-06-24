/**
 * 
 */
package ss.client.ui.clubdealmanagement.admin;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.getters.UpdateAdminsCommand;
import ss.client.ui.clubdealmanagement.ClubdealWindow;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.common.StringUtils;
import ss.domainmodel.SupraSphereMember;

/**
 * @author zobo
 *
 */
public class AdminsComposite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AdminsComposite.class);
	
	private final Composite comp;
	
	private final DialogsMainCli cli;

	private UserAdminList input = null;
	
	private final ClubdealWindow window;
	
	public AdminsComposite( final Composite parent, final DialogsMainCli cli, final ClubdealWindow window ){
		this.cli = cli;
		this.window = window;
		this.comp = new Composite(parent, SWT.NONE);
		this.comp.setLayout(new GridLayout());
		createContents( this.comp );
	}
	
	private void createContents( final Composite parent ){
		showFacade( parent );
	}
	
	private void showFacade( final Composite parent ){
		final Button enterButton = new Button( parent, SWT.PUSH );
		enterButton.setText("Manage Admins");
		enterButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		enterButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				UserMessageDialogCreator.warningYesCancelButton("Open admins management?", new WarningDialogListener(){

					public void performCancel() {
						
					}

					public void performOK() {
						enterButton.dispose();
						AdminsComposite.this.comp.redraw();
						showManagement( parent );						
					}
					
				}, false);
			}
			
		});
		parent.layout();
	}
	
	private void showManagement( final Composite parent ){
		
		this.input  = getInput();
		if (this.input.isEmpty()) {
			logger.fatal("No members!");
			UserMessageDialogCreator.error("FATAL: No members!");
			return;
		}
		final TableViewer viewer = createTable( parent, this.input  );
		
		Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setLayout(new GridLayout(5,false));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Button promote = new Button( buttons, SWT.PUSH );
		promote.setText("Promote to Admin");
		promote.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		promote.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				promote( getFromSelection(viewer.getSelection()) );
			}
			
		});
		
		final Button demote = new Button( buttons, SWT.PUSH );
		demote.setText("Demote");
		demote.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		demote.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				demote( getFromSelection(viewer.getSelection()) );
			}
			
		});
		
		final Button givePrimary = new Button( buttons, SWT.PUSH );
		givePrimary.setText("Give primary");
		givePrimary.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		givePrimary.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				providePrimary( getFromSelection(viewer.getSelection()) );
			}
			
		});
		
		final Button cancelButton = new Button( buttons, SWT.PUSH );
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(SWT.END, SWT.END, true, false));
		cancelButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				cancelButton.dispose();
				givePrimary.dispose();
				demote.dispose();
				promote.dispose();
				viewer.getTable().dispose();
				AdminsComposite.this.comp.redraw();
				showFacade( parent );
			}
			
		});
		parent.layout();
	}
	
	private UserAdmin getFromSelection( ISelection selection ){
		if(selection==null) {
			return null;
		}
		Object selectedObject = ((StructuredSelection)selection).getFirstElement();
		if(selectedObject==null) {
			return null;
		}
		return (UserAdmin)selectedObject;
	}
	
	private void providePrimary( final UserAdmin ua ){
		if (ua == null) {
			logger.error("UserAdmin to provide primary is null");
		}
		if ( !ua.isAdmin() ) {
			UserMessageDialogCreator.warning("Can not give primary admin privileges to non admin");
			return;
		}
		if ( !ua.isPrimary() ) {
			UserMessageDialogCreator.warningYesCancelButton("Are you sure to give primary admin privileges to "
					+ ua + "? You will not have them anymore.", new WarningDialogListener(){

						public void performCancel() {
							
						}

						public void performOK() {
							providePrimaryImpl( ua );					
						}
						
					}, false);
		}
	}
	
	private void providePrimaryImpl( final UserAdmin oldUa ){
		final UserAdmin ua = new UserAdmin( oldUa );
		ua.setAdmin(true);
		ua.setPrimary(true);
		performRequest( ua );
	}
	
	private void demote( final UserAdmin ua ){
		if (ua == null) {
			logger.error("UserAdmin to demote is null");
		}
		if ( ua.isPrimary() ) {
			UserMessageDialogCreator.warning("Can not demote primary admin");
			return;
		}
		if ( ua.isAdmin() ) {
			UserMessageDialogCreator.warningYesCancelButton("Are you sure to demote from admin "
					+ ua + "?", new WarningDialogListener(){

						public void performCancel() {
							
						}

						public void performOK() {
							demoteImpl( ua );					
						}
						
					}, false);
		}
	}
	
	private void demoteImpl( final UserAdmin oldUa ){
		final UserAdmin ua = new UserAdmin( oldUa );
		ua.setAdmin(false);
		ua.setPrimary(false);
		performRequest( ua );
	}
	
	private void promote( final UserAdmin ua ){
		if (ua == null) {
			logger.error("UserAdmin to promote is null");
		}
		if (!ua.isAdmin() && !ua.isPrimary()) {
			UserMessageDialogCreator.warningYesCancelButton("Are you sure to promote to admin "
					+ ua + "?", new WarningDialogListener(){

						public void performCancel() {
							
						}

						public void performOK() {
							promoteImpl( ua );					
						}
						
					}, false);
		}
	}
	
	private void promoteImpl( final UserAdmin oldUa ){
		final UserAdmin ua = new UserAdmin( oldUa );
		ua.setAdmin(true);
		ua.setPrimary(false);
		performRequest( ua );
	}
	
	private void performRequest(  final UserAdmin ua ){
		UpdateAdminsCommand command = new UpdateAdminsCommand();
		command.setUserAdmin( ua );
		final String result = command.execute(this.cli, String.class);
		if ( StringUtils.isNotBlank(result) ) {
			logger.error("Error on server: " + result);
			UserMessageDialogCreator.error("Error on server: \"" + result + "\", operation was not perfomed");
		} else {
			if ( result != null ) {
				UserMessageDialogCreator.info("Operation succesfull, ContactManagement will be force closed");
				this.window.close();
			} else {
				logger.error("Unknown error on server");
				UserMessageDialogCreator.error("Unknown error on server, operation was not perfomed");
			}
		}
	}
	
	/**
	 * @return
	 */
	private UserAdminList getInput() {
		UserAdminList data = new UserAdminList();
		List<SupraSphereMember> members = this.cli.getVerifyAuth().getAllMembers();
		for ( SupraSphereMember member : members ) {
			final UserAdmin ua = new UserAdmin( member.getContactName(), member.getLoginName() );
			if (this.cli.getVerifyAuth().isAdmin(ua.getContact(), ua.getLogin())) {
				ua.setAdmin(true);
			}
			if (this.cli.getVerifyAuth().isPrimaryAdmin(ua.getContact(), ua.getLogin())) {
				ua.setPrimary(true);
			}
			data.add( ua );
		}
		return data;
	}

	private TableViewer createTable(final Composite viewComposite, final UserAdminList input) {
		TableViewer viewer = new TableViewer(viewComposite, SWT.FULL_SELECTION
				| SWT.BORDER);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new AdminsContentProvider());
		viewer.setLabelProvider(new AdminsLabelProvider());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		new TableColumn(viewer.getTable(), SWT.NONE).setText("Member");
		new TableColumn(viewer.getTable(), SWT.NONE).setText("Admin");
		new TableColumn(viewer.getTable(), SWT.NONE).setText("Primary");
		CellEditor[] editors = new CellEditor[3];
		editors[0] = new CheckboxCellEditor(viewer.getTable(), SWT.LEFT);
		editors[1] = editors[0];
		editors[2] = editors[0];
		viewer.setCellEditors(editors);
		viewer.setColumnProperties(new String[] { "Member", "Admin", "Primary" });
		viewer.setCellModifier(new AdminsCellModifier());
		viewer.getTable().setEnabled(true);
		viewer.setInput( input );
		
		viewer.getTable().getColumns()[0].setWidth(250);
		viewer.getTable().getColumns()[1].setWidth(100);
		viewer.getTable().getColumns()[2].setWidth(100);
		//update(this.file.getMessageId());
		return viewer;
	}
	
	public Control getControl(){
		return this.comp;
	}
	
	public void setLayoutData( final Object layoutData ){
		this.comp.setLayoutData(layoutData);
	}
}
