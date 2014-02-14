/**
 * 
 */
package ss.client.networking.binary;

import ss.client.ui.progressbar.DownloadProgressBar;
import ss.framework.networking2.blob.BlobLoaderListener;

/**
 *
 */
public class BlobLoaderObserver implements BlobLoaderListener {

	private DownloadProgressBar progressBar = null;
	
	private int updateProgressThreshold = 0;
	
	private int collectedProgressUpdate = 0;
	
	public synchronized void bytesLoaded(int bytestransmitted) {
		this.collectedProgressUpdate += bytestransmitted;
		if ( this.collectedProgressUpdate > this.updateProgressThreshold ) {
			this.collectedProgressUpdate = 0;
			final DownloadProgressBar dpb = this.progressBar;
			if (dpb != null) {
				dpb.updateDownloadBar(bytestransmitted);
			}
		}
	}

	public synchronized void started(String message, int bytesToTransmit) {
		finished();
		this.updateProgressThreshold = bytesToTransmit / 50;
		this.progressBar = new DownloadProgressBar(bytesToTransmit,	null);
	}

	public synchronized void finished() {
		final DownloadProgressBar dpb = this.progressBar;
		if (dpb != null) {
			dpb.destroyDownloadBar();
		}
		this.updateProgressThreshold = 0;
		this.collectedProgressUpdate = 0;
	}

}
