package ss.lab.dm3.persist.transaction.concurrent;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.connection.SystemConnectionProvider;

public class WorkerPool implements TaskExecutor {

	private List<Worker> workers = new ArrayList<Worker>();
	
	public WorkerPool(SystemConnectionProvider connectionProvider, int connectionsCount ) {
		super();		
		for( int n = 0; n < connectionsCount; ++ n ) {
			this.workers.add( new Worker( connectionProvider.create() ) );
		}
	}

	public void dispose() {
		for( Worker worker : this.workers ) {
			worker.dispose();
		}
		this.workers.clear();
	}
	
	public void beginExecute(AbstractTask... tasks) {
		List<Worker> workers = RandomHelper.randomItems( this.workers, tasks.length );
		for (int n = 0; n < tasks.length; n++) {
			AbstractTask abstractTask = tasks[n];
			workers.get( n ).beginExecute( abstractTask );
		}
	}
	
	

}
