/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.ui.preferences.forwarding.SphereForwardingSubComposite;

/**
 * @author roman
 *
 */
public class ForwardingRulesChangesDetector extends AbstractChangesDetector {
	
	private SphereForwardingSubComposite composite;
	
	public ForwardingRulesChangesDetector(SphereForwardingSubComposite composite) {
		this.composite = composite;
	}
	
	public void collectChangesAndUpdate() {
		this.composite.applyPerformed();
	}

	public void rollbackChanges() {
		this.composite.performFinalAction();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.preferences.changesdetector.AbstractChangesDetector#getWarningString()
	 */
	@Override
	protected String getWarningString() {
		return this.bundle.getString(FORWARDING_RULES);
	}
}
