/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.byspheres;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionListener;
import ss.client.ui.spheremanagement.SphereTreeComposite;
import ss.client.ui.spheremanagement.memberaccess.AbstractAccessManagerComposite;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.client.ui.spheremanagement.memberaccess.MemberAccessManager;

/**
 * 
 */
public class BySpheresAccessManagerComposite extends AbstractAccessManagerComposite implements IChangable {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(BySpheresAccessManagerComposite.class);

	private ManagedSphere viewSphere;

	private SphereTreeComposite sphereTree;

	private MemberListComposite memberList;

	private ManagedSphere newSelection;
	
	private IChangesDetector detector;

	/**
	 * @param parent
	 * @param style
	 */
	public BySpheresAccessManagerComposite(Composite parent, MemberAccessManager manager, IChangesDetector detector ) {
		super(parent, manager );
		this.detector = detector;
		setLayout(new GridLayout(2, false));
		this.sphereTree = new SphereTreeComposite(this, getManager());
		GridData gridData;
		gridData = new GridData(0, SWT.FILL, false, true);
		gridData.minimumWidth = 200;
		gridData.widthHint = 200;
		this.sphereTree.setLayoutData(gridData);
		this.memberList = new MemberListComposite(this, getManager(), getDetector());
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.memberList.setLayoutData(gridData);

		Composite buttonPanel = new Composite(this, SWT.NONE);
		// buttonPanel.setBackground( new Color(getDisplay(), 0, 255, 0 ) );
		buttonPanel.setLayoutData(new GridData(0, SWT.TOP, true, false, 2, 1));
		buttonPanel.setLayout(LayoutUtils.createNoMarginGridLayout(3));
		LayoutUtils.addSpacer(buttonPanel);
		createApplyButton( buttonPanel );

		this.manager.addSelectedSphereChangedListener(new SphereActionListener() {
			public void selectedSphereChanged(ManagedSphere selectedSphere) {
				setNewSelection(selectedSphere);
				if(getViewSphere()!=null && (getNewSelection()==null || !getNewSelection().equals(getViewSphere())) && getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(BySpheresAccessManagerComposite.this);
				} else {
					performFinalAction();
				}
			}

			public void showContextMenu(ManagedSphere selectedSphere) {	
			}
		});
	}

	/**
	 * @param sphere
	 */
	protected void setNewSelection(ManagedSphere sphere) {
		this.newSelection = sphere;
	}

	/**
	 * @param newSelection
	 */
	protected void setViewSphere(ManagedSphere newSelection) {
		this.viewSphere = newSelection;
	}

	public ManagedSphere getViewSphere() {
		return this.viewSphere;
	}

	public void performFinalAction() {
		getDetector().setChanged(false);
		this.memberList.refreshMembers();
		setViewSphere(getNewSelection());
	}

	/**
	 * @return
	 */
	private ManagedSphere getNewSelection() {
		return this.newSelection;
	}

	public void revertSelection() {
		this.sphereTree.selectSphere(getViewSphere());
	}

	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void jumpToNextItem() {
		
	}
}
