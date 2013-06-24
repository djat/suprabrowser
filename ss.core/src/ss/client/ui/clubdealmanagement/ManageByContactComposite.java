/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.clubdealmanagement.contact.ChangeUserContactName;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ManageByContactComposite extends AbstractManageComposite<ContactStatement, ClubdealWithContactsObject> {

	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_MANAGEBYCONTACTCOMPOSITE);
	
	@SuppressWarnings("unused")
	private final static Logger logger = SSLogger
			.getLogger(ManageByContactComposite.class);
	
	private TableViewer peopleViewer;

	private ManageContactTypeComposite manageTypeComposite;

	public static final String NAME = "MANAGEBYCONTACTCOMPOSITE.NAME";

	public static final String TYPE = "MANAGEBYCONTACTCOMPOSITE.TYPE";
	
	public static final String CLUBDEALS = "MANAGEBYCONTACTCOMPOSITE.CLUBDEALS";
	
	public static final String APPLY = "MANAGEBYCONTACTCOMPOSITE.APPLY";

	public ManageByContactComposite(ClubdealFolder folder) {
		super(folder);
		createContent();
	}

	@Override
	protected void createContent() {
		setLayout(new GridLayout(2, false));

		this.peopleViewer = new TableViewer(this, SWT.SINGLE | SWT.BORDER);
		this.peopleViewer.setContentProvider(new PeopleContentProvider());
		this.peopleViewer.setLabelProvider(new PeopleLableProvider());
		this.peopleViewer.setInput(getManager());
		this.peopleViewer.getTable().setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.peopleViewer.addSelectionChangedListener(getListListener());
		this.peopleViewer.getTable().addMouseListener( getMouseListener() );

		Composite viewComposite = new Composite(this, SWT.NONE);
		viewComposite.setLayout(new GridLayout(2, false));
		viewComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite tableComposite = new Composite(viewComposite, SWT.BORDER);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label clubdealLabel = new Label(tableComposite, SWT.LEFT);
		clubdealLabel.setText(bundle.getString(CLUBDEALS));

		createTable(tableComposite);
		
		this.manageTypeComposite = new ManageContactTypeComposite( viewComposite , getFolder() );
		this.manageTypeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Composite buttonComp = new Composite(viewComposite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		buttonComp.setLayoutData( data );
		buttonComp.setLayout(new GridLayout(4, false));

		this.applyButton = new Button(buttonComp, SWT.PUSH);
		this.applyButton.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, true, false, 1, 1 ) );
		this.applyButton.setText(bundle.getString(APPLY));
		this.applyButton.setEnabled(false);
		this.applyButton.addSelectionListener(getApplyListener());

		layout();
	}

	/**
	 * @return
	 */
	private MouseListener getMouseListener() {
		final MouseListener listener = new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				final VerifyAuth auth = ManageByContactComposite.this.getManager().getClient().getVerifyAuth();
				if ( auth == null ) {
					logger.error("auth is null");
					return;
				}
				if ( !auth.isAdmin() ) {
					if (logger.isDebugEnabled()) {
						logger.debug("Only admin can change users contact name");
					}
					return;
				}
				if(e.button==3 ) {
					if (logger.isDebugEnabled()) {
						logger.debug("Right click perfomed");
					}
					final Object o = ((StructuredSelection)ManageByContactComposite.this.peopleViewer.getSelection()).getFirstElement(); 
					if( o==null ) {
						logger.error("First element is null");
						return;
					}
					if (!(o instanceof ContactStatement)){
						logger.error("Object is not a ContactStatement");
						return;
					}
					final ContactStatement st = (ContactStatement) o;
					
					final String login = auth.getLoginForContact(st.getContactNameByFirstAndLastNames());
					boolean enabled = true;
					if (StringUtils.isBlank(login)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Not a user");
						}
						enabled = false;
					}
					if ( auth.isAdmin(st.getContactNameByFirstAndLastNames(), login) && !auth.isPrimaryAdmin() ) {
						if (logger.isDebugEnabled()) {
							logger.debug("Change admins contact name can only primary admin");
						}
						enabled = false;
					}
					
					final Menu menu = new Menu( ManageByContactComposite.this.peopleViewer.getTable() );
					final MenuItem editItem = new MenuItem(menu,SWT.PUSH);
					editItem.setText("Change member contact name...");
					editItem.addSelectionListener(new SelectionAdapter(){
						public void widgetSelected(SelectionEvent e) {
							final ChangeUserContactName cucn = new ChangeUserContactName( ManageByContactComposite.this.getShell() );
							cucn.setContact(st);
							cucn.open();
						}
					});
					editItem.setEnabled(enabled);
					menu.setVisible( true );					
				}
			}

			public void mouseUp(MouseEvent e) {
			}
			
		};
		return listener;
	}

	/**
	 * @param viewComposite
	 */
	private void createTable(Composite viewComposite) {
		this.viewer = CheckboxTableViewer.newCheckList(viewComposite, SWT.CHECK | SWT.BORDER);
		this.viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer.setContentProvider(new ClubdealContentProvider());
		this.viewer.setLabelProvider(new ClubdealLabelProvider(this));
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);
		new TableColumn(this.viewer.getTable(), SWT.NONE).setText(bundle.getString(NAME));
		new TableColumn(this.viewer.getTable(), SWT.NONE).setText(bundle.getString(TYPE));
		CellEditor[] editors = new CellEditor[2];
		editors[0] = new TextCellEditor(this.viewer.getTable(), SWT.LEFT);
		editors[1] = new ComboBoxCellEditor(this.viewer.getTable(), this.getManager().getContactTypes().toArray(), SWT.READ_ONLY | SWT.LEFT);

		this.viewer.setColumnProperties(new String[] { NAME, TYPE });
		this.viewer.setCellModifier(new TypeCellModifier(this, this.viewer));
		this.viewer.getTable().setEnabled(false);

		this.viewer.setCellEditors(editors);
		this.viewer.setInput(getManager());
		this.viewer.addCheckStateListener(getCheckStateListener());
		this.viewer.addDoubleClickListener(getDoubleClickListener());
		packColumns();
	}

	/**
	 * @return
	 */
	private IDoubleClickListener getDoubleClickListener() {
		return new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent de) {
				ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject)((StructuredSelection)de.getSelection()).getFirstElement();
				SphereOpenManager.INSTANCE.request(clubdeal.getClubdealSystemName());
			}
		};
	}

	@Override
	protected void checkAvailableItems() {
//		if(getSelection()==null) {
//			return;
//		}
//		List<ClubdealWithContactsObject> list = getManager().getClubdeals(
//				getSelection().getContactNameByFirstAndLastNames());
//		for (TableItem item : this.viewer.getTable().getItems()) {
//			item.setChecked(list.contains(item.getData()));
//		}
		if(getSelection()==null) {
			return;
		}
		List<ClubdealWithContactsObject> list = getManager().getClubdeals(
				getSelection().getContactNameByFirstAndLastNames());
		this.viewer.setCheckedElements(list.toArray());
	}

	@Override
	protected IChangesCollector getNewCollector() {
		return new ContactChangesCollector(this);
	}

	@Override
	protected void setChanged(boolean value) {
		super.setChanged(value);
		getChangesDetector().setContactsChanged(value);
	}

	@Override
	public void refreshViewer() {
		((ComboBoxCellEditor) this.viewer.getCellEditors()[1])
				.setItems(this.getManager().getContactTypes().toArray());
		this.manageTypeComposite.refreshList();
		super.refreshViewer();
	}

	@Override
	public ContactStatement getSelection() {
		if(this.peopleViewer == null) {
			return null;
		}
		if(this.peopleViewer.getSelection()==null) {
			return null;
		}
		return (ContactStatement)((StructuredSelection)this.peopleViewer.getSelection()).getFirstElement();
	}

	@Override
	protected boolean revertCheckingIfNeed(ClubdealWithContactsObject clubdeal) {
		ContactStatement contact = getSelection();
		boolean canModerate = ModerationUtils.INSTANCE.canModerate(getFolder().getWindow().getClient(), 
				clubdeal.getClubdealSystemName(), contact
				.getContactNameByFirstAndLastNames());
		if (canModerate) {
			return false;
		}
		boolean checkState = this.viewer.getChecked(clubdeal);
		this.viewer.setChecked(clubdeal, !checkState);
		return true;
	}

	/**
	 * @return
	 */
	public Button getApplyButton() {
		return this.applyButton;
	}
}
