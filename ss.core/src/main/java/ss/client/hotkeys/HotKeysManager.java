package ss.client.hotkeys;

import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.TypedListener;
import org.mozilla.interfaces.nsIDOMKeyEvent;

import ss.client.ui.SupraSphereFrame;

public class HotKeysManager {
	private static HotKeysManager manager;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HotKeysManager.class);

	private SupraSphereFrame sF = null;

	private static final String CLOSE_BROWSER_TAB = "close browser tab";

	private static final String MOVE_CURSOR_TO_INPUT_BAR = "move cursor to input bar";

	private static final String PERSONAL_SPHERE = "personal sphere";

	private static final String SHOW_FIND_PANE = "show find pane";

	private static final String GO_TO_LAST_OPENED_TAB = "go to last opened tab";

	private static final String NEW_BROWSER_TAB = "new browser tab";
	
	private static final String BROWSER_BACK = "browser back";
	
	private static final String BROWSER_FORWARD = "browser forward";

	private Hashtable<String, AbstractAction> tableOfAction = new Hashtable<String, AbstractAction>();

	private HotKeysManager() {
		this.tableOfAction.put(CLOSE_BROWSER_TAB, new CloseBrowserTabAction());
		this.tableOfAction.put(MOVE_CURSOR_TO_INPUT_BAR,
				new MoveCursorToInputBarAction());
		this.tableOfAction.put(PERSONAL_SPHERE, new PersonalSphereAction());
		this.tableOfAction.put(SHOW_FIND_PANE, new ToggleSearchPaneAction());
		this.tableOfAction.put(GO_TO_LAST_OPENED_TAB,
				new GoToLastOpenedTabAction());
		this.tableOfAction.put(NEW_BROWSER_TAB, new NewBrowserTabAction());
		this.tableOfAction.put(BROWSER_BACK, new BrowserBackAction());
		this.tableOfAction.put(BROWSER_FORWARD, new BrowserForwardAction());
	}

	public synchronized static HotKeysManager getInstance() {
		if (manager == null) {
			manager = new HotKeysManager();
		}
		return manager;
	}

	private String bindActionKey(KeyEvent e) {
		String key = null;
		if (e.stateMask == SWT.CONTROL) {
			switch (e.keyCode) {
			case 119: // w
				key = HotKeysManager.CLOSE_BROWSER_TAB;
				break;
			case 108: // l
				key = HotKeysManager.MOVE_CURSOR_TO_INPUT_BAR;
				break;
			case 116: // t
				key = HotKeysManager.NEW_BROWSER_TAB;
				break;
			case 104: // h
				key = HotKeysManager.PERSONAL_SPHERE;
				break;
			case 102: // f
				key = HotKeysManager.SHOW_FIND_PANE;
				break;
			case 9: // tab
				key = HotKeysManager.GO_TO_LAST_OPENED_TAB;
				break;

			}
		}
		return key;
	}

	private String bindActionKey(nsIDOMKeyEvent e) {
		String key = null;
		if (e.getCtrlKey()) {
			long code = e.getKeyCode();
			if (code == nsIDOMKeyEvent.DOM_VK_W) {
				key = HotKeysManager.CLOSE_BROWSER_TAB;
			} else if (code == nsIDOMKeyEvent.DOM_VK_L) {
				key = HotKeysManager.MOVE_CURSOR_TO_INPUT_BAR;
			} else if (code == nsIDOMKeyEvent.DOM_VK_T) {
				key = HotKeysManager.NEW_BROWSER_TAB;
			} else if (code == nsIDOMKeyEvent.DOM_VK_F) {
				key = HotKeysManager.SHOW_FIND_PANE;
			} else if (code == nsIDOMKeyEvent.DOM_VK_H) {
				key = HotKeysManager.PERSONAL_SPHERE;
			} else if (code == nsIDOMKeyEvent.DOM_VK_TAB) {
				key = HotKeysManager.GO_TO_LAST_OPENED_TAB;
			}
		}
		else if (e.getAltKey()) {
			long code = e.getKeyCode();
			
			logger.warn("KEY CODE: "+e.getKeyCode());
			if (code == 37) {
				
				key = HotKeysManager.BROWSER_BACK;
			}
			else if (code == 39) {
				key = HotKeysManager.BROWSER_FORWARD;
			}
			
		}
		return key;
	}

	public void beginMonitor(final SupraSphereFrame sF) {
		this.sF = sF;
		sF.getDisplay().addFilter(SWT.KeyDown,
				new TypedListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						HotKeysManager.getInstance().execAction(e);
					}

				}));
	}

	public void execAction(KeyEvent e) {
		String hotKey = this.bindActionKey(e);
		AbstractAction actionType;
		if (hotKey != null && this.tableOfAction.containsKey(hotKey)) {
			actionType = this.tableOfAction.get(hotKey);
			actionType.execute();
		}
	}

	public void execAction(nsIDOMKeyEvent e) {
		String hotKey = this.bindActionKey(e);
		AbstractAction actionType;
		if (hotKey != null && this.tableOfAction.containsKey(hotKey)) {
			actionType = this.tableOfAction.get(hotKey);
			actionType.execute();
		}
	}

	public void execAction(String key) {
		try {
			this.tableOfAction.get(key).execute();
		} catch (NullPointerException e) {
			logger.error("Action "+key+" doesn't exist", e);
		}
	}

	public SupraSphereFrame getSupraFrame() {
		return this.sF;
	}
}
