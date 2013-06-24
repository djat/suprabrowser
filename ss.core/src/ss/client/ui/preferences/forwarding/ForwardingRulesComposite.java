/**
 * 
 */
package ss.client.ui.preferences.forwarding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.preferences.ForwardingController;
import ss.client.preferences.PreferencesController;
import ss.client.ui.SupraSphereFrame;

/**
 * @author zobo
 *
 */
public class ForwardingRulesComposite extends Composite {
	


	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForwardingRulesComposite.class);

	private SupraSphereFrame sF;

	private ForwardingController controller;

	private PreferencesController prefController;

	public ForwardingRulesComposite(Composite parent, int style, ForwardingController controller, PreferencesController prefController, SupraSphereFrame sF) {
		super(parent, style);
		this.controller = controller;
		this.prefController = prefController;
		this.sF = sF;
		createGUI(this);
	}
	
	private void createGUI(Composite parent) {
		parent.setLayout(new GridLayout());

		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		Composite global = new GlobalUserForwardingSubComposite(parent, SWT.BORDER, this.controller);
		global.setLayoutData(layoutData);
		
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		Composite spheresUser = new SphereUserForwardingSubComposite(parent, SWT.BORDER, this.controller, this.prefController, this.sF);
		spheresUser.setLayoutData(layoutData);
	}
}
