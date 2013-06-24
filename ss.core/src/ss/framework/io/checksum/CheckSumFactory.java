/**
 * 
 */
package ss.framework.io.checksum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 */
public class CheckSumFactory  {
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CheckSumFactory.class);
	
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * Singleton instance
	 */
	public final static CheckSumFactory INSTANCE = new CheckSumFactory();

	private CheckSumFactory() {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.io.checksum.ICheckSumFactory#create(java.lang.String)
	 */
	public final synchronized String createFileChecksum(String fileName) throws CantCreateCheckSumException {
		try {
			final InputStream stream = new FileInputStream(fileName);
			try {
				return createCheckSum(stream);
			}
			finally {
				try {
					stream.close();
				} catch (IOException ex) {
					logger.error( "Can't close stream", ex );
				}
			}
		} catch (Exception ex) {
			throw new CantCreateCheckSumException( "Can't load file: " + fileName, ex );
		}
	}

	/**
	 * @param stream
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public String createCheckSum(InputStream stream) throws CantCreateCheckSumException  {
		AbstractCheckSumBuilder builder;
		try {
			builder = createBuilder();
		} catch (Exception ex) {
			throw new CantCreateCheckSumException( "Can't create checksum builder", ex );
		}
		final byte[] buff = new byte[BUFFER_SIZE];
		int numRead;
		do {
			try {
				numRead = stream.read(buff);
			} catch (IOException ex) {
				throw new CantCreateCheckSumException( "Can't read from stream", ex );
			}
			if (numRead > 0) {
				builder.add( buff, numRead );
			}
		} while ( numRead > -1 );
		return builder.getResult();
	}

	/**
	 * @return
	 */
	private AbstractCheckSumBuilder createBuilder() throws Exception {
		return new Md5CheckSumBuilder();
	}

}
