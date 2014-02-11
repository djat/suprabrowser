package ss.lab.dm3.persist.transaction.concurrent;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.persist.Domain;

public class ConcurrentUnit {

	private final Long targetId;
	
	private final SuccessfulTask successfulTask;
	
	private final List<ConcurrentTask> concurrentTasks = new ArrayList<ConcurrentTask>();

	private final CallbackResultWaiter mainWaiter = new CallbackResultWaiter();
	
	private final MultiWaiter concurrentWaiter;

	private final String successfulName;
	
	private final MultiWaiter allTaskWaiter;

	public ConcurrentUnit(Long targetId, int concurentCount ) {
		super();
		this.targetId = targetId;
		this.successfulTask = new SuccessfulTask( this );
		for( int n = 0; n < concurentCount; ++ n ) { 
			this.concurrentTasks.add( new ConcurrentTask( this ) );
		}
		this.concurrentWaiter = new MultiWaiter( this.concurrentTasks.size() );
		this.successfulName = RandomHelper.randomName();
		this.allTaskWaiter = new MultiWaiter( this.concurrentTasks.size() + 1 );
	}
	
	public void execute(TaskExecutor workerProvider) {
		ArrayList<AbstractTask> taskToExecute = new ArrayList<AbstractTask>();
		taskToExecute.add( this.successfulTask );
		taskToExecute.addAll( this.concurrentTasks );
		workerProvider.beginExecute( taskToExecute.toArray( new AbstractTask[ taskToExecute.size() ] ) );
	}

	public Long getTargetId() {
		return targetId;
	}

	public void waitConcurrents() {
		concurrentWaiter.waitAll();
	}

	public void onConcurrentReady() {
		concurrentWaiter.onReady();
	}

	public void waitMain() {
		this.mainWaiter.waitToResult();
		Domain.sleep( RandomHelper.random( 10, 25 ) );
	}

	public void onMainReady() {
		this.mainWaiter.onSuccess( null );
	}

	public String getSuccessfulName() {
		return successfulName;
	}

	public void onTaskDone() {
		this.allTaskWaiter.onReady();
	}

	public void waitAll() {
		this.allTaskWaiter.waitAll();
	}
	
}
