package ss.client.hotkeys;

import ss.client.ui.docking.ISearchable;


public class ToggleSearchPaneAction extends AbstractAction {
	
	public void performExecute() {
		ISearchable searchable = getSupraFrame().getActiveSearchable();
		if(searchable != null) {
			searchable.toggleSearchPane();
		}
	}
}
