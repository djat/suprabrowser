/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SDisplay;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.ModerateAccessMember;
import ss.domainmodel.configuration.ModerateAccessMemberList;
import ss.domainmodel.configuration.ModerationAccessModel;
import ss.domainmodel.configuration.ModerationAccessModelList;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ModeratorsComposite extends Composite implements IClubdealListParent {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ModeratorsComposite.class);
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_MANAGEBYCLUBDEALCOMPOSITE);
	
	private static final String PEOPLE = "MANGEBYCLUBDEALCOMPOSITE.PEOPLE";
	
	private ClubdealFolder folder;
	
	private ClubdealList list;
	
	private TableViewer viewer;
	
	private ConfigurationValue configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
	
	private boolean changed = false;
	
	private Composite tableButtonComp;

	private Button applyButton;
	/**
	 * @param parent
	 * @param style
	 */
	public ModeratorsComposite(ClubdealFolder folder, int style) {
		super(folder, style);
		this.folder = folder;
		createContent();
	}
	
	public void createContent() {
		setLayout(new GridLayout(2, false));
		
		this.list = new ClubdealList(this);
		this.list.addSelectionListener(getSelectionListener());
		
		this.tableButtonComp = new Composite(this, SWT.NONE);
		this.tableButtonComp.setLayout(new GridLayout());
		this.tableButtonComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.viewer = new TableViewer(this.tableButtonComp, SWT.BORDER | SWT.CHECK);
		this.viewer.setContentProvider(new ContactContentProvider());
		this.viewer.setLabelProvider(new ContactLabelProvider(null));
		this.viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);
		this.viewer.getTable().addSelectionListener(getCheckTableListener());
		this.viewer.setInput(getManager());
		this.viewer.getTable().setEnabled(false);
		TableColumn col = new TableColumn(this.viewer.getTable(), SWT.LEFT);
		col.setText(bundle.getString(PEOPLE));
		packColumns();
		
		Composite comp = new Composite(this.tableButtonComp, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		this.applyButton = new Button(comp, SWT.PUSH);
		this.applyButton.setText("Apply");
		this.applyButton.setEnabled(false);
		this.applyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveChanges();
			}
		});
	}
	
	protected void packColumns() {
		for(TableColumn col : this.viewer.getTable().getColumns()) {
			col.setWidth(100);
		}
	}

	public ClubdealManager getManager() {
		return this.folder.getWindow().getManager();
	}

	public void refresh() {
		refreshModeratorConfiguration();
		this.list.refresh();
		checkMemberState(this.list.getSelection());
	}
	
	/**
	 * 
	 */
	private void refreshModeratorConfiguration() {
		this.configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
	}

	protected SelectionListener getCheckTableListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.detail==SWT.CHECK) {
					ContactStatement contact = (ContactStatement)e.item.getData();
					boolean isAdmin = ModeratorsComposite.this.folder.getWindow().getClient().getVerifyAuth().isAdmin(contact);
					if(isAdmin) {
						((TableItem)e.item).setChecked(true);
						return;
					}
					if(!getSelectedClubDeal().hasContact(contact)) {
						((TableItem)e.item).setChecked(false);
						return;
					}
					setChanged(true);
				}
			}
		};
	}
	
	private void setChanged(final boolean changed) {
		this.changed = changed;
		getChangesDetector().setAccessChanged(changed);
		setApplyEnabled(changed);
	}
	
	/**
	 * @param changed2
	 */
	private void setApplyEnabled(boolean changed) {
		this.applyButton.setEnabled(changed);
	}

	public boolean isChanged() {
		return this.changed;
	}
	
	public TableViewer getViewer() {
		return this.viewer;
	}
	
	public ClubdealWithContactsObject getSelectedClubDeal() {
		return this.list.getSelection();
	}

	public ISelectionChangedListener getSelectionListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent se) {
				getViewer().getTable().setEnabled(se.getSelection() != null);
				if (se.getSelection() == null) {
					checkMemberState(null);
					return;
				}
				ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject) ((StructuredSelection) se
						.getSelection()).getFirstElement();
				checkMemberState(clubdeal);
			}
		};
	}

	protected void checkMemberState(final ClubdealWithContactsObject clubdeal) {
		if(clubdeal==null) {
			setTotalFalseChecked();
			return;
		}
		ModerationAccessModel cdAccess = getAccessList().getBySystemName(clubdeal.getClubdealSystemName());
		if(cdAccess==null) {
			setTotalFalseChecked();
			return;
		}
		ModerateAccessMemberList memberlist = cdAccess.getMemberList();
		for(TableItem item : this.viewer.getTable().getItems()) {
			ContactStatement contact = (ContactStatement)item.getData();
			ModerateAccessMember member = memberlist.getMemberByContactName(contact.getContactNameByFirstAndLastNames()); 
			item.setChecked(member!=null && member.isModerator());
			boolean isAdmin = this.folder.getWindow().getClient().getVerifyAuth().isAdmin(contact);
			int colorConst = isAdmin || clubdeal.hasContact(contact) ? SWT.COLOR_BLACK : SWT.COLOR_DARK_GRAY;
			item.setForeground(SDisplay.display.get().getSystemColor(colorConst));
		}
	}

	/**
	 * 
	 */
	private void setTotalFalseChecked() {
		for (TableItem item : this.viewer.getTable().getItems()) {
			ContactStatement contact = (ContactStatement) item.getData();
			boolean isAdmin = this.folder.getWindow().getClient().getVerifyAuth().isAdmin(contact);
			item.setChecked(isAdmin);
		}
	}

	public ModerationAccessModelList getAccessList() {
		return this.configuration.getClubdealModerateAccesses();
	}

	/**
	 * 
	 */
	public void saveChanges() {
		if(!isChanged()) { 
			return;
		}
		
		ClubdealWithContactsObject clubdeal = this.list.getSelection();
		ModerationAccessModel cdAccess = getAccessList().getBySystemName(clubdeal.getClubdealSystemName());
		if(cdAccess==null) {
			cdAccess = createClubdealAccess(clubdeal);
			getAccessList().addClubdealAccess(cdAccess);
		}
		
		for(TableItem item : this.viewer.getTable().getItems()) {
			ContactStatement contact = (ContactStatement)item.getData();
			ModerateAccessMember member = cdAccess.getMemberList().getMemberByContactName(contact.getContactNameByFirstAndLastNames());
			if(member==null) {
				member = new ModerateAccessMember();
				member.setContactName(contact.getContactNameByFirstAndLastNames());
				member.setLoginName(contact.getLogin());
				member.setModerator(false);
				cdAccess.getMemberList().addMember(member);
			}
			updateModeratorVisibility(item, member);
			member.setModerator(item.getChecked());
		}
		SsDomain.CONFIGURATION.setMainConfigurationValue(this.configuration);
		
		setChanged(false);
	}

	/**
	 * @param item
	 * @param member
	 */
	private void updateModeratorVisibility(final TableItem item,
			final ModerateAccessMember member) {
		if (member.isModerator() == item.getChecked()) {
			return;
		}
		this.folder.getWindow().getClient().updateClubdealVisibilityForMember(
				getSelectedClubDeal().getClubdeal(), member.getContactName(),
				item.getChecked());
	}
	
	/**
	 * @param clubdeal
	 * @return
	 */
	private ModerationAccessModel createClubdealAccess(
			final ClubdealWithContactsObject clubdeal) {
		ModerationAccessModel cdAccess = new ModerationAccessModel();
		cdAccess.setDisplayName(clubdeal.getClubDealDisplayName());
		cdAccess.setSystemName(clubdeal.getClubdealSystemName());
		return cdAccess;
	}

	private ChangesDetector getChangesDetector() {
		return this.folder.getWindow().getChangesDetector();
	}
}
