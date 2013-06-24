/**
 * 
 */
package ss.client.ui;

import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;


/**
 *
 */
public interface ISphereView {

	String getSphereId();

	Hashtable getRawSession();
	
	/**
	 * @return
	 */
	VerbosedSession getVerbosedSession();
	
	Document getSphereDefinition();

	boolean isRootView();

	/**
	 * @return
	 */
	List<String> getSelectedMembersNames();

	/**
	 * @return
	 */
	SupraSphereFrame getSupraSphereFrame();
	
}
