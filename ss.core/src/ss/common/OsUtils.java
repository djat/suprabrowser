/**
 * 
 */
package ss.common;

import java.io.File;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.common.os.IOsUtils;

/**
 * 
 */
public class OsUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(OsUtils.class);
	
	private static IOsUtils IMPLEMENTATION; 
	
	private static synchronized IOsUtils getImplementation() {
		if ( IMPLEMENTATION == null ) {
			IMPLEMENTATION = ss.os.OsUtilsImplementationFactory.INSTANCE.createOsUtils();
		}
		return IMPLEMENTATION;
	}

	public static void startFile(File forList, String execName, String newname,
			String filename) {
		getImplementation().startFile(forList, execName, newname, filename);	
	}

	public static void restoreWindow(Shell shell, Text text) {
		getImplementation().restoreWindow(shell, text);
	}

	public static void setWindowPos(final Shell shell) {
		getImplementation().setWindowPos(shell);
	}

	public static void execCommand(String execName) {
		getImplementation().execCommand(execName);
	}

	public static String getOfficeProgram() {
		return getImplementation().getOfficeProgram();
	}

	public static String getPDFProgram() {
		return getImplementation().getPDFProgram();
	}
}
