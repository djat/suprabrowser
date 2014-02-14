/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.clubdealmanagement.contact.ContactEditedListener;
import ss.client.ui.clubdealmanagement.contact.ContactEditor;
import ss.client.ui.clubdealmanagement.contact.ContactToMemberConverter;
import ss.client.ui.clubdealmanagement.contact.PromoteToUserDialog;
import ss.client.ui.clubdealmanagement.sphere.role.ManageSphereRoleComposite;
import ss.client.ui.spheremanagement.memberaccess.SphereMemberBundle;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.clubdeals.ClubDeal;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;


/**
 * @author roman
 *
 */
public class ManageByClubdealComposite extends AbstractManageComposite<ClubdealWithContactsObject, ContactStatement> implements IClubdealListParent {
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ManageByClubdealComposite.class);
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_MANAGEBYCLUBDEALCOMPOSITE);
	
	private ClubdealList list;
	
	private Button addButton = null;
	
	private Button removeButton;

	private Composite rightSideComposite;
	
	private static final String PEOPLE = "MANGEBYCLUBDEALCOMPOSITE.PEOPLE";
	
	private static final String REMOVE = "MANGEBYCLUBDEALCOMPOSITE.REMOVE";
	
	private static final String ADD = "MANGEBYCLUBDEALCOMPOSITE.ADD";
	
	private static final String APPLY = "MANGEBYCLUBDEALCOMPOSITE.APPLY";
	
	public ManageByClubdealComposite(final ClubdealFolder folder) {
		super(folder);
		createContent();
	}

	@Override
	protected void createContent() {
		setLayout(new GridLayout(2, false));
		this.list = new ClubdealList(this);
		
		final Composite viewComposite = new Composite(this, SWT.NONE);
		viewComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewComposite.setLayout(new GridLayout(2, false));
		
		final Composite leftSideComposite = new Composite(viewComposite, SWT.BORDER);
		leftSideComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		leftSideComposite.setLayout(new GridLayout());
		
		this.rightSideComposite = new Composite(viewComposite, SWT.BORDER);
		this.rightSideComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.rightSideComposite.setLayout(new GridLayout());
		
		fillLeftSideComposite(leftSideComposite);
		
		ManageSphereRoleComposite roleComposite = new ManageSphereRoleComposite(this);
		roleComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite buttonCompositeNew = new Composite(viewComposite, SWT.NONE);
		buttonCompositeNew.setLayout(new GridLayout(1, false));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		buttonCompositeNew.setLayoutData(data);
		
		this.applyButton = new Button(buttonCompositeNew, SWT.PUSH);
		this.applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		this.applyButton.setText(bundle.getString(APPLY));
		this.applyButton.setEnabled(false);
		this.applyButton.addSelectionListener(getApplyListener());
		
		this.list.addSelectionListener(getListListener());
		this.list.addSelectionListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent ev) {
				if(ev!=null && ev.getSelection()!=null && ((StructuredSelection)ev.getSelection()).getFirstElement()!=null) {
					ManageByClubdealComposite.this.removeButton.setEnabled(true);
				}
			}
		});
		
		layout();
	}

	/**
	 * @param leftSideComposite
	 */
	private void fillLeftSideComposite(final Composite leftSideComposite) {
		this.viewer = CheckboxTableViewer.newCheckList(leftSideComposite, SWT.BORDER);
		this.viewer.setContentProvider(new ContactContentProvider());
		this.viewer.setLabelProvider(new ContactLabelProvider(this));
		this.viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);
		this.viewer.getTable().addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if(e.button==3 ) {
					if (logger.isDebugEnabled()) {
						logger.debug("Right click perfomed");
					}
					final Object o = ((StructuredSelection)ManageByClubdealComposite.this.viewer.getSelection()).getFirstElement(); 
					if( o==null ) {
						logger.error("First element is null");
						return;
					}
					if (!(o instanceof ContactStatement)){
						logger.error("Object is not a ContactStatement");
						return;
					}
					final ContactStatement st = (ContactStatement) o;
					final ContactStatement contact = 
						getSelectedClubdeal().getContactByName(st.getContactNameByFirstAndLastNames());
					if (contact == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Contact is not in clubdeal");
						}
						return;
					}
					final Menu menu = new Menu( ManageByClubdealComposite.this.viewer.getTable() );
					final MenuItem editItem = new MenuItem(menu,SWT.PUSH);
					editItem.setText("Edit contact...");
					editItem.addSelectionListener(new SelectionAdapter(){
						public void widgetSelected(SelectionEvent e) {
							editContact( contact );
						}
					});
					final MenuItem promoteItem = new MenuItem(menu,SWT.PUSH);
					promoteItem.setText("Make member...");
					promoteItem.addSelectionListener(new SelectionAdapter(){
						public void widgetSelected(SelectionEvent e) {
							promoteToUser( contact, getSelectedClubdeal().getClubdeal().getBindedDocument() );
						}
					});
					menu.setVisible( true );
				}
			}

			public void mouseUp(MouseEvent e) {
			}
			
		});
		this.viewer.addDoubleClickListener(new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				if ( event == null ) {
					logger.error("Event is null");
					return;
				}
				final Object o = ((StructuredSelection)event.getSelection()).getFirstElement(); 
				if( o==null ) {
					logger.error("First element is null");
					return;
				}
				if (!(o instanceof ContactStatement)){
					logger.error("Object is not a ContactStatement");
					return;
				}
				final ContactStatement st = (ContactStatement) o;
				final ContactStatement contact = 
					getSelectedClubdeal().getContactByName(st.getContactNameByFirstAndLastNames());
				editContact( contact );
			}
			
		});
		this.viewer.addCheckStateListener(getCheckStateListener());
		this.viewer.setInput(getManager());
		this.viewer.getTable().setEnabled(false);
		TableColumn col = new TableColumn(this.viewer.getTable(), SWT.LEFT);
		col.setText(bundle.getString(PEOPLE));
		packColumns();
		
		Composite buttonComposite = new Composite(leftSideComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(3, false));
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.removeButton = new Button(buttonComposite, SWT.PUSH);
		this.removeButton.setText(bundle.getString(REMOVE));
		this.removeButton.setEnabled(false);
		this.removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeClubDeal();
				ManageByClubdealComposite.this.viewer.getTable().setEnabled(false);
				ManageByClubdealComposite.this.list.refresh();
			}
		});
		
		this.addButton = new Button(buttonComposite, SWT.PUSH);
		this.addButton.setText(bundle.getString(ADD));
		this.addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new  AddClubdealDialog(ManageByClubdealComposite.this);
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
	}
	
	private void promoteToUser( final ContactStatement contact, final Document sphereDoc ){
		if ( (contact == null) || (sphereDoc == null) ) {
			return;
		}
		final DialogsMainCli cli = getFolder().getWindow().getClient();
		final String contactName = contact.getContactNameByFirstAndLastNames();
		if ( StringUtils.isBlank(contactName) ) {
			return;
		}
    	final String login = cli.getVerifyAuth().getLoginForContact( contactName );
        boolean isUserWithLoginNameExists = (contact.getLogin() != null) ? cli.getVerifyAuth().isUserExist(contact.getLogin()) : false;
        if ( (login != null) || isUserWithLoginNameExists ) {
        	if (logger.isDebugEnabled()) {
				logger.debug("contact already a member: " + contactName);
			}
        	UserMessageDialogCreator.warningYesCancelButton("Make contact: " + contactName + " a member of sphere?", new WarningDialogListener(){

				public void performCancel() {
				}

				public void performOK() {
		        	final List<SphereMemberBundle> added = new ArrayList<SphereMemberBundle>();
					final List<SphereMemberBundle> removed = new ArrayList<SphereMemberBundle>();
					MemberReference member = new MemberReference();
					member.setLoginName(login);
					member.setContactName(contactName);
					SphereStatement sphere = SphereStatement.wrap( sphereDoc );
					SphereMemberBundle item = new SphereMemberBundle( sphere , member );
					added.add( item  );
					cli.updateMemberVisibility(added, removed);					
				}
        		
        	}, false);
        } else {
        	if (logger.isDebugEnabled()) {
				logger.debug("contact is not a member yet: " + contactName);
			}
        	final PromoteToUserDialog dialog = new PromoteToUserDialog( getShell() );
			dialog.setContact(contact);
			final Hashtable session = (Hashtable) getFolder().getWindow().getClient().session.clone();
			session.put("sphere_id", getSelectedClubdeal().getClubdealSystemName());
			dialog.setConverter( new ContactToMemberConverter( getFolder().getWindow().getClient(),
					session) );
			dialog.open();
        }
	}
	
	private void editContact( final ContactStatement contact ){
		if ( contact != null ) {
			ContactEditor editor = new ContactEditor( contact , 
					getFolder().getWindow().getClient(), getSelectedClubdeal().getClubdealSystemName() );
			editor.addListener(new ContactEditedListener(){

				public void contactEdited(ContactStatement newContact) {
					getManager().setUp();
				}
				
			});
			editor.open();
		} else {
			UserMessageDialogCreator.warning( "Contact is not exists in sphere, can not be modified", "no contact");
		}
	}

	public ClubdealWithContactsObject getSelectedClubdeal() {
		return getSelection();
	}

	/**
	 * @param prefName
	 */
	public void saveClubdeal( final ClubDeal sphere, final String prefEmailAlias ) {
		getManager().addClubdeal( sphere, prefEmailAlias );
		this.list.refresh();
		this.viewer.getTable().setEnabled(false);
	}
	
	@Override
	protected void checkAvailableItems() {
		if(getSelectedClubdeal()==null) {
			return;
		}
		List<Object> checkedContacts = new ArrayList<Object>();
		for(TableItem item : this.viewer.getTable().getItems()) {
			if(getSelectedClubdeal().hasContact(item.getText())) {
				checkedContacts.add(item.getData());
			}
		}
		this.viewer.setCheckedElements(checkedContacts.toArray());
	}

	@Override
	protected IChangesCollector getNewCollector() {
		return new ClubDealChangesCollector(this);
	}

	@Override
	protected void setChanged(boolean value) {
		super.setChanged(value);
		getChangesDetector().setClubdealChanged(value);
	}

	/**
	 * 
	 */
	private void removeClubDeal() {
		getManager().putClubdealToRemoveList(getSelectedClubdeal());
		setChanged(true);
	}

	@Override
	public ClubdealWithContactsObject getSelection() {
		return this.list.getSelection();
	}

	@Override
	protected boolean revertCheckingIfNeed(ContactStatement contact) {
		ClubdealWithContactsObject clubdeal = getSelectedClubdeal();
		boolean canModerate = ModerationUtils.INSTANCE.canModerate(getFolder().getWindow().getClient(), 
				clubdeal.getClubdealSystemName(), contact
				.getContactNameByFirstAndLastNames());
		if (canModerate) {
			return false;
		}
		boolean checkState = this.viewer.getChecked(contact);
		this.viewer.setChecked(contact, !checkState);
		return true;
	}
	
	public Composite getRightSideComposite() {
		return this.rightSideComposite;
	}

	/**
	 * @return
	 */
	public Button getApplyButton() {
		return this.applyButton;
	}
}
