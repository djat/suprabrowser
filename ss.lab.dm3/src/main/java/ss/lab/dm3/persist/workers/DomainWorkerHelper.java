package ss.lab.dm3.persist.workers;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.persist.Domain;

public class DomainWorkerHelper {

	public static CallbackResultWaiter run( Domain domain, DomainWorkerHost host, String method ) {
		SimpleDomainWorkerRunner runner = new SimpleDomainWorkerRunner( domain );
		runner.add( host, method );
		return runner.execute();
	}
}
