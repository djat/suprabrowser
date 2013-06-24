/**
 * 
 */
package ss.framework.networking2.blob;

/**
 *
 */
public class CantTansferMessageFactory {
	
	public static String formatForbiddenMessage( String fileName ) {
		return "Can't open file: " + fileName; 
	}
	
	public static String formatFileNotFoundMessage( String fileName ) {
		return "File not found: " + fileName;
	}
	
}
