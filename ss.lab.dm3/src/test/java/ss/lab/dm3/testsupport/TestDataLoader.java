/**
 * 
 */
package ss.lab.dm3.testsupport;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainResolverHelper;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.DomainLoader;
import ss.lab.dm3.persist.workers.DomainWorker;
import ss.lab.dm3.persist.workers.DomainWorkerContext;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SupraSphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;

/**
 *
 */
public class TestDataLoader extends DomainWorker<DomainWorkerContext> {

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.workers.DomainWorker#run(ss.lab.dm3.persist.workers.DomainWorkerContext)
	 */
	@Override
	public Object run(DomainWorkerContext context) {
		return createLoader();
	}

	/**
	 * @return
	 */
	private static DomainLoader createLoader() {
		DomainLoader loader = new DomainLoader( QueryHelper.combine( SupraSphere.createEqByClass(),
			Sphere.createEqByClass(),
			UserInSphere.createEqByClass(),
			UserAccount.createEqByClass() ) );
		return loader;
	}
	
	public static void load() {
		final Domain domain = DomainResolverHelper.getCurrentDomain();
		CallbackResultWaiter waiter = new CallbackResultWaiter(); 
		createLoader().beginLoad(domain, waiter );
		waiter.waitToResult();
	}

}
