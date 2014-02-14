/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.bymembers;

import java.util.Hashtable;
import java.util.Set;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ss.domainmodel.MemberReference;

/**
 * @author roman
 *
 */
public class PeopleListComposite extends Composite {

	private final static String PEOPLE = "People";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PeopleListComposite.class);
	
	private TableViewer tableViewer;
		
	private Hashtable<MemberReference, Boolean> membersWithState;

	/**
	 * @param parent
	 * @param arg1
	 */
	public PeopleListComposite(Composite parent, Hashtable<MemberReference, Boolean> members) {
		super(parent, SWT.NONE);
		this.membersWithState = members;		
		setLayout(new GridLayout());
		createLabel();
		createPeopleList();		
	}

	/**
	 * 
	 */
	private void createLabel() {
		Label label = new Label(this, SWT.LEFT | SWT.BOTTOM);
		label.setText(PEOPLE);
	}

	private void createPeopleList() {
		this.tableViewer = new TableViewer(this);
		this.tableViewer.setLabelProvider(new LabelProvider());
		this.tableViewer.setContentProvider(new ContentProvider());		
		Table table = this.tableViewer.getTable(); 
		table.setLayoutData(new GridData(GridData.FILL_BOTH));		
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(PEOPLE);
		Set<MemberReference> memberList = this.membersWithState.keySet();//SupraSphereFrame.INSTANCE.client.getVerifyAuth().getAllMembers();
		this.tableViewer.setInput(memberList);		
		col.pack();
	}
	
	private class LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}

		public String getColumnText(Object obj, int index) {
			MemberReference member = (MemberReference)obj;
			return member.getContactName();
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
	
	private class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object obj) {
		return ((Set)obj).toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}
	
	public void addSelectionListener( ISelectionChangedListener listener) {
		this.tableViewer.addSelectionChangedListener( listener );
	}

	/**
	 * @param previousMember
	 */
	public void selectMember(MemberReference previousMember) {
		for(TableItem item : this.tableViewer.getTable().getItems()) {
			if(((MemberReference)item.getData()).getLoginName().equals(previousMember.getLoginName())) {
				this.tableViewer.getTable().setSelection(item);
				return;
			}
		}
	}

}
