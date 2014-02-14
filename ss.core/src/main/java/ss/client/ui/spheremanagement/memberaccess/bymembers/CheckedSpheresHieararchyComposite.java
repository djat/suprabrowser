/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.bymembers;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereTreeContentProvider;
import ss.client.ui.spheremanagement.memberaccess.IMemberAccessUiOwner;
import ss.client.ui.spheremanagement.memberaccess.MemberAccessManager;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SphereItem.SphereType;

/**
 * 
 */
public class CheckedSpheresHieararchyComposite extends Composite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CheckedSpheresHieararchyComposite.class);

	private CheckboxTreeViewer checkViewer;

	private final SupraSphereMember member;

	private Button setButton;

	private TreeItem loginSphereItem;

	private boolean isMemberOnline = false;

	private static final String SET_AS_A_LOGIN_SPHERE = "CHECKTREECOMPOSITE.SET_AS_A_LOGIN_SPHERE";

	private static final String CANNOT_CHANGE = "CHECKTREECOMPOSITE.CANNOT_CHANGE";

	private static final String SPHERES_ACCESS = "CHECKTREECOMPOSITE.SPHERES_ACCESS";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_SPHEREMANAGEMENT_MEMBERACCESS_CHECKTREECOMPOSITE);

	private final IMemberAccessUiOwner owner;

	private IChangesDetector detector;

	/**
	 * 
	 */
	public CheckedSpheresHieararchyComposite(Composite parent,
			IMemberAccessUiOwner owner, MemberReference memberReference,
			Boolean isMemberOnline, IChangesDetector detector) {
		super(parent, SWT.NONE);
		this.owner = owner;
		this.detector = detector;
		final String contactName = memberReference != null ? memberReference
				.getContactName() : null;
		if (logger.isDebugEnabled()) {
			logger.debug("Finding suprasphere member by " + contactName);
		}
		this.member = this.getManager().getMemberByContactName(contactName);
		this.isMemberOnline = isMemberOnline.booleanValue();
		if (logger.isDebugEnabled()) {
			logger.debug("Found " + this.member);
		}
		createContents();
		setUpCheckStateAndLoginSphere();
		layout();
	}

	/**
	 * 
	 */
	private void createContents() {
		setLayout(new GridLayout());
		createLabel();
		createSphereTree();
		createButtons();
	}

	private void createButtons() {
		Composite comp = new Composite(this, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		comp.setLayout(new GridLayout(2, false));

		if (this.member == null) {
			createEmptyLabel(comp);
		} else {
			if (this.isMemberOnline) {
				((GridLayout) comp.getLayout()).makeColumnsEqualWidth = false;
				createCannotLabel(comp);
			} else {
				createSetButton(comp);
			}
			this.owner.createApplyButton(comp);
		}
	}

	private void createSetButton(Composite comp) {
		this.setButton = new Button(comp, SWT.PUSH);
		this.setButton.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		this.setButton.setText(this.bundle.getString(SET_AS_A_LOGIN_SPHERE));
		this.setButton.setEnabled(false);
		this.setButton
				.addSelectionListener(new SetLoginSphereSelectionListener(this));
	}

	private void createCannotLabel(Composite comp) {
		Label label = new Label(comp, SWT.LEFT);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		label.setText(this.bundle.getString(CANNOT_CHANGE));
	}

	private void createEmptyLabel(Composite comp) {
		Label label = new Label(comp, SWT.LEFT);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	}

	private void createLabel() {
		Composite comp = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);

		Label nameLabel = new Label(comp, SWT.BEGINNING | SWT.BOTTOM);

		FontData data = getFont().getFontData()[0];
		nameLabel.setFont(new Font(Display.getDefault(), data.getName(), data
				.getHeight(), SWT.BOLD));

		Label label = new Label(comp, SWT.BEGINNING | SWT.BOTTOM);

		if (this.member != null) {
			nameLabel.setText(this.member.getContactName());
			label.setText(this.bundle.getString(SPHERES_ACCESS));
		}
	}

	private void createSphereTree() {
		if (this.member == null) {
			Composite blankComp = new Composite(this, SWT.BORDER);
			blankComp.setBackground(Display.getDefault().getSystemColor(
					SWT.COLOR_WHITE));
			blankComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		} else {
			this.checkViewer = new CheckboxTreeViewer(this);
			this.checkViewer.setLabelProvider(new TreeLabelProvider(this));
			this.checkViewer
					.setContentProvider(new SphereTreeContentProvider());

			this.checkViewer.getTree().setLayoutData(
					LayoutUtils.createFullFillGridData());
			this.checkViewer.getTree().addMouseListener(new MouseAdapter(){
				@Override
				public void mouseDown(MouseEvent e) {
					if ( e.button == 3 ) {
						final Menu menu = new Menu( CheckedSpheresHieararchyComposite.this.checkViewer.getTree() );
						final MenuItem check = new MenuItem( menu, SWT.PUSH );
						check.setText("Check all subspheres");
						check.addSelectionListener(new SelectionAdapter(){
							@Override
							public void widgetSelected(SelectionEvent e) {
								if ( CheckedSpheresHieararchyComposite.this.checkViewer.getSelection() == null ) {
									return;
								}
								final TreeSelection selection = (TreeSelection)CheckedSpheresHieararchyComposite.this.checkViewer.getSelection();																
								final ManagedSphere managedSphere = (ManagedSphere)selection.getFirstElement();
								final String contactName = CheckedSpheresHieararchyComposite.this.member.getContactName();
								checkAllSubSpheres(managedSphere, contactName, true);
								getDetector().setChanged(true);
								setUpCheckState();
							}
						});
						final MenuItem uncheck = new MenuItem( menu, SWT.PUSH );
						uncheck.setText("Uncheck all subspheres");
						uncheck.addSelectionListener(new SelectionAdapter(){
							@Override
							public void widgetSelected(SelectionEvent e) {
								if ( CheckedSpheresHieararchyComposite.this.checkViewer.getSelection() == null ) {
									return;
								}
								final TreeSelection selection = (TreeSelection)CheckedSpheresHieararchyComposite.this.checkViewer.getSelection();																
								final ManagedSphere managedSphere = (ManagedSphere)selection.getFirstElement();
								final String contactName = CheckedSpheresHieararchyComposite.this.member.getContactName();
								checkAllSubSpheres(managedSphere, contactName, false);
								getDetector().setChanged(true);
								setUpCheckState();
							}
						});
						menu.setVisible(true);
					}
				}
			});
			this.checkViewer.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent e) {
					final ManagedSphere managedSphere = (ManagedSphere) e
							.getElement();
					final String contactName = CheckedSpheresHieararchyComposite.this.member
							.getContactName();
					if(!managedSphere.isEditable()) {
						TreeItem item = findTreeItem(((CheckboxTreeViewer)e.getSource()).getTree().getItems(), managedSphere);
						item.setChecked(!e.getChecked());
						return;
					}
					managedSphere.setMemberEnabled(contactName, e.getChecked());
					getDetector().setChanged(true);
				}
			});
			this.checkViewer
					.addSelectionChangedListener(new ISelectionChangedListener() {
						public void selectionChanged(SelectionChangedEvent se) {
							if (CheckedSpheresHieararchyComposite.this.setButton != null) {
								CheckedSpheresHieararchyComposite.this.setButton
										.setEnabled(!belongAdmin());
							}
						}
					});
			if (logger.isDebugEnabled()) {
				logger.debug("Getting root sphere");
			}
			final ManagedSphere rootSphere = this.getManager().getRootSphere();
			if (logger.isDebugEnabled()) {
				logger.debug("Root recevied");
			}
			this.checkViewer.setInput(new Object[] { rootSphere });
		}
	}
	
	private void checkAllSubSpheres( final ManagedSphere sphere, final String contactName, final boolean enabled ){
		if ( sphere == null ) {
			return;
		}
		sphere.setMemberEnabled(contactName, enabled);
		if (sphere.getChildren() != null) {
			for ( ManagedSphere child : sphere.getChildren() ) {
				checkAllSubSpheres(child, contactName, enabled);
			}
		}
	}

	protected void setUpCheckStateAndLoginSphere() {
		if (this.member == null) {
			return;
		}
		this.checkViewer.expandAll();
		setUpCheckState();
		final ManagedSphere sphere = this.getManager().getRootSphere()
				.findSphere(this.member.getLoginSphereSystemName());
		this.loginSphereItem = findTreeItem(this.checkViewer.getTree()
				.getItems(), sphere);
		this.checkViewer.refresh();
	}

	/**
	 * 
	 */
	private void setUpCheckState() {
		final List<ManagedSphere> enabledSpheres = this.getManager()
				.getRootSphere().listEnabledSpheres(
						this.member.getContactName());
		final Object[] enabledSpheresArray = enabledSpheres.toArray();
		this.checkViewer.setCheckedElements(enabledSpheresArray);
		
	}

	private TreeItem findTreeItem(TreeItem[] items, ManagedSphere sphere) {
		if (items == null) {
			return null;
		}
		for (TreeItem item : items) {
			if (item.getData() == sphere) {
				return item;
			}
			TreeItem resultFromChild = findTreeItem(item.getItems(), sphere);
			if (resultFromChild != null) {
				return resultFromChild;
			}
		}
		return null;
	}
	
	

	/**
	 * @param item
	 */
	private void setLoginSphereItem(TreeItem item) {
		this.loginSphereItem = item;
	}

	/**
	 * @param
	 * 
	 */
	public int getLoginSphereLevel() {
		return getLevelForItem(getLoginSphereItem());
	}

	public MemberReference getMember() {
		return this.member;
	}

	/**
	 * @return
	 */
	public Tree getTree() {
		return this.checkViewer.getTree();
	}

	/**
	 * @return
	 */
	public TreeItem[] getTreeSelection() {
		return getTree().getSelection();
	}

	/**
	 * @param element
	 * @param checked
	 */
	public void setSubtreeChecked(Object element, boolean checked) {
		this.checkViewer.setSubtreeChecked(element, checked);
	}

	public List<TreeItem> getParentsForItem(TreeItem item) {
		List<TreeItem> parents = new ArrayList<TreeItem>();
		TreeItem tempItem = item;
		while (tempItem.getParentItem() != null) {
			parents.add(tempItem.getParentItem());
			tempItem = tempItem.getParentItem();
		}
		return parents;
	}

	public int getLevelForItem(TreeItem item) {
		return getParentsForItem(item).size();
	}

	/**
	 * @return
	 */
	public ManagedSphere getLoginSphere() {
		if (getLoginSphereItem() == null) {
			return null;
		}
		return (ManagedSphere) getLoginSphereItem().getData();
	}

	/**
	 * 
	 */
	public void refreshTree() {
		this.checkViewer.refresh();
	}

	/**
	 * @param loginSphereItem
	 */
	public void setAsLoginSphere(TreeItem loginSphereItem) {
		loginSphereItem.setChecked(true);
		setLoginSphereItem(loginSphereItem);
		this.owner.getManager().setChangedLoginSphereObject(getMember(), ((ManagedSphere)loginSphereItem.getData()).getId());
	}

	/**
	 * @return
	 */
	public TreeItem getLoginSphereItem() {
		return this.loginSphereItem;
	}

	public boolean belongAdmin() {
		return SupraSphereFrame.INSTANCE.client.getVerifyAuth().isAdmin(
				this.member.getContactName(), this.member.getLoginName());
	}

	/**
	 * 
	 */
	public TreeViewer getTreeViewer() {
		return this.checkViewer;
	}

	/**
	 * @return the manager
	 */
	private MemberAccessManager getManager() {
		return this.owner.getManager();
	}
	
	private IChangesDetector getDetector() {
		return this.detector;
	}

}
