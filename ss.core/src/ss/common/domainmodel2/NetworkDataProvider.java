/**
 * 
 */
package ss.common.domainmodel2;

import java.util.List;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.AbstractDataProvider;
import ss.framework.domainmodel2.ChangedData;
import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.DataChangedEvent;
import ss.framework.domainmodel2.DataProviderException;
import ss.framework.domainmodel2.Record;
import ss.framework.domainmodel2.UpdateData;
import ss.framework.domainmodel2.network.SelectCommand;
import ss.framework.domainmodel2.network.SelectResult;
import ss.framework.domainmodel2.network.UpdateCommand;
import ss.framework.domainmodel2.network.UpdatePerformedEvent;
import ss.framework.domainmodel2.network.UpdateResult;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.EventHandler;
import ss.framework.networking2.EventHandlingContext;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.executors.SingleThreadHandlerExecutor;

/**
 * 
 */
public final class NetworkDataProvider extends AbstractDataProvider {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NetworkDataProvider.class);

	private final Protocol protocol;

	private final String updateOwnerId;
	
	private final SingleThreadHandlerExecutor updateExecutor = new SingleThreadHandlerExecutor( "-ExternalUpdate" );
	
	/**
	 * @param protocol
	 */
	public NetworkDataProvider(final Protocol protocol, String updateOwnerId ) {
		super();
		if ( updateOwnerId == null ) {
			throw new ArgumentNullPointerException( "updateOwnerId" );
		}
		this.protocol = protocol;
		this.updateOwnerId = updateOwnerId;
		this.protocol.registerHandler(new DataChangeEventHandler(), this.updateExecutor);
		this.updateExecutor.start( protocol.getDisplayName() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.domainmodel2.IDataProvider#selectItems(ss.framework.domainmodel2.Criteria)
	 */
	public synchronized List<Record> selectItems(Criteria criteria)
			throws DataProviderException {
		checkDisposed();
		SelectCommand selectCommand = new SelectCommand(criteria);
		SelectResult result;
		try {
			result = selectCommand.execute(this.protocol, SelectResult.class);
		} catch (CommandExecuteException ex) {
			throw new DataProviderException(ex);
		}
		if (result == null) {
			throw new DataProviderException("Result is null for select "
					+ criteria);
		}
		return result.getRecords();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.domainmodel2.IDataProvider#sendUpdate(ss.framework.domainmodel2.UpdateData)
	 */
	public synchronized UpdateResult update(UpdateData data)
			throws DataProviderException {
		checkDisposed();
		UpdateCommand updateCommand = new UpdateCommand(data);
		try {
			UpdateResult result = updateCommand.execute(this.protocol,
					UpdateResult.class);
			if ( result == null ) {
				throw new DataProviderException( "Null result for " + data );
			}
			return result;
		} catch (CommandExecuteException ex) {
			throw new DataProviderException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.domainmodel2.AbstractDataProvider#disposing()
	 */
	@Override
	protected void disposing() {
		super.disposing();
		this.updateExecutor.shootdown();
		this.protocol.beginClose();
	}


	/**
	 * @param changedData
	 * @return
	 */
	private boolean isExternalUpdate(final ChangedData changedData) {
		return !changedData.getOwnerId().equals( this.updateOwnerId );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#isAlive()
	 */
	public boolean isAlive() {
		return this.protocol.isValid();
	}

	private class DataChangeEventHandler extends
			EventHandler<UpdatePerformedEvent> {
		/**
		 * @param notificationClass
		 */
		public DataChangeEventHandler() {
			super(UpdatePerformedEvent.class);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.common.networking2.EventHandler#handleEvent(ss.common.networking2.EventHandlingContext)
		 */
		@Override
		protected void handleEvent(
				EventHandlingContext<UpdatePerformedEvent> context) {
			context.cancelBuble();
			final UpdatePerformedEvent event = context.getMessage();
			final ChangedData changedData = event.getChangedData();
			if (isExternalUpdate(changedData)) {
				if (logger.isDebugEnabled()) {
					logger.debug("notify external update");
				}
				notifyDataChanged(new DataChangedEvent(changedData,
						isExternalUpdate(changedData)));
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Skip self update " + changedData);
				}
			}
		}


}
}
