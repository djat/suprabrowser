package ss.server.domainmodel2;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ss.common.networking2.ProtocolStartUpInformation;
import ss.framework.domainmodel2.IDataProvider;
import ss.framework.networking2.Protocol;
import ss.server.db.DbUrlProvider;
import ss.server.domainmodel2.db.DbDataProvider;
import ss.server.networking2.ServerProtocolManager;

public class SupraServerDataProviderFactory  {

	public final static SupraServerDataProviderFactory INSTANCE = new SupraServerDataProviderFactory();

	private final IDataProvider serverDataProvider; 
	
	private SupraServerDataProviderFactory() {
		final DbDataProvider dbDataProvider = new DbDataProvider( DbUrlProvider.INSTANCE.getDbUrl() );
		final RuntimeDataProvider runtimeDataProvider = new RuntimeDataProvider( dbDataProvider ); 
		this.serverDataProvider = new ServerDataProvider( dbDataProvider, runtimeDataProvider ); 
	}

	/**
	 * @return the dataProvider
	 */
	public IDataProvider getServerDataProvider() {
		return this.serverDataProvider;
	}

	/**
	 * @param session
	 * @param cdatain
	 * @param cdataout
	 */
	public void createAndStart(ProtocolStartUpInformation startUpInfo, DataInputStream datain, DataOutputStream dataout) {
		Protocol protocol = new Protocol( datain, dataout, startUpInfo.generateProtocolDisplayName( "DomainModel" ) );
		new NetworkDataProviderHandler( protocol, this.serverDataProvider );
		protocol.start(ServerProtocolManager.INSTANCE);
	}
	
}
