/**
 * 
 */
package ss.common.path.application;

import java.util.List;

import ss.common.path.AbstractFolderCondition;

/**
 *
 */
public class FileApplicationFolderCondition extends AbstractFolderCondition {

	private String targetFileNameInLowwerCase;
	
	/**
	 * @param fileName
	 */
	public FileApplicationFolderCondition(String fileName) {
		super();
		this.targetFileNameInLowwerCase = fileName.toLowerCase();
	}


	/* (non-Javadoc)
	 * @see ss.framework.install.AbstractApplicationFolderCondition#containsMarkers(java.util.List)
	 */
	@Override
	protected boolean containsMarkers(List<String> fileNames) {
		for ( String fileName : fileNames ) {
			fileName = fileName.toLowerCase();
			if ( fileName.equals( this.targetFileNameInLowwerCase ) ) {
				return true;
			}
		}
		return false;
	}

}
