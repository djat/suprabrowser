/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.common.StringUtils;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class FileExecutor extends StatementExecutor {

	/**
	 * @param mp
	 * @param statement
	 */
	public FileExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
	}


	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		if (doWebHighlight()) {
			return;
		}
		FileStatement file = FileStatement.wrap(this.statement.getBindedDocument());
		String html = "<table>";
		String[] strs = file.getDataId().split("_____");
		String headerStyle = "style=\"color:darkgray; font-weight:bold; font-size:12; text-align:bottom\"";
		String style = "style=\"font-size:12; font-weight:bold\"";
		html += "<tr><td "+headerStyle+" >Filename: </td><td "+style+">"+strs[strs.length-1]+"</td></tr>";
		html += "<tr><td "+headerStyle+" >Giver: </td><td "+style+">"+file.getGiver()+"</td></tr>";
		html += "<tr><td "+headerStyle+" >Moment: </td><td "+style+">"+file.getDisplayMomentLong()+"</td></tr>";
		html += "<tr><td "+headerStyle+">Size: </td><td "+style+">"+file.getSizeToString()+"</td></tr>";
		if(StringUtils.isNotBlank(file.getBody())) {
			html += "<tr><td "+headerStyle+">Description: </td><td "+style+">"+file.getBody()+"</td></tr>";
		}
		html += "</table>";
		
		this.mp.getSmallBrowser().setText(html);
	}

}
