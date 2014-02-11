package ss.lab.dm3.snapshoots.services;

import java.util.Hashtable;

import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.snapshoots.OnlineUserListProvider;
import ss.lab.dm3.snapshoots.SnapshotObject;
import ss.lab.dm3.snapshoots.SnapshotObjectProvider;


/**
 * @author Dmitry Goncharov
 *
 */
public class SnapshotObjectsBackEnd implements SnapshotObjectsProvider {

	private final Hashtable<Class<?>,SnapshotObjectProvider<?>> providers = new Hashtable<Class<?>,SnapshotObjectProvider<?>>(); 
	
	/**
	 * 
	 */
	public SnapshotObjectsBackEnd() {
		super();
		OnlineUserListProvider provider = new OnlineUserListProvider();
		this.providers.put( provider.getTransientObjectClazz(), provider );
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.transientobjects.objs.TransientObjectsFrontEnd#get(java.lang.Class, java.io.Serializable[])
	 */
	public SnapshotObject get(
			Class<? extends SnapshotObject> transientObjectClass) throws ServiceException {
		SnapshotObjectProvider<?> transientObjectProvider = this.providers.get(transientObjectClass);
		if ( transientObjectProvider == null ) {
			throw new ServiceException( "Can't find transient object provider by " + transientObjectClass );
		}
		return transientObjectProvider.provide();
	}

}
