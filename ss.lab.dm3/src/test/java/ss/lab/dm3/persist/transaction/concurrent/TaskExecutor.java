package ss.lab.dm3.persist.transaction.concurrent;

public interface TaskExecutor {

	void beginExecute(AbstractTask ... tasks );

}
