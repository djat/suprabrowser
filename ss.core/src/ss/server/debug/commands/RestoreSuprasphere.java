/**
 * 
 */
package ss.server.debug.commands;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.FolderUtils;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.SupraSphereStatement;
import ss.server.debug.RemoteCommandContext;
import ss.server.debug.ssrepair.Context;

/**
 * 
 */
public class RestoreSuprasphere extends AbstractRepairerCommand {

	private SupraSphereStatement suprasphere = null;

	private File file = new File(FolderUtils.getApplicationFolder(),
		"suprasphere_for_restore.xml");
	
	/**
	 * @param repairerClass
	 */
	public RestoreSuprasphere() {
		super( null );
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
		if (!this.file.exists()) {
			repairerContext.addError("Can't restore suprasphere, because backup file is not found "
					+ this.file);
		} else {
			Document supraSphereDoc;
			try {
				supraSphereDoc = XmlDocumentUtils.load(this.file);
			} catch (DocumentException ex) {
				throw new RuntimeException("Can't load " + this.file, ex);
			}
			this.suprasphere = new SupraSphereStatement();
			SupraSphereStatement existed = repairerContext.getSupraSphere();
			this.suprasphere = SupraSphereStatement.wrap(supraSphereDoc);
			final String displayName = this.suprasphere.getDisplayName();
			final String systemName = this.suprasphere.getSystemName();
			if ( !existed.getDisplayName().equals( displayName )  
					|| !existed.getSystemName().equals(systemName) ) {
				repairerContext.addError("Suprasphere has invalid display name or system name. Display name "
						+ displayName + ", system name " + systemName );
				this.suprasphere = null;
			}
		}
		
		if (this.suprasphere != null) {
			repairerContext.changeSupraSphere("Backuping suprasphere from "
					+ this.file, this.suprasphere);
		}
	}

}
