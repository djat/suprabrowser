package ss.client.ui.tempComponents;

import org.eclipse.swt.widgets.Composite;

import ss.client.ui.tempComponents.interfaces.IShown;

public abstract class AbstractShowablePane extends Composite implements IShown {
	
	private boolean isShown = false;
	
	public AbstractShowablePane(Composite parent, int style) {
		super(parent, style);
	}

	public boolean isShown() {
		return this.isShown;
	}

	public void setShown(boolean value) {
		this.isShown = value;

	}

}
