/**
 * 
 */
package ss.common.path.swt;

import java.util.List;

import ss.common.path.AbstractFolderCondition;

/**
 *
 */
public class SwtLibraryFolderCondition extends AbstractFolderCondition {

	/* (non-Javadoc)
	 * @see ss.common.path.AbstractFolderCondition#containsMarkers(java.util.List)
	 */
	@Override
	protected boolean containsMarkers(List<String> fileNames) {
		boolean hasSwtJar = false;
		boolean hasSwtLibs = false;
		for( String fileName : fileNames ) {
			fileName = fileName.toLowerCase();
			if ( fileName.equals( "swt.jar" ) ) {
				hasSwtJar = true;
			}
			else if ( fileName.contains( "swt" ) &&
				 fileName.endsWith( ".dll") ) {
				hasSwtLibs = true;
			}
			else if ( fileName.contains( "swt") &&
				fileName.endsWith( ".so" ) ) {
				hasSwtLibs = true;
			}
		}
		return hasSwtJar && hasSwtLibs;
	}

}
