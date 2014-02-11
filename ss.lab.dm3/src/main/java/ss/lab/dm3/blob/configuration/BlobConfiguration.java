package ss.lab.dm3.blob.configuration;

import java.io.File;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class BlobConfiguration {
	
	public static final String ATTACHMENT_FOLDER = "attachments";
		
	private File baseDir = new File( System.getProperty("user.dir"), ATTACHMENT_FOLDER );
	
	public BlobConfiguration() {
	}

	public File getBaseDir() {
		return this.baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}
	
}
