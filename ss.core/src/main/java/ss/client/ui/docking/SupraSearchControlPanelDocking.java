/**
 * 
 */
package ss.client.ui.docking;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.SupraColors;
import ss.client.ui.tempComponents.SupraSearchControlPanel;
import ss.client.ui.tempComponents.SupraSearchPane;
import swtdock.ILayoutPart;
import swtdock.PartDragDrop;

/**
 * @author roman
 *
 */
public class SupraSearchControlPanelDocking extends AbstractDockingComponent {

	private static final String SUPRA_SEARCH_CONTROL_PANEL = "SUPRASEARCHCONTROLPANELDOCKING.SUPRA_SEARCH_CONTROL_PANEL";

	private SupraSearchPane ssp;

	private SupraSphereFrame sF;

	private int minHeight = 30;

	private SupraSearchControlPanel controlPanel;

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_DOCKING_SUPRASEARCHCONTROLPANELDOCKING);

	/**
	 * @param id
	 * @param dm
	 */
	public SupraSearchControlPanelDocking(SupraSearchPane ssp) {
		super(ssp.getDockingManager());
		this.sF = ssp.getSF();
		this.ssp = ssp;
	}

	@Override
	public String getName() {
		return this.bundle.getString(SUPRA_SEARCH_CONTROL_PANEL);
	}

	@Override
	public void createContent(Composite parent) {
		parent.setLayout(new FillLayout());
		this.controlPanel = new SupraSearchControlPanel(this, parent);
	}

	@Override
	public SupraSearchControlPanel getContent() {
		return this.controlPanel;
	}

	@Override
	public int getMinimumWidth() {
		return 0;
	}

	@Override
	public int getMinimumHeight() {
		return this.minHeight;
	}

	public void setMinimumHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
		if (target == null) {
			if ((direction == PartDragDrop.SHELL_RIGHT)
					|| (direction == PartDragDrop.SHELL_LEFT))
				return false;
		} else if (!super.checkPossibilityOfDocking(direction, target))
			return false;
		if ((direction == PartDragDrop.RIGHT)
				|| (direction == PartDragDrop.LEFT))
			return false;
		return true;
	}

	public void createControl(Composite parent) {
		if (this.control != null && !this.control.isDisposed())
			return;

		this.control = new Composite(parent, SWT.NONE);
		this.control.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		this.control.setLayout(gridLayout);

		DockingTopTitle headComposite = new DockingTopTitle(this.control,
				SWT.NONE);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.verticalAlignment = GridData.FILL;
		headComposite.setLayoutData(gridData);

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		headComposite.setLayout(gridLayout);

		this.label = new CLabel(headComposite, SWT.NONE);
		this.label.setAlignment(SWT.LEFT);
		this.label.setText(getName());
		this.label.setBackground(SupraColors.DOCKING_LABEL_NORMAL);

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		this.label.setLayoutData(gridData);

		Composite mainControl = new Composite(this.control, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		mainControl.setLayoutData(gridData);

		createContent(mainControl);
	}

	public int getSpacing() {
		return 0;
	}

	@Override
	public boolean checkIfCanDockOn(int direction) {
		if ((direction == PartDragDrop.RIGHT)
				|| (direction == PartDragDrop.LEFT))
			return false;
		return true;
	}

	@Override
	protected void createToolBar(Composite parent) {
		// DO NOTHING
	}

	/**
	 * @return
	 */
	public SupraSphereFrame getSF() {
		return this.sF;
	}

	/**
	 * @return
	 */
	public SupraSearchPane getSupraSearchPane() {
		return this.ssp;
	}

}
