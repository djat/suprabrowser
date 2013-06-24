/**
 * 
 */
package ss.client.ui.peoplelist;

import java.util.Date;
import java.util.ResourceBundle;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.common.DateUtils;
import ss.domainmodel.UserActivity;

/**
 * @author d!ma
 *
 */
public class ToolTipMessageBuilder {
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_PEOPLELIST_TOOLTIPMESSAGEBUILDER);

	private static final String LAST_LOGGED = "TOOLTIPMESSAGEBUILDER.LAST_LOGGED";
	
	private static final String LAST_OPENED = "TOOLTIPMESSAGEBUILDER.LAST_OPENED";
	
	private static final String LAST_ACTIVITY = "TOOLTIPMESSAGEBUILDER.LAST_ACTIVITY";
	
	private static final String IDLE = "TOOLTIPMESSAGEBUILDER.IDLE";
	
	private static final String NEVER_LOGIN = "TOOLTIPMESSAGEBUILDER.NEVER_LOGIN";
	
	private static final String NEVER_OPENED = "TOOLTIPMESSAGEBUILDER.NEVER_OPENED";
	
	private final StringBuilder sb = new StringBuilder();
	
	private final IPeopleListOwner owner;
	
	

	/**
	 * @param messagesPaneOwner
	 */
	public ToolTipMessageBuilder(final IPeopleListOwner owner) {
		super();
		this.owner = owner;
	}



	/**
	 * @param presence
	 * @return
	 */
	public synchronized String create(final UserActivity presence) {
		this.sb.delete( 0, this.sb.length() );
		appendln( "<html>" );
		appendPara( "<b>",this.owner.getClientProtocol().getVerifyAuth().getRealName( presence.getUserLogin() ), "</b>" );
		if ( this.owner.isRoot() ) {
			appendRow( this.bundle.getString(LAST_LOGGED), presence.hasLastLoginDate() ? presence.getLastLoginDate() : this.bundle.getString(NEVER_LOGIN) );
		}
		else {
			appendRow( this.bundle.getString(LAST_OPENED), presence.hasLastLoginDate() ? presence.getLastLoginDate() : this.bundle.getString(NEVER_OPENED) );
		}
		if ( presence.hasLastActivityDate() ) {
			appendRow( this.bundle.getString(LAST_ACTIVITY), presence.getLastActivityDate() );
		}
		if ( presence.isIdle() ) {
			appendRow( this.bundle.getString(IDLE), presence.getIdleTime() );
		}
		appendln( "</html>");
		return this.sb.toString();
	}



	/**
	 * @param string
	 */
	private void appendln(String string) {
		this.sb.append( string )
			.append("\r\n");		
	}



	/**
	 * 
	 */
	private void appendPara(String ... words ) {
		this.sb.append("<p>&nbsp;");
		for( String word : words) {
			this.sb.append( word );
		}
		this.sb.append("&nbsp;</p>")
			.append("\r\n");			
	}
	
	/**
	 * 
	 */
	private void appendRow(String caption, String message ) {
		appendPara(caption, ": ", message );
	}



	/**
	 * @param presence
	 * @return
	 */
	public synchronized String createForTable(UserActivity presence) {
		this.sb.delete( 0, this.sb.length() );
		this.sb.append(this.owner.getClientProtocol().getVerifyAuth().getRealName( presence.getUserLogin()));
		if ( this.owner.isRoot() ) {
			this.sb.append("\n"+this.bundle.getString(LAST_LOGGED)+": "+(presence.hasLastLoginDate() ? getCorrectedLoginDate(presence) : this.bundle.getString(NEVER_LOGIN) ));
		}
		else {
			this.sb.append( "\n"+this.bundle.getString(LAST_OPENED)+": "+( presence.hasLastLoginDate() ? getCorrectedLoginDate(presence) : this.bundle.getString(NEVER_OPENED)));
		}
		if ( presence.hasLastActivityDate() ) {
			this.sb.append( "\n"+this.bundle.getString(LAST_ACTIVITY)+": "+getCorrectedActivityDate(presence) );
		}
		if ( presence.isIdle() && this.owner.getClientProtocol().isMemberOnline(presence.getUserLogin())) {
			this.sb.append( "\n"+this.bundle.getString(IDLE)+": "+presence.getIdleTime());
		}
		return this.sb.toString();
	}
	
	private String getCorrectedActivityDate(UserActivity presence) {
		try {
			long millis = Long.parseLong(presence.getLastActivityDate());
			return DateUtils.dateToCanonicalString(new Date(millis - SupraSphereFrame.INSTANCE.getTimeDifference()));
		} catch (Exception ex) {
			return presence.getLastActivityDate();
		} 
	}
	
	private String getCorrectedLoginDate(UserActivity presence) {
		try {
			long millis = Long.parseLong(presence.getLastLoginDate());
			return DateUtils.dateToCanonicalString(new Date(millis - SupraSphereFrame.INSTANCE.getTimeDifference()));
		} catch (Exception ex) {
			return presence.getLastLoginDate();
		} 
	}
}
