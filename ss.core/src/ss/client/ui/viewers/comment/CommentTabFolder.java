package ss.client.ui.viewers.comment;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.localization.LocalizationLinks;


public class CommentTabFolder extends CTabFolder {

	private CommentTabItem postItem;

	private CommentTabItem blockCommentsItem;

	private CommentTabItem allCommentedPlaceItem;

	private CommentApplicationWindow caw;

	private final static String BLOCK_COMMENTS = "COMMANTTABFOLDER.BLOCK_COMMENTS";

	private final static String ALL_COMMENTED_PLACES = "COMMANTTABFOLDER.ALL_COMMENTED_PLACES";

	private final static String POST_NEW_COMMENT = "COMMANTTABFOLDER.POST_A_NEW_COMMENT";

	private final static String REPLY_TO_COMMENT = "COMMANTTABFOLDER.REPLY_TO_COMMENT";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_COMMENT_COMMENTTABFOLDER);

	public CommentTabFolder(Composite comp, CommentApplicationWindow caw) {
		super(comp, SWT.TOP);
		this.caw = caw;
		this.setVisible(true);
		setLayout(new GridLayout(3, true));

		this.layoutItemsSE();

		this.setSelection(0);
		this.layout();
		this.redraw();
		addTabChangeListener();
	}

	private void addTabChangeListener() {
		this.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				if (CommentTabFolder.this.getSelection() == CommentTabFolder.this
						.getPostItem()) {
					getPostCommentPane().setFocusToInputField();
				} else if (CommentTabFolder.this.getSelection() == CommentTabFolder.this
						.getBlockCommentsItem()) {
					CommentTabFolder.this.getBlockCommentsPane()
							.getTextContainer().setFocus();
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {

			}
		});
	}

	private void layoutItemsSE() {
		this.postItem = new CommentTabItem(this);
		this.postItem.setText(this.bundle.getString(POST_NEW_COMMENT));
		this.postItem.setContent(new PostCommentPane(this));

		if (this.caw.getSelectedComment() != null) {
			this.postItem.setText(this.bundle.getString(REPLY_TO_COMMENT));

			this.blockCommentsItem = new CommentTabItem(this);
			this.blockCommentsItem.setText(this.bundle.getString(BLOCK_COMMENTS));
			this.blockCommentsItem.setContent(new BlockCommentsPane(this));

			this.allCommentedPlaceItem = new CommentTabItem(this);
			this.allCommentedPlaceItem.setText(this.bundle.getString(ALL_COMMENTED_PLACES));
			this.allCommentedPlaceItem.setContent(new AllCommentedPlacesPane(
					this));
		}
	}

	public CommentApplicationWindow getApplWindow() {
		return this.caw;
	}

	public CommentTabItem getPostItem() {
		return this.postItem;
	}

	public CommentTabItem getBlockCommentsItem() {
		return this.blockCommentsItem;
	}

	public PostCommentPane getPostCommentPane() {
		return (PostCommentPane) this.postItem.getInternalPane();
	}

	public BlockCommentsPane getBlockCommentsPane() {
		return (BlockCommentsPane) this.blockCommentsItem.getInternalPane();
	}

	public AllCommentedPlacesPane getAllCommentedPlacesPane() {
		return (AllCommentedPlacesPane) this.allCommentedPlaceItem
				.getInternalPane();
	}
}
