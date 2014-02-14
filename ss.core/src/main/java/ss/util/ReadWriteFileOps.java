package ss.util;

import java.io.*;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class ReadWriteFileOps {
	
	private static final Logger logger = SSLogger.getLogger(ReadWriteFileOps.class);

	static public String getContents(File aFile) {
		StringBuffer contents = new StringBuffer();

		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(aFile));
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		return contents.toString();
	}

}
