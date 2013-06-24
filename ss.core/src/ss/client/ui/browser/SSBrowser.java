/**
 * 
 */
package ss.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SSBrowser extends SupraBrowser {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(SSBrowser.class);
	
	private int currentindex = -1;
	
	private final List<String> loadedDocuments = new ArrayList<String>();
	
	public SSBrowser(Composite parent, int style, boolean belongsToBrowserPane) {
		super(parent, style, belongsToBrowserPane);
	}

	public SSBrowser(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public boolean back() {
		boolean canGoBack = isBackEnabled();
		if(canGoBack) {
			goToDoc(this.currentindex-1);
			this.currentindex--;
		}
		return canGoBack;
	}

	@Override
	public boolean forward() {
		boolean canGoForward = isForwardEnabled();
		if(canGoForward) {
			goToDoc(this.currentindex+1);
			this.currentindex++;
		}
		return canGoForward;
	}
	
	@Override
	public boolean isBackEnabled() {
		return this.currentindex > 0;
	}

	@Override
	public boolean isForwardEnabled() {
		return this.currentindex < this.loadedDocuments.size()-1;
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		this.loadedDocuments.add(this.currentindex+1, text);
		this.currentindex++;
		
		while(this.loadedDocuments.size()>(this.currentindex+1)) {
			this.loadedDocuments.remove(this.loadedDocuments.size()-1);
		}
	}
	
	private void goToDoc(final int index) {
		super.setText(this.loadedDocuments.get(index));
	}
}
