package ss.client.ui.spheremanagement;

import java.util.ArrayList;
import java.util.List;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public abstract class AbstractOutOfDateable implements IOutOfDateable {

	private volatile boolean outOfDate = true;

	private final List<IOutOfDateable> components = new ArrayList<IOutOfDateable>();
	
	/**
	 * 
	 */
	public AbstractOutOfDateable() {
		super();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.IOutOfDateable#checkOutOfDate()
	 */
	public synchronized final void checkOutOfDate() {
		if ( this.outOfDate ) {
			for( IOutOfDateable component : this.components )  {
				component.checkOutOfDate();
			}
			reload();
			this.outOfDate = false;
		}
	}

	protected final void addComponent( IOutOfDateable outOfDateable ) {
		if( outOfDateable == null ) {
			throw new ArgumentNullPointerException( "outOfDateable" );
		}
		this.components.add( outOfDateable );
	}
	
	/**
	 * 
	 */
	protected abstract void reload();

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.IOutOfDateable#outOfDate()
	 */
	public synchronized final void outOfDate() {
		this.outOfDate = true;
		for( IOutOfDateable component : this.components )  {
			component.outOfDate();
		}
		outOfDateing();
	}
	
	/**
	 * 
	 */
	protected void outOfDateing() {		
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.IOutOfDateable#forseReload()
	 */
	public synchronized void forseReload() {
		outOfDate();
		checkOutOfDate();
	}

}