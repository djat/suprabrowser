package ss.client.ui.browser.manager;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.swt.browser.Browser;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;


public final class WebBrowserManager implements NsObjectFinalizer {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(WebBrowserManager.class);
	

	/**
	 * 
	 */
	private static final String ORG_MOZILLA_XPCOM_INTERNAL_XPCOMJAVA_PROXY_BASE = "org.mozilla.xpcom.internal.XPCOMJavaProxyBase";
	
	public final static WebBrowserManager INSTANCE = new WebBrowserManager();

	private final Set<nsISupports> unsafeDeadInterfaces = new HashSet<nsISupports>();
	
	private final Hashtable<Browser,NsWrapperBase> aliveBrowsers = new Hashtable<Browser,NsWrapperBase>();
	
	private final ReleaseStrategy releaseStrategy = ReleaseStrategy.REFERENCE_HOLD; 
	
	private WebBrowserManager() {
	}
	
	private synchronized void checkUnsafeWebBrowser( nsIWebBrowser webBrowser ) throws UseReleasedBrowserException {
		if ( this.unsafeDeadInterfaces.contains( webBrowser ) ) {
			throw new UseReleasedBrowserException( webBrowser );
		}
	}
		
	/**
	 * @param swtBrowser
	 * @param unsafeWebBrowser
	 */	
	public synchronized nsIWebBrowser getSafeWebBrowser(Browser swtBrowser, nsIWebBrowser unsafeWebBrowser) {
		if ( swtBrowser == null ) {
			throw new NullPointerException( "swtBrowser" );
		}
		if ( unsafeWebBrowser == null ) {
			throw new NullPointerException( "webBrowser" );
		}
		if ( isAutoReleaseStrategy() ) {
			return unsafeWebBrowser;
		}
		checkUnsafeWebBrowser( unsafeWebBrowser );
		final NsWrapperBase registeredWrapper = this.aliveBrowsers.get( swtBrowser );		
		if ( registeredWrapper != null ) {
			if ( !NsObjectWrapper.getWrapper(registeredWrapper).isWrapperFor(unsafeWebBrowser) ) {
				logger.error( "Different web browser instances. Resitered " + registeredWrapper  + ", actual " + unsafeWebBrowser + " for swt browser " + swtBrowser );
				return null;
			}
			return (nsIWebBrowser) registeredWrapper;
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Register browser " + swtBrowser + ", webBrowser " + unsafeWebBrowser );
			}
			return registerWebBrowser( swtBrowser, unsafeWebBrowser );
		}		
	}
	
	/**
	 * @param swtBrowser
	 * @param webBrowser
	 * @return
	 */
	private nsIWebBrowser registerWebBrowser(Browser swtBrowser, final nsIWebBrowser webBrowser) {
		final NsWrapperBase wrapper = NsObjectWrapper.createWrapper( this, webBrowser); 
		this.aliveBrowsers.put( swtBrowser, wrapper );
		return (nsIWebBrowser) wrapper;
	}

	public synchronized void release( Browser swtBrowser ) {
		if ( swtBrowser == null ) {
			throw new NullPointerException( "swtBrowser" );
		}
		final NsWrapperBase wrapper = this.aliveBrowsers.get(swtBrowser);
		if ( wrapper != null ) {
			if (logger.isDebugEnabled()) {
				logger.debug( "Releasing " + swtBrowser + ", webBrowser " + wrapper );
			}
			this.aliveBrowsers.remove(swtBrowser);
			wrapper.release();
		}
		else {
			if ( !isAutoReleaseStrategy() ) {
				logger.warn( "Web browser is null for " + swtBrowser );
			}
		}
	}

	private boolean isAutoReleaseStrategy() {
		return this.releaseStrategy == ReleaseStrategy.AUTO;
	}
	
	
	/**
	 * @return the releaseStrategy
	 */
	public ReleaseStrategy getReleaseStrategy() {
		return this.releaseStrategy;
	}

	void addDeadInterface( nsISupports unsafeImplementation ) {
		if ( this.releaseStrategy.isHoldDisposedBrowser() ) {
			if (logger.isDebugEnabled()) {
				logger.debug( "Remember dead ns object " + unsafeImplementation	);
			}			
			this.unsafeDeadInterfaces.add(unsafeImplementation);
		}
	}
	
	@Override
	public String toString() {
		return "Alive browsers count " + this.aliveBrowsers.size();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.manager.NsObjectFinalizer#release(org.mozilla.interfaces.nsISupports)
	 */
	public void finalize(nsISupports impl) {
		if ( impl == null ) {
			logger.error( "Can't finalize null object " );
			return;
		}
		if (!this.releaseStrategy.isManualFinalization()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skip finalization for " + impl);
			}
		} else {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Finalizing implementation for " + impl);
				}
				final Class<?> proxyBaseClazz = impl
						.getClass()
						.getClassLoader()
						.loadClass(
								ORG_MOZILLA_XPCOM_INTERNAL_XPCOMJAVA_PROXY_BASE);
				final Method finalizeMethod = proxyBaseClazz.getMethod(
						"finalize", new Class[] {});
				finalizeMethod.invoke(impl, new Object[] {});
			} catch (Throwable ex) {
				logger.error("Can't directly finalize proxy " + impl, ex);
			}
		}
		this.unsafeDeadInterfaces.add(impl);
	}
	
	/**
	 *
	 */
	public static class UseReleasedBrowserException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5869363138149531699L;

		/**
		 * @param webBrowser
		 */
		public UseReleasedBrowserException(nsIWebBrowser webBrowser) {
			super( "Use released webBrowser " + webBrowser );
		}
	}

}
