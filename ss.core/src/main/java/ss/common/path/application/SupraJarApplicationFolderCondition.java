/**
 * 
 */
package ss.common.path.application;

import java.util.List;

import ss.common.path.AbstractFolderCondition;

/**
 *
 */
public class SupraJarApplicationFolderCondition extends AbstractFolderCondition {

	/* (non-Javadoc)
	 * @see ss.framework.install.AbstractApplicationFolderCondition#containsMarkers(java.util.List)
	 */
	@Override
	protected boolean containsMarkers(List<String> fileNames) {
		boolean hasSupraJar = false;
		boolean hasLibs = false;
		for( String fileName : fileNames ) {
			fileName = fileName.toLowerCase();
			if ( fileName.contains( "supra" ) &&
				 fileName.endsWith( ".jar" ) ) {
				hasSupraJar = true;
			}
			if ( fileName.equals( "libs" ) ) {
				hasLibs = true;
			}
		}
		return hasSupraJar && hasLibs;
	}

	

}
