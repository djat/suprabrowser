/**
 * 
 */
package ss.common.file;


import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public class SpecificFileProcessor {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpecificFileProcessor.class);
	
	public final static SpecificFileProcessor INSTANCE = new SpecificFileProcessor();
	
	private final SpecificFileProcessorsTable table = SpecificFileProcessorsTable.INSTANCE;
	
	private SpecificFileProcessor(){

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/**
	 * @param provider
	 * @return List of sphereIds where specific action occured ( depends on file processor )
	 * @throws ClassNotFoundException
	 */
	public ReturnData process( final IDataForSpecificFileProcessingProvider provider ) throws ClassNotFoundException {
		if (StringUtils.isBlank( provider.getSystemFullPath() )) {
			logger.error("systemFileName is blank");
			return null;
		}
		if ((provider.getSphereIds() == null) || (provider.getSphereIds().isEmpty())){
			logger.error("sphereIds is blank");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Processing systemFileName: " + provider.getSystemFullPath() + " ; fileName: " + provider.getFileName());
		}
		final String extention =  getExtention( StringUtils.isBlank(provider.getFileName()) ? provider.getSystemFullPath() : provider.getFileName() );
		if (logger.isDebugEnabled()) {
			logger.debug("File extention is " + (StringUtils.isBlank(extention) ? "blank" : extention));
		}
		return performProcess( provider , (extention == null ? null : extention.toLowerCase()) );
	}
	
	private ReturnData performProcess( final IDataForSpecificFileProcessingProvider provider, final String extention ) throws ClassNotFoundException{
		final ISpecificFileProcessing processor = this.table.getProcessor( extention );
		if ( processor == null ) {
			logger.error("Processor is null");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Processor called: " + processor.getFileTypeDescription());
		}
		return processor.process( provider );
	}
	
	private static String getExtention( final String fileName ){
		if (fileName == null) {
			logger.error("File name is null");
			return null;
		}
		final int index = fileName.lastIndexOf('.');
		if (index != -1){
			return fileName.substring(index + 1);
		}
		return null;
	}
	
	public static boolean isNotHidenFile( final String fileName ){
		final String ext = getExtention(fileName);
		if (StringUtils.isBlank(ext)) {
			return true;
		}
		if (ext.equalsIgnoreCase("vcf")){
			return false;
		}
		return true;
	}
}
