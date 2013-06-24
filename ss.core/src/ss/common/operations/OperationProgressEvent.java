package ss.common.operations;

import java.util.EventObject;

public class OperationProgressEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5546987116545003840L;

	private String message;
	
	private double progress;	

	/**
	 * @param source
	 */
	public OperationProgressEvent(IOperation operation ) {
		this(operation, "", -1 );
	}

	/**
	 * @param operationSender
	 * @param message
	 * @param progress
	 */
	public OperationProgressEvent(IOperation operation, String message, double progress) {
		super( operation );
		this.message = message;
		this.progress = progress;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the progress
	 */
	public double getProgress() {
		return this.progress;
	}

	/**
	 * Returns true if event has progress information 
	 * @return
	 */
	public boolean hasProgress() {
		return this.progress >= 0;
	}
	
	
}
