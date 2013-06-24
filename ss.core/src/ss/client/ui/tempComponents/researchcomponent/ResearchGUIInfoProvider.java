/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import org.eclipse.swt.widgets.MenuItem;

/**
 * @author zobo
 * 
 */
public class ResearchGUIInfoProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ResearchGUIInfoProvider.class);

	private final MenuItem lookInOwn;

	private final MenuItem lookInOthers;
	
	private final MenuItem newFromLastResearch;

	ResearchGUIInfoProvider(final MenuItem lookInOwn, final MenuItem lookInOthers, final MenuItem newFromLastResearch) {
		this.lookInOwn = lookInOwn;
		this.lookInOthers = lookInOthers;
		this.newFromLastResearch = newFromLastResearch;
	}

	public boolean isOwnKeywords() {
		return this.lookInOwn.getSelection();
	}

	public boolean isOthersKeywords() {
		return this.lookInOthers.getSelection();
	}

	public void setLookInOthers(final boolean value) {
		this.lookInOthers.setSelection(value);
	}

	public void setLookInOwn(final boolean value) {
		this.lookInOwn.setSelection(value);
	}
	
	public boolean isNewFromLastResearch() {
		return this.newFromLastResearch.getSelection();
	}

	public void setNewFromLastResearch( final boolean value ) {
		this.newFromLastResearch.setSelection( value );
	}

	/**
	 * @param initial
	 */
	public void update(final ResearchComponentDataContainer initial) {
		if (initial == null) {
			logger.error("Initial is null");
			return;
		}
		this.lookInOwn.setSelection(initial.isLookInOwn());
		this.lookInOthers.setSelection(initial.isLookInOthers());
		this.newFromLastResearch.setSelection(initial.isNewFromLastResearch());
	}
}
