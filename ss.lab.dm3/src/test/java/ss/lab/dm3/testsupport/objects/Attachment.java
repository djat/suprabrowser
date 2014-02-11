package ss.lab.dm3.testsupport.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.lucene.document.Document;

import ss.lab.dm3.annotation.SearchableField;
import ss.lab.dm3.blob.BlobManager;
import ss.lab.dm3.blob.IBlobObject;
import ss.lab.dm3.blob.IProgressListener;
import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.backend.search.SearchHelper;
import ss.lab.dm3.persist.backend.search.SecureLockCollector;
import ss.lab.dm3.persist.search.ISearchableSecure;

@Entity
public class Attachment extends DomainObject implements IBlobObject, ISearchableSecure {

	/**
	 * @param path
	 */
	@SearchableField
	private String name;
	
	private BlobState blobState;
	
	private long size;
		
	/**
	 * @param progressAdapter
	 */
	public void setData(InputStream in, IProgressListener progressListener) {
		BlobManager manager = getBlobManager();
		manager.bind( getQualifiedId(), in, progressListener );
		setBlobState( BlobState.CREATED_OVERWRITED );
		setSize( UNKNOWN_BLOB_SIZE );
	}

	/**
	 * @param string
	 * @param progressAdapter 
	 * @throws FileNotFoundException 
	 */
	public void setData(String filePath, IProgressListener progressListener) throws FileNotFoundException {
		final File file = new File( filePath );
		setName( file.getName() );
		final FileInputStream fileIn = new FileInputStream( file );
		setData( fileIn, progressListener);
		setSize( file.length() ); 
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void beginDownloadTo(OutputStream out, IProgressListener progressListener) {
		BlobManager manager = getBlobManager();
		manager.beginDownload( getQualifiedId(), out, progressListener );
	}
	
	@Transient
	protected BlobManager getBlobManager() {
		return getDomain().getBlobManager();
	}

	@Enumerated(EnumType.STRING)
	public BlobState getBlobState() {
		return this.blobState;
	}

	public void setBlobState(BlobState blobState) {
		this.blobState = blobState;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void collectFields(Document collector) {
		SearchHelper.collectByDefault(this, collector);
	}

	public void collectSecureLocks(SecureLockCollector collector) {
	}

	@Transient
	public boolean isPublicForSearch() {
		return true;
	}	
	
}
