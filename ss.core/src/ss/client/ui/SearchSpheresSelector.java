/**
 * 
 */
package ss.client.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.LuceneSearchDialog.SpheresQuery;
import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.common.SphereReferenceList;
import ss.common.VerifyAuth;
import ss.domainmodel.SphereReference;

/**
 * @author dankosedin
 * 
 */
public class SearchSpheresSelector extends BaseDialog {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchSpheresSelector.class);

	private VerifyAuth auth;

	private SpheresQuery query;

	private Table table;
	
	private static final String SPHERES_FOR_SEARCH = "SEARCHSPHERESELECTOR.SPHERES_FOR_SEARCH";
	
	private static final String QUICK_SELECT_UNSELECT = "SEARCHSPHERESELECTOR.QUICK_SELECT_UNSELECT";
	
	private static final String MY_PRIVATE = "SEARCHSPHERESELECTOR.MY_PRIVATE";
	
	private static final String PRIVATES_TO_ME = "SEARCHSPHERESELECTOR.PRIVATES_TO_ME";
	
	private static final String MY_EMAIL_BOX = "SEARCHSPHERESELECTOR.MY_EMAIL_BOX";
	
	private static final String GROUP_SPHERES = "SEARCHSPHERESELECTOR.GROUP_SPHERES";
	
	private static final String DONE = "SEARCHSPHERESELECTOR.DONE";
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_SEARCHSPHERESELECTOR);

	public SearchSpheresSelector(VerifyAuth auth, SpheresQuery query) {
		this.auth = auth;
		this.query = query;
	}

	@Override
	protected Dimension getStartUpDialogSize() {
		return new Dimension(300, 450);
	}

	@Override
	protected String getStartUpTitle() {
		return this.bundle.getString(SPHERES_FOR_SEARCH);
	}

	@Override
	protected void initializeControls() {
		final Shell shell = getShell();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);

		ArrayList<String> spheres = new ArrayList<String>();

		final String login = (String) this.auth.getSession().get("username");
		final SphereReferenceList allSpheres = this.auth
				.getAllSpheresByLoginName(login);

		for (SphereReference ref : allSpheres) {
			spheres.add(ref.getDisplayName());
		}

		final Text text = new Text(shell, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		BaseDataModel<String> model = new BaseDataModel<String>(spheres,
				BaseDataModel.FilterType.CaseInsensitive);
		model.setDataSourceLabeler(new DataSourceLabeler<String>() {
			public String getDataLabel(String dataSource) {
				return dataSource;
			}
		});

		GridData gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;

		gridData.grabExcessHorizontalSpace = true;
		text.setLayoutData(gridData);

		Group quickSelectors = new Group(shell, SWT.SHADOW_NONE);
		quickSelectors.setLayoutData(getGroupGridData());
		quickSelectors.setText(this.bundle.getString(QUICK_SELECT_UNSELECT));
		GridLayout layoutOfType = new GridLayout();
		layoutOfType.numColumns = 2;
		quickSelectors.setLayout(layoutOfType);

		final Button privateSelect = new Button(quickSelectors, SWT.PUSH);
		privateSelect.setText(this.bundle.getString(MY_PRIVATE));
		privateSelect.setLayoutData(getSelectorLayoutData());
		privateSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (SphereReference ref : allSpheres) {
					if (isMyPrivate(ref, login)) {
						for (TableItem item : SearchSpheresSelector.this.table
								.getItems()) {
							if (item.getText().equals(ref.getDisplayName())) {
								item.setChecked(!item.getChecked());
							}
						}
					}
				}
			}
		});

		final Button privatesSelect = new Button(quickSelectors, SWT.PUSH);
		privatesSelect.setText(this.bundle.getString(PRIVATES_TO_ME));
		privatesSelect.setLayoutData(getSelectorLayoutData());
		privatesSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (SphereReference ref : allSpheres) {
					if (isPrivateToMe(ref, login)) {
						for (TableItem item : SearchSpheresSelector.this.table
								.getItems()) {
							if (item.getText().equals(ref.getDisplayName())) {
								item.setChecked(!item.getChecked());
							}
						}
					}
				}
			}
		});

		final Button emailSelect = new Button(quickSelectors, SWT.PUSH);
		emailSelect.setText(this.bundle.getString(MY_EMAIL_BOX));
		emailSelect.setLayoutData(getSelectorLayoutData());
		emailSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (SphereReference ref : allSpheres) {
					if (ref.isEmailBox()) {
						for (TableItem item : SearchSpheresSelector.this.table
								.getItems()) {
							if (item.getText().equals(ref.getDisplayName())) {
								item.setChecked(!item.getChecked());
							}
						}
					}
				}
			}
		});

		final Button groupSelect = new Button(quickSelectors, SWT.PUSH);
		groupSelect.setText(this.bundle.getString(GROUP_SPHERES));
		groupSelect.setLayoutData(getSelectorLayoutData());
		groupSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (SphereReference ref : allSpheres) {
					if ((!isPrivateToMe(ref, login))
							&& (!isMyPrivate(ref, login))
							&& (!ref.isEmailBox())) {
						for (TableItem item : SearchSpheresSelector.this.table
								.getItems()) {
							if (item.getText().equals(ref.getDisplayName())) {
								item.setChecked(!item.getChecked());
							}
						}
					}
				}
			}
		});

		this.table = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		this.table.setSize(100, 100);

		gridData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		this.table.setLayoutData(gridData);

		for (String member : spheres) {
			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setText(member);
		}

		this.table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				if (event.detail != SWT.CHECK) {
					if (item.getChecked()) {
						item.setChecked(false);
					} else {
						item.setChecked(true);
					}
				} else {
					int index = SearchSpheresSelector.this.table
							.getSelectionIndex();
					if(index<0) {
						return;
					}
					logger.warn(index);
					TableItem selected = SearchSpheresSelector.this.table
							.getItem(index);
					if (selected == item) {
						item.setChecked(!item.getChecked());
					}
				}
			}
		});

		final Button but = new Button(shell, SWT.PUSH);

		new TypeAheadComponent<String>(text, model,
				new ResultAdapter<String>() {

					@Override
					public void processResult(String selection, String real) {
						but.notifyListeners(SWT.Selection, new Event());
					}

					@Override
					public void processListSelection(String listSelection,
							String real) {
						for (TableItem item : SearchSpheresSelector.this.table
								.getItems()) {
							if (item.getText().equals(listSelection)) {
								item.setChecked(!item.getChecked());
							}
						}
						text.setText("");
					}

				});

		but.setText(this.bundle.getString(DONE));
		but.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ArrayList<String> checkedItems = new ArrayList<String>();
				for (TableItem item : SearchSpheresSelector.this.table
						.getItems()) {
					if (item.getChecked()) {
						checkedItems.add(allSpheres.getSphereByDisplayName(
								item.getText()).getSystemName());
					}
				}
				String query;
				if (checkedItems.size() > 0) {
					String sphereQuery = "sphere_id:(";
					for (String sphere : checkedItems) {
						sphereQuery += " ||" + sphere;
					}
					query = sphereQuery + ")";
				} else
					query = "";
				SearchSpheresSelector.this.query.setQuery(query);
				SearchSpheresSelector.this.query.deliver();
				SearchSpheresSelector.this.close();
			}
		});

	}

	/**
	 * @param hAligment
	 * @return
	 */
	private Object getSelectorLayoutData() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	private GridData getGroupGridData() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	private boolean isMyPrivate(SphereReference ref, final String login) {
		return ref.getSystemName().equals(
				SearchSpheresSelector.this.auth
						.getPersonalSphereFromLogin(login));
	}

	private boolean isPrivateToMe(SphereReference ref, final String login) {
		return !isMyPrivate(ref, login) && ref.isMember();
	}
}
