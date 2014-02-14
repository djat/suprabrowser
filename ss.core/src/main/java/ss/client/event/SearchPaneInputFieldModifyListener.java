/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.widgets.SearchPane;

/**
 * @author roman
 *
 */
public class SearchPaneInputFieldModifyListener implements ModifyListener {

	private SearchPane sp;
	private final static Color CANNOT_FOUND_COLOR = new Color(Display.getDefault(), 230, 180, 150);
	private int previousLength = 0;
	private boolean found = true;
	
	public SearchPaneInputFieldModifyListener(SearchPane sp) {
		this.sp = sp;
	}
	
	public void modifyText(ModifyEvent e) {
		boolean enabled = (((Text)e.widget).getText()!=null && !((Text)e.widget).getText().trim().equals(""));
		
		this.sp.getFindNextButton().setEnabled(enabled);
		
		if(enabled) {
			textNotNullAction(e);
		} else {
			noTextAction(e);
		}
	}
	
	private void noTextAction(ModifyEvent e) {
		this.sp.getParentDocking().getBrowser().unhighlightFindResult();
		((Text)e.widget).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		this.previousLength = 0;
	}
	
	private void textNotNullAction(ModifyEvent e) {
		String sought = ((Text)e.widget).getText();
		
		if(this.previousLength<sought.length()) {
			textLonger(e, sought);
		} else {
			textReduce(e, sought);
		}
		this.previousLength = ((Text)e.widget).getText().length();
	}
	
	private void textLonger(ModifyEvent e, String sought) {
		if(!this.sp.getParentDocking().getBrowser().findFirst(sought)) {
			textNotFoundAction(e);
		} else {
			((Text)e.widget).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			this.found = true;
		}
	}
	
	private void textReduce(ModifyEvent e, String sought) {
		if(this.found) {
			if(!this.sp.getParentDocking().getBrowser().findAtSamePosition(sought)) {
				textNotFoundAction(e);
			} else {
				((Text)e.widget).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				this.found = true;
			}
		} else {
			textLonger(e, sought);
		}
	}
	
	private void textNotFoundAction(ModifyEvent e) {
		this.sp.getParentDocking().getBrowser().unhighlightFindResult();
		((Text)e.widget).setBackground(CANNOT_FOUND_COLOR);
		this.sp.getFindNextButton().setEnabled(false);
		this.found = false;
	}

}
