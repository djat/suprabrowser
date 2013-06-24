/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.List;

import ss.client.ui.MessagesPane;
import ss.client.ui.viewers.comment.BlockCommentsPane;
import ss.client.ui.viewers.comment.CommentTabFolder;
import ss.domainmodel.CommentStatement;

/**
 * @author roman
 *
 */
public class CommentedPlacesListMouseAdapter extends MouseAdapter {

	private CommentTabFolder folder;
	
	public CommentedPlacesListMouseAdapter(CommentTabFolder folder) {
		this.folder = folder;
	}
	
	public void mouseDown(MouseEvent e) {
		
		if(e.button==1) {
			int index = ((List)e.widget).getSelectionIndex();
			String selection = ((List)e.getSource()).getItem(index);
			
			CommentStatement correspComment = this.folder.getAllCommentedPlacesPane().getComments().get(index);
			
			MessagesPane mp = this.folder.getApplWindow().getMP();
			mp.selectMessage(correspComment);
			mp.setLastSelectedDoc(correspComment.getBindedDocument());
			
			this.folder.getApplWindow().setNumber(correspComment.getNumber());
			
			mp.getSupraSphereFrame().getActiveBrowser().findCommentedPlace(correspComment, true);
			
			BlockCommentsPane blockPane = this.folder.getBlockCommentsPane(); 
			blockPane.setTextToSubjectField(selection);
			blockPane.fillEditorPane(selection);
			blockPane.layout();
			blockPane.redraw();

			this.folder.setSelection(this.folder.getBlockCommentsItem());
			
			blockPane.getTextContainer().setFocus();

			this.folder.getPostCommentPane().getSubjectField().setText(selection);
		}
	}
}
