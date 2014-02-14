/**
 * 
 */
package ss.client.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.LuceneSearchDialog.GiversQuery;
import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.common.VerifyAuth;
import ss.domainmodel.SphereReference;

/**
 * @author dankosedin
 * 
 */
public class SearchGiversSelector extends BaseDialog {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchGiversSelector.class);

	private VerifyAuth auth;

	private GiversQuery query;

	private Table table;

	private static final String GIVERS_FOR_SEARCH = "SEARCHGIVERSSELECTOR.GIVERS_FOR_SEARCH";

	private static final String DONE = "SEARCHGIVERSSELECTOR.DONE";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_SEARCHGIVERSSELECTOR);

	public SearchGiversSelector(VerifyAuth auth, GiversQuery query) {
		this.auth = auth;
		this.query = query;
	}

	@Override
	protected Dimension getStartUpDialogSize() {
		return new Dimension(300, 450);
	}

	@Override
	protected String getStartUpTitle() {
		return this.bundle.getString(GIVERS_FOR_SEARCH);
	}

	@Override
	protected void initializeControls() {
		final Shell shell = getShell();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);

		ArrayList<String> givers = new ArrayList<String>();

		final String login = (String) this.auth.getSession().get("username");
		logger.info("login ="+login);
		final List<SphereReference> allGivers = this.auth.getAllAvailablePrivateSpheres(login);				

		for (SphereReference ref : allGivers) {
			logger.info("memmber ="+ref.toString());
			givers.add(ref.getDisplayName());
		}

		final Text text = new Text(shell, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		BaseDataModel<String> model = new BaseDataModel<String>(givers,
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

		for (String member : givers) {
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
					int index = SearchGiversSelector.this.table
							.getSelectionIndex();
					if(index<0) {
						return;
					}
					TableItem selected = SearchGiversSelector.this.table
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
						for (TableItem item : SearchGiversSelector.this.table
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
				for (TableItem item : SearchGiversSelector.this.table
						.getItems()) {
					if (item.getChecked()) {
						checkedItems.add(item.getText());
					}
				}
				String query;
				if (checkedItems.size() > 0) {
					String sphereQuery = "giver:(";
					for (String sphere : checkedItems) {
						sphereQuery += " ||" + sphere;
					}
					query = sphereQuery + ")";
				} else
					query = "";
				SearchGiversSelector.this.query.setQuery(query);
				SearchGiversSelector.this.query.deliver();
				SearchGiversSelector.this.close();
			}
		});

	}

	/**
	 * @param hAligment
	 * @return
	 */
//	private Object getSelectorLayoutData() {
//		GridData gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		return gridData;
//	}

//	private GridData getGroupGridData() {
//		GridData gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		return gridData;
//	}

//	private boolean isMyPrivate(SphereReference ref, final String login) {
//		return ref.getSystemName().equals(
//				SearchGiversSelector.this.auth
//						.getPersonalSphereFromLogin(login));
//	}

//	private boolean isPrivateToMe(SphereReference ref, final String login) {
//		return !isMyPrivate(ref, login) && ref.isMember();
//	}
}
