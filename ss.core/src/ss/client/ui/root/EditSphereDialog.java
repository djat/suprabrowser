/**
 * 
 */
package ss.client.ui.root;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.networking.protocol.actions.UpdateSphereDefinitionAction;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.viewers.ISphereLocationEditor;
import ss.client.ui.viewers.SphereLocationDialog;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SpherePhisicalLocationItem;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.configuration.SphereRoleList;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.framework.networking2.Command;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.ReplyHandler;
import ss.framework.networking2.SuccessReply;

/**
 * @author zobo
 *
 */
public class EditSphereDialog extends Dialog implements ISphereLocationEditor {
	
	public interface EditSphereDefinitionDialogListener {
		
		public void sphereEdited();
		
		public void operationCanceled();
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EditSphereDialog.class);
	
	private final ManagedSphere sphere;
	
	private final SpherePhisicalLocationItem locationItem = new SpherePhisicalLocationItem();
	
	private Combo typeCombo;
	
	private List<EditSphereDefinitionDialogListener> listeners = new ArrayList<EditSphereDefinitionDialogListener>();

	private Text text;

	protected EditSphereDialog( final Shell parent, final ManagedSphere sphere ) {
		super( parent );
		if ( sphere == null ) {
			throw new NullPointerException("ManagedSphere can not be null");
		}
		this.sphere = sphere;
		this.locationItem.copyAll(this.sphere.getStatement().getPhisicalLocation());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		
		dialogArea.setLayout(new GridLayout(2,false));
		
		Label label = new Label(dialogArea, SWT.LEFT);
		label.setText("New Name (optional):");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		
		this.text = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
		this.text.setText( this.sphere.getDisplayName() );
		this.text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(dialogArea, SWT.LEFT);
		label.setText("New Type (optional):");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		
		final String role = this.sphere.getStatement().getRole();
		
		this.typeCombo = new Combo(dialogArea, SWT.BORDER | SWT.READ_ONLY | SWT.LEFT);
		this.typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		SphereRoleList list = SsDomain.CONFIGURATION.getMainConfigurationValue().getSphereRoleList();
		for(SphereRoleObject roleObject : list) {
			this.typeCombo.add(roleObject.getRoleName());
		}
		if(!list.contains(SphereRoleObject.getDefaultName())) {
			this.typeCombo.add(SphereRoleObject.getDefaultName(), 0);
		}
		int index = -1;
		if (SphereRoleObject.isValid(role)) {
			index = this.typeCombo.indexOf(role);
		}
		if (index < 0) {
			index = this.typeCombo.indexOf(SphereRoleObject.getDefaultName());
		}
		this.typeCombo.select(index);
		
		final Button editLocationButton = new Button(dialogArea, SWT.PUSH);
		editLocationButton.setText("Edit Location...");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		editLocationButton.setLayoutData(data);
		editLocationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startEditLocation();
			}
		});
		
		return dialogArea;
	}

	/**
	 * 
	 */
	public void startEditLocation() {
		SphereLocationDialog dialog = new SphereLocationDialog(this, true);
		dialog.setBlockOnOpen(true);
		dialog.open();
	}
	
	public SpherePhisicalLocationItem getPhisicalLocationItem() {
		return this.locationItem;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(320, 180);
	}

	@Override
	protected void okPressed() {
		
		final String newSphereName = this.text.getText();
		if (!isSetNewSphereName( newSphereName )) {
			return;
		}
		UserMessageDialogCreator.warningYesCancelButton("Do you really want to change old name: \"" + 
				this.sphere.getDisplayName() + "\" to new one: \"" + newSphereName + "\"", new WarningDialogListener(){

					public void performCancel() {
					}

					public void performOK() {
						okPressedImpl(newSphereName);
					}
			
		}, false);
	}
	
	private void okPressedImpl( final String newSphereName ){
		final String role = this.typeCombo.getText();
		
		SphereStatement st = this.sphere.getStatement();
		
		boolean hasChanges = false;
		if (checkChanged(role, st.getRole())) {
			st.setRole(role);
			hasChanges = true;
		}
		if (checkChanged(newSphereName,st.getDisplayName())) {
			st.setDisplayName(newSphereName);
			hasChanges = true;
		}
		if(!this.locationItem.isSame(st.getPhisicalLocation())) {
			st.getPhisicalLocation().copyAll(this.locationItem);
			hasChanges = true;
		}
		if(hasChanges) {
			UpdateSphereDefinitionAction command = new UpdateSphereDefinitionAction();
			command.setDefinition(st);
			command.beginExecute(SupraSphereFrame.INSTANCE.client, new ReplyHandler(){
				@Override
				protected void commandSuccessfullyExecuted(Command command,
						SuccessReply successReply) {
					super.commandSuccessfullyExecuted(command, successReply);
					for ( EditSphereDefinitionDialogListener listener : getListeners() ) {
						listener.sphereEdited();
					}
				}

				@Override
				protected void exeptionOccured(CommandExecuteException exception)
						throws CommandHandleException {
					cancelPressed();
					super.exeptionOccured(exception);
				}
			});
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No values changed, no need to update");
			}
		}
		super.okPressed();
	}
	
	/**
	 * @param newSphereName
	 * @return
	 */
	private boolean isSetNewSphereName(String newSphereName) {
		if ( StringUtils.isBlank(newSphereName) ) {
			UserMessageDialogCreator.warning("Sphere name can not be blank", "wrong sphere name");
			return false;
		}
		if ( !newSphereName.equals(this.sphere.getDisplayName()) 
				&& SupraSphereFrame.INSTANCE.client.getVerifyAuth().isSphereExists( newSphereName )) {
			UserMessageDialogCreator.warning("Sphere with such name already exists", "wrong sphere name");
			return false;
		}
		return true;
	}

	private List<EditSphereDefinitionDialogListener> getListeners(){
		return this.listeners;
	}
	
	@Override
	protected void cancelPressed() {
		for ( EditSphereDefinitionDialogListener listener : this.listeners ) {
			listener.operationCanceled();
		}
		super.cancelPressed();
	}

	private boolean checkChanged( final String newValue, final String oldValue ){
		return !(StringUtils.getNotNullString(newValue).equals(
				StringUtils.getNotNullString(oldValue)));
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText( "Edit sphere: " + ((this.sphere != null) ? this.sphere.getDisplayName() : "null") );
	}
	
	public void addListener( final EditSphereDefinitionDialogListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("listener can not be null");
		}
		synchronized (this.listeners) {
			this.listeners.add( listener );
		}
	}
	
	public void removeListener( final EditSphereDefinitionDialogListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("listener can not be null");
		}
		synchronized (this.listeners) {
			this.listeners.remove( listener );
		}
	}

	/**
	 * @param phisicalLocationItem
	 */
	public void setPhisicalLocation(
			final SpherePhisicalLocationItem phisicalLocationItem) {
		this.locationItem.copyAll(phisicalLocationItem);
	}
}
