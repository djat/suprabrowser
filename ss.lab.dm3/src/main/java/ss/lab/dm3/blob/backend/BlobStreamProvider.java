package ss.lab.dm3.blob.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class BlobStreamProvider implements IBlobStreamProvider {

	private final File baseDir;
	
	/**
	 * @param configuration
	 */
	public BlobStreamProvider(BlobConfiguration configuration) {
		super();
		this.baseDir = configuration.getBaseDir().getAbsoluteFile();
		if ( !this.baseDir.isDirectory() ) {
			this.baseDir.mkdirs();
		}
		if ( !this.baseDir.isDirectory() ) {
			throw new IllegalArgumentException( "Illegal base directory " + this.baseDir  + " by " + configuration );
		}
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.backend.IBlobStreamProvider#delete(ss.lab.dm3.orm.QualifiedObjectId)
	 */
	public void delete(QualifiedObjectId<?> resourceId) {
		File file = getFile( resourceId );
		file.delete(); 
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.backend.IBlobStreamProvider#openRead(ss.lab.dm3.orm.QualifiedObjectId)
	 */
	public InputStream openRead(QualifiedObjectId<?> resourceId) {
		File file = getFile(resourceId);
		try {
			return new FileInputStream( file );
		}
		catch (FileNotFoundException ex) {
			throw new BlobException( "Can't open file " + file + " for " + resourceId, ex );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.backend.IBlobStreamProvider#openWrite(ss.lab.dm3.orm.QualifiedObjectId)
	 */
	public OutputStream openWrite(QualifiedObjectId<?> resourceId) {
		File file = getFile(resourceId);
		if ( !file.exists() ) {
			try {
				file.createNewFile();
			}
			catch (IOException ex) {
				throw new BlobException( "Can't create file " + file + " for " + resourceId, ex );
			}
		}
		try {
			return new FileOutputStream( file );
		}
		catch (FileNotFoundException ex) {
			throw new BlobException( "Can't open file " + file + " for " + resourceId, ex );
		}
	}


	/**
	 * @param resourceId
	 * @return
	 */
	private File getFile(QualifiedObjectId<?> resourceId) {
		final String relativeName = String.valueOf( resourceId.getId() );
		return new File( this.baseDir, relativeName );
	}
}
