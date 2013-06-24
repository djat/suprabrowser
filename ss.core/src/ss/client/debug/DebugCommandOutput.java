package ss.client.debug;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.common.XmlDocumentUtils;

public class DebugCommandOutput implements IDebugCommandOutput {
	
	private final static String LINE_END = StringUtils.getLineSeparator(); 
	
	private final StringBuilder sb = new StringBuilder();
	
	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#append(java.lang.Exception)
	 */
	public IDebugCommandOutput append(Exception commandResult) {
		return this.append( commandResult != null ? commandResult.toString() : null );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#append(java.lang.Object)
	 */
	public IDebugCommandOutput append(Object commandResult) {
		this.sb.append( commandResult != null ? commandResult  : "[null]" );		
		return this;
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#append(java.lang.String)
	 */
	public IDebugCommandOutput append(String commandResult) {
		this.sb.append( commandResult != null ? commandResult  : "[null]" );		
		return this;
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#append(java.lang.StringBuilder)
	 */
	public IDebugCommandOutput append(StringBuilder commandResult) {
		return append( commandResult != null ? commandResult.toString() : null );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#appendln()
	 */
	public IDebugCommandOutput appendln() {
		this.sb.append( LINE_END );
		return this;
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#appendln(java.lang.Exception)
	 */
	public IDebugCommandOutput appendln(Exception commandResult) {
		return append(commandResult).appendln();
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#appendln(java.lang.String)
	 */
	public IDebugCommandOutput appendln(String commandResult) {
		return append(commandResult).appendln();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return this.sb.toString();
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandOutput#appendln(org.dom4j.Document)
	 */
	public IDebugCommandOutput appendln(Document sphereDocument) {
		return appendln( XmlDocumentUtils.toPrettyString(sphereDocument));
	}
	
	

	
}
