package ss.lab.dm3.persist.transaction.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainResolverHelper;
import ss.lab.dm3.persist.workers.DomainWorkerHost;
import ss.lab.dm3.testsupport.objects.UserAccount;
import static d1.FastAccess.*;

public abstract class AbstractTask implements Runnable, DomainWorkerHost {

	protected final Log log = LogFactory.getLog(getClass());
	
	protected final ConcurrentUnit unit;

	public AbstractTask(ConcurrentUnit unit) {
		super();
		this.unit = unit;
	}

	public abstract void doWork();

	public void run() {
		try {
			doWork();
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug("Task done " + this );
			}
			this.unit.onTaskDone();
		}
	}

	public UserAccount getAccount() {
		Long targetId = unit.getTargetId();
		UserAccount account = getDomain().find( UserAccount.class, targetId );
		if ( account == null ) {
			throw new IllegalStateException( $("Can't find account with id {0}", targetId ) );
		}
		return account;	
	}
	
	public Domain getDomain() {
		return DomainResolverHelper.getCurrentDomain();
	}
	
	
	
}
