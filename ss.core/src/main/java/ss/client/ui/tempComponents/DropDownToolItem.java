/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.event.createevents.CreateEmailAction;
import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.event.createevents.CreateTerseAction;
import ss.client.ui.AbstractControlPanel;
import ss.client.ui.ControlPanel;
import ss.client.ui.typeahead.TypeAheadManager;

/**
 * 
 */
public class DropDownToolItem {

	@SuppressWarnings("unused")
	private static final String[] BOOKMARK_ACTIVATION = new String[] { "http",
			"www" };

	private Vector<DropDownItemAbstractAction> actions = new Vector<DropDownItemAbstractAction>();

	private DropDownItemAbstractAction mainAction = null;

	private Menu dropDownMenu;

	private Vector<DropDownMenuItem> menuItems = new Vector<DropDownMenuItem>();

	private ToolBar toolBar;

	private ToolItem toolItem;

	private AbstractControlPanel parent;

	private boolean keywordActionEnabled = false;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DropDownToolItem.class);

	public DropDownToolItem(ToolBar toolBar, AbstractControlPanel parent) {
		this.toolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		this.toolBar = toolBar;
		this.parent = parent;
		init();
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

	private void fireNewActiveAction() {
		this.parent.layout();
		String actionName = this.getMainAction().getName();
		if(!(this.parent instanceof ControlPanel)) {
			return;
		}
		Text sendField = ((ControlPanel)this.parent).getSendField();
		TypeAheadManager.INSTANCE.removeTypeAhead(sendField);
		if (actionName.equals(CreateBookmarkAction.BOOKMARK_TITLE)) {
			TypeAheadManager.INSTANCE.addDomainkAutoComplite(sendField);
		} else if (actionName.equals(CreateKeywordsAction.KEYWORD_TITLE)) {
			TypeAheadManager.INSTANCE.addKeywordAutoComplete(sendField);
		} else if (actionName.equals(CreateTerseAction.TERSE_TITLE)) {
			TypeAheadManager.INSTANCE.addBookmarkAutoComplite(sendField);
		} else if (actionName.equals(CreateEmailAction.EMAIL_TITLE)) {
			TypeAheadManager.INSTANCE.addEmailAutoComplite((ControlPanel)this.parent, sendField);
		}
	}


	/**
	 * 
	 */
	/*
	 * public void addBookmarkAutoComplite() { if(this.parent instanceof
	 * ControlPanel) { final ControlPanel controlPanel =
	 * (ControlPanel)this.parent; checkAndRemoveExistingTypeAhead();
	 * FilteredModel model = new FilteredModel<String>( new FilteredDataSource<String>() {
	 * 
	 * private DialogsMainCli cli = controlPanel.getMP().client;
	 * 
	 * private Hashtable session = controlPanel.getMP().getSession();
	 * 
	 * public Vector<String> getData(String filter) {
	 * 
	 * return this.cli.getPrivateDomainNames(filter, session); } }, 500,
	 * BaseDataModel.FilterType.NoFilter, new DataSourceLabeler<String>() {
	 * 
	 * public String getDataLabel(String data) { return data; } });
	 * this.typeahead = new TypeAheadComponent(this.toolBar.getShell(),
	 * controlPanel.getSendField(), model, new ResultAdapter() {
	 * 
	 * @Override public void processListSelection(String listSelection) {
	 * controlPanel.getSendField().setText(""); loadBrowser(listSelection); }
	 * 
	 * });//, BOOKMARK_ACTIVATION); } }
	 */

	public String getActionName(int index) {
		return this.actions.get(index).getName();
	}

	public int getActionsCount() {
		return this.actions.size();
	}

	public DropDownItemAbstractAction getMainAction() {
		return this.mainAction;
	}

	private Menu getDropDownMenu() {
		return this.dropDownMenu;
	}

	public String getSelectedActionName() {
		return this.toolItem.getText();
	}

	private ToolBar getToolBar() {
		return this.toolBar;
	}

	private ToolItem getToolItem() {
		return this.toolItem;
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
		if (logger.isDebugEnabled()) {
			logger.debug("select action: "+actionName);
		}
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
		fireNewActiveAction();
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
		setKeywordActionEnabled(this.keywordActionEnabled);
	}

	public void setKeywordActionEnabled(boolean value) {
		this.keywordActionEnabled = value;
		for (int i = 0; i < this.menuItems.size(); i++) {
			this.menuItems.get(i);
		}
		for (DropDownMenuItem dropItem : this.menuItems) {
			if (dropItem.getAction() instanceof CreateKeywordsAction) {
				dropItem.getItem().setEnabled(value);
			} else {
				if (!dropItem.getItem().getEnabled()) {
					dropItem.getItem().setEnabled(true);
				}
			}
		}
	}

	private static void setItemAppearance(Item item,
			DropDownItemAbstractAction action) {
		item.setText(action.getName());
		item.setImage(action.getImage());
	}

	@SuppressWarnings("unused")
	private static void setItemAppearance(Button item,
			DropDownItemAbstractAction action) {
		item.setText(action.getName());
		item.setImage(action.getImage());
	}

//	/**
//	 * @param typeahead
//	 *            the typeahead to set
//	 */
//	private void setTypeahead(TypeAheadComponent typeahead) {
//		removeTypeAhead();
//		this.typeahead = typeahead;
//	}

//	/**
//	 * @return the typeahead
//	 */
//	private TypeAheadComponent getTypeahead() {
//		if(!(this.parent instanceof ControlPanel)) { 
//			return null;
//		}
//		return TagActionProcessor.getTypeahead((ControlPanel)this.parent);
//	}
	
//	private TypeAheadComponent getTypeahead() {
//		return this.typeahead;
//	}

	private class DropDownMenuItem {

		private MenuItem item;

		private DropDownMenuItem(final DropDownItemAbstractAction action) {

			this.item = new MenuItem(DropDownToolItem.this.dropDownMenu,
					SWT.PUSH);

			if (action instanceof CreateKeywordsAction) {
				this.item.setEnabled(false);
			}

			this.setAction(action);
			this.item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					DropDownItemAbstractAction action = getAction();
					action.perform();
					if(!DropDownToolItem.this.parent.isTypeLocked()) {
						setMainAction(action);
					}
					DropDownToolItem.this.parent.layout();
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
}