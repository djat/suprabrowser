package ss.server.debug.ssrepair;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import ss.common.StringUtils;
import ss.domainmodel.SupraSphereStatement;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;

public class Context {

	private final String title;

	private final List<String> errors = new ArrayList<String>();

	private final List<String> messages = new ArrayList<String>();

	private final boolean commitResults;

	private final XMLDB xmldb;
	
	private SupraSphereStatement cashedSupraSphere = null;
	
	private boolean cashSupraSphere = true; 

	/**
	 * @param supraSphere
	 */
	public Context(String title, boolean commitResults) {
		super();
		this.title = title;
		this.commitResults = commitResults;
		this.xmldb = new XMLDB();
	}

	public SupraSphereStatement getSupraSphere() {
		if ( !this.cashSupraSphere || 
			  this.cashedSupraSphere == null ) {
			this.cashedSupraSphere = getSupraSphereFromDb();
		}
		return this.cashedSupraSphere;
	}
	
	private SupraSphereStatement getSupraSphereFromDb() {
		try {
			return SupraSphereStatement.wrap(Utils.getUtils(this.xmldb)
					.getSupraSphereDocument());
		} catch (NullPointerException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param message
	 */
	public void addError(String message) {
		this.errors.add(message);
	}

	/**
	 * @param message
	 */
	public void addMessage(String message) {
		this.messages.add(message);
	}
	
	/**
	 * @param message
	 */
	public void addMessage(String title, String details ) {
		this.messages.add( title + StringUtils.getLineSeparator() + details );
	}

	/**
	 * @return
	 */
	public String getReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Runs ").append(this.title).append(", commitResults: ")
				.append(this.commitResults).append(
						StringUtils.getLineSeparator());
		if (this.errors.size() > 0) {
			sb.append("Errors (").append(this.errors.size()).append(")");
			sb.append(StringUtils.getLineSeparator());
			for (String error : this.errors) {
				if (sb.length() > 0) {
					sb.append(StringUtils.getLineSeparator());
				}
				sb.append(error);
			}
		}
		if (this.messages.size() > 0) {
			sb.append("Messages (").append(this.messages.size())
					.append(") {{{");
			sb.append(StringUtils.getLineSeparator()).append(
					StringUtils.getLineSeparator());
			for (String message : this.messages) {
				if (sb.length() > 0) {
					sb.append(StringUtils.getLineSeparator()).append(StringUtils.getLineSeparator());
				}
				sb.append(message);
			}
			sb.append(StringUtils.getLineSeparator()).append(
					StringUtils.getLineSeparator());
			sb.append("}}}").append(StringUtils.getLineSeparator());
		}
		return sb.toString();
	}

	/**
	 * 
	 */
	public XMLDB getXmlDb() {
		return this.xmldb;
	}

	/**
	 * @return
	 */
	public boolean isCommitResults() {
		return this.commitResults;
	}

	/**
	 * @param string
	 * @param supraSphere
	 */
	public void changeSupraSphere(String message, SupraSphereStatement supraSphere) {
		String commitState;
		if ( this.isCommitResults() ) {
			this.getXmlDb().replaceDoc( supraSphere.getDocumentCopy(), supraSphere.getSystemName() );
			commitState = "Change COMMITED";
		}
		else {
			commitState = "Change NOT COMMITED";
		}
		this.addMessage( message + StringUtils.getLineSeparator() + commitState, supraSphere.toString() );
		this.cashedSupraSphere = null;
	}

}
