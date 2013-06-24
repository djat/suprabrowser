package ss.common.domain.service;

import java.lang.reflect.Method;

import ss.common.VerifyAuth;
import ss.domainmodel.SupraSphereStatement;
import ss.lab.dm3.utils.ReflectionHelper;
import ss.refactor.supraspheredoc.old.SsDocSurpaSphereFacade;

public class SupraSphereFacadeFactory {

	/**
	 * @param verifyAuth
	 * @return
	 */
	public ISupraSphereFacade create(VerifyAuth verifyAuth) {
		Method getter = ReflectionHelper.findGetter(verifyAuth.getClass(), "interalSupraSphere" );
		Object result;
		try {
			getter.setAccessible(true);
			result = getter.invoke( verifyAuth, new Object[] {} );
		} catch (Exception ex) {
			if ( ex instanceof RuntimeException ) {
				throw (RuntimeException)ex;
			}
			else {
				throw new RuntimeException( "Can't get suprasphere from verify auth", ex );
			}
		}
		final SupraSphereStatement supraSphereStatement = (SupraSphereStatement)result;
		return new SsDocSurpaSphereFacade(supraSphereStatement);
	}
	
}
