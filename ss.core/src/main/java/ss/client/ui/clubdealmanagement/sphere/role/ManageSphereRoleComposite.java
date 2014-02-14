/**
 * 
 */
package ss.client.ui.clubdealmanagement.sphere.role;


import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.clubdealmanagement.AbstractClubdealDialog;
import ss.client.ui.clubdealmanagement.ManageByClubdealComposite;
import ss.client.ui.clubdealmanagement.WarnDialog;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.SphereRoleList;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ManageSphereRoleComposite extends Composite {
	
	private static final Logger logger = SSLogger.getLogger(ManageSphereRoleComposite.class);

	private final ManageByClubdealComposite parentComposite;
	
	private TableViewer rolesViewer;
	
	private SphereRoleList roleList = SsDomain.CONFIGURATION.getMainConfigurationValue().getSphereRoleList();

	private Button removeButton;

	private Button renameButton;
	
	public ManageSphereRoleComposite(final ManageByClubdealComposite parentComposite) {
		super(parentComposite.getRightSideComposite(), SWT.NONE);
		this.parentComposite = parentComposite;
		createContent();
	}

	/**
	 * 
	 */
	private void createContent() {
		setLayout(new GridLayout());
		
		Label label = new Label(this, SWT.LEFT);
		label.setText("Sphere Roles");
		
		this.rolesViewer = new TableViewer(this, SWT.BORDER);
		
		this.rolesViewer.setContentProvider(new SphereRoleContentProvider());
		this.rolesViewer.setLabelProvider(new SphereRoleLabelProvider());
		this.rolesViewer.setInput(this.roleList);
		this.rolesViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.rolesViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent arg0) {
				if(getRemoveButton()==null) {
					return;
				}
				getRemoveButton().setEnabled(getSelectedRole()!=null);
				getRenameButton().setEnabled(getSelectedRole()!=null);
			}
		});
		
		createButtonComposite();
	}
	
	private Button getRemoveButton() {
		return this.removeButton;
	}
	
	private Button getRenameButton() {
		return this.renameButton;
	}

	/**
	 * 
	 */
	private void createButtonComposite() {
		Composite buttonComposite = new Composite(this, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(3, true));
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new AbstractClubdealDialog() {							
					@Override
					protected String getLabelText() {
						return "New Role Name: ";
					}
					@Override
					protected boolean savePressed() {
						return addNewRole(this.getName());
					}
					@Override
					public String getTitle() {
						return "Add New Role Name";
					}
				};
				dialog.setBlockOnOpen(true);
				dialog.open();
				ManageSphereRoleComposite.this.rolesViewer.refresh();
			}
		});
		
		this.removeButton = new Button(buttonComposite, SWT.PUSH);
		this.removeButton.setText("Remove");
		this.removeButton.setEnabled(false);
		this.removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new WarnDialog() {
					@Override
					public void cancelPressed() {
						close();
					}
					@Override
					public String getMessage() {
						return "Are you sure you want to delete this contact type? Contacts administrate window will be reloaded.";
					}
					@Override
					public void okPressed() {
						Runnable runnable = new Runnable() {
							public void run() {
								removeSelectedRole();
								getParentComposite().getApplyButton().setEnabled(false);
								getParentComposite().getFolder().getWindow().refresh();
							}
						};
						SDisplay.display.async(runnable);
						close();
					}
				};
				dialog.open();
			}
		});
		
		this.renameButton = new Button(buttonComposite, SWT.PUSH);
		this.renameButton.setText("Rename");
		this.renameButton.setEnabled(false);
		this.renameButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.renameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new AbstractClubdealDialog() {
					@Override
					protected String getFieldText() {
						return getSelectedRole().getRoleName();
					}
					@Override
					protected String getLabelText() {
						return "New Role Name:";
					}
					@Override
					protected boolean savePressed() {
						if(!SphereRoleObject.isValid(getName())) {
							return false;
						}
						final String newName = getName();
						Runnable runnable = new Runnable() {
							public void run() {
								renameSelectedRole(newName);
							}
						};
						SDisplay.display.async(runnable);
						return true;
					}
					
					@Override
					public String getTitle() {
						return "Rename Sphere Role";
					}
				};
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
	}
	
	private SphereRoleObject getSelectedRole() {
		ISelection selection = this.rolesViewer.getSelection();
		if(selection==null) {
			return null;
		}
		Object selectedObject = ((StructuredSelection)selection).getFirstElement();
		if(selectedObject==null) {
			return null;
		}
		return (SphereRoleObject)selectedObject;
	}
	
	private void removeSelectedRole() {
		SphereRoleObject roleObject = getSelectedRole();
		if(roleObject==null) {
			return;
		}
		this.roleList.removeRole(roleObject);
		resetMainConfiguration();
		this.rolesViewer.refresh();
		SupraSphereFrame.INSTANCE.client.handleSphereRoleRemoved(roleObject);
	}
	
	private void renameSelectedRole(final String newName) {
		if(!SphereRoleObject.isValid(newName)) {
			logger.error("New role name is not valid");
			return;
		}
		SphereRoleObject roleObject = getSelectedRole();
		if(roleObject==null) {
			logger.error("Cannot rename null sphere role");
			return;
		}
		SupraSphereFrame.INSTANCE.client.handleSphereRoleRenamed(roleObject, newName);
		roleObject.setRole(newName);
		resetMainConfiguration();
		this.rolesViewer.refresh();
	}
	
	public boolean addNewRole(final String roleName) {
		if(!SphereRoleObject.isValid(roleName)) {
			MessageDialog.openError(SDisplay.display.get().getActiveShell(), "Error", "Incorrect role name! Maybe sphere role with such name already exists.");
			return false;
		}
		boolean successful = this.roleList.addRole(roleName);
		if(!successful) {
			MessageDialog.openError(SDisplay.display.get().getActiveShell(), "Error", "Incorrect role name! Maybe sphere role with such name already exists.");
		} else {
			resetMainConfiguration();
		}
		return successful;
	}

	/**
	 * 
	 */
	private void resetMainConfiguration() {
		ConfigurationValue configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
		configuration.getSphereRoleList().copyAll(this.roleList);
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
	}
	
	private ManageByClubdealComposite getParentComposite() {
		return this.parentComposite;
	}
}
