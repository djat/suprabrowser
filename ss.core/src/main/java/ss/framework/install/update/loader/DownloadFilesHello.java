/**
 * 
 */
package ss.framework.install.update.loader;

import java.io.Serializable;

import ss.framework.install.QualifiedVersion;

/**
 *
 */
public final class DownloadFilesHello implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6207502114738168690L;
	
	private final String targetApplicationVersion;

	/**
	 * @param applicationName
	 * @param targetApplicationVersion
	 */
	public DownloadFilesHello(final QualifiedVersion targetApplicationVersion) {
		super();
		this.targetApplicationVersion = targetApplicationVersion.toString();
	}

	/**
	 * @return the target application version 
	 */
	public QualifiedVersion getTargetApplicationVersion() {
		return QualifiedVersion.safeParse( this.targetApplicationVersion );
	}
	
	
	
}
