/**
 * 
 */
package ss.common.file;

import java.util.Hashtable;

import ss.common.file.commoncase.CommonCaseFileOperator;
import ss.common.file.vcf.VCardOperator;

/**
 * @author zobo
 *
 */
public class SpecificFileProcessorsTable {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpecificFileProcessorsTable.class);
	
	private final Hashtable<String, ISpecificFileProcessing> processors;
	
	private final ISpecificFileProcessing defaultProcessor = CommonCaseFileOperator.INSTANCE;
	
	public static final SpecificFileProcessorsTable INSTANCE = new SpecificFileProcessorsTable();
	
	private SpecificFileProcessorsTable(){
		this.processors = new Hashtable<String, ISpecificFileProcessing>();
		register(VCardOperator.INSTANCE);
	}
		
	private void register(final ISpecificFileProcessing processor){
		final String extention = processor.getExtention().toLowerCase();
		if (this.processors.containsKey(extention)) {
			logger.error("Processor for Extention: " + extention + " already registred, description: " + 
					this.processors.get(extention).getFileTypeDescription());
			logger.error("Processor with description: " + processor.getFileTypeDescription() + " can not be registered");
			return;
		}
		this.processors.put(extention, processor);
	}
	
	/**
	 * @param extention of file
	 * @return if no such processor for extention (or extention is null) returning null
	 */
	public ISpecificFileProcessing getProcessor( final String extention ){
		if (extention == null) {
			logger.warn("extention is null, invoking default processor");
			return this.defaultProcessor;
		}
		final ISpecificFileProcessing processor = this.processors.get( extention );
		return ((processor == null) ? this.defaultProcessor : processor);
	}
}
