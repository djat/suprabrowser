/**
 * 
 */
package ss.client.ui.browser.manager;

import org.mozilla.interfaces.nsISupports;

/**
 *
 */
public interface NsObjectFinalizer {
	
	void finalize( nsISupports impl );
}
