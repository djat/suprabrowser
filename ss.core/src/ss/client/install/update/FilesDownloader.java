/**
 * 
 */
package ss.client.install.update;

import java.io.IOException;
import java.util.Hashtable;

import ss.client.networking.NetworkConnection;
import ss.client.networking.NetworkConnectionFactory;
import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking.binary.BlobLoaderObserver;
import ss.common.ArgumentNullPointerException;
import ss.framework.install.update.CantUpdateApplicationException;
import ss.framework.install.update.loader.AbstractFilesDownloader;
import ss.framework.networking2.blob.BlobLoaderListener;

/**
 *
 */
public class FilesDownloader extends AbstractFilesDownloader {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FilesDownloader.class);
	
	
	private final Hashtable<String,String> sessionForLogin;
	
	/**
	 * @param sessionForLogin
	 */
	public FilesDownloader(final Hashtable<String, String> sessionForLogin) {
		super();
		if (sessionForLogin == null) {
			throw new ArgumentNullPointerException("sessionForLogin");
		}
		this.sessionForLogin = sessionForLogin;
	}

	
	/**
	 * @return
	 * @throws CantUpdateApplicationException 
	 */
	@Override
	protected NetworkConnection openConnection( String procotolName ) throws CantUpdateApplicationException {
		final NetworkConnectionProvider connectionProvider = NetworkConnectionFactory.INSTANCE.createProvider( procotolName, this.sessionForLogin );
		try {
			return connectionProvider.openConnection();
		} catch (IOException ex) {
			connectionProvider.forceClose();
			throw new CantUpdateApplicationException( "Can't establish download protocol",  ex );
		}
	}


	/* (non-Javadoc)
	 * @see ss.framework.install.update.loader.AbstractFilesDownloader#createFilesDownloadListener()
	 */
	@Override
	protected BlobLoaderListener createFilesDownloadListener() {
		return new BlobLoaderObserver();
	}

	
	
	


}
