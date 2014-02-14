/**
 * 
 */
package ss.framework.domainmodel2;

import javax.swing.event.EventListenerList;

import ss.common.ArgumentNullPointerException;


/**
 *
 */
public abstract class AbstractDataProvider implements IDataProvider {

	private boolean disposed = false;
	
	private final EventListenerList listeners = new EventListenerList();
	
	private final Object listenersMutex = new Object();
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#dispose()
	 */
	public final void dispose() {
		if ( this.disposed ) {
			return;
		}
		this.disposed = true;
		disposing();
	}

	/**
	 * 
	 */
	protected void disposing() {
		synchronized(this.listenersMutex) {
			for( DataProviderListener listener : this.listeners.getListeners(DataProviderListener.class) ){
				removeDataProviderListener(listener);
			}
		}
	}
	
	protected final void checkDisposed() throws DataProviderException {
		if ( this.disposed ) {
			throw new DataProviderException( "Data provider disposed" );
		}
	}
	
	/**
	 * @param changedData
	 */
	protected final void notifyDataChanged(DataChangedEvent e) {
		synchronized(this.listenersMutex) {
			if ( e == null ) {
				throw new ArgumentNullPointerException( "e" );
			}
			// Guaranteed to return a non-null array
			Object[] listeners = this.listeners.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == DataProviderListener.class) {
					((DataProviderListener) listeners[i + 1]).dataChanged(e);
				}
			}		
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#removeDataProviderListener(ss.framework.domainmodel2.DataProviderListener)
	 */
	public final void removeDataProviderListener(DataProviderListener listener) {
		synchronized(this.listenersMutex) {
			this.listeners.remove( DataProviderListener.class, listener );
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#setDataProviderListener(ss.framework.domainmodel2.DataProviderListener)
	 */
	public final void addDataProviderListener(
			DataProviderListener listener) {
		synchronized(this.listenersMutex) {
			this.listeners.add( DataProviderListener.class, listener);
		}
	}

		
}
