/**
 * 
 */
package ss.client.ui.preferences.delivery;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.ControlPanel;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.MemberReference;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.global.SSLogger;
import ss.client.ui.spheremanagement.LayoutUtils;

/**
 * @author roman
 *
 */
public class EditDeliveryPreferencesComposite extends Composite {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger
			.getLogger(EditDeliveryPreferencesComposite.class);

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_DELIVERY_EDITDELIVERYPREFERENCESCOMPOSITE);
	
	private static final ResourceBundle newSphereBundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWSPHERE);
	
	private static final String ENABLED_KEY = "EDITDELIVERYPREFERENCESCOMPOSITE.ENABLED";
	private static final String NAME_KEY = "EDITDELIVERYPREFERENCESCOMPOSITE.NAME";
	private static final String WORKFLOW_MODEL = "EDITDELIVERYPREFERENCESCOMPOSITE.WORKFLOW_MODEL";
	private static final String DEFAULT_DELIVERY = "EDITDELIVERYPREFERENCESCOMPOSITE.DEFAULT_DELIVERY";
	private static final String CONFIGURE = "EDITDELIVERYPREFERENCESCOMPOSITE.CONFIGURE";
	private static final String APPLY = "EDITDELIVERYPREFERENCESCOMPOSITE.APPLY";
	private static final String DEFAULT_TYPE = "EDITDELIVERYPREFERENCESCOMPOSITE.DEFAULT_TYPE";
	private static final String DATE_RANGE = "EDITDELIVERYPREFERENCESCOMPOSITE.DATE_RANGE";
	
	private static final String HOUR1 = "NEWSPHERE.1HOUR";
	private static final String HOURS2 = "NEWSPHERE.2HOURS";
	private static final String HOURS3 = "NEWSPHERE.3HOURS";
	private static final String HOURS6 = "NEWSPHERE.6HOURS";
	private static final String DAY1 = "NEWSPHERE.1DAY";
	private static final String DAYS2 = "NEWSPHERE.2DAYS";
	private static final String DAYS3 = "NEWSPHERE.3DAYS";
	private static final String DAYS4 = "NEWSPHERE.4DAYS";
	private static final String DAYS5 = "NEWSPHERE.5DAYS";
	private static final String WEEK1 = "NEWSPHERE.1WEEK";
	private static final String WEEKS2 = "NEWSPHERE.2WEEKS";
	private static final String WEEKS4 = "NEWSPHERE.4WEEKS";
	private static final String ALL = "NEWSPHERE.ALL";
	private static final String NONE = "NEWSPHERE.NONE";

	private String sphereId;

	private TableViewer tv;

	private Button applyButton;

	private Button configureButton;

	private List<AbstractDelivery> input;

	private Combo deliveryCombo;
	
	private static final int COMBO_WIDTH = 150;

	public static final String ENABLED = bundle.getString(ENABLED_KEY);
	public static final String DELIVERY_NAME = bundle.getString(NAME_KEY);
	

	public static final String[] TABLE_PROPS = new String[] { DELIVERY_NAME, ENABLED };

	private SphereOwnPreferences preferences = null;

	private WorkflowConfiguration workflowConfiguration = null;

	private Combo typeCombo;

	private Combo dateCombo;
	
	private IChangesDetector detector;

	/**
	 * 
	 */
	EditDeliveryPreferencesComposite(Composite parent, String sphereId, IChangesDetector detector) {
		super(parent, SWT.NONE);
		this.detector = detector;
		this.sphereId = sphereId;
		if (this.sphereId != null) {
			this.preferences = SphereOwnPreferences.wrap(
					SsDomain.SPHERE_HELPER.getSpherePreferences(sphereId)
							.getDocumentCopy(), SphereOwnPreferences.class);
			this.workflowConfiguration = this.preferences
					.getWorkflowConfiguration();
			
			checkMemberPresence();
		}
		layoutGUI();
	}

	/**
	 * 
	 */
	private void checkMemberPresence() {
		List<MemberReference> contacts= SupraSphereFrame.INSTANCE.client.getVerifyAuth().getMembersForSphere(this.sphereId);
		
		this.workflowConfiguration.synchronizeContactsForSphere(contacts);
		
		SsDomain.SPHERE_HELPER.setSpherePreferences(this.sphereId,
				this.preferences);
	}

	/**
	 * 
	 */
	private void layoutGUI() {
		setLayout(new GridLayout(1, false));

		createDeliveryTable();
		createConfigureButton();
		createComboComposite();
		createApplyButton();
	}

	/**
	 * 
	 */
	private void createComboComposite() {
		Composite comboComposite = new Composite(this, SWT.NONE);
		
		comboComposite.setLayout(new GridLayout(2, false));
		comboComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createDefaultDeliveryCombo(comboComposite);
		createDefaultTypeCombo(comboComposite);
		createExpirationDateCombo(comboComposite);
	}


	
	private GridData getGridDataForCombo() {
		GridData data = new GridData();
		data.widthHint = COMBO_WIDTH;
		data.horizontalAlignment = GridData.BEGINNING;
		
		return data;
	}
	/**
	 * 
	 */
	private void createDefaultDeliveryCombo(Composite comboComp) {
		Label label = new Label(comboComp, SWT.LEFT);
		label.setText(bundle.getString(DEFAULT_DELIVERY));

		this.deliveryCombo = new Combo(comboComp, SWT.CENTER | SWT.READ_ONLY);
		this.deliveryCombo.setLayoutData(getGridDataForCombo());

		for (String deliveryDisplayName : this.workflowConfiguration
				.getEnabledDisplayNames()) {
			this.deliveryCombo.add(deliveryDisplayName);
		}

		selectDefaultDelivery();

		addDeliveryComboListeners();
	}
	
	/**
	 * 
	 */
	private void createDefaultTypeCombo(Composite comboComp) {
		Label label = new Label(comboComp, SWT.LEFT);
		label.setText(bundle.getString(DEFAULT_TYPE));

		this.typeCombo = new Combo(comboComp, SWT.CENTER | SWT.READ_ONLY);
		this.typeCombo.setLayoutData(getGridDataForCombo());

		this.typeCombo.add(ControlPanel.getTypeTerse());
		this.typeCombo.add(ControlPanel.getTypeBookmark());
		this.typeCombo.add(ControlPanel.getTypeContact());
		this.typeCombo.add(ControlPanel.getTypeEmail());
		this.typeCombo.add(ControlPanel.getTypeFile());
		this.typeCombo.add(ControlPanel.getTypeMessage());
		this.typeCombo.add(ControlPanel.getTypeRss());
		this.typeCombo.add(ControlPanel.getTypeSphere());
		
		selectDefaultType();

		addTypeComboListeners();
	}
	
	/**
	 * 
	 */
	private void createExpirationDateCombo(Composite comboComp) {
		Label label = new Label(comboComp, SWT.LEFT);
		label.setText(bundle.getString(DATE_RANGE));

		this.dateCombo = new Combo(comboComp, SWT.CENTER | SWT.READ_ONLY);
		this.dateCombo.setLayoutData(getGridDataForCombo());

		this.dateCombo.add(newSphereBundle.getString(NONE));
		this.dateCombo.add(newSphereBundle.getString(HOUR1));
		this.dateCombo.add(newSphereBundle.getString(HOURS2));
		this.dateCombo.add(newSphereBundle.getString(HOURS3));
		this.dateCombo.add(newSphereBundle.getString(HOURS6));
		this.dateCombo.add(newSphereBundle.getString(DAY1));
		this.dateCombo.add(newSphereBundle.getString(DAYS2));
		this.dateCombo.add(newSphereBundle.getString(DAYS3));
		this.dateCombo.add(newSphereBundle.getString(DAYS4));
		this.dateCombo.add(newSphereBundle.getString(DAYS5));
		this.dateCombo.add(newSphereBundle.getString(WEEK1));
		this.dateCombo.add(newSphereBundle.getString(WEEKS2));
		this.dateCombo.add(newSphereBundle.getString(WEEKS4));
		this.dateCombo.add(newSphereBundle.getString(ALL));
		
		selectDefaultDateExpiration();

		addDateComboListeners();
	}

	/**
	 * 
	 */
	private void addDateComboListeners() {
		this.dateCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				EditDeliveryPreferencesComposite.this.applyButton
						.setEnabled(true);
				setDefaultDateRangeToPreferences(((Combo) se.widget).getText());
			}
		});
	}

	/**
	 * @param text
	 */
	protected void setDefaultDateRangeToPreferences(String text) {
		this.workflowConfiguration.setExpirationDate(text);
	}

	/**
	 * 
	 */
	private void selectDefaultDateExpiration() {
		String dateExpiration = this.workflowConfiguration.getExpirationDate();
		
		int index = this.dateCombo.indexOf(dateExpiration);
		if(index<0) {
			index = this.dateCombo.indexOf(newSphereBundle.getString(WEEK1));
		}
		
		this.dateCombo.select(index);
	}

	private void selectDefaultType() {
		String startType = this.workflowConfiguration.getDefaultType();
		
		int index = this.typeCombo.indexOf(startType);
		
		this.typeCombo.select(index);
	}

	/**
	 * 
	 */
	private void addTypeComboListeners() {
		this.typeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				EditDeliveryPreferencesComposite.this.applyButton
						.setEnabled(true);
				setDefaultTypeToPreferences(((Combo) se.widget).getText());
			}
		});
	}

	/**
	 * @param text
	 */
	protected void setDefaultTypeToPreferences(String text) {
		this.workflowConfiguration.setDefaultType(text);
		
	}

	private void addDeliveryComboListeners() {
		this.deliveryCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				EditDeliveryPreferencesComposite.this.applyButton
						.setEnabled(true);
				setDefaultDeliveryToPreferences(((Combo) se.widget).getText());
			}
		});
		this.deliveryCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				setDefaultDeliveryToPreferences(((Combo) me.widget).getText());
			}
		});
	}

	private void selectDefaultDelivery() {
		AbstractDelivery defaultDelivery = this.workflowConfiguration
				.getDefaultDelivery();
		if (defaultDelivery == null) {
			defaultDelivery = this.workflowConfiguration.getNormalDelivery();
		}
		int index = this.deliveryCombo.indexOf(defaultDelivery.getDisplayName());
		if(index < 0) {
			index = 0;
		}
		this.deliveryCombo.select(index);
	}

	private void setDefaultDeliveryToPreferences(String text) {
		this.workflowConfiguration.setDefaultDelivery(text);
	}

	/**
	 * 
	 */
	private void createApplyButton() {
		this.applyButton = new Button(this, SWT.PUSH);
		this.applyButton.setText(bundle.getString(APPLY));
		this.applyButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		this.applyButton.setEnabled(false);
		this.applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				applyPressed();
			}
		});
	}
	
	/**
	 * 
	 */
	private void createConfigureButton() {
		this.configureButton = new Button(this, SWT.PUSH);
		this.configureButton.setText(bundle.getString(CONFIGURE));
		this.configureButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		this.configureButton.pack();
		this.configureButton.addSelectionListener(new SelectionListener() {

			EditDeliveryPreferencesComposite editComp = EditDeliveryPreferencesComposite.this;

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				if (this.editComp.tv.getTable().getSelectionCount() == 0) {
					return;
				}
				ConfigureDeliveryDialog.showDialog(this.editComp);
			}
		});
		this.configureButton.setEnabled(false);
	}

	/**
	 * 
	 */
	public void applyPressed() {
		SsDomain.SPHERE_HELPER.setSpherePreferences(this.sphereId,
				this.preferences);
		this.applyButton.setEnabled(false);
	}

	/**
	 * 
	 */
	private void createDeliveryTable() {
				
		setLayout(LayoutUtils.createFullFillGridLayout());
		GridData gridData;
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.BEGINNING;
		
		Label label = new Label(this, SWT.BEGINNING | SWT.BOTTOM);
		label.setText(bundle.getString(WORKFLOW_MODEL));
		label.setLayoutData(gridData);
		
		this.tv = new TableViewer(this, SWT.BORDER);
		this.tv.setLabelProvider(new DeliveryTableLabelProvider());
		this.tv.setContentProvider(new DeliveryTableContentProvider());


				
		TableColumn nameColumn = new TableColumn(this.tv.getTable(), SWT.LEFT);
		nameColumn.setText(DELIVERY_NAME);
		nameColumn.setWidth(200);
		TableColumn enabledColumn = new TableColumn(this.tv.getTable(), SWT.LEFT);
		enabledColumn.setText(ENABLED);
		enabledColumn.pack();
		
		this.tv.getTable().setHeaderVisible(true);
		this.tv.getTable().setLinesVisible(true);

		this.tv.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		this.input = this.workflowConfiguration.getDeliveries();

		setTableCellEditors();

		this.tv.setInput(this.input);

		this.tv.refresh();

		addListenersToTable();
	}

	private void setTableCellEditors() {
		CellEditor[] editors = new CellEditor[2];
		
		editors[0] = new TextCellEditor(this.tv.getTable(), SWT.LEFT);
		editors[1] = new CheckboxCellEditor(this.tv.getTable());

		editors[1].addListener(new ICellEditorListener() {
			public void applyEditorValue() {
				EditDeliveryPreferencesComposite.this.applyButton
						.setEnabled(true);
				toggleItemInCombo();
			}

			public void cancelEditor() {
			}

			public void editorValueChanged(boolean arg0, boolean arg1) {
			}
		});

		this.tv.setColumnProperties(TABLE_PROPS);
		this.tv.setCellModifier(new DeliveryTableCellModifier(this));
		this.tv.setCellEditors(editors);
	}

	private void addListenersToTable() {
		this.tv.getTable().addMouseListener(new MouseAdapter() {
			EditDeliveryPreferencesComposite editComp = EditDeliveryPreferencesComposite.this;

			public void mouseDoubleClick(MouseEvent me) {
				ConfigureDeliveryDialog.showDialog(this.editComp);
			}

			public void mouseDown(MouseEvent me) {
				AbstractDelivery delivery = getSelectedDelivery();
				this.editComp.configureButton.setEnabled(delivery != null &&
						delivery.isConfigurable());
			}
		});
	}

	private void toggleItemInCombo() {
		TableItem item = this.tv.getTable().getSelection()[0];
		AbstractDelivery delivery = (AbstractDelivery) item.getData();
		if (!delivery.isEnabled()) {
			EditDeliveryPreferencesComposite.this.deliveryCombo.add(delivery
					.getDisplayName());
		} else {
			EditDeliveryPreferencesComposite.this.deliveryCombo.remove(delivery
					.getDisplayName());
		}
		if (EditDeliveryPreferencesComposite.this.deliveryCombo.getSelectionIndex() < 0) {
			EditDeliveryPreferencesComposite.this.deliveryCombo.select(0);
		}
	}

	/**
	 * @return
	 */
	protected AbstractDelivery getSelectedDelivery() {
		TableItem[] items = this.tv.getTable().getSelection();
		return items != null && items.length > 0 ? (AbstractDelivery) items[0]
				.getData() : null;
	}

	/**
	 * @return
	 */
	public String getSelectedName() {
		AbstractDelivery delivery = getSelectedDelivery();
		return delivery != null ? delivery.getDisplayName() : null;
	}

	/**
	 * 
	 */
	public void refreshTableNames() {
		TableItem item = this.tv.getTable().getSelection()[0];
		int index = -1;
		int selected = this.deliveryCombo.getSelectionIndex();
		if (item.getText(0).equals("true")) {
			index = this.deliveryCombo.indexOf(item.getText());
		}
		this.tv.refresh();

		if (index >= 0) {
			this.deliveryCombo.setItem(index, item.getText(1));
		}

		this.deliveryCombo.select(selected);
	}

	/**
	 * 
	 */
	public TableViewer getTableViewer() {
		return this.tv;
	}

	/**
	 * @param displayName
	 */
	public void removeFromCombo(String displayName) {
		int index = this.deliveryCombo.indexOf(displayName);
		if(index<0) {
			return;
		}
		
		this.deliveryCombo.remove(displayName);
		if (this.deliveryCombo.getSelectionIndex() < 0) {
			this.deliveryCombo.select(0);
		}
	}

	/**
	 * @return the applyButton
	 */
	Button getApplyButton() {
		return this.applyButton;
	}

	/**
	 * @param nameText
	 * @return
	 */
	public boolean checkAlreadyExist(String nameText,
			AbstractDelivery deliveryToChange) {
		for (AbstractDelivery delivery : this.workflowConfiguration
				.getDeliveries()) {	
			if (delivery.getDisplayName().equals(nameText)
					&& !deliveryToChange.getClass().equals(delivery.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param oldName
	 * @param text
	 */
	public void refreshDefaultDeliveryCombo(String oldName, String newName) {
		int index = this.deliveryCombo.indexOf(oldName);
		
		if(index < 0) {
			return;
		}
		
		this.deliveryCombo.setItem(index, newName);
		
		if(this.deliveryCombo.getSelectionIndex() < 0) {
			this.deliveryCombo.select(index);
		}
	}
	
	public IChangesDetector getDetector() {
		return this.detector;
	}
}
