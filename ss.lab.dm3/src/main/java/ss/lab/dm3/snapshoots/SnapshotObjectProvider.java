package ss.lab.dm3.snapshoots;

/**
 * @author Dmitry Goncharov
 *
 */
public abstract class SnapshotObjectProvider<T extends SnapshotObject> {

	private final Class<T> transientObjectClazz;
	
	/**
	 * @param transientObjectClazz
	 */
	public SnapshotObjectProvider(Class<T> transientObjectClazz) {
		super();
		this.transientObjectClazz = transientObjectClazz;
	}
	
	public Class<T> getTransientObjectClazz() {
		return this.transientObjectClazz;
	}

	public abstract T provide();
	
}
