/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.ISphereLocationEditor;
import ss.client.ui.viewers.SphereLocationDialog;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SpherePhisicalLocationItem;
import ss.domainmodel.clubdeals.ClubDeal;
import ss.domainmodel.clubdeals.ClubDealUtils;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.domainmodel.configuration.SphereRoleList;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class AddClubdealDialog extends AbstractClubdealDialog implements ISphereLocationEditor {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(AddClubdealDialog.class);
	
	private static final String NEW_CLUB_DEAL_NAME = "New Sphere Name: ";

	private static final String ADD_NEW_CLUB_DEAL_TITLE = "Add New Sphere";

	private static final String DESIRED_ID = "Desired Id (optional): ";

	private final SpherePhisicalLocationItem locationItem = new SpherePhisicalLocationItem();
	
	private Combo sphereRoleCombo = null;
	
	private Text idText = null;
	
	private Text emailAliasText = null;
	
	private final ManageByClubdealComposite parentComposite;

	private Combo sphereCombo;
	
	public AddClubdealDialog(final ManageByClubdealComposite parentComposite) {
		this.parentComposite = parentComposite;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		super.createContents(parent);
		
		Label roleLabel = new Label(this.fieldComp, SWT.LEFT);
		roleLabel.setText("Sphere Type: ");
		
		this.sphereRoleCombo = new Combo(this.fieldComp, SWT.LEFT | SWT.READ_ONLY);
		this.sphereRoleCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		SphereRoleList list = SsDomain.CONFIGURATION.getMainConfigurationValue().getSphereRoleList();
		for(SphereRoleObject roleObject : list) {
			this.sphereRoleCombo.add(roleObject.getRoleName());
		}
		if(!list.contains(SphereRoleObject.getDefaultName())) {
			this.sphereRoleCombo.add(SphereRoleObject.getDefaultName(), 0);
		}
		this.sphereRoleCombo.select(0);
		
		Label label = new Label(this.fieldComp, SWT.LEFT);
		label.setText( DESIRED_ID );
		label.moveBelow( this.text );
		
		this.idText = new Text(this.fieldComp, SWT.SINGLE | SWT.BORDER);
		this.idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.idText.moveBelow(label);
		
		Label emailAliasLabel = new Label(this.fieldComp, SWT.LEFT);
		emailAliasLabel.setText( "Desired Email Alias (optional):" );
		emailAliasLabel.moveBelow( this.idText );
		
		this.emailAliasText = new Text(this.fieldComp, SWT.SINGLE | SWT.BORDER);
		this.emailAliasText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.emailAliasText.moveBelow(emailAliasLabel);
		
		Label parentSphereLabel = new Label(this.fieldComp, SWT.LEFT);
		parentSphereLabel.setText("Parent Sphere: ");
		
		this.sphereCombo = new Combo(this.fieldComp, SWT.READ_ONLY | SWT.LEFT);
		this.sphereCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.sphereCombo.setVisibleItemCount(7);
		int index = 0;
		int i = 0;
		final String supraSphere = this.parentComposite.getFolder().getWindow().getClient().getVerifyAuth().getSupraSphereName();
		for(ClubdealWithContactsObject cd : this.parentComposite.getManager().getAllClubdeals()) {
			String name = cd.getClubDealDisplayName();
			this.sphereCombo.add( name );
			if (StringUtils.equals( supraSphere, name )) {
				index = i;
			}
			i++;
		}
		this.sphereCombo.select( index );
		
		final Button editLocationButton = new Button(this.fieldComp, SWT.PUSH);
		editLocationButton.setText("Edit Location...");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		editLocationButton.setLayoutData(data);
		editLocationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startEditLocation();
			}
		});
		return parent;
	}

	@Override
	protected String getLabelText() {
		return NEW_CLUB_DEAL_NAME;
	}

	@Override
	public String getTitle() {
		return ADD_NEW_CLUB_DEAL_TITLE;
	}

	@Override
	protected boolean savePressed() {
		final ClubDeal sphere = createSphere();
		if(sphere==null) {
			return false;
		}
		this.parentComposite.saveClubdeal( sphere, getDesiredEmaiAlias() );
		return true;
	}

	/**
	 * @return
	 */
	private String getDesiredParentSphere() {
		return StringUtils.isNotBlank(this.sphereCombo.getText()) ? this.sphereCombo.getText() : SupraSphereFrame.INSTANCE.client.getVerifyAuth().getSupraSphereName();
	}

	public String getDesiredId() {
		return this.idText.getText();
	}
	
	public String getSphereRole() {
		return this.sphereRoleCombo.getText();
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(420, 255);
	}
	
	protected String getDesiredEmaiAlias() {
		return this.emailAliasText.getText();
	}

	public SpherePhisicalLocationItem getPhisicalLocationItem() {
		return this.locationItem;
	}

	public void setPhisicalLocation(
			SpherePhisicalLocationItem phisicalLocationItem) {
		this.locationItem.copyAll(phisicalLocationItem);
	}

	public void startEditLocation() {
		SphereLocationDialog dialog = new SphereLocationDialog(this, true);
		dialog.setBlockOnOpen(true);
		dialog.open();
	}
	
	private ClubDeal createSphere() {
		if(StringUtils.isBlank(getName())) {
			UserMessageDialogCreator.error("Sphere name can't be empty!");
			return null;
		}
		final String name = StringUtils.isBlank(getDesiredId()) ? getName() : (getDesiredId() + " - " + getName());
		if(!this.parentComposite.getManager().checkNameAvailable( name )) { 
			UserMessageDialogCreator.error("Sphere with such name already exist!");
			return null;
		}
		final ClubDeal sphere = ClubDealUtils.INSTANCE.createClubDeal( this.parentComposite.getManager().getClient(), getName() );
		sphere.setCurrentSphere(this.parentComposite.getManager().getClient().getVerifyAuth().getSystemName(getDesiredParentSphere()));
		sphere.setSphereCoreId(this.parentComposite.getManager().getClient().getVerifyAuth().getSystemName(getDesiredParentSphere()));
		sphere.setRole(getSphereRole());
		sphere.getPhisicalLocation().copyAll(this.locationItem);
		return sphere;
	}
}
