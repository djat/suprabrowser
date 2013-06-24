/**
 * 
 */
package ss.framework.install.update.loader;

/**
 *
 */
public interface IFilePathResolver {

	/**
	 * @param fileHeader
	 */
	String resolve(DownloadFileHeader fileHeader);

}
