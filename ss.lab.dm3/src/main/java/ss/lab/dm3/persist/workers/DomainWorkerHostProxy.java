/**
 * 
 */
package ss.lab.dm3.persist.workers;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.utils.ReflectionHelper;

/**
 * @author Dmitry Goncharov
 */
public class DomainWorkerHostProxy<T extends DomainWorkerContext> extends DomainWorker<T> {
	
	private final DomainWorkerHost host;
	
	private final Method method;

	private boolean inTransaction = false;
	/**
	 * @param host
	 * @param method
	 */
	public DomainWorkerHostProxy(DomainWorkerHost host, Method method) {
		super();
		this.host = host;
		this.method = method;
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.transactiontest.DomainWorker#run(ss.lab.dm3.transactiontest.DomainWorkerContext)
	 */
	@Override
	public Object run(T context) {
		if ( this.inTransaction ) {
			if ( !isVoidReturnType() ) {
				this.log.warn( "Return value will be ignored" );
			}
			final Transaction tx = context.getDomain().beginTrasaction();
			try {
				invoke( context );
				return tx;
			}
			catch(RuntimeException ex ) {
				tx.dispose();
				throw ex;
			}
		}
		else {
			final Object invokeResult = invoke( context );
			if ( isVoidReturnType() ) {
				return NOOP;
			}
			else {
				if ( invokeResult == null ) {
					throw new InvalidDomainWorkerResultException( this, invokeResult );
				}
				return invokeResult;
			}
		}
	}

	/**
	/**
	 * @param context
	 * @return
	 */
	private Object invoke(T context) {
		Class<?>[] paramenterClazzes = this.method.getParameterTypes();
		if ( paramenterClazzes.length > 1 ) {
			throw new CantInvokeMethodException( this.method );
		}
		if ( paramenterClazzes.length == 1 ) {
			final Class<?> parameterClazz = paramenterClazzes[ 0 ];
			final Class<? extends DomainWorkerContext> contextClazz = context.getClass();
			if ( parameterClazz.isAssignableFrom( contextClazz ) ) {
				return ReflectionHelper.invoke( this.host, this.method, context );				
			}
			else if ( parameterClazz.isAssignableFrom( Domain.class ) ) {
				return ReflectionHelper.invoke( this.host, this.method, context.getDomain() );
			}
			else {
				throw new CantInvokeMethodException( this.method, contextClazz, parameterClazz );
			}
		}
		else {
			return ReflectionHelper.invoke( this.host, this.method );
		}
	}
	
	/**
	 * @return
	 */
	private boolean isVoidReturnType() {
		final Class<?> returnType = this.method.getReturnType();
		return returnType == void.class;
	}
	
	public boolean isInTransaction() {
		return this.inTransaction;
	}

	public void setInTransaction(boolean inTransaction) {
		this.inTransaction = inTransaction;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "host", this.host )
		.append( "method", this.method )
		.toString();
	}
	
}
