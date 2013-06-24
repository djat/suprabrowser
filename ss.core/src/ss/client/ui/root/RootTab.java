/**
 * 
 */
package ss.client.ui.root;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.PeopleTable;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.peoplelist.IPeopleList;
import ss.client.ui.root.actions.IRootCreateActions;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereReference;
import ss.framework.networking2.ReplyObjectHandler;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class RootTab extends SupraTab implements IRootCreateActions{
	
	private static final String ROOT_TAB = "ROOTTAB.ROOT_TAB";

	private static final String ALL_MEMBERS_PEOPLETABLE_TITLE = "ROOTTAB.ALL_MEMBERS";
	
    private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_ROOT_ROOTTAB);

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootTab.class);

	private RootSphereHierarchyComposite hierarchyComposite;
	
	private RootControlPanel controlPanel;

	private PeopleTable table;
	
	private static RootTab INSTANCE;
	
	public RootTab(final Composite parent){
		super(parent);
		setUpTimeDifferenceWithServer();
	}
	
	/**
	 * 
	 */
	private void setUpTimeDifferenceWithServer() {
		if (logger.isDebugEnabled()) {
			logger.debug("Asking server for server time.");
		}
		SupraSphereFrame.INSTANCE.setTimeDifference(0);
		Runnable runnable = new Runnable() {
			public void run() {
				final Date localDate = Calendar.getInstance().getTime();
				final Date serverDate = SupraSphereFrame.INSTANCE.client.getCurrentDateTime();
				final long difference = serverDate.getTime() - localDate.getTime();
				if (logger.isDebugEnabled()) {
					logger.debug("Time from server recieved: " + serverDate.toString());
					logger.debug("Local time : " + localDate.toString());
					logger.debug("Difference in milisec: " + difference);
				}
				SupraSphereFrame.INSTANCE.setTimeDifference( difference );
			}
		};
		ThreadUtils.startDemon(runnable, "Getter of server time");
	}
	
	@Override
	public boolean isRoot() {
		if (logger.isDebugEnabled()){
			logger.debug("Is root sphere performed, returning true");
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.root.SupraTab#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		INSTANCE = this;
		boolean admin = getSupraSphereFrame().client.getVerifyAuth().isAdmin();
		if (admin) {
			getSupraSphereFrame().getMenuBar().addAdministrativeItems();
		}
		if(VariousUtils.IS_CLUBDEALABLE && (admin || VariousUtils.canAccessClubdealAdministrate(SupraSphereFrame.INSTANCE.client))) {
			getSupraSphereFrame().getMenuBar().addClubdealAdministrateItem();
		}
		getSupraSphereFrame().getMenuBar().enableWholeMenu(true);
		if (logger.isDebugEnabled()){
			logger.debug("Root Tab contents creation started");
		}
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		parent.setLayout(layout);
		GridData layoutData;
		
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);
		layoutData.widthHint = 200;
        createPeopleTable(parent).setLayoutData(layoutData);
		beginCreateSphereHierarchy(parent);
	}

	/**
	 * @param parent
	 */
	private void beginCreateSphereHierarchy(final Composite parent) {
		ThreadUtils.startDemon( new Runnable() {
			public void run() {
				final RootSphereDefinitionProvider prov = new RootSphereDefinitionProvider(getSupraSphereFrame().client);
				prov.checkOutOfDate();
				if (logger.isDebugEnabled()){
					logger.debug("Sphere Definition Provider initialized");
				}
				UiUtils.swtBeginInvoke( new Runnable() {
					public void run() {
						createSphereHierarchy( parent, prov );
						createSupraSearchPanel(parent);
						parent.layout();
					}
				} );
			}
		}, "RootSphereHieararchyCreate" );				
	}
	
	private void createSphereHierarchy( Composite parent, ISphereDefinitionProvider provider ) {
		GridData layoutData;
		this.hierarchyComposite = new RootSphereHierarchyComposite(parent, provider);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.hierarchyComposite.setLayoutData(layoutData);
		if (logger.isDebugEnabled()){
			logger.debug("Root Tab contents creation finished");
		}	
	}
	
	private void createSupraSearchPanel(final Composite parent){
		SupraSearchRootPanel panel = new SupraSearchRootPanel(parent);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false);
		panel.setLayoutData(layoutData);
	}
	
	private Control createPeopleTable(Composite parent) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout(1, false));
		
		GridData layoutData;
		
		this.controlPanel = new RootControlPanel(comp, SWT.NONE, this);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		this.controlPanel.setLayoutData(layoutData);
		
		Label label = new Label(comp, SWT.NONE);
		label.setText(bundle.getString(ALL_MEMBERS_PEOPLETABLE_TITLE));
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);		
		label.setLayoutData(layoutData);
		
		Composite tablecomp = new Composite(comp, SWT.NONE);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tablecomp.setLayoutData(layoutData);
		tablecomp.setLayout(new FillLayout());
		this.table = new PeopleTable(new RootPeopleListOwner());
		this.table.createUi(tablecomp);
		this.table.addMouseListener();
		addUsers(this.table);
		return comp;
	}
	
	/**
	 * @param table
	 */
	@SuppressWarnings("unchecked")
	private void addUsers(PeopleTable table) {
		final List<String> contactNames = getAvaliableContactsNames();
		getSupraSphereFrame().client.queryMembersStatesForUi( contactNames, new ReplyObjectHandler<Hashtable<String,Boolean>>( (Class)Hashtable.class ) {
			@Override
			protected void objectReturned(final Hashtable<String, Boolean> reply) {
				if ( reply != null ) {
					setUpInitialMemberList( reply );
				}
			}
		});
//		if ( privateSpheres.size() > 0 ) {
//			for (SphereReference privateSphere : privateSpheres){
//				table.refreshMemberPresence(privateSphere.getDisplayName(), false);
//			}
//			table.refreshMemberPresence(privateSpheres.get(0).getDisplayName(), true);
//		}
	}

	/**
	 * @return
	 */
	private List<String> getAvaliableContactsNames() {
		VerifyAuth auth = getSupraSphereFrame().client.getVerifyAuth();
		final List<SphereReference> privateSpheres = auth.getAllAvailablePrivateSpheres(getSupraSphereFrame().client.getLogin());
		final List<String> contactNames = new ArrayList<String>();
		for( SphereReference privateSphere : privateSpheres ) {
			contactNames.add( privateSphere.getDisplayName() );
		}
		return contactNames;
	}

	/**
	 * @param reply
	 */
	protected void setUpInitialMemberList(Hashtable<String, Boolean> contactNameToState) {
		final PeopleTable peopleTable = this.table;
		for (String contactName : contactNameToState.keySet() ){
			final Boolean state = contactNameToState.get(contactName);
			peopleTable.refreshMemberPresence( contactName, state != null ? state.booleanValue() : false );
		}
	}

	/**
	 * @return
	 */
	private SupraSphereFrame getSupraSphereFrame() {
		return SupraSphereFrame.INSTANCE;
	}

	/**
	 * @return 
	 * 
	 */
	public String getSelectedSphere() {
		return this.hierarchyComposite.getSelectedSphere().getId();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.root.actions.IRootCreateActions#performCreateSphereAction()
	 */
	public void performCreateSphereAction() {
		this.controlPanel.performCreateSphereAction();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.root.actions.IRootCreateActions#performCreateUserAction()
	 */
	public void performCreateUserAction() {
		this.controlPanel.performCreateUserAction();
	}

	@Override
	public String getDesiredTitle() {
		return bundle.getString(ROOT_TAB);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.root.SupraTab#getPeopleTable()
	 */
	@Override
	public IPeopleList getPeopleTable() {
		return this.table;
	}

	public RootSphereHierarchyComposite getHierarchyComponent() {
		return this.hierarchyComposite;
	}

	private void refreshPeopleTable() {
		addUsers(this.table);
	}

	/**
	 * 
	 */
	public void refreshPeopleTables(ContactStatement contact) {
		refreshPeopleTable();
		this.hierarchyComposite.refreshPeopleTable(contact);
	}

	/**
	 * @return the iNSTANCE
	 */
	public static RootTab getInstance() {
		return INSTANCE;
	}
	
}
