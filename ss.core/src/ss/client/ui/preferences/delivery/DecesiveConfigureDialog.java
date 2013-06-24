/**
 * 
 */
package ss.client.ui.preferences.delivery;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.ui.viewers.NewMemberCellModifier;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.workflow.DecisiveDelivery;
import ss.domainmodel.workflow.ModelMemberCollection;
import ss.domainmodel.workflow.ModelMemberEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class DecesiveConfigureDialog extends ConfigureDeliveryDialog {

	

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(DecesiveConfigureDialog.class);
	
	private List<ModelMemberEntityObject> initialMembers = new ArrayList<ModelMemberEntityObject>();
	
	private DecisiveDelivery decisive;
	
	private TableViewer tv;
	
	public static final String PROP_NAME = bundle.getString(CONTACT_NAME);

	public static final String PROP_ROLE = bundle.getString(ROLE);
	
	public static final String[] allRoles = DecisiveDelivery.getAllRolesNames();
	
	private static final String[] PROPS = new String[]{PROP_NAME, PROP_ROLE};
	/**
	 * @param editComposite
	 * @param typeDelivery
	 */
	public DecesiveConfigureDialog(DecisiveDelivery decisive, EditDeliveryPreferencesComposite editComposite) {
		super(editComposite);
		this.decisive = decisive;
		this.oldName = decisive.getDisplayName();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.preferences.delivery.ConfigureDeliveryDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		super.createContents(parent);
	
		createMembersTable(parent, this.decisive.getMemberCollection());
		
		this.nameText.setEditable(true);
		
		return parent;
	}
	
	private void createMembersTable(Composite parent, ModelMemberCollection members) {
		for(ModelMemberEntityObject member : members) {
			ModelMemberEntityObject newMember = new ModelMemberEntityObject();
			newMember.setContactName(member.getContactName());
			newMember.setUserName(member.getUserName());
			newMember.setRoleName(member.getRoleName());
			this.initialMembers.add(newMember);
		}
		
		this.tv = new TableViewer(parent, SWT.BORDER);
		this.tv.setContentProvider(new ConfigureTableContentProvider());
		this.tv.setLabelProvider(new ConfigureTableLabelProvider());
		this.tv.setInput(members);
		
		this.tv.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		new TableColumn(this.tv.getTable(), SWT.NONE).setText(PROP_NAME);
		new TableColumn(this.tv.getTable(), SWT.NONE).setText(PROP_ROLE);
		
		for(TableColumn col : this.tv.getTable().getColumns()) {
			col.pack();
		}
		
		this.tv.getTable().setLinesVisible(true);
		this.tv.getTable().setHeaderVisible(true);
		this.tv.getTable().moveAbove(this.buttonPane);
		
		CellEditor[] editors = new CellEditor[2];
	    editors[0] = new TextCellEditor(this.tv.getTable(), SWT.LEFT);
	    editors[1] = new ComboBoxCellEditor(this.tv.getTable(), allRoles,
	        SWT.READ_ONLY | SWT.LEFT);
	    editors[1].addListener(new ICellEditorListener() {
			public void applyEditorValue() {
			}
			public void cancelEditor() {
			}
			public void editorValueChanged(boolean arg0, boolean arg1) {
			}	
	    });

	    this.tv.setColumnProperties(PROPS);
	    this.tv.setCellModifier(new NewMemberCellModifier(this));
	    this.tv.setCellEditors(editors);
	
	    this.tv.refresh();
	}
	
	public TableViewer getTV() {
		return this.tv;
	}
	
	protected String getName() {
		return "Decesive";
	}
	
	@Override
	protected void configureShell(Shell shell) {
		shell.setText(bundle.getString(CONFIGURE_DIALOG));
		shell.setLayout(new GridLayout());
		shell.setSize(480, 360);
	}

	@Override
	protected void cancelPressed() {
		this.decisive.getMemberCollection().removeAll();
		for(ModelMemberEntityObject member : this.initialMembers) {
			this.decisive.getMemberCollection().add(member);
		}
		dispose();
	}
	
	@Override
	protected boolean saveNewDeliveryName() {
		if (this.nameText.getText() != null
				&& this.editComposite.checkAlreadyExist(this.nameText.getText(),
						this.decisive)) {
			UserMessageDialogCreator
					.error(bundle.getString(THIS_DELIVERY_NAME_IS_ALREADY_IN_USE));
		} else if (this.nameText.getText() != null
				&& !this.nameText.getText().trim().equals("")) {
			checkValidation();
			this.decisive.setDisplayName(this.nameText.getText());
			return true;
		} else {
			UserMessageDialogCreator.error(bundle.getString(INVALID_DELIVERY_NAME));
		}
		return false;
	}

	private void checkValidation() {
		if(!this.decisive.validate()) {
			this.decisive.setEnabled(false);
			this.editComposite.removeFromCombo(this.decisive.getDisplayName());
		}
	}
	
	@Override
	protected ShellListener getShellListener() {
		return new ShellAdapter() {
			public void shellClosed(ShellEvent se) {
				cancelPressed();
			}
		};
	}
}