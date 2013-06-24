/**
 * 
 */
package ss.framework.dm3.service;

import ss.framework.networking2.Protocol;
import ss.framework.networking2.ProtocolLifetimeAdapter;
import ss.framework.networking2.ProtocolLifetimeEvent;
import ss.lab.dm3.connection.service.AbstractServiceAsyncProvider;
import ss.lab.dm3.connection.service.IServiceInvokator;
import ss.lab.dm3.connection.service.ServiceAsync;
/**
 *
 */
public class NetworkServiceAsyncProvider extends AbstractServiceAsyncProvider {

	private final Protocol protocol;
	
	/**
	 * @param protocol
	 */
	public NetworkServiceAsyncProvider(Protocol protocol) {
		super();
		this.protocol = protocol;
		this.protocol.addProtocolListener( new ProtocolLifetimeAdapter() {
			/* (non-Javadoc)
			 * @see ss.framework.networking2.ProtocolLifetimeAdapter#beginClose(ss.framework.networking2.ProtocolLifetimeEvent)
			 */
			@Override
			public void beginClose(ProtocolLifetimeEvent e) {
				super.beginClose(e);
				dispose();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.connection.service.AbstractServiceAsyncProvider#createServiceInvokator(java.lang.Class)
	 */
	@Override
	protected <T extends ServiceAsync> IServiceInvokator createServiceInvokator(
			Class<T> serviceAsyncClass) {
		return new NetworkServiceInvokator( this.protocol, serviceAsyncClass );
	}

}