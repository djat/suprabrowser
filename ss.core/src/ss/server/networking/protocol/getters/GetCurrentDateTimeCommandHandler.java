/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ss.client.networking.protocol.getters.GetCurrentDateTimeCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class GetCurrentDateTimeCommandHandler extends
		AbstractGetterCommandHandler<GetCurrentDateTimeCommand, Date> {

	public GetCurrentDateTimeCommandHandler(DialogsMainPeer peer) {
		super(GetCurrentDateTimeCommand.class, peer);
	}
	
	@Override
	protected Date evaluate(GetCurrentDateTimeCommand command) throws CommandHandleException {
		return Calendar.getInstance(Locale.US).getTime();
	}
}