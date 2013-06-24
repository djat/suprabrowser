/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 *
 */
public interface IMemberAccessUiOwner {

	Button createApplyButton(Composite parent );

	/**
	 * @return
	 */
	MemberAccessManager getManager();
}
