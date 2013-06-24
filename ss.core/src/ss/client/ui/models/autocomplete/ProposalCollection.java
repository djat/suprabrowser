/**
 * 
 */
package ss.client.ui.models.autocomplete;

import java.util.ArrayList;
import java.util.List;

/**
 * @author d!ma
 *
 */
public class ProposalCollection<T> {

	private final List<Proposal<T>> items = new ArrayList<Proposal<T>>();
	
	
	/**
	 * @return
	 */
	public int getCount() {
		return this.items.size();
	}


	/**
	 * @return array of display texts for proposal
	 */
	public String[] toDisplayTexts() {
		final String[] displayTexts = new String[this.items.size()];
		int n = 0; 
		for( Proposal<T> proposal : this.items ) {
			displayTexts[ n ] = proposal.getDisplayText();
			++ n;
		}
		return displayTexts;
	}


	/**
	 * @param index in bounds
	 * @return proposal by index
	 */
	public Proposal<T> get(int index) {
		return this.items.get(index);
	}
	
	/**
	 * Add proposal to the collection 
	 * @param proposal not null proposal
	 */
	public void add( Proposal<T> proposal ) {
		this.items.add(proposal);
	}


	/**
	 * @return
	 */
	public List<T> toModels() {
		final List<T> models = new ArrayList<T>( getCount() );
		for( Proposal<T> proposal : this.items ) {
			models.add( proposal.getModel() );
		}
		return models;
	}

	
}
