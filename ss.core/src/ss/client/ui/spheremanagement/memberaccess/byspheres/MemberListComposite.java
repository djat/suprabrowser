/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.byspheres;

import java.util.ResourceBundle;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionAdaptor;
import ss.client.ui.spheremanagement.SphereManager;

/**
 * 
 */
public class MemberListComposite extends Composite {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MemberListComposite.class);

	private final static ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_SPHEREMANAGEMENT_MEMBERACCESS_BYSPERES_MEMBERLISTCOMPOSITE);

	/**
	 * 
	 */
	private static final String ACCESSIBLE = "MEMBERLISTCOMPOSITE.ACCESSIBLE";
	private static final String CONTACT_NAME = "MEMBERLISTCOMPOSITE.CONTACT_NAME";
	private static final String MEMBERS_ACCESS = "MEMBERLISTCOMPOSITE.MEMBERS_ACCESS";
	
	
	public static final String VISIBLITY_COLUMN_NAME = bundle.getString(ACCESSIBLE);
	public static final String CONTACT_COLUMN_NAME = bundle.getString(CONTACT_NAME);

	public static final String[] PROPS = new String[] { CONTACT_COLUMN_NAME,
		VISIBLITY_COLUMN_NAME };

	private static final Object[] EMPTY_ARRAY = null; // new Object[] {};

	private TableViewer memberViewer;

	private final SphereManager manager;
	
	private final IChangesDetector detector;

	private final MangerSelectionListener mangerSelectionListener = new MangerSelectionListener();

	/**
	 * @param parent
	 * @param manager
	 * @param style
	 */
	public MemberListComposite(Composite parent, SphereManager manager, IChangesDetector detector) {
		super(parent, SWT.NONE);
		this.manager = manager;
		this.detector = detector;
//		this.manager
//				.addSelectedSphereChangedListener(this.mangerSelectionListener);
		setLayout(LayoutUtils.createFullFillGridLayout());
		Label label = new Label(this, SWT.BEGINNING | SWT.BOTTOM);
		label.setText(bundle.getString(MEMBERS_ACCESS));
		this.memberViewer = new TableViewer(this, SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.memberViewer.getTable().setLayoutData(
				LayoutUtils.createFullFillGridData());
		this.memberViewer.setContentProvider(new MemberContentProvider());
		final MemberLabelProvider memberLabelProvider = new MemberLabelProvider();
		this.memberViewer.setLabelProvider(memberLabelProvider);
		this.memberViewer.setColumnProperties(PROPS);
		this.memberViewer.setCellEditors(new CellEditor[] {
				new TextCellEditor(this.memberViewer.getTable()),
				new CheckboxCellEditor(this.memberViewer.getTable()) });
		this.memberViewer.setCellModifier(new MemberCellModifier(
				this.memberViewer, getDetector()));
		this.memberViewer.setInput(new Object[] {});

		final Table table = this.memberViewer.getTable();
		for (int i = 0; i < PROPS.length; i++)
			new TableColumn(table, i == 1 ? SWT.LEFT : SWT.CENTER)
					.setText(PROPS[i]);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		refreshMembers();
	    updateColumnWidth();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		removeListener();
	}

	/**
	 * 
	 */
	private void removeListener() {
		if ( this.manager != null ) {
			this.manager.removeSelectedSphereChangedListener(this.mangerSelectionListener);
		}
	}

	/**
	 * @return
	 */
	private Object[] getActualMembers() {
		ManagedSphere sphere = this.manager.getSelectedSphere();
		if (sphere != null) {
			return sphere.getMembers().toArray();
		} else {
			return EMPTY_ARRAY;
		}
	}

	/**
	 * Refreshes the view
	 */
	private void updateColumnWidth() {
		// Refresh the view
		this.memberViewer.refresh();
		// Repack the columns
		for (int i = 0, n = this.memberViewer.getTable().getColumnCount(); i < n; i++) {
			TableColumn column = this.memberViewer.getTable().getColumn(i);
			column.pack();
			column.setWidth( column.getWidth() + 15 );			
		}
	}

	void refreshMembers() {
		this.memberViewer.setInput(getActualMembers());
		ManagedSphere sphere = this.manager.getSelectedSphere();
		this.memberViewer.getTable().setEnabled(
				sphere != null ? sphere.isEditable() : false);
	}
	

	/**
	 *
	 */
	private final class MangerSelectionListener extends SphereActionAdaptor {
		@Override
		public void selectedSphereChanged(
				ManagedSphere selectedSphere) {
			if ( !isDisposed() && !getDetector().hasChanges()) {
				refreshMembers();
			}
			else if ( selectedSphere != null ){
				logger.warn( "Can't handler selection " + selectedSphere );
			}
		}
	}


	/**
	 * @return
	 */
	public IChangesDetector getDetector() {
		return this.detector;
	}
}
