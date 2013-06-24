/**
 * 
 */
package ss.common.path;

import java.io.File;
import java.util.List;

import ss.common.ListUtils;

/**
 *
 */
public abstract class AbstractFolderCondition {
	
	public final boolean match( File folder ) {
		return folder != null ? containsMarkers( ListUtils.toList( folder.list() ) ) : false;
	}

	/**
	 * @param strings
	 * @return
	 */
	protected abstract boolean containsMarkers(List<String> fileNames );
}
