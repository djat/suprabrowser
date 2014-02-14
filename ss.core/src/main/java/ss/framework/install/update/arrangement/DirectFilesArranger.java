/**
 * 
 */
package ss.framework.install.update.arrangement;

import java.io.File;

import ss.framework.install.update.CantArrangeFileException;

/**
 *
 */
class DirectFilesArranger extends AbstractFilesArranger<DirectFileArrangement> {

	/* (non-Javadoc)
	 * @see ss.framework.install.update.arrangement.AbstractFilesArranger#createFileArrangement(java.io.File, java.io.File)
	 */
	@Override
	protected DirectFileArrangement createFileArrangement(File to, File from) {
		return new DirectFileArrangement( to, from );
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.arrangement.AbstractFilesArranger#arrangeAll()
	 */
	@Override
	public void arrangeAll() throws CantArrangeFileException {
		try {
			for( DirectFileArrangement arrangement : getArrangements() ) {
				arrangement.arrange();
			}
			for( DirectFileArrangement arrangement : getArrangements() ) {
				arrangement.cleanUp();
			}
		}
		finally {
			for( DirectFileArrangement arrangement : getArrangements() ) {
				try {
					arrangement.rollback();
				}
				catch( Throwable ex ) {
					logger.error( "Can't rollback " +arrangement, ex );
				}
			}
		}		
	}

}
