/**
 * 
 */
package ss.client.ui.browser.manager;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.interfaces.nsISupports;

/**
 *
 */
final class NsObjectInterfaces {

	private final NsObjectFinalizer finalizer;
	
	private final List<NsWrapperBase> wrappers = new ArrayList<NsWrapperBase>();
	
	
	/**
	 * @param finalizer
	 */
	public NsObjectInterfaces(NsObjectFinalizer finalizer) {
		super();
		if (finalizer == null) {
			throw new NullPointerException("finalizer");
		}
		this.finalizer = finalizer;
	}

	/**
	 * @param impl
	 * @return
	 */
	public NsWrapperBase getWrapper(nsISupports impl) {
		NsWrapperBase wrapper = find(impl);
		if ( wrapper == null ) {
			wrapper = NsObjectWrapper.createWrapper( this, impl );
			this.wrappers.add(wrapper);
		}
		return wrapper;
	}

	/**
	 * @param impl
	 * @return
	 */
	private NsWrapperBase find(nsISupports impl) {
		for( NsWrapperBase wrapperBase : this.wrappers ) {
			NsObjectWrapper wrapper = NsObjectWrapper.getWrapper(wrapperBase);
			if ( wrapper.isWrapperFor( impl ) ) {
				return wrapperBase;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	void release() {
		for( int n = this.wrappers.size() - 1; n >= 0; --n ) {
			NsObjectWrapper wrapper = NsObjectWrapper.getWrapper( this.wrappers.get( n ) );
			this.finalizer.finalize( wrapper.detach() );
		}
		this.wrappers.clear();	
	}
	
	
	
	

}
