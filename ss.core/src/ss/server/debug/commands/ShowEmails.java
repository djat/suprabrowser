/**
 * 
 */
package ss.server.debug.commands;

import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.smtp.sender.resendcontrol.PostpondedEmailsInfoGenerator;

/**
 * @author zobo
 *
 */
public class ShowEmails implements IRemoteCommand {

	public String evaluate( RemoteCommandContext context) throws Exception {
		return PostpondedEmailsInfoGenerator.INSTANCE.getInfo();
	}

}
