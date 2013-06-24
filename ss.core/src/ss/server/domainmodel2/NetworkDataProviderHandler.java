/**
 * 
 */
package ss.server.domainmodel2;


import java.util.List;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.DataChangedEvent;
import ss.framework.domainmodel2.DataProviderException;
import ss.framework.domainmodel2.DataProviderListener;
import ss.framework.domainmodel2.IDataProvider;
import ss.framework.domainmodel2.NullDataProvider;
import ss.framework.domainmodel2.Record;
import ss.framework.domainmodel2.UpdateData;
import ss.framework.domainmodel2.network.SelectCommand;
import ss.framework.domainmodel2.network.SelectResult;
import ss.framework.domainmodel2.network.UpdateCommand;
import ss.framework.domainmodel2.network.UpdatePerformedEvent;
import ss.framework.domainmodel2.network.UpdateResult;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.ProtocolLifetimeAdapter;
import ss.framework.networking2.ProtocolLifetimeEvent;
import ss.framework.networking2.RespondentCommandHandler;

/**
 *
 */
public class NetworkDataProviderHandler {
		
	private final Protocol protocol;
	
	private IDataProvider dbDataProvider;
	
	private final DataProviderListener dataProviderListener = new NetworkDataProviderListener();

	/**
	 * @param protocol
	 */
	public NetworkDataProviderHandler(Protocol protocol, IDataProvider dbDataProvider ) {
		super();
		this.protocol = protocol;
		this.protocol.addProtocolListener( new ProtocolLifetimeAdapter() {

			/* (non-Javadoc)
			 * @see ss.common.networking2.ProtocolLifetimeAdapter#beginClose(ss.common.networking2.ProtocolLifetimeEvent)
			 */
			@Override
			public void beginClose(ProtocolLifetimeEvent e) {
				super.beginClose(e);
				protocolClosing();
			}

			
		});
		if ( dbDataProvider == null ) {
			throw new ArgumentNullPointerException( "dbDataProvider" );
		}
		this.dbDataProvider = dbDataProvider;
		this.dbDataProvider.addDataProviderListener( this.dataProviderListener);
		this.protocol.registerHandler( new SelectCommandHandler() );
		this.protocol.registerHandler( new UpdateCommandHandler() );
	}
	

	/**
	 * @param criteria
	 * @return
	 * @throws DataProviderException
	 */
	private synchronized List<Record> selectItems(Criteria criteria) throws DataProviderException {
		return this.dbDataProvider.selectItems( criteria  );
	}
	
	/**
	 * @param updateData
	 * @throws DataProviderException
	 */
	private synchronized UpdateResult update(UpdateData updateData) throws DataProviderException {
		return this.dbDataProvider.update( updateData );		
	}
	
	private void notifyDataChanged(DataChangedEvent e) {
		UpdatePerformedEvent dataChangeEvent = new UpdatePerformedEvent( e.getChangedData() );
		dataChangeEvent.fireAndForget( this.protocol );
	}				
	
	public void dispose() {
		this.protocol.beginClose();
	}
	
	private synchronized void protocolClosing() {
		this.dbDataProvider.removeDataProviderListener( this.dataProviderListener );
		this.dbDataProvider = NullDataProvider.INSTANCE;		
	}
	
	private class NetworkDataProviderListener implements DataProviderListener {
		public void dataChanged(DataChangedEvent e) {
			notifyDataChanged(e);				
		}			
	};
	
	private class SelectCommandHandler extends RespondentCommandHandler<SelectCommand,SelectResult > {
		/**
		 * @param acceptableCommandClass
		 */
		public SelectCommandHandler() {
			super(SelectCommand.class);
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
		 */
		@Override
		protected SelectResult evaluate(SelectCommand command) throws CommandHandleException {
			try {
				Criteria criteria = command.getCriteria();
				return new SelectResult( selectItems(criteria) );
			} catch (DataProviderException ex) {
				throw new CommandHandleException( ex );
			}
		}
	}
	
	private class UpdateCommandHandler extends RespondentCommandHandler<UpdateCommand,UpdateResult> {

		/**
		 * @param messageClass
		 */
		public UpdateCommandHandler() {
			super(UpdateCommand.class);
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
		 */
		@Override
		protected UpdateResult evaluate(UpdateCommand command) throws CommandHandleException {
			try {
				UpdateData updateData = command.getData();
				return update(updateData);
			} catch (DataProviderException ex) {
				throw new CommandHandleException( ex );
			}			
		}			
	}

}
