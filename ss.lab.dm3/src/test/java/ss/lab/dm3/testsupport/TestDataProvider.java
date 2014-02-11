package ss.lab.dm3.testsupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.hibernate.Session;
import org.hibernate.Transaction;


public class TestDataProvider {

	/**
	 * 
	 */
	public static final String DEFAULT_TEST_DATA_SQL = "/data.sql";
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	private Session session;
	
	private final String batchFileName;

	
	/**
	 * 
	 */
	public TestDataProvider() {
		this( TestHibernateUtils.getSessionFactory().getCurrentSession(), DEFAULT_TEST_DATA_SQL );
	}

	/**
	 * @param batchFileName
	 */
	public TestDataProvider(Session session,String batchFileName) {
		super();
		this.batchFileName = batchFileName;
		this.session = session;
	}
	
	public void loadTestData() {
		try {
			final String[] batchLines = getBatchLines();
			for( String line : batchLines ) {
				line = line.trim();
				if ( line.length() > 0 ) {
					if ( this.log.isDebugEnabled() ) {
						this.log.debug( "Executing " + line );
					}
					this.session.createSQLQuery( line ).executeUpdate();
				}
			}
			try {
				Thread.sleep( 100 );
			}
			catch (InterruptedException ex) {
			}
		} catch (IOException ex) {
			throw new RuntimeException( "Can't load test data", ex);
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private String[] getBatchLines() throws IOException {
		StringBuilder sb = new StringBuilder();
		final String rawBatch = loadRawBatch();
		String[] batchLines = rawBatch.split( "\n" );
		for( String batchLine : batchLines ) {
			if ( sb.length() > 0 ) {
				sb.append( " " );
			}
			sb.append( batchLine );
		}
		return sb.toString().split( ";" );
		
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private String loadRawBatch() throws IOException {
		final InputStream in = getClass()
				.getResourceAsStream(this.batchFileName);
		if ( in == null ) {
			throw new IOException( "Can't find " + this.batchFileName + " in class path." );
		}
		InputStreamReader reader = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		char[] buff = new char[512];
		int count = 0;
		do {
			count = reader.read(buff);
			if ( count > 0 ) {
				sb.append( buff, 0, count );
			}
		} while (count > 0);
		return sb.toString();
	}
	
}
