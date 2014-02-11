package ss.lab.dm3.snapshoots;

/**
 * @author Dmitry Goncharov
 *
 */
public interface SnapshotObjectHandler<T extends SnapshotObject> {

	void handle( T object ) throws SnapshotObjectHandlerException;
}
