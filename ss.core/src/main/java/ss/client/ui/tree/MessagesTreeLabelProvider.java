/**
 * 
 */
package ss.client.ui.tree;

import java.io.IOException;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import ss.client.ui.SDisplay;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.MessageStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;
import ss.util.ImagesPaths;

/**
 * @author roman
 * 
 */
public class MessagesTreeLabelProvider implements ILabelProvider {

	public Image getImage(Object obj) {
		try {
			return new Image(SDisplay.display.get(), getClass().getResource(
					getIconPath((MessagesMutableTreeNode) obj)).openStream());
		} catch (IOException ex) {
			return null;
		}
	}

	public String getText(Object obj) {
		Statement statement = (Statement) ((MessagesMutableTreeNode) obj)
				.getUserObject();
		String subject = statement.getSubject();

		return (subject != null) ? subject : "";
	}

	public void addListener(ILabelProviderListener arg0) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}

	private String getIconPath(final MessagesMutableTreeNode node) {
		String iconPath = null;

		Statement statement = (Statement) node.getUserObject();

		String status = node.getNodeStatus();

		if (statement.isMessage() || statement.isSystemMessage()) {

			MessageStatement mess = MessageStatement.wrap(statement
					.getBindedDocument());
			if (mess.isServerSystemMessage()) {
				String terseSysType = mess.getSystemType();
				if (terseSysType.equals(TerseStatement.SYSTEM_TYPE_ERROR)) {
					iconPath = ImagesPaths.ICON_SYSTEM_ERROR;
				} else if (terseSysType
						.equals(TerseStatement.SYSTEM_TYPE_WARNING)) {
					iconPath = ImagesPaths.ICON_SYSTEM_WARNING;
				} else {
					iconPath = ImagesPaths.ICON_SYSTEM_INFO;
				}
			} else {
				iconPath = ImagesPaths.MESSAGE;
			}
		} else if (statement.isEmail()) {
			ExternalEmailStatement email = ExternalEmailStatement
					.wrap(statement.getBindedDocument());
			if (email.isInput()) {
				iconPath = ImagesPaths.EMAIL_IN_ICON;
			} else {
				iconPath = ImagesPaths.EMAIL_OUT_ICON;
			}
		} else if (statement.isFileSystem()) {
			iconPath = ImagesPaths.FILE_SYSTEM;

		} else if (statement.isSystemFile()) {
			iconPath = ImagesPaths.SYSFILE;

		} else if (statement.isKeywords()) {
			iconPath = ImagesPaths.KEYWORDS;

		} else if (statement.isRss()) {
			iconPath = ImagesPaths.RSS;
		} else if (statement.isTerse()) {
			TerseStatement terse = TerseStatement.wrap(statement
					.getBindedDocument());
			if (terse.isServerSystemMessage()) {
				String terseSysType = terse.getSystemType();
				if (terseSysType.equals(TerseStatement.SYSTEM_TYPE_ERROR)) {
					iconPath = ImagesPaths.ICON_SYSTEM_ERROR;
				} else if (terseSysType
						.equals(TerseStatement.SYSTEM_TYPE_WARNING)) {
					iconPath = ImagesPaths.ICON_SYSTEM_WARNING;
				} else {
					iconPath = ImagesPaths.ICON_SYSTEM_INFO;
				}
			} else {
				iconPath = ImagesPaths.TERSE;
			}
		} else if (statement.isBookmark()) {
			iconPath = ImagesPaths.BOOKMARK;
		} else if (statement.isMembership()) {
			iconPath = ImagesPaths.MEMBERSHIP;
		} else if (statement.isSphere()) {
			iconPath = ImagesPaths.SPHERE;
		} else if (statement.isFile()) {
			iconPath = ImagesPaths.FILE;
		} else if (statement.isComment()) {
			iconPath = ImagesPaths.COMMENT;
		} else if (statement.isReply()) {
			iconPath = ImagesPaths.REPLY;
		} else if (statement.isAudio()) {
			if (status.equals("pending")) {
				iconPath = ImagesPaths.AUDIO;
			} else if (status.equals("ratified")) {
				iconPath = ImagesPaths.AUDIO_RATIFIED;
			}
		} else if (statement.isContact()) {
			iconPath = ImagesPaths.CONTACT;
		} else if (statement.isResult()) {
			iconPath = ImagesPaths.RESULT_ICON;
		} else if (statement.isSystemStateMessage()) {
			iconPath = ImagesPaths.ICON_SYSTEM_STATE_MESSAGE;
		} else {
			if ((status != null) && (status.equals("on"))) {
				iconPath = getStatusOnIconPath(statement);
			} else {
				String type = statement.getType();
				iconPath = getDeprecatedObjectsIconPath( type, status );
			}
		}
		return iconPath;
	}

	/**
	 * @param type
	 * @return
	 */
	private String getDeprecatedObjectsIconPath( final String type, final String status ) {
		String iconPath = null;
		if (type.equals("library")) {
			if (status.equals("pending")) {
				iconPath  = ImagesPaths.LIBRARY;
			} else if (status.equals("ratified")) {
				iconPath = ImagesPaths.LIBRARY_RATIFIED;
			}
		} else if (type.equals("tool")) {

			if (status.equals("pending")) {
				iconPath = ImagesPaths.TOOL;
			} else if (status.equals("ratified")) {
				iconPath = ImagesPaths.TOOL_RATIFIED;
			}
		} else if (type.equals("edit")) {

			if (status.equals("pending")) {
				iconPath = ImagesPaths.EDIT;
			} else if (status.equals("ratified")) {
				iconPath = ImagesPaths.EDIT_RATIFIED;
			} else if (status.equals("rejected")) {
				iconPath = ImagesPaths.EDIT_REJECTED;
			}

		} else if (type.equals("vote")) {
			iconPath = ImagesPaths.VOTE;
		} else if (type.equals("persona") || type.equals("filter")) {

			if (status.equals("pending")) {
				iconPath = ImagesPaths.PERSONA;
			} else if (status.equals("ratified")) {
				iconPath = ImagesPaths.PERSONA_RATIFIED;

			}
		} else if (type.equals("rap") || type.endsWith("transcript")) {

			if (status.equals("pending")) {
				iconPath = ImagesPaths.RAP;
			} else {

				iconPath = ImagesPaths.RAP_RATIFIED;
			}
		} else if (type.equals("source")) {

			if (status.equals("pending")) {
				iconPath = ImagesPaths.SOURCE;
			} else {

				iconPath = ImagesPaths.SOURCE_RATIFIED;
			}
		}
		return iconPath;
	}

	private String getStatusOnIconPath(final Statement statement) {
		String iconPath = null;
		if (statement.isKeywords()) {
			iconPath = ImagesPaths.KEYWORDS;
		} else if (statement.isComment() || statement.isMessage()
				|| statement.isBookmark() || statement.isTerse()
				|| statement.isReply() || statement.isEmail()) {
			iconPath = ImagesPaths.COMMON_OFF;
		} else if (statement.isContact()) {
			iconPath = ImagesPaths.COMMON_OFF;

		} else {
			String type = statement.getType();
			if (type.equals("rap") || type.endsWith("transcript")) {
				iconPath = ImagesPaths.COMMON_OFF;
			}
			if (type.equals("edit")) {
				iconPath = ImagesPaths.EDIT_OFF;

			} else if (type.equals("filesystem")) {
				iconPath = ImagesPaths.FILE_SYSTEM_OFF;

			} else if (type.equals("systemfile")) {
				iconPath = ImagesPaths.SYSFILE_OFF;
			} else {
				iconPath = ImagesPaths.COMMON_OFF;
			}
		}
		return iconPath;
	}
}
