/**
 * 
 */
package ss.client.ui.viewers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.common.StringUtils;
import ss.domainmodel.SpherePhisicalLocationItem;
import ss.util.ImagesPaths;

/**
 * @author roman
 *
 */
public class SphereLocationDialog extends Dialog {

	private final SpherePhisicalLocationItem locationItem;
	private Text descriptionText;
	private Text countryText;
	private Text regionText;
	private Text stateText;
	private Text cityText;
	private Text addressText;
	private Text streetText;
	private Text streetContText;
	private Text zipCodeText;
	private Text faxText;
	private Text emailText;
	private Text telephoneText;
	private List<Control> tabControls = new ArrayList<Control>();
	
	private final boolean isEditable;
	private ISphereLocationEditor editor;
	
	/**
	 * @param parentShell
	 */
	protected SphereLocationDialog(final Shell parentShell, final SpherePhisicalLocationItem locationItem, final boolean isEditable) {
		super(parentShell);
		this.locationItem = locationItem;
		this.isEditable = isEditable;
	}

	/**
	 * @param newSphere
	 * @param b
	 */
	public SphereLocationDialog(final ISphereLocationEditor editor, final boolean b) {
		this(editor.getShell(), editor.getPhisicalLocationItem(), b);
		this.editor = editor;
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		
		final Composite fieldComp = new Composite(parent, SWT.NONE);
		fieldComp.setLayout(new GridLayout(2, false));
		fieldComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		///
		final Label zipCodeLabel = new Label(fieldComp, SWT.LEFT);
		zipCodeLabel.setText("Zip Code ");
		
		this.zipCodeText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.zipCodeText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.zipCodeText.setEditable(this.isEditable);
		this.tabControls.add(this.zipCodeText);
		
		//
		final Label countryLabel = new Label(fieldComp, SWT.LEFT);
		countryLabel.setText("Country ");
		
		this.countryText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.countryText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.countryText.setEditable(this.isEditable);
		this.tabControls.add(this.countryText);
		///
		final Label regionLabel = new Label(fieldComp, SWT.LEFT);
		regionLabel.setText("Region ");
		
		this.regionText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.regionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.regionText.setEditable(this.isEditable);
		this.tabControls.add(this.regionText);
		
		///
		final Label stateLabel = new Label(fieldComp, SWT.LEFT);
		stateLabel.setText("State ");
		
		this.stateText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.stateText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.stateText.setEditable(this.isEditable);
		this.tabControls.add(this.stateText);
		
		///
		final Label cityLabel = new Label(fieldComp, SWT.LEFT);
		cityLabel.setText("City ");
		
		this.cityText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.cityText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.cityText.setEditable(this.isEditable);
		this.tabControls.add(this.cityText);
		///
		final Label streetLabel = new Label(fieldComp, SWT.LEFT);
		streetLabel.setText("Street ");
		
		this.streetText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.streetText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.streetText.setEditable(this.isEditable);
		this.tabControls.add(this.streetText);
		///
		final Label streetContLabel = new Label(fieldComp, SWT.LEFT);
		streetContLabel.setText("Street Cont ");
		
		this.streetContText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.streetContText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.streetContText.setEditable(this.isEditable);
		this.tabControls.add(this.streetContText);
		///
		final Label addressLabel = new Label(fieldComp, SWT.LEFT);
		addressLabel.setText("Address ");
		
		this.addressText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.addressText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.addressText.setEditable(this.isEditable);
		this.tabControls.add(this.addressText);
		///
		final Label telephoneLabel = new Label(fieldComp, SWT.LEFT);
		telephoneLabel.setText("Telephone ");
		
		this.telephoneText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.telephoneText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.telephoneText.setEditable(this.isEditable);
		this.tabControls.add(this.telephoneText);
		///
		final Label faxLabel = new Label(fieldComp, SWT.LEFT);
		faxLabel.setText("Fax ");
		
		this.faxText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.faxText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.faxText.setEditable(this.isEditable);
		this.tabControls.add(this.faxText);
		////
		final Label emailLabel = new Label(fieldComp, SWT.LEFT);
		emailLabel.setText("Email ");
		
		this.emailText = new Text(fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.emailText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.emailText.setEditable(this.isEditable);
		this.tabControls.add(this.emailText);
		
		//////////////////
		
		final Label descriptionLabel = new Label(parent, SWT.LEFT);
		descriptionLabel.setText("Description");
		
		this.descriptionText = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		this.descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.descriptionText.setEditable(this.isEditable);
		
		fieldComp.setTabList(this.tabControls.toArray(new Control[]{}));
		
		if(this.locationItem!=null) {
			fillFields();
		}
		
		final Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		int childrenCount = this.isEditable ? 2 : 1; 
		buttonComp.setLayout(new GridLayout(childrenCount, false));
		
		if(this.isEditable) {
			final Button saveButton = new Button(buttonComp, SWT.PUSH);
			saveButton.setText("Save");
			saveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			saveButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					savePhisicalLocation();
					close();
				}
			});
			
			final Button cancelButton = new Button(buttonComp, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			cancelButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					close();
				}
			});
		} else {
			final Button okButton = new Button(buttonComp, SWT.PUSH);
			okButton.setText("OK");
			okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			okButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					close();
				}
			});
		}
		
		return parent;
	}

	/**
	 * 
	 */
	protected void savePhisicalLocation() {
		this.editor.setPhisicalLocation(getPhisicalLocationItem());
	}

	/**
	 * @return
	 */
	private SpherePhisicalLocationItem getPhisicalLocationItem() {
		SpherePhisicalLocationItem locationItem = new SpherePhisicalLocationItem();
		locationItem.setAddress(StringUtils.getTrimmedString(this.addressText.getText()));
		locationItem.setCity(StringUtils.getTrimmedString(this.cityText.getText()));
		locationItem.setCountry(StringUtils.getTrimmedString(this.countryText.getText()));
		locationItem.setDescription(StringUtils.getTrimmedString(this.descriptionText.getText()));
		locationItem.setEmail(StringUtils.getTrimmedString(this.emailText.getText()));
		locationItem.setFax(StringUtils.getTrimmedString(this.faxText.getText()));
		locationItem.setRegion(StringUtils.getTrimmedString(this.regionText.getText()));
		locationItem.setStreet(StringUtils.getTrimmedString(this.streetText.getText()));
		locationItem.setStreetcont(StringUtils.getTrimmedString(this.streetContText.getText()));
		locationItem.setTelephone(StringUtils.getTrimmedString(this.telephoneText.getText()));
		locationItem.setZipcode(StringUtils.getTrimmedString(this.zipCodeText.getText()));
		locationItem.setState(StringUtils.getTrimmedString(this.stateText.getText()));
		return locationItem;
	}

	/**
	 * 
	 */
	private void fillFields() {
		this.addressText.setText(StringUtils.getTrimmedString(this.locationItem.getAddress()));
		this.cityText.setText(StringUtils.getTrimmedString(this.locationItem.getCity()));
		this.countryText.setText(StringUtils.getTrimmedString(this.locationItem.getCountry()));
		this.emailText.setText(StringUtils.getTrimmedString(this.locationItem.getEmail()));
		this.faxText.setText(StringUtils.getTrimmedString(this.locationItem.getFax()));
		this.regionText.setText(StringUtils.getTrimmedString(this.locationItem.getRegion()));
		this.streetContText.setText(StringUtils.getTrimmedString(this.locationItem.getStreetcont()));
		this.streetText.setText(StringUtils.getTrimmedString(this.locationItem.getStreet()));
		this.telephoneText.setText(StringUtils.getTrimmedString(this.locationItem.getTelephone()));
		this.zipCodeText.setText(StringUtils.getTrimmedString(this.locationItem.getZipcode()));
		this.descriptionText.setText(StringUtils.getTrimmedString(this.locationItem.getDescription()));
		this.stateText.setText(StringUtils.getTrimmedString(this.locationItem.getState()));
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 530);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("Location");
		try {
			Image sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
			newShell.setImage(sphereImage);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
