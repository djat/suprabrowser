package ss.lab.dm3.persist.transaction.concurrent;

import ss.lab.dm3.connection.Waiter;
import ss.lab.dm3.connection.WaiterCheckpoint;
import ss.lab.dm3.persist.Domain;

public class Sleeper {

	public static void sleep( long miliseconds ) {
		Waiter waiter = Domain.createResponseWaiter();
		WaiterCheckpoint checkpoint = new WaiterCheckpoint();
		synchronized (checkpoint) {
			waiter.await( checkpoint, miliseconds );
		}
	}
}
