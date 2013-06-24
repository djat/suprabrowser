/**
 * 
 */
package ss.client.ui.models.autocomplete;

/**
 * @author d!ma
 *
 */
public class Proposal<T> {

	private final T model;
	
	private final String displayText;

	/**
	 * @param model
	 * @param displayText
	 */
	public Proposal(final T model, final String displayText) {
		super();
		this.model = model;
		this.displayText = displayText;
	}

	/**
	 * @return the data
	 */
	public T getModel() {
		return this.model;
	}

	/**
	 * @return the displayText
	 */
	public String getDisplayText() {
		return this.displayText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Proposal: " + this.displayText + ", " + this.model;
	}
	
	
}
