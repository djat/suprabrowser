package ss.client.debug;

import org.dom4j.Document;

public interface IDebugCommandOutput {

	/**
	 * Appends object 
	 */
	IDebugCommandOutput append(Object commandResult);
	
	/**
	 * Appends string 
	 */
	IDebugCommandOutput append(String commandResult);
	
	/**
	 * Appends string builder 
	 */
	IDebugCommandOutput append(StringBuilder commandResult);
	
	/**
	 * Appends exception info 
	 */
	IDebugCommandOutput append(Exception commandResult);
	
	/**
	 * Appends break to new line
	 */
	IDebugCommandOutput appendln();

	/**
	 * Appends text and break to new line
	 */
	IDebugCommandOutput appendln(String commandResult);

	/**
	 * Appends exception information and break to new line
	 */
	IDebugCommandOutput appendln(Exception commandResult);

	/**
	 * Appends formatted xml document and break to new line
	 * @param sphereDocument xml document or null
	 */
	IDebugCommandOutput appendln(Document sphereDocument);

}
