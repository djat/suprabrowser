package ss.lab.dm3.snapshoots.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.snapshoots.SnapshotObject;

/**
 * @author Dmitry Goncharov
 *
 */
public class OnlineUserList extends SnapshotObject implements Iterable<OnlineUser> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5915590633999941777L;
	
	private final List<OnlineUser> users;

	/**
	 * @param users
	 */
	public OnlineUserList(List<OnlineUser> users) {
		super();
		this.users = users;
	}
	
	/**
	 * 
	 */
	public OnlineUserList() {
		this( new ArrayList<OnlineUser>() );
	}

	/**
	 * @param onlineUser
	 */
	public void add(OnlineUser onlineUser) {
		this.users.add( onlineUser );
	}
	
	public OnlineUser get( int index ) {
		return this.users.get(index);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<OnlineUser> iterator() {
		return this.users.iterator();
	}

	/**
	 * @return
	 */
	public int getSize() {
		return this.users.size();
	}	

}
