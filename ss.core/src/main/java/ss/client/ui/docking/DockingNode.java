/**
 * 
 */
package ss.client.ui.docking;

import swtdock.ILayoutPart;
import swtdock.PartSashContainer;
import swtdock.PartTabFolder;

/**
 * @author zobo
 * 
 */
public class DockingNode {

	private DockingNode nodeLeft = null;

	private DockingNode nodeRight = null;

	private double div = 0;

	private int direction;

	@SuppressWarnings("unused")
	private DockingNode parent = null;

	private ILayoutPart partLeft = null;

	private ILayoutPart partRight = null;

	private SupraDockingManager manager;

	/**
	 * 
	 */
	public DockingNode(SupraDockingManager manager) {
		super();
		this.manager = manager;
	}

	public ILayoutPart move() {
		ILayoutPart toReturn = null;
		if ((this.partLeft != null) && (this.partRight != null)) {
			this.manager.movePart(this.partLeft, this.direction,
					this.partRight, (float) (this.div));
			if ((this.partLeft.getContainer()) instanceof PartTabFolder) {
				toReturn = (PartSashContainer) this.partLeft.getContainer()
						.getContainer();
			} else {
				toReturn = (PartSashContainer) this.partLeft.getContainer();
			}
		} else if (this.partLeft != null) {
			this.manager.movePart(this.partLeft, this.direction, this.nodeRight
					.move(), (float) (this.div));
			if ((this.partLeft.getContainer()) instanceof PartTabFolder) {
				toReturn = (PartSashContainer) this.partLeft.getContainer()
						.getContainer();
			} else {
				toReturn = (PartSashContainer) this.partLeft.getContainer();
			}
		} else {
			toReturn = this.nodeLeft.move();
			this.manager.movePart(toReturn, this.direction, this.nodeRight
					.move(), (float) (this.div));
			toReturn = (PartSashContainer) toReturn.getContainer();
		}
		return toReturn;
	}
}
