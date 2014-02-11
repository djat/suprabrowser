package ss.lab.dm3.security2;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.service.AbstractServiceProvider;
import ss.lab.dm3.security2.services.SecurityProviderAsync;

/**
 * @author Dmitry Goncharov
 */
public class SecurityManager {

	private Authentication authentication;
	
	private SecurityProviderAsync securityProvider;  
	/**
	 * @param connection
	 */
	public SecurityManager(AbstractServiceProvider serviceProvider) {
		this.securityProvider = serviceProvider.getAsyncService(SecurityProviderAsync.class);
		CallbackResultWaiter waiter = new CallbackResultWaiter();
		this.securityProvider.getAuthentication( waiter );
		this.authentication = waiter.waitToResult( Authentication.class );
	}

	/**
	 * 
	 */
	public Authentication getAuthentication() {
		return this.authentication;
	}

	/**
	 * @param authority
	 * @return
	 */
	public boolean hasAuthority(Authority authority) {
		return getAuthentication().getAuthorities().contains(authority);
	}
	
}
