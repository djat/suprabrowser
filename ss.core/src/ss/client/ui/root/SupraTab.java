/**
 * 
 */
package ss.client.ui.root;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.peoplelist.IPeopleList;

/**
 * @author zobo
 *
 */
public abstract class SupraTab {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraTab.class);
	
	private final Composite control;
	
	public SupraTab(final Composite parent){
		this.control = new Composite(parent, SWT.NONE);
		createContent(this.control);
	}

	protected abstract void createContent(final Composite parent);

	public final Composite getControl() {
		return this.control;
	}
	
	public boolean isRoot(){
		if (logger.isDebugEnabled()){
			logger.debug("Is root sphere performed, returning false");
		}
		return false;
	}
	
	public String getDesiredTitle(){
		return null;
	}

	/**
	 * @return
	 */
	public IPeopleList getPeopleTable() {
		return null;
	}

	/**
	 * 
	 */
	public void refreshMemberPresence( String contactName, boolean isOnline ) {
		IPeopleList peopleList = getPeopleTable();
		if ( peopleList != null ) {
			peopleList.refreshMemberPresence(contactName, isOnline );
		}
	}
}
