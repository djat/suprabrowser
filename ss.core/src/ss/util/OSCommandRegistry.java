/**
 * 
 */
package ss.util;

import ss.common.OsUtils;

/**
 * @author david
 *
 */
public class OSCommandRegistry {
	
	
	public static String getProgramForFileExtension(String filename) {
		filename = filename.toLowerCase();
		if (CheckFileExtension.isKnownEditableDocument(filename)) {
			return getOfficeProgram();
		}
		else if (CheckFileExtension.isPDF(filename)) {
			return getPDFProgram();
		}
		else {
			return getOfficeProgram();
		}
 		
	}
	
	public static String getOfficeProgram() {
		return OsUtils.getOfficeProgram();
	}
	
	public static String getPDFProgram() {
		return OsUtils.getPDFProgram();		
	}

}
