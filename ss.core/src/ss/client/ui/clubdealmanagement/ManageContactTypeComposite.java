/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SDisplay;
import ss.common.StringUtils;
import ss.domainmodel.SphereMember;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ManageContactTypeComposite extends Composite {

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_MANAGETYPECOMPOSITE);

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger
			.getLogger(ManageContactTypeComposite.class);

	private TableViewer viewer;

	private ClubdealFolder folder;

	private Button removeButton;

	private Button renameButton;

	private static final String ADD_NEW_TYPE_NAME = "MANAGETYPECOMPOSITE.ADD_NEW_TYPE_NAME";
	
	private static final String TYPES = "MANAGETYPECOMPOSITE.TYPES";

	private static final String ADD = "MANAGETYPECOMPOSITE.ADD";

	private static final String NEW_TYPE_NAME = "MANAGETYPECOMPOSITE.NEW_TYPE_NAME";
	
	private static final String REMOVE = "MANAGETYPECOMPOSITE.REMOVE";
	
	private static final String RENAME = "MANAGETYPECOMPOSITE.RENAME";
	
	private static final String NEW_NAME = "MANAGETYPECOMPOSITE.NEW_NAME";
	
	private static final String RENAME_TYPE_NAME = "MANAGETYPECOMPOSITE.RENAME_TYPE_NAME";
	

	public ManageContactTypeComposite(final Composite parent , final ClubdealFolder folder) {
		super(parent, SWT.BORDER);
		setLayout(new GridLayout(1, false));
		this.folder = folder;
		createContent();
		layout();
	}

	private void createContent() {
		Composite comp = new Composite(this, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(TYPES));

		this.viewer = new TableViewer(comp, SWT.BORDER);
		this.viewer.getTable().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		this.viewer.setContentProvider(new TypeListContentProvider());
		this.viewer.setLabelProvider(new TypeListLabelProvider());
		this.viewer.setInput(this.folder.getWindow().getManager());
		this.viewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						setButtonEnabled(event);
					}
				});

		Composite buttonComposite = new Composite(comp, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(3, true));
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText(bundle.getString(ADD));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new AbstractClubdealDialog() {							
					@Override
					protected String getLabelText() {
						return bundle.getString(NEW_TYPE_NAME);
					}
					@Override
					protected boolean savePressed() {
						addNewType(this.getName());
						return true;
					}
					@Override
					public String getTitle() {
						return bundle.getString(ADD_NEW_TYPE_NAME);
					}
				};
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});

		this.removeButton = new Button(buttonComposite, SWT.PUSH);
		this.removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.removeButton.setText(bundle.getString(REMOVE));
		this.removeButton.setEnabled(false);
		this.removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new WarnDialog() {
					@Override
					public void cancelPressed() {
						close();
					}
					@Override
					public String getMessage() {
						return "Are you sure you want to delete this contact type? Contacts administrate window will be reloaded.";
					}
					@Override
					public void okPressed() {
						Runnable runnable = new Runnable() {
							public void run() {
								getGlobalApplyButton().setEnabled(false);
								ManageContactTypeComposite.this.folder.getWindow().getManager()
									.removeType(getSelectedType());
								ManageContactTypeComposite.this.folder.getWindow().refresh();
							}
						};
						SDisplay.display.async(runnable);
						close();
					}
				};
				dialog.open();
			}
		});

		this.renameButton = new Button(buttonComposite, SWT.PUSH);
		this.renameButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.renameButton.setText(bundle.getString(RENAME));
		this.renameButton.setEnabled(false);
		this.renameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dialog = new AbstractClubdealDialog() {
					@Override
					protected String getFieldText() {
						return getSelectedType();
					}
					@Override
					protected String getLabelText() {
						return bundle.getString(NEW_NAME);
					}
					@Override
					protected boolean savePressed() {
						if(StringUtils.isBlank(this.getName()) || getName().equals(SphereMember.NO_TYPE)) {
							return false;
						}
						final String newName = getName();
						Runnable runnable = new Runnable() {
							public void run() {
								getGlobalApplyButton().setEnabled(false);
								ManageContactTypeComposite.this.folder.getWindow().getManager()
									.renameType(getSelectedType(), newName);
								ManageContactTypeComposite.this.folder.getWindow().refresh();
							}
						};
						SDisplay.display.async(runnable);
						return true;
					}
					
					@Override
					public String getTitle() {
						return bundle.getString(RENAME_TYPE_NAME);
					}
				};
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
	}

	protected void refreshList() {
		this.viewer.refresh();
	}

	/**
	 * @param b
	 */
	protected void setButtonEnabled(SelectionChangedEvent event) {
		boolean enabled = event!=null && event.getSelection()!=null && getSelectedType()!=null && !getSelectedType().equals(SphereMember.NO_TYPE);
		this.removeButton.setEnabled(enabled);
		this.renameButton.setEnabled(enabled);
	}

	public String getSelectedType() {
		if (this.viewer == null || this.viewer.getSelection() == null) {
			return null;
		}
		if(((StructuredSelection) this.viewer.getSelection()).getFirstElement()==null) {
			return null;
		}
		return ((StructuredSelection) this.viewer.getSelection())
				.getFirstElement().toString();
	}

	/**
	 * @param name
	 */
	public void addNewType(String name) {
		this.folder.getWindow().getManager().addNewType(name);
		refreshList();
		setButtonEnabled(null);
		setHasChanges(true);
	}

	private void setHasChanges(final boolean value) {
		getChangesDetector().setTypesChanged(value);
		this.folder.refreshOtherTabs(getClass());
	}

	/**
	 * 
	 */
	public void saveChanges() {
		this.folder.getWindow().getManager().saveToServer();
		setHasChanges(false);
	}
	
	private ChangesDetector getChangesDetector() {
		return this.folder.getWindow().getChangesDetector();
	}
	
	private Button getGlobalApplyButton() {
		return ((ManageByContactComposite)getParent().getParent()).getApplyButton();
	}
}