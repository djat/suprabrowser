/**
 * 
 */
package ss.client.ui.peoplelist;

import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.domainmodel.UserActivity;

/**
 * @author roman
 *
 */
public class LastUserActivityToolTipProviderSWT {


	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LastUserActivityToolTipProviderSWT.class);
	
	private final Table peopleTable;

	private SphereMember currentMember = null;
	
	private final ToolTipMessageBuilder messageBuilder;
	
	private final IPeopleListOwner owner;
	
	private final static ThreadPoolExecutor UPDATE_EXECUTOR = ThreadUtils.createOneByOneDemonExecutor( "LastUserActivityProcessor" );	
	/**
	 * @param peopleList
	 */
	public LastUserActivityToolTipProviderSWT(final Table peopleTable, final IPeopleListOwner owner) {
		super();
		this.owner = owner;
		this.peopleTable = peopleTable;
		this.messageBuilder = new ToolTipMessageBuilder( this.owner );
		
		
		this.peopleTable.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(org.eclipse.swt.events.MouseEvent me) {
				LastUserActivityToolTipProviderSWT.this.updateToolTipTextByCurrentMember();
			}

			public void mouseExit(org.eclipse.swt.events.MouseEvent me) {
				LastUserActivityToolTipProviderSWT.this.setCurrentMember( null );
			}

			public void mouseHover(org.eclipse.swt.events.MouseEvent me) {
				LastUserActivityToolTipProviderSWT.this.mouseMoved(me);
			}
		});
//		
//		this.peopleTable.addMouseMoveListener(new MouseMoveListener() {
//			public void mouseMove(MouseEvent me) {
//				LastUserActivityToolTipProviderSWT.this.mouseMoved(me);
//			}
//		});
	}
	
	private void mouseMoved(org.eclipse.swt.events.MouseEvent me ){
		TableItem item = this.peopleTable.getItem(new Point(me.x, me.y));
		if(item != null) {
			setCurrentMember( (SphereMember) item.getData() );
			return;
		}
		setCurrentMember( null );
	}

	/**
	 * @param elementAt
	 */
	private void setCurrentMember(SphereMember member) {
		if ( this.currentMember == member ) {
			return;
		}
		this.currentMember = member;
		LastUserActivityToolTipProviderSWT.this.updateToolTipTextByCurrentMember();
	}

	
	/**
	 * 
	 */
	protected synchronized void updateToolTipTextByCurrentMember() {
//		BlockingQueue<Runnable> queue = UPDATE_EXECUTOR.getQueue();
//		while( queue.size() > 1 ) {
//			queue.remove();
//		}
		UPDATE_EXECUTOR.execute( new UpdateToolTipWorker( getCurrentMember() ) );
	}

	/**
	 * @return the currentMember
	 */
	public SphereMember getCurrentMember() {
		return this.currentMember;
	}

	private String getToolTipForMember( SphereMember member ) {
		if ( member == null ) {
			return null;
		}
		final String contactName = member.getName();
		final String login = this.owner.getClientProtocol().getVerifyAuth().getLoginForContact(contactName);
		if ( login == null ) {
			logger.warn( "login name is null for " + contactName );
			return null;
		}		
		UserActivity presence = this.owner.getClientProtocol().getUserActivity( this.owner.getSphereIdForUserActivity(), login );
		if ( presence == null || presence.getUserLogin() == null ) {
			presence = new UserActivity();
			presence.setUserLogin(login);
			presence.setLastLoginDate( UserActivity.NEVER_DATE_TIME );
		}
		return this.messageBuilder.createForTable( presence );		
	}
	
	private class UpdateToolTipWorker implements Runnable {

		private final SphereMember currentMember;
		
		public UpdateToolTipWorker(final SphereMember currentMember) {
			super();
			this.currentMember = currentMember;
		}


		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			//Execute show "top" tool tip request 
			if ( UPDATE_EXECUTOR.getQueue().size() == 0 ) {
				final String toolTip = getToolTipForMember( this.currentMember ); 
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						LastUserActivityToolTipProviderSWT.this.peopleTable.setToolTipText(toolTip);
					}
				});
			}
		}		
	}
}
