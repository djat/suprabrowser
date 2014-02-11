package ss.lab.dm3.persist.workers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.persist.DomainLoader;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.utils.CantCreateObjectException;

/**
 * @author Dmitry Goncharov
 */
public class DomainWorkerExecutor<T extends DomainWorkerContext> {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(DomainWorkerExecutor.class);

	private final T context;

	private final Domain domain;

	private final List<DomainWorker<T>> workers = new ArrayList<DomainWorker<T>>();

	/**
	 * @param context
	 */
	public DomainWorkerExecutor(T context) {
		super();
		this.context = context;
		this.domain = context.getDomain();
	}

	public DomainWorkerExecutor<T> add(DomainWorkerHost host, String methodName) {
		DomainWorkerHostProxy<T> proxy = createDomainWorker(host, methodName);
		return add(proxy);
	}
	
	public DomainWorkerExecutor<T> addInTransaction(DomainWorkerHost host, String methodName) {
		DomainWorkerHostProxy<T> proxy = createDomainWorker(host, methodName);
		proxy.setInTransaction( true );
		return add(proxy);
	}


	/**
	 * @param host
	 * @param methodName
	 * @return
	 */
	private DomainWorkerHostProxy<T> createDomainWorker(DomainWorkerHost host, String methodName) {
		Method method = findMethod(host, methodName);
		if (method == null) {
			throw new CantFindMethodException(host, methodName);
		}
		DomainWorkerHostProxy<T> proxy = new DomainWorkerHostProxy<T>(host,
				method);
		return proxy;
	}

	/**
	 * @param host
	 * @param method
	 * @return
	 */
	private Method findMethod(DomainWorkerHost host, String methodName) {
		Method[] methods = host.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

	public DomainWorkerExecutor<T> add(
			Class<? extends DomainWorker<T>> workerClazz) {
		final DomainWorker<T> createWorker = createWorker(workerClazz);
		return add(createWorker);
	}

	/**
	 * @param worker
	 * @return
	 */
	public DomainWorkerExecutor<T> add(final DomainWorker<T> worker) {
		this.getWorkers().add(worker);
		return this;
	}

	/**
	 * 
	 */
	public void execute(final ICallbackHandler handler) {
		Thread executionThread = new Thread(new Runnable() {
			public void run() {
				try {
					DomainWorkReport report = new DomainWorkReport();
					executeWorkers(getWorkers(), report);
					report.finish();
					handler.onSuccess(report);
				} catch (Throwable ex) {
					log.error("Domain worker executor failed", ex);
					handler.onFail(ex);
				}
			}
		});
		executionThread.start();
	}

	private DomainWorker<T> createWorker(
			Class<? extends DomainWorker<T>> workerClazz) {
		try {
			return workerClazz.newInstance();
		} catch (InstantiationException ex) {
			throw new CantCreateObjectException(workerClazz, ex);
		} catch (IllegalAccessException ex) {
			throw new CantCreateObjectException(workerClazz, ex);
		}
	}

	/**
	 * @param workers
	 * 
	 */
	protected void executeWorkers(List<DomainWorker<T>> workers,
			final DomainWorkReport report) {
		for (final DomainWorker<T> worker : workers) {
			final AtomicReference<CallbackResultWaiter> waiterRef = new AtomicReference<CallbackResultWaiter>();
			final AtomicReference<Object> resultRef = new AtomicReference<Object>();
			report.beginWorker(worker);
			this.domain.execute(new Runnable() {
				public void run() {
					Object continueObject = worker.run(getContext());
					resultRef.set(continueObject);
					waiterRef.set(createWaiter(worker, continueObject));
				}
			});
			CallbackResultWaiter waiter = waiterRef.get();
			if (waiter != null) {
				waiter.waitToResult();
			}
			report.endWorker(resultRef.get());
		}
	}

	/**
	 * @param ret
	 */
	private CallbackResultWaiter createWaiter(DomainWorker<T> worker,
			Object ret) {
		if (DomainWorker.NOOP == ret) {
			return null;
		} else {
			if (ret instanceof CallbackResultWaiter) {
				return (CallbackResultWaiter) ret;
			} else {
				final CallbackResultWaiter resultHandler = createCallbackResponseWaiter(ret);
				if (ret instanceof DomainLoader) {
					((DomainLoader) ret).beginLoad(this.domain, resultHandler);
				} else if (ret instanceof Transaction) {
					((Transaction) ret).beginCommit(resultHandler);
				} else {
					throw new InvalidDomainWorkerResultException(worker, ret);
				}
				return resultHandler;
			}
		}
	}

	/**
	 * @return
	 */
	private CallbackResultWaiter createCallbackResponseWaiter(Object service) {
		return new CallbackResultWaiter(service);
	}

	/**
	 * @return
	 */
	public static DomainWorkerExecutor<DomainWorkerContext> create(Domain domain) {
		final DomainWorkerContext context = new DomainWorkerContext(domain);
		return new DomainWorkerExecutor<DomainWorkerContext>(context);
	}

	/**
	 * @return the context
	 */
	public T getContext() {
		return this.context;
	}

	/**
	 * @return the workers
	 */
	private List<DomainWorker<T>> getWorkers() {
		return this.workers;
	}

}
