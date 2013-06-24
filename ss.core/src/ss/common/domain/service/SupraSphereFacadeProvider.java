package ss.common.domain.service;

import java.util.concurrent.atomic.AtomicLong;

import ss.common.VerifyAuth;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;

@Refactoring(classify=SupraSphereRefactor.class)
public class SupraSphereFacadeProvider {

	protected transient final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(getClass());
	
	public final static SupraSphereFacadeProvider INSTANCE = new SupraSphereFacadeProvider();
	
	private SupraSphereFacadeFactory factory = new SupraSphereFacadeFactory();

	private SupraSphereFacadeProvider() {
	}
	
	private ThreadLocal<VersionedSupraSphereFacade> threadVerifyAuth = new ThreadLocal<VersionedSupraSphereFacade>();
	
	private final AtomicLong actualVersion = new AtomicLong();

	private VerifyAuth actualVerifyAuth = null;

	/**
	 * @param defaultVerifyAuth
	 * @return
	 */
	public synchronized ISupraSphereFacade get(VerifyAuth defaultVerifyAuth) {
		VersionedSupraSphereFacade versionedFacade = this.threadVerifyAuth.get();
		if ( versionedFacade != null && !versionedFacade.isOutOfDate() ) {
			return versionedFacade.getFacade();
		}
		VerifyAuth verifyAuthForFacade = this.actualVerifyAuth != null ? this.actualVerifyAuth : defaultVerifyAuth; 
		versionedFacade = new VersionedSupraSphereFacade( getActualVersion(), this.factory.create( verifyAuthForFacade ) );
		this.threadVerifyAuth.set(versionedFacade);
		return versionedFacade.getFacade();
	}
	
	public synchronized void registrySupraSphereChanges( VerifyAuth actualVerifyAuth ) {
		this.actualVersion.incrementAndGet();
		this.actualVerifyAuth = actualVerifyAuth; 
	}


	/**
	 * @return
	 */
	public long getActualVersion() {
		return this.actualVersion.get();
	}
	
	
	class VersionedSupraSphereFacade {
		
		private final long version;
		
		private final ISupraSphereFacade facade;

		/**
		 * @param version
		 * @param facade
		 */
		public VersionedSupraSphereFacade(long version,
				ISupraSphereFacade facade) {
			super();
			this.version = version;
			this.facade = facade;
		}
		
		/**
		 * @return
		 */
		public boolean isOutOfDate() {
			return this.version != getActualVersion();
		}

		public ISupraSphereFacade getFacade() {
			return this.facade;
		}
		
		
	}


}
