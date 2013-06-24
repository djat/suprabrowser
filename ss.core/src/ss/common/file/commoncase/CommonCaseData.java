/**
 * 
 */
package ss.common.file.commoncase;

import ss.common.file.AbstractSpecificFileData;
import ss.common.file.IDataForSpecificFileProcessingProvider;

/**
 * @author zobo
 *
 */
public class CommonCaseData extends AbstractSpecificFileData {

	private String systemFileName;
	
	/**
	 * @param provider
	 */
	public CommonCaseData( final IDataForSpecificFileProcessingProvider provider ) {
		super(provider);
	}

	@Override
	protected void fillUpData( final IDataForSpecificFileProcessingProvider provider ) {
		this.systemFileName = provider.getFileName();
	}

	public String getSystemFileName() {
		return this.systemFileName;
	}
}
