/**
 * 
 */
package ss.client.ui.root.actions;

import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;

import ss.client.ui.ISphereView;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.VerbosedSession;

/**
 * @author zobo
 *
 */
public class RootSphereView implements ISphereView {

	private VerbosedSession verbosedSession;
	
	private String sphereId;
	
	public RootSphereView(Hashtable session, String sphereId){
		this.verbosedSession = new VerbosedSession();
		this.verbosedSession.setRawSession(session);
		this.sphereId = sphereId;
	}
	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#getRawSession()
	 */
	public Hashtable getRawSession() {
		return this.verbosedSession.getRawSession();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#getSelectedMembersNames()
	 */
	public List<String> getSelectedMembersNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#getSphereDefinition()
	 */
	public Document getSphereDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#getSphereId()
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#getSupraSphereFrame()
	 */
	public SupraSphereFrame getSupraSphereFrame() {
		return SupraSphereFrame.INSTANCE;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#getVerbosedSession()
	 */
	public VerbosedSession getVerbosedSession() {
		return this.verbosedSession;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.ISphereView#isSupraSphereView()
	 */
	public boolean isRootView() {
		// TODO Auto-generated method stub
		return false;
	}

}
