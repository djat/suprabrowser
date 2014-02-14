/**
 * 
 */
package ss.framework.install.update.arrangement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ss.framework.install.update.CantArrangeFileException;
import ss.framework.install.update.IFilesArranger;


/**
 *
 */
public abstract class AbstractFilesArranger<T extends AbstractFileArrangement> implements IFilesArranger {

	@SuppressWarnings("unused")
	static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractFilesArranger.class);

	private final List<T> arrangements = new ArrayList<T>();
	
	/* (non-Javadoc)
	 * @see ss.framework.install.update.IDownloadArranger#move(java.io.File, java.io.File)
	 */
	public void addArrangement(File to, File from) {
		this.arrangements.add( createFileArrangement( to, from ) );
	}

	/**
	 * @param to
	 * @param from
	 * @return
	 */
	protected abstract T createFileArrangement(File to, File from);

	
	public abstract void arrangeAll() throws CantArrangeFileException;

	/**
	 * @return the arrangements
	 */
	protected List<T> getArrangements() {
		return this.arrangements;
	}

	
}
