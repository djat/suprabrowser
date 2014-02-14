/**
 * 
 */
package ss.framework.install.update.arrangement;

import java.io.File;
import java.io.IOException;

import ss.common.FileUtils;
import ss.framework.install.update.CantArrangeFileException;

/**
 *
 */
class DirectFileArrangement extends AbstractFileArrangement {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DirectFileArrangement.class);
	
	/**
	 * @param to
	 * @param from
	 */
	protected DirectFileArrangement(File to, File from) {
		super(to, from);
	}

	public void arrange() throws CantArrangeFileException {
		FileUtils.ensureParentFolderExists(this.to);
		if (!this.to.exists()) {
			AbstractFilesArranger.logger.error("Can't find source file " + this.from );
		}
		if (AbstractFilesArranger.logger.isDebugEnabled()) {
			AbstractFilesArranger.logger.debug("Move to " + this.to + " from " + this.from );
		}
		try {
			FileUtils.replace(this.to, this.from);
		} catch (IOException ex) {
			throw new CantArrangeFileException( "Can't update file " + this.to + ", by " + this.from, ex );
		}		
	}

	public void cleanUp() {
		if ( !this.from.delete() ) {
			logger.warn( "Can't delete from " + this.from );
		}
	}

	public void rollback() {
		// TODO Auto-generated method stub		
	}

}
