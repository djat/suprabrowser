/**
 * 
 */
package ss.client.ui.email;

import java.util.List;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

/**
 * @author zobo
 *
 */
public class EmailFromCompositeUnit extends EmailSimpleSetShellCompositeUnit {

	/**
	 * @param parent
	 * @param style
	 * @param labelText
	 * @param defaultText
	 * @param enabled
	 */
	public EmailFromCompositeUnit(Composite parent, int style,
			String labelText, String defaultText, boolean enabled) {
		super(parent, style, labelText, defaultText, enabled);
	}

	@Override
	protected Vector<String> processDataFiltered(List<String> data, String filter) {
		return new Vector<String>(data);
	}
}
