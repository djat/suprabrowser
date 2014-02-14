/**
 * 
 */
package ss.client.ui.root;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.ui.ControlPanel;
import ss.client.ui.tempComponents.DropDownItemAbstractAction;

/**
 * @author zobo
 *
 */
public class RootDropDownToolItem {
	
	private class DropDownMenuItem {

		private MenuItem item;

		private DropDownMenuItem(final DropDownItemAbstractAction action) {

			this.item = new MenuItem(RootDropDownToolItem.this.dropDownMenu,
					SWT.PUSH);

			if (action instanceof CreateKeywordsAction) {
				this.item.setEnabled(false);
			}

			this.setAction(action);
			this.item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					DropDownItemAbstractAction action = getAction();
					action.perform();
					setMainAction(action);
					RootDropDownToolItem.this.parent.layout();
				}

			});

		}

		public MenuItem getItem() {
			return this.item;
		}

		private DropDownItemAbstractAction getAction() {
			return (DropDownItemAbstractAction) this.item.getData();
		}

		private void setAction(DropDownItemAbstractAction action) {
			this.item.setData(action);
			setItemAppearance(this.item, action);
		}
	}
	
	//private DropDownToolItem item;
	
	private Vector<DropDownItemAbstractAction> actions = new Vector<DropDownItemAbstractAction>();

	private DropDownItemAbstractAction mainAction = null;

	private Menu dropDownMenu;

	private Vector<DropDownMenuItem> menuItems = new Vector<DropDownMenuItem>();
	
	private ToolBar toolBar;

	private ToolItem toolItem;
	
	private ILayoutable parent;
	
	/**
	 * @param toolbar
	 */
	public RootDropDownToolItem(ToolBar toolBar, ILayoutable parent) {
		this.toolBar = toolBar;
		this.parent = parent;
		this.toolItem = new ToolItem(this.toolBar, SWT.DROP_DOWN);
		init();
	}
	
	private void init() {
		this.dropDownMenu = new Menu(this.toolBar.getShell(), SWT.POP_UP);
		this.toolItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					Rectangle rect = getToolItem().getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = getToolBar().toDisplay(pt);
					getDropDownMenu().setLocation(pt.x, pt.y);
					getDropDownMenu().setVisible(true);
				} else {
					if (getMainAction() != null) {
						getMainAction().perform();
					}
				}
			}

		});
	}
	
	public void addAction(DropDownItemAbstractAction action) {
		this.actions.add(action);
		if (getMainAction() == null) {
			setMainAction(action);
		} else {
			addActionToMenu(action);
		}
	}
	
	private void addActionToMenu(DropDownItemAbstractAction action) {
		this.menuItems.add(new DropDownMenuItem(action));
	}

	public void clearActions() {
		this.toolItem.setText("");
		this.mainAction = null;
		for (MenuItem i : this.dropDownMenu.getItems()) {
			i.dispose();
		}
		this.actions.clear();
		this.menuItems.clear();
	}
	
	public void removeElement(String actionName) {
		if (getActionsCount() <= 1) {
			clearActions();
			return;
		}
		if (actionName.equals(this.toolItem.getText())) {
			setMainAction(this.menuItems.get(0).getAction());
		}

		for (DropDownItemAbstractAction action : this.actions) {
			if (action.getName().equals(actionName))
				this.actions.remove(action);
		}
		for (DropDownMenuItem item : this.menuItems) {
			if (item.getAction().getName().equals(actionName)) {
				this.menuItems.remove(item);
				return;
			}
		}

	}

	public void selectActiveAction(String actionName) {
		if (this.parent instanceof ControlPanel) {
			ControlPanel controlPanel = (ControlPanel) this.parent;
			if (actionName != null) {
				if (actionName.equals(CreateKeywordsAction.KEYWORD_TITLE)) {
					controlPanel.getTagBox().setSelection(true);
					controlPanel.getReplyBox().setSelection(false);
				} else {
					controlPanel.getTagBox().setSelection(false);
				}
				if (!actionName.equals(this.toolItem.getText())) {

					for (DropDownMenuItem item : this.menuItems) {
						if (actionName.equals(item.getAction().getName())) {
							setMainAction(item.getAction());
							return;
						}
					}
				}
			}
		}
	}

	public void setData(GridData data) {
		this.toolItem.setData(data);
	}
	
	private void setMainAction(DropDownItemAbstractAction action) {
		this.mainAction = action;
		setItemAppearance(this.toolItem, action);
		updateApearence();
	}
	
	private void updateApearence() {
		int i = 0;
		for (DropDownItemAbstractAction action : this.actions) {
			if (!(this.mainAction.equals(action))) {
				DropDownMenuItem item = this.menuItems.get(i);
				item.setAction(action);
				i++;
			}
		}
	}
	
	private static void setItemAppearance(Item item,
			DropDownItemAbstractAction action) {
		item.setText(action.getName());
		item.setImage(action.getImage());
	}

	private int getActionsCount() {
		return this.actions.size();
	}

	private DropDownItemAbstractAction getMainAction() {
		return this.mainAction;
	}

	private Menu getDropDownMenu() {
		return this.dropDownMenu;
	}

	private ToolBar getToolBar() {
		return this.toolBar;
	}

	private ToolItem getToolItem() {
		return this.toolItem;
	}
}
