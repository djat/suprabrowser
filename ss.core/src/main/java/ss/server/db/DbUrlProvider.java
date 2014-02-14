package ss.server.db;

import java.io.File;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import ss.util.VariousUtils;

public final class DbUrlProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DbUrlProvider.class);
	
	private static final String DYN_SERVER_XML = "dyn_server.xml";

	public final static DbUrlProvider INSTANCE = new DbUrlProvider();
	
	private final Date creationTime = new Date();

	private DbUrlProvider() {
	}

	public synchronized String getDbUrl() {
		File file = VariousUtils.getSupraFile(DYN_SERVER_XML);
		final SAXReader saxReader = new SAXReader();
		try {
			Document doc = saxReader.read(file);
			return doc.getRootElement().element("mysql").attributeValue("url");
		} catch (Exception e) {
			String message = "Couldn't load DB URL";
			logger.error(message, e);
			throw new DBURLNotLoadedException(message, e);
		}
	}

	public Date getCreationTime() {
		return this.creationTime;
	}
	
	public static class DBURLNotLoadedException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2713263557782823945L;

		public DBURLNotLoadedException(String message, Throwable cause) {
			super(message, cause);
		}

	}
	
}
