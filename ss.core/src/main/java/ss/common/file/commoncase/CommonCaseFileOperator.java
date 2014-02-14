/**
 * 
 */
package ss.common.file.commoncase;

import java.util.List;

import ss.common.file.AbstractSpecificFileData;
import ss.common.file.AbstractSpecificFileProcessing;
import ss.common.file.ISpecificFileData;
import ss.common.file.ReturnData;

/**
 * @author zobo
 *
 */
public class CommonCaseFileOperator extends AbstractSpecificFileProcessing {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CommonCaseFileOperator.class);

	public static final CommonCaseFileOperator INSTANCE = new CommonCaseFileOperator();
	
	private CommonCaseFileOperator() {
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/* (non-Javadoc)
	 * @see ss.common.file.AbstractSpecificFileProcessing#getFileDataClass()
	 */
	@Override
	protected Class<? extends AbstractSpecificFileData> getFileDataClass() {
		return CommonCaseData.class;
	}

	/* (non-Javadoc)
	 * @see ss.common.file.AbstractSpecificFileProcessing#process(ss.common.file.ISpecificFileData)
	 */
	@Override
	protected ReturnData process( final ISpecificFileData externalData ) {
		final CommonCaseData data = CommonCaseData.class.cast( externalData );
		return null;
		// TODO Implement
	}

	/* (non-Javadoc)
	 * @see ss.common.file.ISpecificFileProcessing#getExtention()
	 */
	public String getExtention() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.common.file.ISpecificFileProcessing#getFileTypeDescription()
	 */
	public String getFileTypeDescription() {
		return "Other files";
	}

}
