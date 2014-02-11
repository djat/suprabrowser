package ss.lab.dm3.testsupport.service;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.connection.service.ServiceBackEnd;

public class TestServiceBackEnd extends ServiceBackEnd implements TestService {

	private List<String> messages = new ArrayList<String>();
	
	public int getMessagesCount() {
		return this.messages.size();
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.testsupport.service.TestService#sendMessage(java.lang.String)
	 */
	public synchronized void sendMessage(String text) {
		this.messages.add(text);
	}

	public List<String> getMessages() {
		return this.messages;
	}
	
}
