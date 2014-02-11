package ss.lab.dm3.persist.transaction.concurrent;

import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.persist.workers.DomainWorkerHelper;

public class Worker {

	private Connection connection;
	
	public Worker(Connection connection) {
		super();
		this.connection = connection;
	}

	public synchronized void dispose() {
		this.connection.close();
		this.connection = null;
	}

	public void beginExecute(AbstractTask abstractTask) {
		DomainWorkerHelper.run( this.connection.getDomain(), abstractTask, "run" );
	}
	
}
