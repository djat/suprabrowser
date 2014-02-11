package ss.lab.dm3.snapshoots;

import ss.lab.dm3.snapshoots.objects.OnlineUser;
import ss.lab.dm3.snapshoots.objects.OnlineUserList;

/**
 * @author Dmitry Goncharov
 *
 */
public class OnlineUserListProvider extends SnapshotObjectProvider<OnlineUserList> {
	
	/**
	 * @param transientObjectClazz
	 */
	public OnlineUserListProvider() {
		super(OnlineUserList.class);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.transientobjects.objs.TransientObjectProvider#provide()
	 */
	@Override
	public OnlineUserList provide() {
		OnlineUserList onlineUsers = new OnlineUserList();
		// collection connections informations
		// TODO: fix temp implementation 
		onlineUsers.add( new OnlineUser( 23L, 2 ) );
		//
		return onlineUsers;
	}

}
