package ss.framework.networking2;

interface IReplyDispatcher {

	/**
	 * 
	 */
	void cannotSendInitiationCommand();

	/**
	 * @param ex
	 */
	void cannotSendInitiationCommand(InterruptedException ex);
	
	/**
	 * 
	 */
	void replyTimeOut();

	/**
	 * @return
	 */
	String getInitiationSendId();

	/**
	 * @param reply
	 */
	void dispachReply(Reply reply);

	/**
	 * @return the timeout
	 */
	int getTimeout();
	
}
