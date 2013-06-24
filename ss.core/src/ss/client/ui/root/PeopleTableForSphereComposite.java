/**
 * 
 */
package ss.client.ui.root;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ss.client.event.HierarchyPeopleTableMenuListener;
import ss.client.event.HierarchyPeopleTablePrivateMenuItemListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.peoplelist.IMemberSelection;
import ss.client.ui.peoplelist.SphereMember;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.domainmodel.MemberReference;

/**
 *
 */
class PeopleTableForSphereComposite implements IMemberSelection {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PeopleTableForSphereComposite.class);
	
	private TableViewer tv;
	
	private List<MemberReference> memberReferences;
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_SPHEREHIERARCHYDIALOG);
	
	private static final String PEOPLE = "SPHEREHIERARCHYDIALOG.PEOPLE";
	

	public PeopleTableForSphereComposite(RootSphereHierarchyComposite parent, String sphereId) {
		
		if(sphereId!=null) {
			this.memberReferences = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getMembersForSphere(sphereId);
		} else {
			this.memberReferences = new ArrayList<MemberReference>();
		}
		
		this.tv = new TableViewer(parent.getPeopleTableOwner(), SWT.NONE);
		this.tv.setLabelProvider(new PeopleLabelProvider());
		this.tv.setContentProvider(new PeopleContentProvider());
		
		Table table = this.tv.getTable();
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(this.bundle.getString(PEOPLE));
	
		this.tv.setInput(this.memberReferences);
		
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		table.setHeaderVisible(true);
		
		addDoubleClickListener();
		addRightClickListener();
		
		col.pack();
	}

	/**
	 * @param rightClickListener
	 */
	private void addRightClickListener() {
		this.tv.getTable().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if(!SupraSphereFrame.INSTANCE.client.getVerifyAuth().isAdmin()) {
					return;
				}
				if(event.button==3 && event.count==1) {
					showPopupMenu();
				}
			}
		});
	}

	public void addDoubleClickListener() {
		this.tv.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClick(event);
			}
		});	
	}
	
	
	private void handleDoubleClick(DoubleClickEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("sphere hierarcy dialog people table double clicked");
		}
		TableViewer tv = (TableViewer) event.getSource();
		if (tv.getTable().getSelectionCount() > 0) {
			MemberReference contact = (MemberReference) tv.getTable()
					.getSelection()[0].getData();
			SphereOpenManager.INSTANCE.requestUser(contact.getContactName());
		}
	}
	
	public void close() {
		this.tv.getTable().dispose();
	}
	
	public boolean isDisposed() {
		return this.tv.getTable().isDisposed();
	}
	
	public void setLayoutData() {
		this.tv.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	private class PeopleLabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}
		
		public String getColumnText(Object obj, int index) {
			MemberReference contact = (MemberReference)obj;
			if(index==0) {
				return contact.getContactName();
			}
			return null;
		}

		public void addListener(ILabelProviderListener arg0) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		public void removeListener(ILabelProviderListener arg0) {
		}
	}
	
	
	private class PeopleContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object obj) {
			List list = (List) obj; 
			return list.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}

	/**
	 * 
	 */
	public void showPopupMenu() {
		if(this.tv.getTable().getItemCount()<1) {
			return;
		}
		
		Menu menu = new Menu(this.tv.getTable());

		MenuItem showP2PSphereItem = new MenuItem(menu, SWT.CASCADE);
		showP2PSphereItem.setText("Open P2P sphere");

		MenuItem showPrivateSphereItem = new MenuItem(menu, SWT.PUSH);
		showPrivateSphereItem.setText("Open Contact Private Sphere");
		Object o = this.tv.getTable().getSelection()[0].getData();
		showPrivateSphereItem.setData(o);
		showPrivateSphereItem
				.addSelectionListener(new HierarchyPeopleTablePrivateMenuItemListener());

		Menu dropDownP2PMenu = new Menu(showP2PSphereItem);
		showP2PSphereItem.setMenu(dropDownP2PMenu);
		dropDownP2PMenu.setVisible(true);

		setUpDropDownMenu(dropDownP2PMenu);
		
		MenuItem emailBoxItem = new MenuItem(menu, SWT.PUSH);
		MemberReference member = (MemberReference)this.tv.getTable().getSelection()[0].getData();
		final String emailBoxId = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getEmailSphere(member.getLoginName(), member.getContactName());
		emailBoxItem.setText(SupraSphereFrame.INSTANCE.client.getVerifyAuth().getDisplayName(emailBoxId));
		emailBoxItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SphereOpenManager.INSTANCE.request(emailBoxId);
			}
		});

		menu.setVisible(true);

	}

	/**
	 * @param dropDownP2PMenu
	 */
	@SuppressWarnings("unchecked")
	private void setUpDropDownMenu(Menu dropDownP2PMenu) {
		TableItem[] items = this.tv.getTable().getSelection();

		if (items == null || items.length == 0) {
			return;
		}

		MemberReference memberOwner = (MemberReference) items[0].getData();

		for (MemberReference ref : (( List<MemberReference> ) this.tv.getInput())){
			MenuItem item = new MenuItem(dropDownP2PMenu, SWT.PUSH);
			item.setText(ref.getContactName());
			item.setData(ref);
			item.addSelectionListener(new HierarchyPeopleTableMenuListener(memberOwner, ref));
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.ISelectedMemberReturner#getSelectedMember()
	 */
	public SphereMember getSelectedMember() {
		return null;
	}
}
