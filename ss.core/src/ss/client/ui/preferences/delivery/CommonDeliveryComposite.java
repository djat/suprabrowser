/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ss.client.preferences.PreferencesAdminController;
import ss.client.ui.preferences.ManagePreferencesCommonShell;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.preferences.changesdetector.SphereDeliveryChangesDetector;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionAdaptor;
import ss.client.ui.spheremanagement.SphereManager;
import ss.client.ui.spheremanagement.SphereTreeComposite;
import ss.client.ui.spheremanagement.memberaccess.IChangable;

/**
 *
 */
public class CommonDeliveryComposite extends Composite implements IChangable {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CommonDeliveryComposite.class);
	
	private Composite editComposite;
	
	private final PreferencesAdminController controller;

	private ManagePreferencesCommonShell commonShell;

	private SphereManager manager;

	private SphereTreeComposite sphereTree;

	private ManagedSphere viewSphere;

	private ManagedSphere newSelection;
	
	private IChangesDetector detector;
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public CommonDeliveryComposite(ManagePreferencesCommonShell commonShell) {
		super(commonShell.getTabFolder(), SWT.NONE);
		this.commonShell = commonShell;
		this.controller = commonShell.getController();
		this.detector = new SphereDeliveryChangesDetector(this);
		setLayout();
		createSpheresTree();
		createBlankTable();
	}

	/**
	 * 
	 */
	public void createEditDeliveryComposite(String id) {
		disposeEditor();
		this.editComposite = new EditDeliveryPreferencesComposite(this, id, this.detector);
		this.editComposite.setLayoutData( new GridData(GridData.FILL_BOTH) );
		layout();
	}

	/**
	 * 
	 */
	private void setLayout() {
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createSpheresTree() {
		ISphereDefinitionProvider provider = this.controller.getNoEmailBoxProvider();
		
		this.manager = new SphereManager(provider);
		
		this.sphereTree = new SphereTreeComposite(this, this.manager);
		GridData gridData;
		gridData = new GridData(0, SWT.FILL, false, true);
		gridData.minimumWidth = 200;
		gridData.widthHint = 200;
		this.sphereTree.setLayoutData(gridData);
		this.sphereTree.addLabel();
		
		this.manager.addSelectedSphereChangedListener(new SphereActionAdaptor() {
			@Override
			public void selectedSphereChanged(ManagedSphere selectedSphere) {
				setNewSelection(selectedSphere);
				if(getViewSphere()!=null && !getNewSelection().equals(getViewSphere()) && getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(CommonDeliveryComposite.this);
				} else {
					performFinalAction();
				}
			}
		});
	}

	/**
	 * 
	 */
	public void createBlankTable() {
		disposeEditor();
		this.editComposite = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		this.editComposite.setLayout(layout);
		//this.editComposite.setLayout(new GridLayout());
		
		GridData gridData = new GridData();
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.grabExcessVerticalSpace = false;
		//gridData.verticalIndent=15;
		this.editComposite.setLayoutData(gridData);
	    //this.editComposite.setLayoutData(new GridData(GridData.FILL_BOTH ));
		
		
		Label label = new Label(this.editComposite, SWT.BEGINNING | SWT.BOTTOM);
		label.setText("Workflow model");
		
		Composite innerComp = new Composite(this.editComposite, SWT.BORDER);
		innerComp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		innerComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout();
	}

	private void disposeEditor() {
		if(this.editComposite!=null) {
			this.editComposite.dispose();
			this.editComposite = null;
		}
	}

	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void performFinalAction() {
		ManagedSphere sphere = this.manager.getSelectedSphere();
		createEditDeliveryComposite(sphere.getId());
		this.detector.setChanged(false);
		setViewSphere(getNewSelection());
	}

	public void revertSelection() {
		this.sphereTree.selectSphere(getViewSphere());
	}
	
	public ManagedSphere getViewSphere() {
		return this.viewSphere;
	}
	
	protected void setViewSphere(ManagedSphere newSelection) {
		this.viewSphere = newSelection;
	}
	
	private ManagedSphere getNewSelection() {
		return this.newSelection;
	}
	
	protected void setNewSelection(ManagedSphere sphere) {
		this.newSelection = sphere;
	}
	
	public EditDeliveryPreferencesComposite getEditComposite() {
		if(this.editComposite instanceof EditDeliveryPreferencesComposite) {
			return (EditDeliveryPreferencesComposite)this.editComposite;
		}
		return null;
	}

	public void setupEditComposite() {
		ManagedSphere sphere = this.sphereTree.getSelected();
		createEditDeliveryComposite(sphere.getId());
	}

	public void jumpToNextItem() {
		this.commonShell.jumpToNextItem();
	}
}
