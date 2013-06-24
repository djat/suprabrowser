/**
 * 
 */
package ss.client.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogAdapter;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.common.UiUtils;
import ss.framework.networking2.blob.FileDownloader;
import ss.util.SSFileFilter;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class SaveBinaryClientTransmitter extends
		AbstractBinaryClientTransmitter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveBinaryClientTransmitter.class);

	private final Shell shellToDispose;

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param update
	 * @param session
	 */
	public SaveBinaryClientTransmitter(DataOutputStream cdataout,
			DataInputStream cdatain, Hashtable update, Hashtable session,
			Shell shellToDispose) {
		super(cdataout, cdatain, update, session);
		this.shellToDispose = shellToDispose;
	}

	@Override
	protected void performTransmit() {
		final Hashtable update = getUpdate();
		final Hashtable session = getSession();
		final DataOutputStream cdataout = getCdataout();
		final DataInputStream cdatain = getCdatain();
		String filename = "";
		try {
			final byte[] objectBytes = objectToBytes(update);

			cdataout.writeInt(objectBytes.length);

			cdataout.write(objectBytes, 0, objectBytes.length);

			final Hashtable saveInfo = (Hashtable) session.get("saveInfo");

			session.remove("saveInfo");

			final String pdf = (String) saveInfo.get("as_pdf");

			filename = (String) saveInfo.get("data_filename");

			if (pdf != null) {
				if (pdf.equals("true")) {
					filename = filename + ".pdf";
				}
			}
			cdataout.writeUTF(filename);
		} catch (IOException ex) {
			logger.error(
					"IO exception occurs during save file via transmitter", ex);
		}

		final String finalFilename = filename;
		logger.warn("final file name:"+finalFilename);
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				boolean isFileChousen = false;
				final String fileExt = SSFileFilter.getExtension(finalFilename);
				FileDialog fileDialog = new FileDialog(
						SupraSphereFrame.INSTANCE.getShell(), SWT.SAVE);
				fileDialog.setFilterExtensions(new String[] { fileExt });

				final String normalFname = VariousUtils
						.getFnameFromDataFname(finalFilename);
				
				File fileOut = null;

				while (true) {
					fileDialog.setFileName(normalFname);
					String fullName = fileDialog.open();
					if (fullName == null) {
						break;
					}
					fileOut = new File(fullName);
					final String selectedExt = SSFileFilter
							.getExtension(fileOut);

					if (((selectedExt == null) && (fileExt != null))
							|| ((selectedExt != null) && (!selectedExt
									.equals(fileExt)))) {
						UserMessageDialogCreator.warning("You cannot change the file type");
						break;
					} else {
						isFileChousen = true;
						break;
					}
				}

				if (isFileChousen) {
					if (SaveBinaryClientTransmitter.this.shellToDispose != null) {
						SaveBinaryClientTransmitter.this.shellToDispose
						.dispose();
					}
					try {
						final FileDownloader transmitter = new FileDownloader(
								cdatain);
						transmitter.addListener(new BlobLoaderObserver());
						transmitter.download(fileOut);
					} catch (Throwable ex) {
						logger.error("Error during Save Binary", ex);
					}
				}
			}
		});

	}
}
