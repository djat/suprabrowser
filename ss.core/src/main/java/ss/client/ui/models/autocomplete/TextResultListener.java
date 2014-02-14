package ss.client.ui.models.autocomplete;

import org.eclipse.swt.widgets.Text;

public class TextResultListener<T> implements ResultListener<T> {

	private final Text text;
	
	
	/**
	 * @param text
	 */
	public TextResultListener(final Text text) {
		super();
		this.text = text;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.models.autocomplete.ResultListener#processEmptyResult()
	 */
	public void processEmptyResult() {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.models.autocomplete.ResultListener#processListSelection(java.lang.String, java.lang.Object)
	 */
	public void processListSelection(String listSelection, T realData) {
		complete(listSelection);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.models.autocomplete.ResultListener#processResult(java.lang.String, java.lang.Object)
	 */
	public void processResult(String selection, T realData) {
		complete(selection);		
	}

	private void complete( String selection ) {
		if ( !this.text.isDisposed() ) {
			this.text.setText( selection );
			this.text.setSelection(0, selection.length() );
		}		
	}
}
