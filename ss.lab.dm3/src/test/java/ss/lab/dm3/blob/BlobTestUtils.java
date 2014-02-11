package ss.lab.dm3.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ss.lab.dm3.blob.backend.BlobInformationProvider;
import ss.lab.dm3.blob.backend.BlobManagerBackEnd;
import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.blob.service.BlobTransferService;
import ss.lab.dm3.connection.service.ServiceBackEndFactory;
import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.connection.service.backend.BackEndFeatures;
import ss.lab.dm3.connection.service.proxy.ProxyServiceProvider;
import ss.lab.dm3.testsupport.objects.Attachment;

public class BlobTestUtils {

	public enum TestBytesKind {
		Small(3), Medium(512), Large(1750), XXLarge( 50000 );

		private int size;

		TestBytesKind(int size) {
			this.size = size;
		}

		public int getSize() {
			return this.size;
		}
	};

	static byte[][] testBytes;

	static int kindsCount = TestBytesKind.XXLarge.ordinal() + 1;

	static {
		testBytes = new byte[kindsCount][];
		for (TestBytesKind kind : TestBytesKind.values()) {
			byte[] buff = new byte[kind.getSize()];
			for (int n = 0; n < buff.length; ++n) {
				buff[n] = (byte) ((Math.abs(Math.random()) * 26) + 'A');
			}
			testBytes[kind.ordinal()] = buff;
		}
	}

	public static byte[] getTestBytes(TestBytesKind kind) {
		return testBytes[kind.ordinal()];
	}

	public static byte[] getFileBytes(File file) {
		if (!file.exists()) {
			return null;
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			final byte[] buff = new byte[in.available()];
			int count = in.read(buff);
			if (count != buff.length) {
				throw new IllegalStateException("Can't read full file content " + file
						+ ". Expected " + buff.length + " Reads " + count);
			}
			return buff;
		}
		catch (IOException ex) {
			throw new RuntimeException("Can't read from " + file, ex);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException ex) {
					System.err.println("Can't close " + in);
					ex.printStackTrace();
				}
			}
		}
	}

	public static void setFileBytes(File file, byte[] bytes) {
		file = file.getAbsoluteFile();
		if (!file.exists()) {
			File folder = file.getParentFile();
			if ( folder.isDirectory() ) {
				folder.mkdirs();
			}
			try {
				file.createNewFile();
			}
			catch (IOException ex) {
				throw new RuntimeException("Can't create file", ex);
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
		}
		catch (IOException ex) {
			throw new RuntimeException("Can't write to " + file, ex);
		}
		finally {
			try {
				out.close();
			}
			catch (IOException ex) {
				System.err.println("Can't close " + out);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @param configuration
	 * @param attachment
	 * @return
	 */
	public static BlobManager createBlobManager(BlobConfiguration configuration,
			Attachment attachment) {
		BackEndFeatures features = new BackEndFeatures( null );
		{
			final BlobInformationProvider blobInformationProvider = new TestAttachmentProvider( attachment );
			features.setBlobManagerBackEnd( new BlobManagerBackEnd( configuration, blobInformationProvider ) );
		}
		final BackEndContext context = new BackEndContext( features, null );
		ServiceBackEndFactory testServiceBackEndFactory = new ServiceBackEndFactory( context );
		ProxyServiceProvider proxyServiceProvider = new ProxyServiceProvider( testServiceBackEndFactory );
		BlobTransferService blobTransferService = proxyServiceProvider.getSyncProxyService( BlobTransferService.class );
		return new BlobManager( blobTransferService );
	}
}
