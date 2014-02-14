/**
 * 
 */
package ss.common.file;

import java.util.List;

/**
 * @author zobo
 *
 */
public interface ISpecificFileProcessing {
	
	public ReturnData process( IDataForSpecificFileProcessingProvider provider )  throws ClassNotFoundException;
	
	public String getExtention();
	
	public String getFileTypeDescription();
}
