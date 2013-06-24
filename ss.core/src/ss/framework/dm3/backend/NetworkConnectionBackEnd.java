/**
 * 
 */
package ss.framework.dm3.backend;

import ss.framework.networking2.Protocol;
import ss.lab.dm3.connection.AbstractConnectionBackEnd;
import ss.lab.dm3.connection.service.IServiceBackEndFactory;

/**
 *
 */
public class NetworkConnectionBackEnd extends AbstractConnectionBackEnd {

	private final Protocol protocol;

	private final IServiceBackEndFactory serviceBackEndFactory;

	/**
	 * @param protocol
	 * @param serviceBackEndFactory
	 */
	public NetworkConnectionBackEnd(Protocol protocol,
			IServiceBackEndFactory serviceBackEndFactory) {
		super();
		this.protocol = protocol;
		this.serviceBackEndFactory = serviceBackEndFactory;
		this.protocol.registerHandler( new ServiceCommandHandler( this.serviceBackEndFactory ) );
	}

}
