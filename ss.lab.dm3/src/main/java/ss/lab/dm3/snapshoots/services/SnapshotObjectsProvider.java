package ss.lab.dm3.snapshoots.services;

import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.Service;
import ss.lab.dm3.snapshoots.SnapshotObject;

/**
 * @author Dmitry Goncharov
 *
 */
public interface SnapshotObjectsProvider extends Service {

	SnapshotObject get( Class<? extends SnapshotObject> transientObjectClass ) throws ServiceException;
	
}
