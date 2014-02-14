/**
 * 
 */
package ss.client.ui.docking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import swtdock.PartDragDrop;

/**
 * @author zobo
 * 
 */
public class DecoyDockingElement extends AbstractDockingComponent {

	private static final int SPACE_TAKEN = 10;

	private int minWidth = 0;

	private int minHeight = 0;

	private int location;

	public DecoyDockingElement(SupraDockingManager dm, int location) {
		super(dm);
		switch (location) {
		case PartDragDrop.LEFT:
		case PartDragDrop.RIGHT:
		case PartDragDrop.TOP:
		case PartDragDrop.BOTTOM:
			this.location = location;
			break;
		default:
			this.location = PartDragDrop.LEFT;
			break;
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void createContent(Composite parent) {

	}

	@Override
	public Object getContent() {
		return null;
	}

	@Override
	public int getMinimumWidth() {
		return this.minWidth;
	}

	@Override
	public int getMinimumHeight() {
		return this.minHeight;
	}

	@Override
	public boolean checkIfCanDockOn(int direction) {
		return true;
	}

	@Override
	public void createControl(Composite parent) {
		if (this.control != null && !this.control.isDisposed())
			return;

		this.control = new Composite(parent, SWT.NONE);
		this.control.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		if ((this.location == PartDragDrop.TOP)
				|| (this.location == PartDragDrop.BOTTOM)) {
			this.minHeight = SPACE_TAKEN;
		} else if ((this.location == PartDragDrop.LEFT)
				|| (this.location == PartDragDrop.RIGHT)) {
			this.minWidth = SPACE_TAKEN;
		}
	}

	@Override
	protected void createToolBar(Composite parent) {

	}

}
