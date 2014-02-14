/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ResourceBundle;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;


/**
 * @author roman
 *
 */
public class ClubdealList extends Composite {

	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_CLUBDEALLIST);
	
	private final TableViewer viewer;
	
	private final IClubdealListParent manageComposite;
	
	private static final String CLUBDEALS = "CLUBDEALLIST.CLUBDEALS";
	
	public ClubdealList(IClubdealListParent parent) {
		super((Composite)parent, SWT.NONE);
		setLayout(new GridLayout());
		
		Label label = new Label(this, SWT.LEFT);
		label.setText(bundle.getString(CLUBDEALS));
		
		this.viewer = new TableViewer(this, SWT.BORDER);
		this.manageComposite = parent;
		createContent();
	}

	private void createContent() {
		setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		this.viewer.setContentProvider(new ClubdealListContentProvider());
		this.viewer.setLabelProvider(new ClubdealListLabelProvider());
		this.viewer.setInput(getParentComposite().getManager());
		this.viewer.addDoubleClickListener(getDoubleClickListener());
		Table table = this.viewer.getTable(); 
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.viewer.refresh();
		
		layout();
	}
	
	/**
	 * @return
	 */
	private IDoubleClickListener getDoubleClickListener() {
		return new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent de) {
				ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject) ((StructuredSelection) de
						.getSelection()).getFirstElement();
				SphereOpenManager.INSTANCE.request(clubdeal
						.getClubdealSystemName());
			}
		};
	}

	private IClubdealListParent getParentComposite() {
		return this.manageComposite;
	}

	/**
	 * 
	 */
	public void addSelectionListener(final ISelectionChangedListener listener) {
		this.viewer.addSelectionChangedListener(listener);
	}

	/**
	 * 
	 */
	public void refresh() {
		this.viewer.refresh();
	}
	
	public ClubdealWithContactsObject getSelection() {
		if (this.viewer == null) {
			return null;
		}
		if (this.viewer.getSelection() == null) {
			return null;
		}
		return (ClubdealWithContactsObject) ((StructuredSelection) this.viewer
				.getSelection()).getFirstElement();
	}
}
