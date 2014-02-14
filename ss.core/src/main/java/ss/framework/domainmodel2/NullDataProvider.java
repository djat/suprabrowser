/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.List;

import ss.framework.domainmodel2.network.UpdateResult;

/**
 *
 */
public final class NullDataProvider extends AbstractDataProvider {
	
	/**
	 * Singleton instance
	 */
	public final static NullDataProvider INSTANCE = new NullDataProvider();

	private NullDataProvider() {
	}	

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#selectItems(ss.framework.domainmodel2.Criteria)
	 */
	public List<Record> selectItems(Criteria criteria) throws DataProviderException {
		throw new NullDataProviderException();
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#sendUpdate(ss.framework.domainmodel2.UpdateData)
	 */
	public UpdateResult update(UpdateData updateData) throws DataProviderException {
		throw new NullDataProviderException();		
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#isAlive()
	 */
	public boolean isAlive() {
		return false;
	}

	/**
	 *
	 */
	public static class NullDataProviderException extends DataProviderException {

		/**
		 * @param string
		 */
		public NullDataProviderException() {
			super("Illegal data provider");
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 4780779035609199660L;
		
	}

}
