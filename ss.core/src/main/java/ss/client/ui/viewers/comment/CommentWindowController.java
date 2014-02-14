/**
 * 
 */
package ss.client.ui.viewers.comment;

import java.util.ArrayList;

import ss.common.UiUtils;

/**
 * @author roman
 *
 */
public class CommentWindowController {

	private ArrayList<CommentApplicationWindow> windowList = null;
	
	public CommentWindowController() {
		this.windowList = new ArrayList<CommentApplicationWindow>();
	}
	
	public void addCommentWindow(CommentApplicationWindow cw) {
		this.windowList.add(cw);
	}
	
	private boolean hasCommentWindow() {
		if(this.windowList.size()==0)
			return false;
		else
			return this.windowList.get(0)!=null;
	}
	
	public void disposeCommentWindow() {
		if(hasCommentWindow()) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						CommentApplicationWindow caw = getCommentWindowList().get(0);
						getCommentWindowList().remove(0);
						caw.close();
					}	
				});			
		}
	}
	
	public void removeCommentWindow() {
		if(hasCommentWindow()) {
			this.windowList.remove(0);
		}
	}
	
	public ArrayList<CommentApplicationWindow> getCommentWindowList() {
		return this.windowList;
	}
}
