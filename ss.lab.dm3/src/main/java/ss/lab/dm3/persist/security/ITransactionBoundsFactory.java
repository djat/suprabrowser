package ss.lab.dm3.persist.security;

import java.io.Serializable;

/**
 * 
 * Provides transaction bounds
 * 
 * @author Dmitry Goncharov
 *
 */
public interface ITransactionBoundsFactory extends Serializable {

	TransactionBounds createTransactionBounds();
	
}
