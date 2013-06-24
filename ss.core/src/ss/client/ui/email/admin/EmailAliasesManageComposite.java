/**
 * 
 */
package ss.client.ui.email.admin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ss.client.preferences.PreferencesAdminController;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.EmailController;
import ss.client.ui.preferences.ManagePreferencesCommonShell;
import ss.client.ui.preferences.changesdetector.EmailAliasesChangesDetector;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionAdaptor;
import ss.client.ui.spheremanagement.SphereManager;
import ss.client.ui.spheremanagement.SphereTreeComposite;
import ss.client.ui.spheremanagement.memberaccess.IChangable;

/**
 * @author zobo
 *
 */
public class EmailAliasesManageComposite extends Composite implements IChangable {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailAliasesManageComposite.class);
	
	private SphereTreeComposite tree;
	
	private EmailAliasesComposite aliases;
	
	private final PreferencesAdminController controller;
	
	private EmailAliasesChangesDetector detector;
	
	private ManagePreferencesCommonShell commonShell;
	
	private ManagedSphere viewSphere;

	private ManagedSphere newSelection;

	public EmailAliasesManageComposite( ManagePreferencesCommonShell commonShell ) {
		super(commonShell.getTabFolder(), SWT.NONE);
		this.commonShell = commonShell;
		this.controller = commonShell.getController();
		this.detector = new EmailAliasesChangesDetector(this);
		createContents(this);
	}

	/**
	 * @param composite
	 */
	private void createContents(Composite composite) {
		composite.setLayout(new GridLayout(2, false));
		
		SphereManager sphereManager = new SphereManager(this.controller.getSphereDefinitionProvider());
		sphereManager.addSelectedSphereChangedListener(new SphereActionAdaptor(){
			@Override
			public void selectedSphereChanged(ManagedSphere selectedSphere) {
				setNewSelection(selectedSphere);
				if(getViewSphere()!=null && !getNewSelection().equals(getViewSphere()) && getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(EmailAliasesManageComposite.this);
				} else {
					performFinalAction();
				}
			}
		});
		
		this.tree = new SphereTreeComposite(composite, sphereManager);	
		GridData gridData;
		gridData = new GridData(0, SWT.FILL, false, true);
		gridData.minimumWidth = 200;
		gridData.widthHint = 200;
		this.tree.setLayoutData(gridData);
		this.tree.addLabel();
		
		this.aliases = new EmailAliasesComposite(composite, new EmailController(null, SupraSphereFrame.INSTANCE.client.session), getDetector()); 
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.aliases.setLayoutData(gridData);
		
		Composite buttonPanel = new Composite(this, SWT.NONE);
		buttonPanel.setLayoutData(new GridData(0, SWT.TOP, true, false, 2, 1));
		buttonPanel.setLayout(LayoutUtils.createNoMarginGridLayout(3));
		LayoutUtils.addSpacer(buttonPanel);
		Button button;
		button = new Button(buttonPanel, SWT.PUSH);
		button.setText("Apply");
		gridData = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		button.setLayoutData(gridData);
		this.aliases.applyOkActionListener(button);
		
		if (logger.isDebugEnabled()){
			logger.debug("Email Aliases manage composite created");
		}
	}

	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void performFinalAction() {
		ManagedSphere selectedSphere = this.tree.getSelected();
		setViewSphere(selectedSphere);
		this.aliases.callSphere(selectedSphere);
		getDetector().setChanged(false);
	}

	public void revertSelection() {
		this.tree.selectSphere(getViewSphere());
	}
	
	public EmailAliasesComposite getInnerEmailAliasesComposite() {  
		return this.aliases;
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

	public void jumpToNextItem() {
		this.commonShell.jumpToNextItem();
	}
}
