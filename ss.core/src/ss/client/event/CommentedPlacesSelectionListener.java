/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import ss.client.ui.MessagesPane;
import ss.client.ui.viewers.comment.BlockCommentsPane;
import ss.client.ui.viewers.comment.CommentTabFolder;
import ss.domainmodel.CommentStatement;

/**
 * @author roman
 *
 */
public class CommentedPlacesSelectionListener implements SelectionListener {

	private CommentTabFolder folder;
	
	public CommentedPlacesSelectionListener(CommentTabFolder folder) {
		this.folder = folder;
	}
	
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	
	public void widgetSelected(SelectionEvent e) {
		Button button = (Button)e.widget;

		CommentStatement comment = this.folder.getAllCommentedPlacesPane().getButtonTable().get(button);

		MessagesPane mp = this.folder.getApplWindow().getMP();
		mp.selectMessage(comment);
		mp.setLastSelectedDoc(comment.getBindedDocument());

		this.folder.getApplWindow().setNumber(comment.getNumber());

		mp.getSupraSphereFrame().getActiveBrowser().findCommentedPlace(comment, true);

		BlockCommentsPane blockPane = this.folder.getBlockCommentsPane();
		blockPane.setTextToSubjectField(button.getText());
		this.folder.getApplWindow().setSelectedComment(comment);
		blockPane.fillEditorPane(button.getText());

		blockPane.layout();
		blockPane.redraw();

		this.folder.setSelection(this.folder.getBlockCommentsItem());
		blockPane.getTextContainer().setFocus();

		this.folder.getPostCommentPane().getSubjectField().setText(button.getText());

	}

}
