/**
 * 
 */
package ss.client.ui.browser;

import org.eclipse.swt.widgets.Composite;


/**
 * @author roman
 *
 */
public class SupraBrowser extends SupraBrowserProxy {
	
	public SupraBrowser(Composite parent, int style, boolean belongsToBrowserPane) {
		super(parent, style, belongsToBrowserPane);
	}

	public SupraBrowser(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @return
	 */
	public boolean belongToMP() {
		return !this.belongsToBP;
	}
	
}
