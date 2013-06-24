/**
 * 
 */
package ss.framework.install.update.arrangement;

import java.io.File;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ss.common.ArgumentNullPointerException;
import ss.common.PathUtils;
import ss.common.XmlDocumentUtils;
import ss.framework.install.update.CantArrangeFileException;

/**
 *
 */
public class InstallerFilesArranger extends AbstractFilesArranger<InstallerFileArrangement>{
	

	/**
	 * 
	 */
	public static final String BATCH_ARRANGEMENT_FILE_NAME = "ss.batch.arrangement.xml";
	
	private static final String ROOT_NAME = "batch-arrangement";

	private static final String VERSION = "1.0";
	
	private static final String UPDATE_LIST_ELEMENT_NAME = "update";
	
	private static final String ENTRY_ELEMENT_NAME = "entry";

	private static final String VERSION_ELEMENT_NAME = "version";	
	
	private final File targetFolder; 
	
	private final File sourceFolder;
	
	private final String version;
	
	/**
	 * @param targetFolder
	 * @param sourceFolder
	 */
	public InstallerFilesArranger(final File targetFolder, final File sourceFolder, String version) {
		super();
		if (version == null) {
			throw new ArgumentNullPointerException("version");
		}
		this.targetFolder = targetFolder;
		this.sourceFolder = sourceFolder;
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.arrangement.AbstractFilesArranger#createFileArrangement(java.io.File, java.io.File)
	 */
	@Override
	protected InstallerFileArrangement createFileArrangement(File to, File from) {
		return new InstallerFileArrangement( to, from );
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.arrangement.AbstractFilesArranger#arrangeAll()
	 */
	@Override
	public void arrangeAll() throws CantArrangeFileException {
		final Element root = DocumentHelper.createElement( ROOT_NAME );
		root.addAttribute( "version", VERSION );
		final Element description = DocumentHelper.createElement( VERSION_ELEMENT_NAME );
		description.setText( this.version );
		root.add( description );
		final Element updateList = DocumentHelper.createElement( UPDATE_LIST_ELEMENT_NAME );
		updateList.addAttribute( "to",  this.targetFolder.getAbsolutePath() );
		updateList.addAttribute( "from", this.sourceFolder.getAbsolutePath() );
		for( InstallerFileArrangement arrangement : getArrangements() ) {
			Element entry = DocumentHelper.createElement( ENTRY_ELEMENT_NAME );;
			entry.addAttribute( "to", PathUtils.getRelativePath( arrangement.getTo(), this.targetFolder ) );
			entry.addAttribute( "from", PathUtils.getRelativePath( arrangement.getFrom(), this.sourceFolder  ) );
			updateList.add(entry);
		}
		root.add( updateList );
		Document arrangementDocument = (Document) DocumentHelper.createDocument( root);
		this.sourceFolder.mkdirs();
		File installerArrangementFile = new File( this.sourceFolder, BATCH_ARRANGEMENT_FILE_NAME );
		try {
			XmlDocumentUtils.save(installerArrangementFile, arrangementDocument);
		} catch (DocumentException ex) {
			throw new CantArrangeFileException( "Can't create installer arrangement file", ex );
		}
	}
	
}
