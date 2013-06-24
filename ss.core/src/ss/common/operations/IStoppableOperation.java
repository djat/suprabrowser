package ss.common.operations;

public interface IStoppableOperation extends IOperation {
 
 	/**
 	 * Stop operation execution
 	 */
	void queryBreak();
}
