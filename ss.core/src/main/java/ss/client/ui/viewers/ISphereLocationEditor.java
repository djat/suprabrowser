/**
 * 
 */
package ss.client.ui.viewers;

import org.eclipse.swt.widgets.Shell;

import ss.domainmodel.SpherePhisicalLocationItem;

/**
 * @author roman
 *
 */
public interface ISphereLocationEditor {

	void startEditLocation();
	
	SpherePhisicalLocationItem getPhisicalLocationItem();
	
	Shell getShell();
	
	public void setPhisicalLocation(final SpherePhisicalLocationItem phisicalLocationItem);
}
