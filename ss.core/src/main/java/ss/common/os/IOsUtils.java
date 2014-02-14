/**
 * 
 */
package ss.common.os;

import java.io.File;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 */
public interface IOsUtils {

	void startFile(File forList, String execName, String newname,
			String filename);

	void restoreWindow(Shell shell, Text text);

	void setWindowPos(final Shell shell);

	void execCommand(String execName);

	String getOfficeProgram();

	String getPDFProgram();

}