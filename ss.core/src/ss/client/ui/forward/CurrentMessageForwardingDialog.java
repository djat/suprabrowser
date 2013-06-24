/**
 * 
 */
package ss.client.ui.forward;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;
import ss.common.UiUtils;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class CurrentMessageForwardingDialog extends Dialog {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(CurrentMessageForwardingDialog.class);
	
	private static final String GROUP = "Group";
	private static final String PERSONAL = "Personal";
	private static final String PRIVATE = "Private";
	private static final String SPHERE = "Sphere";
	private static final String EMAIL = "Email";
	private static final String CANCEL = "Cancel";
	private static final String DONE = "Done";
	private static final String FORWARDING_DIALOG = "Forwarding Dialog";
	
	private TableViewer sphereTableViewer;
	
	private SpheresCollectionByTypeObject sphereOwner;

	private List sphereTypeList;
	
	private Composite sphereComp;
	
	private Button doneButton;
	
	private AbstractDoneSelectionListener doneListener;
	
	private java.util.List<Document> docsToForward;
	
	private Hashtable addresses;

	private Button sphereButton;
	
	public CurrentMessageForwardingDialog(
			final java.util.List<Document> docsToForward,
			final Hashtable addresses,
			final SpheresCollectionByTypeObject sphereOwner) {
		super(SupraSphereFrame.INSTANCE.getShell());
		this.sphereOwner = sphereOwner;
		this.docsToForward = docsToForward;
		this.addresses = SupraSphereFrame.INSTANCE.client
				.getPersonalContactsForEmail(SupraSphereFrame.INSTANCE.client.session);
	}
	
	public CurrentMessageForwardingDialog(final java.util.List<Document> docsToForward, final SpheresCollectionByTypeObject sphereOwner) {
		this(docsToForward, null, sphereOwner);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		shell.setText(FORWARDING_DIALOG);
	}

	@Override
	protected Control createContents(final Composite parent) {
		parent.setLayout(new FillLayout());
		
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout());
		
		this.sphereComp = new Composite(mainComp, SWT.BORDER);
		this.sphereComp.setLayout(new GridLayout(3, false));
		this.sphereComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createForwardingModeList();
		createSphereTypeList();
		createSphereList();
		createButtonComp(mainComp);
		
		return parent;
	}
	
	/**
	 * 
	 */
	private void createButtonComp(final Composite mainComp) {
		Composite buttonComp = new Composite(mainComp, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		buttonComp.setLayout(new GridLayout(2, true));
		
		this.doneButton = new Button(buttonComp, SWT.PUSH);
		this.doneButton.setText(DONE);
		this.doneButton.setEnabled(false);
		this.doneListener = createDoneButtonListener();
		this.doneButton.addListener(SWT.Selection, this.doneListener);
		
		Button cancelButton = new Button(buttonComp, SWT.PUSH);
		cancelButton.setText(CANCEL);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
	}

	/**
	 * 
	 */
	private void createForwardingModeList() {
		Composite radioComp = new Composite(this.sphereComp, SWT.NONE);
		radioComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		radioComp.setLayout(new GridLayout());
		
		this.sphereButton = new Button(radioComp, SWT.RADIO);
		this.sphereButton.setText(SPHERE);
		this.sphereButton.setSelection(true);
		
		Button emailButton = new Button(radioComp, SWT.RADIO);
		emailButton.setText(EMAIL);
		
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshDoneButtonListener();
			}
		};
		this.sphereButton.addSelectionListener(listener);
		emailButton.addSelectionListener(listener);
	}
	
	public Collection<Document> getDocsToForward() {
		return Collections.unmodifiableList(this.docsToForward);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE;
	}
	
	private void createSphereTypeList() {
		this.sphereTypeList = new List(this.sphereComp, SWT.READ_ONLY | SWT.BORDER);
		this.sphereTypeList.add(GROUP);
		this.sphereTypeList.add(PRIVATE);
		this.sphereTypeList.add(PERSONAL);
		this.sphereTypeList.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.sphereTypeList.select(0);
		this.sphereTypeList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createSphereList();
				refreshDoneButtonListener();
			}
		});
	}
	
	/**
	 * 
	 */
	protected void refreshDoneButtonListener() {
		this.doneButton.removeListener(SWT.Selection, this.doneListener);
		this.doneListener = createDoneButtonListener();
		this.doneButton.addListener(SWT.Selection, this.doneListener);
	}

	private void createSphereList() {
		if(this.sphereTableViewer==null) {
			initializeSphereTable();
		}
		if(this.doneButton!=null) {
			this.doneButton.setEnabled(false);
		}
		String[] selectedTypes = this.sphereTypeList.getSelection();
		if(selectedTypes==null || selectedTypes.length==0) {
			this.sphereTableViewer.setInput(null);
			this.sphereTableViewer.refresh();
		}
		String type = selectedTypes[0];
		Collection<String> input = null;
		if(type.equals(GROUP)) {
			input = this.sphereOwner.getGroupSpheres();
		} else if(type.equals(PRIVATE)) {
			input = this.sphereOwner.getPrivateSpheres();
		} else {
			input = this.sphereOwner.getPersonalSpheres();
		}
		this.sphereTableViewer.setInput(input);
		this.sphereTableViewer.refresh();
		for(TableItem item : this.sphereTableViewer.getTable().getItems()) {
			item.setChecked(false);
		}
	}

	/**
	 * 
	 */
	private void initializeSphereTable() {
		this.sphereTableViewer = new TableViewer(this.sphereComp, SWT.BORDER | SWT.CHECK);
		this.sphereTableViewer.setLabelProvider(new SphereTableLabelProvider());
		this.sphereTableViewer.setContentProvider(new SphereTableContentProvider());
		this.sphereTableViewer.getTable().setLinesVisible(false);
		this.sphereTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.sphereTableViewer.getTable().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.detail!=SWT.CHECK) {
					return;
				}
				boolean hasChecked = false;
				for(TableItem item : ((Table)e.widget).getItems()) {
					if(item.getChecked()) {
						hasChecked = true;
					}
				}
				getDoneButton().setEnabled(hasChecked);
			}
		});
	}
	
	private Button getDoneButton() {
		return this.doneButton;
	}
	
	private AbstractDoneSelectionListener createDoneButtonListener() {
		boolean isSphereForwarding = this.sphereButton.getSelection();
		String selectedSphereType = this.sphereTypeList.getSelection()[0];
		if(!isSphereForwarding) {
			if(selectedSphereType.equals(GROUP)) {
				return new DoneEmailGroupListener(this);
			} else {
				return new DoneEmailMemberListener(this);
			}
		}
		return new DoneSphereListener(this);
	}

	/**
	 * @return
	 */
	public java.util.List<String> getCheckedSpheres() {
		java.util.List<String> checkedSpheres = new ArrayList<String>();
		for(TableItem item : this.sphereTableViewer.getTable().getItems()) {
			if(item.getChecked()) {
				checkedSpheres.add(item.getText());
			}
		}
		return checkedSpheres;
	}
	
	public Hashtable getAddresses() {
		return this.addresses;
	}

	@Override
	public int open() {
		return UiUtils.swtEvaluate(new Callable<Integer>() {
			public Integer call() throws Exception {
				return superOpen();
			}
		});
	}
	
	private int superOpen() {
		return super.open();
	}
}
