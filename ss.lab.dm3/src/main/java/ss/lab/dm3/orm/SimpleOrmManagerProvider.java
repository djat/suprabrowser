package ss.lab.dm3.orm;

/**
 * @author Dmitry Goncharov
 */
public class SimpleOrmManagerProvider implements OrmManagerProvider  {

	private OrmManager ormManager;

	/**
	 * @param ormManager
	 */
	public SimpleOrmManagerProvider(OrmManager ormManager) {
		super();
		this.ormManager = ormManager;
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.IOrmManagerProvider#get()
	 */
	public OrmManager get() {
		return this.ormManager;
	}
}
