/**
 * 
 */
package ss.common.file;

import ss.common.ReflectionUtils;

/**
 * @author zobo
 *
 */
public abstract class AbstractSpecificFileProcessing implements ISpecificFileProcessing {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractSpecificFileProcessing.class);

	public ReturnData process( final IDataForSpecificFileProcessingProvider provider ) {
		final Class<? extends AbstractSpecificFileData> clazz = getFileDataClass();
		final AbstractSpecificFileData ret = ReflectionUtils.create(clazz, provider);
		if (ret != null) {
			return process(ret);
		} else {
			logger.error("Error creating data class, returning");
			return null;
		}
	}

	protected abstract ReturnData process(ISpecificFileData data);
	
	protected abstract Class<? extends AbstractSpecificFileData> getFileDataClass();
}
