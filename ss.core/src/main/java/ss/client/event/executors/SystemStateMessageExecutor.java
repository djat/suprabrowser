/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.common.UiUtils;
import ss.domainmodel.Statement;
import ss.domainmodel.systemstatemessage.SystemStateMessage;

/**
 * @author roman
 *
 */
public class SystemStateMessageExecutor extends StatementExecutor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SystemStateMessageExecutor.class);
	
	private SystemStateMessage ssm;
	
	public SystemStateMessageExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.ssm = SystemStateMessage.wrap(statement.getBindedDocument());
	}

	@Override
	protected void browserExecute() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				getBrowser().setText(SystemStateMessageExecutor.this.ssm.getHTMLView());
			}
		});
	}

}
