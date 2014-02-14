/**
 * 
 */
package ss.common;

import org.dom4j.Document;

import ss.domainmodel.SupraSphereStatement;

/**
 * @author zobo
 *
 */
public class SupraSphereStatementContainer {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSphereStatementContainer.class);
	
	public static final SupraSphereStatementContainer INSTANCE = new SupraSphereStatementContainer();

	private SupraSphereStatementContainer(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	private SupraSphereStatement statement;
	
	private Document document;
	
	private final Object mutex = new Object();
	
	public void setSupraSphereDocument( final Document supraDocument ){
		if (supraDocument == null){
			logger.error("Supra Sphere Document is null");
			return;
		}
		if ((this.document == null)||(this.document != supraDocument)){
			update(supraDocument);
		}
	}
	
	/**
	 * @param supraDocument
	 */
	private void update( final Document supraDocument ) {
		synchronized (this.mutex) {
			this.document = supraDocument;
			this.statement = this.document != null ? SupraSphereStatement.wrap(this.document) : null;
		}
	}

	public SupraSphereStatement getSupraSphere(){
		synchronized (this.mutex) {
			return this.statement;
		}
	}
	
	public Document getSupraSphereDocument(){
		synchronized (this.mutex) {
			return this.document;
		}
	}

	/**
	 * @param sphereDocument
	 * @return
	 */
	public SupraSphereStatement getSupraSphere( final Document sphereDocument ) {
		setSupraSphereDocument( sphereDocument );
		return getSupraSphere();
	}
}
