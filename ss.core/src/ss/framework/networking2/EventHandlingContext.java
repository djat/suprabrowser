/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public final class EventHandlingContext<N extends Event> extends ActiveMessageHandlingContext<N> {

	private boolean cancelBuble = false;

	/**
	 * @param notification
	 */
	public EventHandlingContext(N notification) {
		super(notification);
	}

	/**
	 * @return
	 */
	public boolean isCancelBuble() {
		return this.cancelBuble;
	}

	/**
	 * 
	 */
	public void cancelBuble() {
		this.cancelBuble = true;
	}

	
}
