package ss.framework.dm3;

import ss.framework.dm3.backend.NetworkConnectionBackEnd;
import ss.framework.dm3.service.NetworkServiceAsyncProvider;
import ss.framework.networking2.Protocol;
import ss.lab.dm3.connection.AbstractConnectionBackEnd;
import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.connection.ConnectionBuilder;
import ss.lab.dm3.connection.service.IServiceBackEndFactory;

public class NetworkConnectionBuilder {

	private ConnectionBuilder connectionBuilder;
	
	public Connection createNetworkConnection(Protocol protocol) {
		return this.connectionBuilder.create( new NetworkServiceAsyncProvider(protocol) );
	}

	public AbstractConnectionBackEnd createConnectionBackEnd( Protocol protocol, String accountName ) {
		IServiceBackEndFactory serviceBackEndFactory = this.connectionBuilder.createServiceBackEndFactory( accountName );
		return new NetworkConnectionBackEnd( protocol, serviceBackEndFactory );
	}
	
}
