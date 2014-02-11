package ss.lab.dm3.security2.services;

import ss.lab.dm3.connection.service.ServiceBackEnd;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.security2.Authentication;

public class SecurityProviderBackEnd extends ServiceBackEnd implements SecurityProvider {

	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.services.SecurityProviderFronEnd#getAuthentication()
	 */
	public Authentication getAuthentication() throws ServiceException {
		return getContext().getAuthentication();
	}

}
