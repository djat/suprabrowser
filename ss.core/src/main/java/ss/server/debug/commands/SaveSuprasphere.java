/**
 * 
 */
package ss.server.debug.commands;

import java.io.File;

import ss.common.FolderUtils;
import ss.common.XmlDocumentUtils;
import ss.server.debug.RemoteCommandContext;
import ss.server.debug.ssrepair.Context;

/**
 *
 */
public class SaveSuprasphere extends AbstractRepairerCommand {
	
	private static final int MAX_TRY_COUNT = 1000;
	
	/**
	 * @param repairerClass
	 */
	public SaveSuprasphere() {
		super(null);
	}
	
	
			/* (non-Javadoc)
	 * @see ss.server.debug.commands.AbstractRepairerCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	@Override
	public String evaluate(RemoteCommandContext context) throws Exception {
		final Context repairerContext = new Context( getClass().getSimpleName(), "commit".equals( context.getArgs() ) );
		repaire(repairerContext);
		return repairerContext.getReport();
	}

	/* (non-Javadoc)
	 * @see ss.server.debug.commands.AbstractRepairerCommand#repaire(ss.server.debug.ssrepair.Context)
	 */
	@Override
	protected void repaire(Context repairerContext) {
		final String applicationFolder = FolderUtils.getApplicationFolder();
		File file;
		int counter = 0;
		do {
			file = new File( applicationFolder, "suprasphere_backup_" + counter + ".xml" );
			++ counter;
			if ( counter > MAX_TRY_COUNT ) {
				throw new RuntimeException( "Can't find free file name to save suprasphere. Last try is " + file );
			}
		} while( file.exists() );
		try {
			file.createNewFile();
			XmlDocumentUtils.save(file, repairerContext.getSupraSphere().getDocumentCopy() );
			repairerContext.addMessage( "Suprasphere saved to " + file );
		} catch (Throwable ex) {
			throw new RuntimeException( "Can't save suprasphere to file " + file, ex );
		}
	}



}
