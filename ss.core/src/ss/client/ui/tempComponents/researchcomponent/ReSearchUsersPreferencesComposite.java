/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.common.ListUtils;

/**
 * @author zobo
 *
 */
public class ReSearchUsersPreferencesComposite extends
		ReSearchAbstractPreferencesComposite {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReSearchUsersPreferencesComposite.class);
	
	private TableViewer viewer;
	
	private Hashtable<String, Boolean> users;

	public ReSearchUsersPreferencesComposite(Composite parent) {
		super(parent);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent) {
		parent.setLayout( new GridLayout() );
		
		Label label = new Label( parent, SWT.CENTER );
		label.setText( "Check users to get tags:" );
		label.setLayoutData( LayoutUtils.createFillHorizontalGridData() );
		
		this.users = new Hashtable<String, Boolean>();
		createTable( parent );
	}
	
	private void createTable(final Composite viewComposite) {
		this.viewer = new TableViewer(viewComposite, SWT.FULL_SELECTION
				| SWT.BORDER);
		this.viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		this.viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object data) {
				return ((Hashtable<String, Boolean>) data).keySet().toArray();
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
				
			}
			
		});
		this.viewer.setLabelProvider( new AllowedUsersLabelProvider(this) );
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);

		new TableColumn(this.viewer.getTable(), SWT.NONE).setText("X");
		new TableColumn(this.viewer.getTable(), SWT.NONE).setText("Contact Name");
		CellEditor[] editors = new CellEditor[2];
		editors[0] = new CheckboxCellEditor(this.viewer.getTable(), SWT.LEFT);
		editors[1] = editors[0];
		this.viewer.setCellEditors(editors);
		this.viewer.setColumnProperties(new String[] { "X", "Contact Name" });
		this.viewer.setCellModifier(new AllowedUsersCellModifier(this));
		this.viewer.getTable().setEnabled(true);
		this.viewer.setInput( this.users );
		packColumns();
	}

	protected void packColumns() {
		this.viewer.getTable().getColumns()[0].setWidth(30);
		this.viewer.getTable().getColumns()[1].setWidth(250);
	}
	
	@ss.refactor.Refactoring(classify=ss.refactor.supraspheredoc.SupraSphereRefactor.class)
	private Vector<String> getContactNames(){
		final Vector<String> list = new Vector<String>();
	
		final String contactName = SupraSphereFrame.INSTANCE.client.getContact();
		final Vector<String> members = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getContactsForMembersEnabled1(contactName);
		
		if ( (members == null) || (members.isEmpty()) ) {
			logger.warn("No allowed members");
			return list;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("members: " + ListUtils.allValuesToString( members ));
		}
	
		for (String member : members) {
			if (!contactName.equals(member)) {
				list.add( member );
			}
		}		
		return list;
	}
	
	

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#fill(ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer)
	 */
	@Override
	public void fill(ResearchComponentDataContainer container) {
		if (logger.isDebugEnabled()) {
			logger.debug(container.toString());
		}
		
		final List<String> allowedUsersContactNames = new ArrayList<String>();
		for (String contact : this.users.keySet()){
			if (this.users.get( contact ).booleanValue()) {
				allowedUsersContactNames.add( contact );
			}
		}
		container.setAllowedUsersContactNames( allowedUsersContactNames );
		if (logger.isDebugEnabled()) {
			logger.debug(container.toString());
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#getTitle()
	 */
	@Override
	public String getTitle() {
		return "users";
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#set(ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer)
	 */
	@Override
	public void set( final ResearchComponentDataContainer container ) {
		this.users = new Hashtable<String, Boolean>();
		final List<String> contacts = getContactNames();
		final List<String> allowedcontacts;
		if ( container == null ) {
			logger.error( "container is null" );
			allowedcontacts = contacts;
		} else {
			allowedcontacts = container.getAllowedUsersContactNames();
		}
		for (String contact : contacts) {
			this.users.put( contact, new Boolean( allowedcontacts == null ?
					true : allowedcontacts.contains(contact)) );
		}
		this.viewer.setInput( this.users );
		this.viewer.refresh();
	}

	public Hashtable<String, Boolean> getUsers() {
		return this.users;
	}

	/**
	 * @return
	 */
	public TableViewer getViewer() {
		return this.viewer;
	}
}
