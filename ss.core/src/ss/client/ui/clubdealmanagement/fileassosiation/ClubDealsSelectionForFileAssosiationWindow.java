/**
 * 
 */
package ss.client.ui.clubdealmanagement.fileassosiation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Document;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.FileStatement;
import ss.domainmodel.clubdeals.ClubdealCollection;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;

/**
 * @author zobo
 *
 */
public class ClubDealsSelectionForFileAssosiationWindow extends
		ApplicationWindow implements IDataHashProvider {

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_FILEASSOCIATION_CLUBDEALSSELECTIONFORFILEASSOCIATIONWINDOW);

	private Hashtable<ClubdealWithContactsObject, Boolean> hash = new Hashtable<ClubdealWithContactsObject, Boolean>();

	private static final String CLUBDEALS_TITLE = "CLUBDEALSELECTIONFORFILEASSOCIATION.CLUBDEALS_TITLE";
	
	private static final String APPLY = "CLUBDEALSELECTIONFORFILEASSOCIATION.APPLY";

	private static final String SELECT_CLUB_DEAL_TO_ASSOSIATE = "CLUBDEALSELECTIONFORFILEASSOCIATION.SELECT_CLUB_DEAL_TO_ASSOSIATE";
	
	private static final String X = "CLUBDEALSELECTIONFORFILEASSOCIATION.X";
	
	private static final String CLUBDEAL_ID = "CLUBDEALSELECTIONFORFILEASSOCIATION.CLUBDEAL_ID";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClubDealsSelectionForFileAssosiationWindow.class);

	private TableViewer viewer;

	final private FileStatement file;
	
	private ClubdealCollection cdCollection;
	
	private Set<String> associatedClubdeals = new HashSet<String>();

	/**
	 * @param parentShell
	 */
	public ClubDealsSelectionForFileAssosiationWindow(final ClubdealCollection cdCollection, final FileStatement file, final List<String> associatedClubdeals) {
		super(SupraSphereFrame.INSTANCE.getShell());
		this.cdCollection = cdCollection;
		this.file = file;
		this.associatedClubdeals.addAll(associatedClubdeals);
		logger.debug(this.associatedClubdeals);
		initHash();
		logger.debug(bundle);
		logger.debug(bundle.getString(X));
	}

	/**
	 * 
	 */
	private void initHash() {
		for(ClubdealWithContactsObject cd : getCdCollection()) {
			Boolean bool = this.associatedClubdeals.contains(cd.getClubdealSystemName());
			logger.info("add to hash:"+cd.getClubdealSystemName()+" "+bool);
			getHash().put(cd, bool);
		}
	}

	/**
	 * @return the file
	 */
	public FileStatement getFile() {
		return this.file;
	}

	/**
	 * @return the cdCollection
	 */
	public ClubdealCollection getCdCollection() {
		return this.cdCollection;
	}

	private void update(final String messageId) {
		this.viewer.update(getCdCollection().toArray(), null);
	}

	@Override
	protected void configureShell(final Shell shell) {
		shell.setSize(250, 400);
		int monitorHeight = SDisplay.display.get().getPrimaryMonitor()
				.getBounds().height;
		int monitorWidth = SDisplay.display.get().getPrimaryMonitor()
				.getBounds().width;
		shell.setLocation(monitorWidth / 2 - 320, monitorHeight / 2 - 240);
		shell.setText(bundle.getString(CLUBDEALS_TITLE));
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());

		final Label label = new Label(parent, SWT.LEFT);
		label.setText(bundle.getString(SELECT_CLUB_DEAL_TO_ASSOSIATE));
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		label.setLayoutData(data);

		createTable(parent);

		final Button button = new Button(parent, SWT.PUSH);
		button.setText(bundle.getString(APPLY));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				applyPerformed();
			}

		});
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.verticalAlignment = SWT.BEGINNING;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		button.setLayoutData(data);

		return parent;
	}

	private void createTable(final Composite viewComposite) {
		this.viewer = new TableViewer(viewComposite, SWT.FULL_SELECTION
				| SWT.BORDER);
		this.viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer.setContentProvider(new FileAssosiationContentProvider());
		this.viewer.setLabelProvider(new FileAssosiationLabelProvider(this));
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);

		new TableColumn(this.viewer.getTable(), SWT.NONE).setText(bundle.getString(X));
		new TableColumn(this.viewer.getTable(), SWT.NONE)
				.setText(bundle.getString(CLUBDEAL_ID));
		CellEditor[] editors = new CellEditor[2];
		editors[0] = new CheckboxCellEditor(this.viewer.getTable(), SWT.LEFT);
		editors[1] = editors[0];
		this.viewer.setCellEditors(editors);
		this.viewer.setColumnProperties(new String[] { "X", "Sphere" });
		this.viewer.setCellModifier(new FileAssosiationCellModifier(this));
		this.viewer.getTable().setEnabled(true);
		this.viewer.setInput(this.cdCollection);
		packColumns();
		update(this.file.getMessageId());
	}

	protected void packColumns() {
		this.viewer.getTable().getColumns()[0].setWidth(30);
		this.viewer.getTable().getColumns()[1].setWidth(250);
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE;
	}

	private void applyPerformed() {
		Hashtable<ClubdealWithContactsObject, Boolean> data = getHash();
		if (data == null) {
			logger.error("data is null");
			return;
		}
		List<String> added = new Vector<String>();
		List<String> removed = new Vector<String>();
		for(ClubdealWithContactsObject clubdeal : data.keySet()) {
			final Boolean bool = data.get(clubdeal);
			if(!bool && this.associatedClubdeals.contains(clubdeal.getClubdealSystemName())) {
				removed.add(clubdeal.getClubdealSystemName());
			}
			if(bool && !this.associatedClubdeals.contains(clubdeal.getClubdealSystemName())) {
				added.add(clubdeal.getClubdealSystemName());
			}
		}
		
		List<Document> fileDocs = new ArrayList<Document>();
		fileDocs.add(this.file.getBindedDocument());
		
		SupraSphereFrame.INSTANCE.client.forwardMessagesSubTree(added, fileDocs);
		SupraSphereFrame.INSTANCE.client.recallFile(removed, this.file.getDataId());
		
		this.associatedClubdeals.removeAll(removed);
		this.associatedClubdeals.addAll(added);
	}

	public Hashtable<ClubdealWithContactsObject, Boolean> getHash() {
		return this.hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.clubdealmanagement.fileassosiation.IDataHashProvider#getViewer()
	 */
	public TableViewer getViewer() {
		return this.viewer;
	}
}
