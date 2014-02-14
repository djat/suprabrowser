/**
 * 
 */
package ss.framework.install.update;

import java.io.File;

/**
 *
 */
public interface IFilesArranger {

	/**
	 * @param to
	 * @param from
	 */
	void addArrangement(File to, File from);

	/**
	 * 
	 */
	void arrangeAll() throws CantArrangeFileException;

}
