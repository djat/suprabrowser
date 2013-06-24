/**
 * 
 */
package ss.common;

import java.io.IOException;

import org.apache.log4j.Logger;

import ss.common.tasks.UnPackTask;
import ss.global.SSLogger;
import ss.util.InitializedReference;

/**
 * @author dankosedin
 * 
 */
public class LocationUtils {

	private static final String TEMP_FOLDER = "sstmp";

	private static final String TINYMCE_JAR = "tinymce.jar";

	private static final String MICROBLOG_JAR = "microblog.jar";

	private static String tempFolderPath = null;

	private static Logger logger = SSLogger.getLogger(LocationUtils.class);

	private static InitializedReference<String> MICRO = new InitializedReference<String>();

	private static InitializedReference<String> TINY = new InitializedReference<String>();	

	public static void init() {
		try {
			tempFolderPath = FileUtils.createTempFolder(TEMP_FOLDER).getPath();
		} catch (IOException ex) {
			logger.error("Failed to create temp folder", ex);
		}
		UnPackTask microTask = new UnPackTask(MICROBLOG_JAR, tempFolderPath,
				MICRO);
		microTask.run();

		UnPackTask tinyTask = new UnPackTask(TINYMCE_JAR, tempFolderPath, TINY);
		tinyTask.run();
	}

	public static void clean() {
		FileUtils.deleteFolder(MICRO.detachValue());
		FileUtils.deleteFolder(TINY.detachValue());
		FileUtils.deleteFolder(tempFolderPath);
	}

	public static String getMicroblogBase() {
		return getAndCheck(MICRO, MICROBLOG_JAR + " content");
	}

	public static String getTinymceBase() {
		return getAndCheck(TINY, TINYMCE_JAR + " content");
	}

	private static String getAndCheck(InitializedReference<String> arg,
			String comment) {
		String value = arg.getValue();
		if (value == null) {
			throw new RuntimeException("Failed to get initialized path of "
					+ comment);
		}
		return value;
	}

}
