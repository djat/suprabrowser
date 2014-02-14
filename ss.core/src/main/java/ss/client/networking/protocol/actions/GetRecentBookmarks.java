/**
 * Jul 5, 2006 : 4:23:25 PM
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.client.networking.protocol.getters.GetRecentBookmarksCommand;
import ss.server.networking.SC;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class GetRecentBookmarks {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetRecentBookmarks.class);
	
	// TODO move
	public static final String VALUE = "value";

	// TODO move
	public static final String RESPONSE_ID = "response_id";

	private final DialogsMainCli cli;

	public GetRecentBookmarks(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	private void preprocessBookmarks( Vector<Document> bookmarks, String messageId,
									String localSphereId, 	Hashtable session ) {
		logger.info("got recent back: ");		
		String sphereId = (String) session.get(SessionConstants.SPHERE_ID);
		logger.info("current sphere in update: " + sphereId);
		logger.info("localSphere for insert: " + localSphereId);
		for (Document doc : bookmarks ) {
			if (doc.getRootElement().element(RESPONSE_ID) == null) {
				doc.getRootElement().addElement(RESPONSE_ID).addAttribute(
						VALUE, messageId);
				Hashtable newUpdate = new Hashtable();
				newUpdate.put(SessionConstants.DOCUMENT, doc);
				newUpdate.put(SessionConstants.SPHERE, localSphereId);
				newUpdate.put(SessionConstants.IS_UPDATE, "true");
				this.cli.callInsert( new PostponedUpdate( newUpdate ) );
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	public Vector<Document> getRecentBookmarks(Hashtable session, String homeSphereId,
			String homeMessageId, String localSphereId) {
		GetRecentBookmarksCommand command = new GetRecentBookmarksCommand();
		command.putSessionArg( session );
		command.putArg(SC.HOME_SPHERE_ID, homeSphereId);
		command.putArg(SC.HOME_MESSAGE_ID, homeMessageId);
		Vector<Document> bookmarks = command.execute( this.cli, Vector.class );
		preprocessBookmarks( bookmarks, homeMessageId, localSphereId, session );
		return bookmarks; 
	}

}
