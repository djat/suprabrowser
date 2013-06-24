/**
 * 
 */
package ss.client.ui.spheremanagement;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.memberaccess.ClientMemberDefinitionProvider;
import ss.client.ui.spheremanagement.memberaccess.ManageAccessComposite;
import ss.client.ui.spheremanagement.memberaccess.MemberAccessManager;

/**
 * 
 */
public class ManageSphereDialog extends Window {

	/**
	 * @param parentShell
	 */
	public ManageSphereDialog() {
		super((Shell) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(640, 480);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		ISphereDefinitionProvider sphereDefinitionProvider = new SphereDefinitionProviderForPreferences(
			SupraSphereFrame.INSTANCE.client);
		sphereDefinitionProvider.checkOutOfDate();
		ClientMemberDefinitionProvider memberDefinitionProvider = new ClientMemberDefinitionProvider(
			SupraSphereFrame.INSTANCE.client);
		memberDefinitionProvider.checkOutOfDate();
		MemberAccessManager manager = new MemberAccessManager(
			sphereDefinitionProvider, memberDefinitionProvider);
		manager.checkOutOfDate();
		ManageAccessComposite accessShell = new ManageAccessComposite(parent,
			manager, null);
		accessShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return parent;
	}

}
