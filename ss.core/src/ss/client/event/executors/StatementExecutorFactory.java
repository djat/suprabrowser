/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class StatementExecutorFactory {

	public static StatementExecutor createExecutor(MessagesPane mp, Statement statement) {
		StatementExecutor executor = null;
		if(statement.isBookmark()) {
			return new BookmarkExecutor(mp, statement);
		} else if(statement.isTerse()) {
			return new TerseExecutor(mp, statement);
		} else if(statement.isMessage() || statement.isSystemMessage()) {
			return new MessageExecutor(mp, statement);
		} else if(statement.isContact()) {
			return new ContactExecutor(mp, statement);
		} else if(statement.isRss()) {
			return new RssExecutor(mp, statement);
		} else if(statement.isEmail()) {
			return new EmailExecutor(mp, statement);
		} else if(statement.isFile()) {
			return new FileExecutor(mp, statement);
		} else if(statement.isKeywords()) {
			return new KeywordsExecutor(mp, statement);
		} else if(statement.isSphere()) {
			return new SphereExecutor(mp, statement);
		} else if(statement.isResult()) {
			return new ResultExecutor(mp, statement);
		} else if(statement.isSystemStateMessage()) {
			return new SystemStateMessageExecutor(mp, statement);
		}else if(statement.isComment()) {
			return new CommentExecutor(mp, statement);
		} 
		return executor;
	}
}
