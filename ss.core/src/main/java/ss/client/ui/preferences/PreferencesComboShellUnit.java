/**
 * 
 */
package ss.client.ui.preferences;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author zobo
 *
 */
public abstract class PreferencesComboShellUnit extends Composite {
protected static final int LABEL_WIDTH = 70;
	
	protected Label label;

	protected Combo combo;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferenceAbstractShellUnit.class);

	/**
	 * @param parent
	 * @param style
	 */
	public PreferencesComboShellUnit(Composite parent, int style,
			String labelString, List<String> data, boolean allowModify){
		super(parent, style);
		createContent(this, labelString, data, allowModify);
	}

	protected abstract void createContent(Composite parent, String labelString, List<String> data, boolean allowModify);

	public String getValue() {
		return this.combo.getText();
	}

	public void setValue(String string) {
		if (string != null){
			for (int i = 0; i < this.combo.getItemCount(); i++){
				if (string.equals(this.combo.getItem(i))){
					this.combo.select(i);
					return;
				}
			}
		}
		this.combo.select(0);
	}

	public void setName(String labelString) {
		this.label.setText(labelString);
	}
	
	protected void fillCombo(List<String> data){
		for (String s : data){
			this.combo.add(s);
              		
		}
		this.combo.select(0);
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
	
	public boolean getPermittion(){
		return this.combo.getEnabled();
	}
}
