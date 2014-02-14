package ss.server.networking.protocol;

import java.util.Hashtable;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;

//TODO Client dont send this. need to remove
/**
 * @deprecated
 */
public class Check_authHandler implements ProtocolHandler{
	
	
	/**
	 * @deprecated
	 * @param peer
	 */
	public Check_authHandler()
	{
		
	}

	public void handleCheck_auth() {
		// TODO Auto-generated method stub
		
	}

	public String getProtocol() {
		return SSProtocolConstants.CHECK_AUTH;
	}

	public void handle(Hashtable update) {		
		handleCheck_auth();
	}

}
