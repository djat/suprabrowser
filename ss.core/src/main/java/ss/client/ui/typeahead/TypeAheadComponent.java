/**
 * 
 */
package ss.client.ui.typeahead;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.models.autocomplete.DataModel;
import ss.client.ui.models.autocomplete.ResultListener;

/**
 * @author d!ma
 * 
 */
public class TypeAheadComponent<T> {

	private final TypeAheadComponentNew<T> impl;

	/**
	 * TypeAheadComponent with TextContentProvider as implementation of IControlContentProvider
	 */
	public TypeAheadComponent(final Text text,
			DataModel<T> model, ResultListener<T> resultListener) {
		this(text, model, resultListener, new TextContentProvider( text ), false, true);
	}
	
	public TypeAheadComponent(final Text text,
			DataModel<T> model, ResultListener<T> resultListener, IControlContentProvider contentProvider, boolean isEscDropComponent, boolean isCleanControl) {
		this.impl = new TypeAheadComponentNew<T>( text, model, resultListener, contentProvider, isEscDropComponent, isCleanControl);
	}

	public TypeAheadComponent(final Text text,
			DataModel<T> model, ResultListener<T> resultListener, boolean isEscDropComponent) {
		this( text, model, resultListener, new TextContentProvider( text ), isEscDropComponent , true);
	}

	/**
	 * 
	 */
	public void dispose() {
		this.impl.dispose();		
	}

	/**
	 * 
	 */
	public void openPopup() {
		this.impl.openPopup();
		
	}

	/**
	 * returns KeyListener which make visible TypeAheadComponent
	 */
	public KeyListener getKeyListener() {
		return this.impl.getControlKeyListener();
	}

}
