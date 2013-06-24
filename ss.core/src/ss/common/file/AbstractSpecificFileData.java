/**
 * 
 */
package ss.common.file;

/**
 * @author zobo
 *
 */
public abstract class AbstractSpecificFileData implements ISpecificFileData {
	
	public AbstractSpecificFileData(final IDataForSpecificFileProcessingProvider provider){
		fillUpData( provider );
	}

	/**
	 * @param provider
	 */
	protected abstract void fillUpData(IDataForSpecificFileProcessingProvider provider);
}
