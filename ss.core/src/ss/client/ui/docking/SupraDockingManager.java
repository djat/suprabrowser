/**
 * 
 */
package ss.client.ui.docking;

/**
 * @author zobo
 * 
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;

import ss.client.ui.tempComponents.SupraColors;
import swtdock.ContainerPlaceholder;
import swtdock.IDockingManager;
import swtdock.IDockingManagerConsts;
import swtdock.ILayoutContainer;
import swtdock.ILayoutPart;
import swtdock.IPageLayout;
import swtdock.IPartDropListener;
import swtdock.IZoomManager;
import swtdock.LayoutPart;
import swtdock.LayoutTree;
import swtdock.PartDragDrop;
import swtdock.PartDropEvent;
import swtdock.PartPlaceholder;
import swtdock.PartSashContainer;
import swtdock.PartTabFolder;
import swtdock.RootLayoutContainer;

public class SupraDockingManager implements IDockingManager, IZoomManager {

	private RootLayoutContainer mainLayout;

	private Composite parentWidget;

	private Hashtable dragParts = new Hashtable();

	private IPartDropListener partDropListener;

	private ILayoutPart zoomPart;

	private int folderStyle;

	public SupraDockingManager(Composite parentWidget, int style) {
		this.parentWidget = parentWidget;
		this.folderStyle = style;
		this.mainLayout = new RootLayoutContainer();
		this.mainLayout.createControl(parentWidget);
		this.mainLayout
				.setBackgroundSashColor(SupraColors.SASH_BACKGROUND_COLOR);
		this.partDropListener = new AnonPartDropListener(this);

		enableAllDrag();
		enableAllDrop();

	}

	private class AnonPartDropListener implements IPartDropListener {
		private SupraDockingManager dm;

		public AnonPartDropListener(SupraDockingManager dm) {
			this.dm = dm;
		}

		public void dragOver(PartDropEvent e) {
			this.dm.onPartDragOver(e);
		}

		public void drop(PartDropEvent e) {
			this.dm.onPartDrop(e);
		}
	}

	public void addPart(ILayoutPart part) {

		if (isZoomed())
			zoomOut();

		PartPlaceholder placeholder = null;
		ILayoutPart testPart = findPart(part.getID());
		if (testPart != null && testPart instanceof PartPlaceholder)
			placeholder = (PartPlaceholder) testPart;

		if (placeholder == null) {

			ILayoutPart relative = this.mainLayout.findBottomRight();
			if (relative != null && !(relative instanceof ContainerPlaceholder)) {
				stack(part, relative);
			} else {
				this.mainLayout.add(part);
			}
		} else {
			ILayoutContainer container = placeholder.getContainer();
			if (container != null) {
				if (container instanceof ContainerPlaceholder) {
					ContainerPlaceholder containerPlaceholder = (ContainerPlaceholder) container;
					ILayoutContainer parentContainer = containerPlaceholder
							.getContainer();
					container = (ILayoutContainer) containerPlaceholder
							.getRealContainer();
					if (container instanceof LayoutPart) {
						parentContainer.replace(containerPlaceholder,
								(LayoutPart) container);
					}
					containerPlaceholder.setRealContainer(null);
				}

				if (container instanceof PartTabFolder) {
					PartTabFolder folder = (PartTabFolder) container;
					part.reparent(folder.getControl().getParent());
				} else {
					part.reparent(this.mainLayout.getParent());
				}

				container.replace(placeholder, part);
			}
		}

		if (part.isViewPane())
			enableDrag(part);
		enableDrop(part);

	}

	public boolean bringPartToTop(ILayoutPart part) {
		ILayoutContainer container = part.getContainer();
		if (container != null && container instanceof PartTabFolder) {
			PartTabFolder folder = (PartTabFolder) container;
			int nIndex = folder.indexOf(part);
			if (folder.getSelection() != nIndex) {
				folder.setSelection(nIndex);
				return true;
			}
		}
		return false;
	}

	public boolean isPartVisible(String partId) {
		ILayoutPart part = findPart(partId);
		if (part == null)
			return false;
		if (part instanceof PartPlaceholder)
			return false;

		ILayoutContainer container = part.getContainer();
		if (container != null && container instanceof ContainerPlaceholder)
			return false;

		if (container != null && container instanceof PartTabFolder) {
			PartTabFolder folder = (PartTabFolder) container;
			if (folder.getVisiblePart() == null)
				return false;
			return part.getID().equals(folder.getVisiblePart().getID());
		}
		return true;
	}

	public boolean isZoomed() {
		return (this.zoomPart != null);
	}

	public void openTracker(ILayoutPart pane) {
		PartDragDrop dnd = (PartDragDrop) this.dragParts.get(pane);
		dnd.openTracker();
	}

	@SuppressWarnings("unchecked")
	private void collectDragParts(ArrayList result, ILayoutPart[] parts) {
		for (int i = 0, length = parts.length; i < length; i++) {
			ILayoutPart part = parts[i];
			if (part.isViewPane()) {
				result.add(part);
			} else if (part instanceof ILayoutContainer) {
				collectDragParts(result, ((ILayoutContainer) part)
						.getChildren());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void collectDropTargets(ArrayList result, ILayoutPart[] parts) {
		for (int i = 0, length = parts.length; i < length; i++) {
			ILayoutPart part = parts[i];
			if (part.isViewPane()) {
				result.add(part);
			} else if (part instanceof ILayoutContainer) {
				collectDropTargets(result, ((ILayoutContainer) part)
						.getChildren());
			}
		}
	}

	public PartPlaceholder[] collectPlaceholders(ILayoutPart[] parts) {
		PartPlaceholder[] result = new PartPlaceholder[0];

		for (int i = 0, length = parts.length; i < length; i++) {
			ILayoutPart part = parts[i];
			if (part instanceof ILayoutContainer) {
				PartPlaceholder[] newParts = collectPlaceholders(((ILayoutContainer) part)
						.getChildren());
				PartPlaceholder[] newResult = new PartPlaceholder[result.length
						+ newParts.length];
				System.arraycopy(result, 0, newResult, 0, result.length);
				System.arraycopy(newParts, 0, newResult, result.length,
						newParts.length);
				result = newResult;
			} else if (part instanceof PartPlaceholder) {
				PartPlaceholder[] newResult = new PartPlaceholder[result.length + 1];
				System.arraycopy(result, 0, newResult, 0, result.length);
				newResult[result.length] = (PartPlaceholder) part;
				result = newResult;
			}
		}

		return result;
	}

	public void collectViewPanes(ArrayList result) {
		collectViewPanes(result, this.mainLayout.getChildren());
	}

	@SuppressWarnings("unchecked")
	private void collectViewPanes(ArrayList result, ILayoutPart[] parts) {
		for (int i = 0, length = parts.length; i < length; i++) {
			ILayoutPart part = parts[i];
			if (part.isViewPane()) {
				result.add(part);
			} else if (part instanceof ILayoutContainer) {
				collectViewPanes(result, ((ILayoutContainer) part)
						.getChildren());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void dispose() {
		disableAllDrag();
		Composite parent = (Composite) this.mainLayout.getParent();
		ArrayList children = new ArrayList();
		collectViewPanes(children, this.mainLayout.getChildren());

		for (int i = 0; i < children.size(); i++) {
			LayoutPart part = (LayoutPart) children.get(i);
			part.reparent(parent);
		}

		this.mainLayout.dispose();

		this.mainLayout.disposeSashes();
	}

	private void derefPart(ILayoutPart part) {

		ILayoutContainer oldContainer = part.getContainer();

		if (oldContainer == null)
			return;

		oldContainer.remove(part);
		updateContainerVisibleTab(oldContainer);

		ILayoutPart[] children = oldContainer.getChildren();
		boolean hasChildren = (children != null) && (children.length > 0);
		if (hasChildren) {
			int childVisible = 0;
			for (int i = 0; i < children.length; i++)
				if (children[i].getControl() != null)
					childVisible++;

			if (oldContainer instanceof PartTabFolder) {
				PartTabFolder folder = (PartTabFolder) oldContainer;
				if (childVisible == 0) {
					ILayoutContainer parentContainer = folder.getContainer();
					for (int i = 0; i < children.length; i++) {
						folder.remove(children[i]);
						parentContainer.add(children[i]);
					}
					hasChildren = false;
				} else if (childVisible == 1) {
					LayoutTree layout = this.mainLayout.getLayoutTree();
					layout = layout.find(folder);
					layout.setBounds(layout.getBounds());
				}
			}
		}
		if (!hasChildren) {
			if (oldContainer instanceof LayoutPart) {
				LayoutPart parent = (LayoutPart) oldContainer;
				ILayoutContainer parentContainer = parent.getContainer();
				if (parentContainer != null) {
					parentContainer.remove(parent);
					parent.dispose();
				}
			}
		}
	}

	private void disableAllDrag() {

		Iterator iterator = this.dragParts.values().iterator();
		while (iterator.hasNext()) {
			PartDragDrop part = (PartDragDrop) iterator.next();

			ILayoutContainer container = part.getSourcePart().getContainer();
			if (container instanceof PartTabFolder)
				((PartTabFolder) container).disableDrag(part.getSourcePart());

			part.dispose();
		}

		this.dragParts.clear();
	}

	void disableDrag(ILayoutPart part) {
		if (this.dragParts.containsKey(part)) {
			PartDragDrop partDragDrop = (PartDragDrop) this.dragParts.get(part);
			partDragDrop.dispose();
			this.dragParts.remove(part);
		}

		ILayoutContainer container = part.getContainer();
		if (container instanceof PartTabFolder)
			((PartTabFolder) container).disableDrag(part);
	}

	private void enableAllDrag() {

		ArrayList draggableParts = new ArrayList();
		collectDragParts(draggableParts, this.mainLayout.getChildren());

		Iterator iterator = draggableParts.iterator();
		while (iterator.hasNext()) {
			LayoutPart part = (LayoutPart) iterator.next();
			if (part.isViewPane())
				enableDrag(part);
		}

	}

	private void enableAllDrop() {

		ArrayList dropTargets = new ArrayList();
		collectDropTargets(dropTargets, this.mainLayout.getChildren());

		Iterator iterator = dropTargets.iterator();
		while (iterator.hasNext()) {
			LayoutPart part = (LayoutPart) iterator.next();
			enableDrop(part);
		}
	}

	@SuppressWarnings("unchecked")
	void enableDrag(ILayoutPart part) {
		Control control = part.getDragHandle();
		if (control != null) {
			PartDragDrop dragSource = new PartDragDrop(part, control);
			dragSource.addDropListener(this.partDropListener);
			this.dragParts.put(part, dragSource);
		}

		ILayoutContainer container = part.getContainer();
		if (container instanceof PartTabFolder)
			((PartTabFolder) container).enableDrag(part, this.partDropListener);
	}

	private void enableDrop(ILayoutPart part) {
		Control control = part.getControl();
		if (control != null)
			control.setData(part);
	}

	public ILayoutPart findPart(String id) {
		ILayoutPart part = findPart(id, this.mainLayout.getChildren());
		if (part != null)
			return part;

		return null;
	}

	private ILayoutPart findPart(String id, ILayoutPart[] parts) {
		for (int i = 0, length = parts.length; i < length; i++) {
			ILayoutPart part = parts[i];
			if (part.getID().equals(id)) {
				return part;
			} else if (part instanceof ILayoutContainer) {
				part = findPart(id, ((ILayoutContainer) part).getChildren());
				if (part != null)
					return part;
			}
		}
		return null;
	}

	public boolean hasPlaceholder(String id) {
		ILayoutPart testPart = findPart(id);
		return (testPart != null && testPart instanceof PartPlaceholder);
	}

	public RootLayoutContainer getLayout() {
		return this.mainLayout;
	}

	public void movePart(ILayoutPart part, int position,
			ILayoutPart relativePart, float div) {
		ILayoutContainer newContainer = relativePart.getContainer();
		if (part == null){
			return;
		}
		if (newContainer instanceof RootLayoutContainer) {
			RootLayoutContainer sashContainer = (RootLayoutContainer) newContainer;
			int relativePosition = IPageLayout.LEFT;
			if (position == PartDragDrop.RIGHT)
				relativePosition = IPageLayout.RIGHT;
			else if (position == PartDragDrop.TOP)
				relativePosition = IPageLayout.TOP;
			else if (position == PartDragDrop.BOTTOM)
				relativePosition = IPageLayout.BOTTOM;

			ILayoutContainer oldContainer = part.getContainer();
			if (oldContainer != sashContainer) {
				derefPart(part);
				sashContainer.add(part, relativePosition, div, relativePart);
			} else {
				sashContainer.move(part, relativePosition, relativePart, div);
			}
			part.setFocus();
		} else if (newContainer instanceof PartTabFolder) {
			movePart(part, position, (PartTabFolder) newContainer, div);
		}
	}

	void onPartDragOver(PartDropEvent e) {
		int offScreenPosition = PartDragDrop.INVALID;
		if (e.relativePosition == PartDragDrop.OFFSCREEN) {
			e.relativePosition = PartDragDrop.INVALID;

			return;
		}

		if ((e.dragSource != null)
				&& ((e.relativePosition == PartDragDrop.SHELL_BOTTOM)
						|| (e.relativePosition == PartDragDrop.SHELL_TOP)
						|| (e.relativePosition == PartDragDrop.SHELL_LEFT) || (e.relativePosition == PartDragDrop.SHELL_RIGHT))) {
			if (e.dragSource instanceof AbstractDockingComponent)
				if (!((AbstractDockingComponent) e.dragSource)
						.checkPossibilityOfDocking(e.relativePosition, null)) {
					e.relativePosition = PartDragDrop.INVALID;
				}
			return;
		}

		if (e.dropTarget == null
				&& e.relativePosition != PartDragDrop.OFFSCREEN) {
			e.dropTarget = null;
			e.relativePosition = offScreenPosition;
			return;
		}

		if (!(e.dropTarget.isViewPane() || e.dropTarget instanceof PartTabFolder)) {
			e.dropTarget = null;
			e.relativePosition = offScreenPosition;
			return;
		}

		if (e.dropTarget != null) {
			if (e.relativePosition == PartDragDrop.CENTER) {
				e.relativePosition = PartDragDrop.INVALID;
				return;
			}

			if (!((AbstractDockingComponent) e.dragSource)
					.checkPossibilityOfDocking(e.relativePosition, e.dropTarget)) {
				e.relativePosition = PartDragDrop.INVALID;
				return;
			}
		}

		if (e.dragSource.isViewPane()) {
			if (e.dragSource == e.dropTarget) {
				if (e.relativePosition == PartDragDrop.CENTER) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.INVALID;
					return;
				}
				ILayoutContainer container = e.dragSource.getContainer();
				if (!(container instanceof PartTabFolder)) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.INVALID;
					return;
				}
				if (((PartTabFolder) container).getItemCount() == 1) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.INVALID;
					return;
				}
			}

			if ((Object) e.dragSource.getContainer() == (Object) e.dropTarget) {
				if (((PartTabFolder) e.dropTarget).getItemCount() == 1) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.INVALID;
					return;
				}
			}

			return;
		}

		if (e.dragSource instanceof PartTabFolder) {

			if (e.dragSource == e.dropTarget) {
				e.dropTarget = null;
				e.relativePosition = PartDragDrop.INVALID;
				return;
			}

			if (e.dropTarget.isViewPane()) {
				if ((Object) e.dropTarget.getContainer() == (Object) e.dragSource) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.INVALID;
					return;
				}
			}

			return;
		}

		e.dropTarget = null;
		e.relativePosition = offScreenPosition;
	}

	void onPartDrop(PartDropEvent e) {
		if (e.relativePosition == PartDragDrop.INVALID) {
			return;
		}

		switch (e.relativePosition) {
		case PartDragDrop.OFFSCREEN:
			break;
		case PartDragDrop.CENTER:
			break;
		case PartDragDrop.LEFT:
		case PartDragDrop.RIGHT:
		case PartDragDrop.TOP:
		case PartDragDrop.BOTTOM:
			movePart(e.dragSource, e.relativePosition, e.dropTarget,
					calculateDiv(e));
			break;
		case PartDragDrop.SHELL_LEFT:
		case PartDragDrop.SHELL_RIGHT:
		case PartDragDrop.SHELL_TOP:
		case PartDragDrop.SHELL_BOTTOM:
			moveOnTop(e);
			break;
		}
	}

	private void moveOnTop(PartDropEvent e) {
		PartSashContainer s;
		if (!(e.dragSource.getContainer() instanceof PartSashContainer)) {
			s = (PartSashContainer) e.dragSource.getContainer()
					.getRootContainer();
			e.dragSource = (PartTabFolder) e.dragSource.getContainer();
		} else {
			s = (PartSashContainer) e.dragSource.getContainer();
		}
		float div = 0.5f;
		float sum = 0;
		if ((e.relativePosition == PartDragDrop.SHELL_LEFT)
				|| (e.relativePosition == PartDragDrop.SHELL_RIGHT)) {
			sum = e.dragSource.getBounds().width + s.getBounds().width;
			div = e.dragSource.getBounds().width / sum;
			if (e.relativePosition == PartDragDrop.SHELL_RIGHT)
				div = 1 - div;
		} else {
			sum = e.dragSource.getBounds().height + s.getBounds().height;
			div = e.dragSource.getBounds().height / sum;
			if (e.relativePosition == PartDragDrop.SHELL_BOTTOM)
				div = 1 - div;
		}
		int direction = 0;
		switch (e.relativePosition) {
		case PartDragDrop.SHELL_LEFT:
			direction = IPageLayout.LEFT;
			break;
		case PartDragDrop.SHELL_RIGHT:
			direction = IPageLayout.RIGHT;
			break;
		case PartDragDrop.SHELL_TOP:
			direction = IPageLayout.TOP;
			break;
		case PartDragDrop.SHELL_BOTTOM:
			direction = IPageLayout.BOTTOM;
			break;
		}
		s.moveOnTop(e.dragSource, direction, div);
	}

	public void removePart(ILayoutPart part) {
		if (part.isViewPane())
			disableDrag(part);

		Composite parent = (Composite) this.mainLayout.getParent();
		part.reparent(parent);

		ILayoutContainer container = part.getContainer();
		if (container != null) {
			container.replace(part, new PartPlaceholder(part.getID()));
			updateContainerVisibleTab(container);

			if (container == this.mainLayout)
				return;

			ILayoutPart[] children = container.getChildren();
			if (children != null) {
				boolean allInvisible = true;
				for (int i = 0, length = children.length; i < length; i++) {
					if (!(children[i] instanceof PartPlaceholder)) {
						allInvisible = false;
						break;
					}
				}
				if (allInvisible && (container instanceof LayoutPart)) {
					LayoutPart cPart = (LayoutPart) container;
					if (container instanceof PartTabFolder)
						((PartTabFolder) container).dispose();

					ILayoutContainer parentContainer = cPart.getContainer();
					ContainerPlaceholder placeholder = new ContainerPlaceholder(
							cPart.getID());
					placeholder.setRealContainer((LayoutPart) container);
					parentContainer.replace(cPart, placeholder);
				}
			}
		}
	}

	public void restoreState(IMemento memento) {
		IMemento childMem = memento
				.getChild(IDockingManagerConsts.TAG_MAIN_WINDOW);
		this.mainLayout.restoreState(childMem);

		this.parentWidget.setRedraw(true);
	}

	public void saveState(IMemento memento) {
		IMemento layout = memento
				.createChild(IDockingManagerConsts.TAG_MAIN_WINDOW);
		this.mainLayout.saveState(layout);
	}

	private void stack(ILayoutPart part, ILayoutPart refPart) {
		this.parentWidget.setRedraw(false);
		if (part instanceof PartTabFolder) {
			ILayoutPart visiblePart = ((PartTabFolder) part).getVisiblePart();
			ILayoutPart[] children = ((PartTabFolder) part).getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].isViewPane())
					stackView(children[i], refPart);
			}
			if (visiblePart != null) {
				bringPartToTop(visiblePart);
				visiblePart.setFocus();
			}
		} else {
			stackView(part, refPart);
			bringPartToTop(part);
			part.setFocus();
		}
		this.parentWidget.setRedraw(true);
	}

	private void stackView(ILayoutPart newPart, ILayoutPart refPart) {
		derefPart(newPart);

		ILayoutContainer newContainer;
		if (refPart instanceof ILayoutContainer)
			newContainer = (ILayoutContainer) refPart;
		else
			newContainer = refPart.getContainer();

		if (newContainer instanceof PartTabFolder) {
			PartTabFolder folder = (PartTabFolder) newContainer;
			Composite newParent = folder.getParent();
			newPart.reparent(newParent);

			folder.add(newPart);
			folder.enableDrag(newPart, this.partDropListener);
		} else if (newContainer instanceof RootLayoutContainer) {
			PartTabFolder folder = new PartTabFolder(this.folderStyle);
			((RootLayoutContainer) newContainer).replace(refPart, folder);
			folder.add(refPart);
			folder.add(newPart);
			if (!(refPart instanceof ILayoutContainer))
				folder.enableDrag(refPart, this.partDropListener);
			folder.enableDrag(newPart, this.partDropListener);
		}
	}

	private void updateContainerVisibleTab(ILayoutContainer container) {
		if (!(container instanceof PartTabFolder))
			return;
	}

	public void zoomOut() {

		if (this.zoomPart == null)
			return;

		if (this.zoomPart.isViewPane()) {
			this.parentWidget.setRedraw(false);
			this.mainLayout.zoomOut();
			this.zoomPart.setZoomed(false);
			this.parentWidget.setRedraw(true);
		} else {
			this.parentWidget.setRedraw(false);
			this.mainLayout.zoomOut();
			this.parentWidget.setRedraw(true);
		}

		this.zoomPart = null;
	}

	public boolean partChangeAffectsZoom(ILayoutPart pane) {
		if (this.zoomPart == null)
			return false;
		if (pane.isZoomed())
			return false;

		return true;
	}

	public void zoomIn(ILayoutPart pane) {

		this.zoomPart = pane;

		if (pane.isViewPane()) {
			this.parentWidget.setRedraw(false);
			this.mainLayout.zoomIn(pane);
			pane.setZoomed(true);
			this.parentWidget.setRedraw(true);
		}

		else {
			this.zoomPart = null;
			return;
		}
	}

	public void updateNames(ILayoutPart part) {
		ILayoutContainer container = part.getContainer();
		if (container instanceof PartTabFolder) {
			((PartTabFolder) container).updateTabs();
		}
	}

	private float calculateDiv(PartDropEvent e) {
		float div = (float) 0.5;
		float sum;

		if ((e.relativePosition == PartDragDrop.LEFT)
				|| (e.relativePosition == PartDragDrop.RIGHT)) {
			if ((e.dropTarget instanceof AbstractDockingComponent)
					&& (e.dragSource instanceof AbstractDockingComponent)) {
				sum = (float) ((AbstractDockingComponent) (e.dropTarget))
						.getWidth()
						+ ((AbstractDockingComponent) (e.dragSource))
								.getWidth();
				div = ((AbstractDockingComponent) (e.dragSource)).getWidth()
						/ sum;
			} else if ((e.dropTarget instanceof AbstractDockingComponent)
					&& (e.dragSource instanceof PartTabFolder)) {
				sum = (float) ((AbstractDockingComponent) (e.dropTarget))
						.getWidth()
						+ ((PartTabFolder) (e.dragSource)).getBounds().width;
				div = ((PartTabFolder) (e.dragSource)).getBounds().width / sum;
			} else if ((e.dropTarget instanceof PartTabFolder)
					&& (e.dragSource instanceof AbstractDockingComponent)) {
				sum = (float) ((PartTabFolder) (e.dropTarget)).getBounds().width
						+ ((AbstractDockingComponent) (e.dragSource))
								.getWidth();
				div = ((AbstractDockingComponent) (e.dragSource)).getWidth()
						/ sum;
			}
			if (e.relativePosition == PartDragDrop.RIGHT)
				div = 1 - div;
		} else if ((e.relativePosition == PartDragDrop.TOP)
				|| (e.relativePosition == PartDragDrop.BOTTOM)) {
			if ((e.dropTarget instanceof AbstractDockingComponent)
					&& (e.dragSource instanceof AbstractDockingComponent)) {
				sum = (float) ((AbstractDockingComponent) (e.dropTarget))
						.getHeight()
						+ ((AbstractDockingComponent) (e.dragSource))
								.getHeight();
				div = ((AbstractDockingComponent) (e.dragSource)).getHeight()
						/ sum;
			} else if ((e.dropTarget instanceof AbstractDockingComponent)
					&& (e.dragSource instanceof PartTabFolder)) {
				sum = (float) ((AbstractDockingComponent) (e.dropTarget))
						.getHeight()
						+ ((PartTabFolder) (e.dragSource)).getBounds().height;
				div = ((PartTabFolder) (e.dragSource)).getBounds().height / sum;
			} else if ((e.dropTarget instanceof PartTabFolder)
					&& (e.dragSource instanceof AbstractDockingComponent)) {
				sum = (float) ((PartTabFolder) (e.dropTarget)).getBounds().height
						+ ((AbstractDockingComponent) (e.dragSource))
								.getHeight();
				div = ((AbstractDockingComponent) (e.dragSource)).getHeight()
						/ sum;
			}
			if (e.relativePosition == PartDragDrop.BOTTOM)
				div = 1 - div;
		}

		return div;
	}
}