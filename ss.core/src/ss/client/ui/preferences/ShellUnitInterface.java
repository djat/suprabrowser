/**
 * 
 */
package ss.client.ui.preferences;

import org.eclipse.swt.widgets.Composite;

/**
 * @author zobo
 *
 */
public interface ShellUnitInterface {
	public PreferenceAbstractShellUnit getNewUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify);
}
