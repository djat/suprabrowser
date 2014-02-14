/**
 * 
 */
package ss.client.ui.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author roman
 *
 */
public class ThreadSystemMessagesController {
	
	private List<String> threadList = new ArrayList<String>();
	
	public ThreadSystemMessagesController() {
	}
	
	public void addThread(final String threadId) {
		if(threadId==null || isShown(threadId)) {
			return;
		}
		this.threadList.add(threadId);
	}
	
	public boolean isShown(final String threadId) {
		if(threadId==null) {
			return false;
		}
		return this.threadList.contains(threadId);
	}
	
	public void hideThread(final String threadId) {
		if(threadId==null || !isShown(threadId)) {
			return;
		}
		this.threadList.remove(threadId);
	}
}
