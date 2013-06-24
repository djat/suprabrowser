/**
 * 
 */
package ss.framework.networking2.blob;

/**
 *
 */
public interface BlobLoaderListener {
	
	void bytesLoaded(int transferredBytesCount);
	
	void started( String message, int transferLength);
	
	void finished();
}
