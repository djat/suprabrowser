/**
 * 
 */
package ss.framework.domainmodel2;

/**
 * 
 */
public class RunntimeDomainObject extends DomainObject {

	private IntField xmldbPoolFreeSize = super.createField(IntField.class,
			"xmldbPoolFreeSize");

	private IntField xmldbPoolInUseSize = super.createField(IntField.class,
			"xmldbPoolInUseSize");

	private IntField dm2PoolFreeSize = super.createField(IntField.class,
			"dm2PoolFreeSize");

	private IntField dm2PoolTotalSize = super.createField(IntField.class,
			"dm2PoolTotalSize");
	
	private IntField dm2PoolConnectionCounter = super.createField(IntField.class,
			"dm2PoolConnectionCounter");
	
	private StringField dbUsageUptime = super.createField(StringField.class,
			"dbUsageUptime");



	/**
	 * @param space
	 */
	public RunntimeDomainObject(AbstractDomainSpace space) {
		super(space);
	}

	/**
	 * @return the dm2PoolFreeSize
	 */
	public int getDm2PoolFreeSize() {
		return this.dm2PoolFreeSize.get();
	}

	/**
	 * @param dm2PoolFreeSize
	 *            the dm2PoolFreeSize to set
	 */
	public void setDm2PoolFreeSize(int dm2PoolFreeSize) {
		this.dm2PoolFreeSize.setSilently(dm2PoolFreeSize);
	}

	/**
	 * @return the dm2PoolTotalSize
	 */
	public int getDm2PoolTotalSize() {
		return this.dm2PoolTotalSize.get();
	}

	/**
	 * @param dm2PoolTotalSize
	 *            the dm2PoolTotalSize to set
	 */
	public void setDm2PoolTotalSize(int dm2PoolTotalSize) {
		this.dm2PoolTotalSize.setSilently(dm2PoolTotalSize);
	}

	/**
	 * @return the xmldbPoolFreeSize
	 */
	public int getXmldbPoolFreeSize() {
		return this.xmldbPoolFreeSize.get();
	}

	/**
	 * @param xmldbPoolFreeSize
	 *            the xmldbPoolFreeSize to set
	 */
	public void setXmldbPoolFreeSize(int xmldbPoolFreeSize) {
		this.xmldbPoolFreeSize.setSilently(xmldbPoolFreeSize);
	}

	/**
	 * @return the xmldbPoolInUseSize
	 */
	public int getXmldbPoolInUseSize() {
		return this.xmldbPoolInUseSize.get();
	}

	/**
	 * @param xmldbPoolInUseSize
	 *            the xmldbPoolInUseSize to set
	 */
	public void setXmldbPoolInUseSize(int xmldbPoolInUseSize) {
		this.xmldbPoolInUseSize.setSilently(xmldbPoolInUseSize);
	}

	/**
	 * @return the dm2PoolConnectionCounter
	 */
	public int getDm2PoolConnectionCounter() {
		return this.dm2PoolConnectionCounter.get();
	}

	/**
	 * @param dm2PoolConnectionCounter the dm2PoolConnectionCounter to set
	 */
	public void setDm2PoolConnectionCounter(int dm2PoolConnectionCounter) {
		this.dm2PoolConnectionCounter.setSilently( dm2PoolConnectionCounter );
	}

	public String getDbUsageUptime() {
		return this.dbUsageUptime.get();
	}

	public void setDbUsageUptime(String dbUsageUptime) {
		this.dbUsageUptime.setSilently( dbUsageUptime );
	}
	
//	public Record toRecord() {
//		Record record = new Record(getClass());
//		save(record);
//		return record;
//	}
}
