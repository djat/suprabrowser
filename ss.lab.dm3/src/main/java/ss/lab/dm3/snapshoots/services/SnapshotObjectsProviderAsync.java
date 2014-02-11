package ss.lab.dm3.snapshoots.services;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.snapshoots.SnapshotObject;

/**
 * @author Dmitry Goncharov
 *
 */
public interface SnapshotObjectsProviderAsync extends ServiceAsync {

	void get( Class<? extends SnapshotObject> transientObjectClass, ICallbackHandler handler );
}
