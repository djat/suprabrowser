/**
 * 
 */
package ss.client.ui.root;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.relation.sphere.manage.EditRelatedSpheresDialog;
import ss.client.ui.relation.sphere.manage.SphereRelationModel;
import ss.client.ui.root.EditSphereDialog.EditSphereDefinitionDialogListener;
import ss.client.ui.root.SelectDestinationSphereDialog.SelectDestinationSphereDialogListener;
import ss.client.ui.root.actions.IRootCreateActions;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereTreeComposite;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.common.UiUtils;
import ss.domainmodel.ContactStatement;
import ss.util.SessionConstants;

/**
 * 
 */
public class RootSphereHierarchyComposite extends Composite {

	private ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_SPHEREHIERARCHYCOMPOSITE);
	
	private static final String OPEN = "SPHEREHIERARCHYCOMPOSITE.OPEN";

	private static final String CREATE_SPHERE = "SPHEREHIERARCHYCOMPOSITE.CREATE_SPHERE";

	private static final String CREATE_CONTACT = "SPHEREHIERARCHYCOMPOSITE.CREATE_CONTACT";
	
	private static final String DELETE_SPHERE = "SPHEREHIERARCHYCOMPOSITE.DELETE_SPHERE";
	
	private static final String DELETE_SPHERE_TOTAL = "SPHEREHIERARCHYCOMPOSITE.DELETE_SPHERE_TOTAL";
	
	private static final String EDIT_SPHERE_RELATIONS = "SPHEREHIERARCHYCOMPOSITE.EDIT_SPHERE_RELATIONS";
	
	private static final String MOVE_SPHERE = "SPHEREHIERARCHYCOMPOSITE.MOVE_SPHERE";
	
	private static final String EDIT_SPHERE = "SPHEREHIERARCHYCOMPOSITE.EDIT_SPHERE";
	
	private static final String THIS_SPHERE_HAS_CHILD_SPHERES = "SPHEREHIERARCHYCOMPOSITE.THIS_SPHERE_HAS_CHILD_SPHERES";
	
	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE = "SPHEREHIERARCHYCOMPOSITE.ARE_YOU_SURE_YOU_WANT_TO_DELETE";

	public static RootSphereHierarchyComposite INSTANCE;

	private Composite peopleTableComp = null;

	private SashForm shellSashForm;

	protected PeopleTableForSphereComposite peopleTable;

	private SphereTreeComposite treeComposite;

	private IRootCreateActions rootActions = null;
	
	private final ISphereDefinitionProvider provider;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootSphereHierarchyComposite.class);

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RootSphereHierarchyComposite(Composite parent,
			ISphereDefinitionProvider provider) {
		super(parent, SWT.BORDER);
		this.provider = provider;
		createContents(provider);
		
		setRootActions(RootTab.getInstance());
	}

	protected void createContents(ISphereDefinitionProvider provider) {
		GridLayout gLayout = new GridLayout();
		gLayout.horizontalSpacing = 0;
		gLayout.makeColumnsEqualWidth = false;
		gLayout.marginBottom = 0;
		gLayout.marginHeight = 0;
		gLayout.marginLeft = 0;
		gLayout.marginRight = 0;
		gLayout.marginTop = 0;
		gLayout.marginWidth = 0;
		gLayout.numColumns = 2;
		gLayout.verticalSpacing = 0;
		setLayout(gLayout);

		this.shellSashForm = new SashForm(this, SWT.HORIZONTAL);
		this.shellSashForm.setLayout(new GridLayout(2, false));
		this.shellSashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.shellSashForm.setBackground(new Color(Display.getDefault(), 190, 190, 190));

		RootSphereManager manager = new RootSphereManager(
				provider);		
		manager.addSelectedSphereChangedListener(new RootSphereHierarchyListener(
						this));

		this.treeComposite = new SphereTreeComposite(this.shellSashForm,
				manager, SWT.NONE);
		this.treeComposite.addHeader();

		this.peopleTableComp = new Composite(this.shellSashForm, SWT.NONE);
		this.peopleTableComp
				.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.peopleTableComp.setLayout(layout);
		createBlankPeopleTable();

		this.layout();
	}

	/**
	 * @param selectedSphere
	 */
	public void refreshPeopleList(ManagedSphere selectedSphere) {
		if (selectedSphere == null) {
			createBlankPeopleTable();
			return;
		}

		if (this.peopleTable != null && !this.peopleTable.isDisposed()) {
			this.peopleTable.close();
		}
		this.peopleTable = new PeopleTableForSphereComposite(this,
				selectedSphere.getId());
		this.peopleTable.setLayoutData();
		this.peopleTableComp.layout();
	}

	private void createBlankPeopleTable() {
		if (this.peopleTable != null && !this.peopleTable.isDisposed()) {
			this.peopleTable.close();
		}
		this.peopleTable = new PeopleTableForSphereComposite(this, null);
		this.peopleTable.setLayoutData();
		this.peopleTableComp.layout();
	}

	public Composite getPeopleTableOwner() {
		return this.peopleTableComp;
	}

	/**
	 * 
	 */
	protected void handleRightClickTable() {
		// DO NOTHING FOR SIMPLE USER
	}

	public ManagedSphere getSelectedSphere() {
		return this.treeComposite.getSelected();
	}

	public void handleRightClickTree() {
		ManagedSphere sphere = this.treeComposite.getSelected();
		if (sphere == null) {
			return;
		} else {
			showMenu(sphere);
		}
	}

	private void showMenu(final ManagedSphere sphere) {
		Menu menu = new Menu(this.treeComposite);

		boolean isDeleted = this.treeComposite.getSelected().getStatement().isDeleted();
		
		MenuItem open = new MenuItem(menu, SWT.PUSH);
		open.setText(this.bundle.getString(OPEN));
		open.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				SphereOpenManager.INSTANCE.request(sphere.getId());
			}
		});
		open.setEnabled(!isDeleted);
		
		if (this.rootActions != null) {
			MenuItem createSphere = new MenuItem(menu, SWT.PUSH);
			createSphere.setText(this.bundle.getString(CREATE_SPHERE));
			createSphere.setEnabled(!isDeleted);
			createSphere.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					RootSphereHierarchyComposite.this.rootActions.performCreateSphereAction();
				}
			});
			
			MenuItem createUser = new MenuItem(menu, SWT.PUSH);
			createUser.setText(this.bundle.getString(CREATE_CONTACT));
			createUser.setEnabled(!isDeleted);
			createUser.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					RootSphereHierarchyComposite.this.rootActions.performCreateUserAction();
				}
			});
		}
		
		
		
		if(SupraSphereFrame.INSTANCE.client.getVerifyAuth().isAdmin()) {
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			final MenuItem editSphereRelation = new MenuItem(menu, SWT.PUSH);
			editSphereRelation.setText(this.bundle.getString(EDIT_SPHERE_RELATIONS) );
			editSphereRelation.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					performEditSphereRelation( RootSphereHierarchyComposite.this.treeComposite.getSelected() );
				}
			});
			
			final MenuItem moveSphere = new MenuItem(menu, SWT.PUSH);
			moveSphere.setText(this.bundle.getString(MOVE_SPHERE));
			moveSphere.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					performMoveSphere();
				}
			});
			
			final MenuItem editSphere = new MenuItem(menu, SWT.PUSH);
			editSphere.setText(this.bundle.getString(EDIT_SPHERE));
			editSphere.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					performEditSphere();
				}
			});
						
			new MenuItem(menu, SWT.SEPARATOR);
			
			final MenuItem deleteSphere = new MenuItem(menu, SWT.PUSH);
			deleteSphere.setText(this.bundle.getString(DELETE_SPHERE));
			deleteSphere.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					performDeleteSphere();
				}
			});
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			final MenuItem deleteSphereTotally = new MenuItem(menu, SWT.PUSH);
			deleteSphereTotally.setText(this.bundle.getString(DELETE_SPHERE_TOTAL));
			deleteSphereTotally.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					performDeleteSphereTotally();
				}
			});
			
			if(this.treeComposite.getSelected().getId().equals(SupraSphereFrame.INSTANCE.client.session.get(SessionConstants.SUPRA_SPHERE)) 
					|| this.treeComposite.getSelected().getStatement().isEmailBox()) {
				moveSphere.setEnabled(false);
				deleteSphere.setEnabled(false);
				deleteSphereTotally.setEnabled(false);
			}
			if ( this.treeComposite.getSelected().getStatement().isDeleted() ) {
				moveSphere.setEnabled(false);
				deleteSphere.setEnabled(false);
			}
		}
	
		if(isDeleted) {
			open.setEnabled(false);
			
		}
		
		menu.setVisible(true);
	}
	
	private void performEditSphere(){
		final ManagedSphere sphere = this.treeComposite.getSelected();
		if ( sphere == null ) {
			logger.error("Selected ManagedSphere is null");
			return;
		}
		EditSphereDialog dialog = new EditSphereDialog( getShell(), sphere );
		dialog.addListener(new EditSphereDefinitionDialogListener(){

			public void operationCanceled() {
			}

			public void sphereEdited() {
				UiUtils.swtBeginInvoke(new Runnable(){

					public void run() {
						refreshSphereTree();
					}
					
				});
			}
			
		});
		dialog.open();
	}
	
	protected void performEditSphereRelation( ManagedSphere selectedSphere) {
		SphereRelationModel model = SphereRelationModel.create( this.provider, selectedSphere != null ? selectedSphere.getId() : null );
		EditRelatedSpheresDialog dlgEditRelatedSpheres = new EditRelatedSpheresDialog( getShell(), model );
		dlgEditRelatedSpheres.open();
	}

	private void performMoveSphere(){
		final ManagedSphere sphere = this.treeComposite.getSelected();
		if ( sphere == null ) {
			logger.error("Selected ManagedSphere is null");
			return;
		}
		final SelectDestinationSphereDialog destination = new SelectDestinationSphereDialog(
				sphere, this.provider, SupraSphereFrame.INSTANCE.client);
		destination.addListener(new SelectDestinationSphereDialogListener(){
			public void operationCanceled() {
			}

			public void sphereMoved(String targedSphereId) {
				refreshSphereTree();
			}		
		});
		destination.open( getShell() );
	}

	/**
	 * @param sphere
	 */
	private void performDeleteSphere() {
		ManagedSphere sphere = this.treeComposite.getSelected();
		String warningString = "";

		if(sphere.getChildren().isEmpty()) {
			warningString = this.bundle.getString(ARE_YOU_SURE_YOU_WANT_TO_DELETE);
		} else {
			warningString = this.bundle.getString(THIS_SPHERE_HAS_CHILD_SPHERES);
		}

		UserMessageDialogCreator.warningYesCancelButton(warningString, new WarningDialogListener() {
			public void performCancel() {
			}

			public void performOK() {
				List<ManagedSphere> spheresToDelete = RootSphereHierarchyComposite.this.treeComposite.getSelectedSubtree();
				SupraSphereFrame.INSTANCE.client.deleteSpheres(spheresToDelete);
			}
		}, false);
	}
	
	private void performDeleteSphereTotally() {
		final ManagedSphere sphere = this.treeComposite.getSelected();
		String warningString = "";

		if(sphere.getChildren().isEmpty()) {
			warningString = "Are you sure want to delete sphere totally? All data will be removed";
		} else {
			warningString = this.bundle.getString(THIS_SPHERE_HAS_CHILD_SPHERES);
			UserMessageDialogCreator.error("deleting error", warningString);
			return;
		}

		UserMessageDialogCreator.warningYesCancelButton(warningString, new WarningDialogListener() {
			public void performCancel() {
			}

			public void performOK() {
				List<ManagedSphere> spheresToDelete = new ArrayList<ManagedSphere>();
				spheresToDelete.add( sphere );
				SupraSphereFrame.INSTANCE.client.deleteSpheresTotal(spheresToDelete);
			}
		}, false);
	}

	public void setRootActions(IRootCreateActions rootActions) {
		this.rootActions = rootActions;
	}

	/**
	 * 
	 */
	public void refreshSphereTree() {
		findMissingSphere();
	}

	/**
	 * 
	 */
	private void findMissingSphere() {
		this.treeComposite.rebuild();
	}

	/**
	 * @param contact
	 */
	public void refreshPeopleTable(final ContactStatement contact) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(getSelectedSphere()==null || !getSelectedSphere().getId().equals(contact.getCurrentSphere())) {
					return;
				}
				refreshPeopleList(getSelectedSphere());
			}
		});
	}
}
