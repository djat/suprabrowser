/**
 * 
 */
package ss.client.ui.preferences;

import java.io.IOException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author zobo
 * 
 */
public abstract class PreferenceAbstractShellUnit extends Composite{

	protected static final int LABEL_WIDTH = 70;
	
	protected Label label;

	protected Button checkButton;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferenceAbstractShellUnit.class);

	/**
	 * @param parent
	 * @param style
	 */
	public PreferenceAbstractShellUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify){
		super(parent, style);
		if (logger.isDebugEnabled()){
			logger.debug("constructing new shell unit: String: " + labelString +", selection: " + selection + ",allowModify: " + allowModify);
		}
		createContent(this, labelString, selection, allowModify);
	}

	protected abstract void createContent(Composite parent, String labelString,
			boolean selection, boolean allowModify);

	public boolean getValue() {
		return this.checkButton.getSelection();
	}

	public void setValue(boolean value) {
		this.checkButton.setSelection(value);
	}

	public void setName(String labelString) {
		this.label.setText(labelString);
	}

	public void setImage(String path) {
		try {
			Image preferencesImage = new Image(Display.getDefault(), getClass()
					.getResource(path).openStream());
			this.label.setImage(preferencesImage);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}
	
	public abstract void setAllValues(boolean checked, boolean enabled);
}
