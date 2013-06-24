/**
 * 
 */
package ss.server.debug.ssrepair;

import java.util.Set;

import ss.common.StringUtils;
import ss.domainmodel.SupraSphereStatement;
import ss.server.db.DbUtils;

/**
 *
 */
public abstract class AbstractRepairer {
	
	protected final Context context;

	/**
	 * @param context
	 */
	public AbstractRepairer(final Context context) {
		super();
		this.context = context;
	}
	
	/**
	 * @return
	 */
	protected final SupraSphereStatement getSupraSphere() {
		return this.context.getSupraSphere();
	}
	
	/**
	 * @param title 
	 * @param sphereIds
	 * @return
	 */
	protected final String formatSelectSphereDefinitionQuery(String title, final Set<String> sphereIds) {
		if ( sphereIds.size() > 0 ) {
			final StringBuilder sb = new StringBuilder();
			sb.append( title ).append( StringUtils.getLineSeparator() );
			sb.append( "SELECT `xmldata` FROM `supraspheres` WHERE `type` = 'sphere'" );
			sb.append( " and (" );
			boolean first = true;
			for( String sphereId : sphereIds ) {
				if ( !first ) {
					sb.append( " or " );
				}
				else {
					first = false;
				}
				sb.append( "`xmldata` " + DbUtils.likeXmlInlineAttribute( "system_name", sphereId ) );
			}
			sb.append( ")" );
			return sb.toString();
		}
		else {
			return title + StringUtils.getLineSeparator() + "NO SPHERES FOUND";
		}
	}
	
	public final void repair() {
		prepareToRepair();
		performRepair();
	}

	/**
	 * 
	 */
	protected abstract void prepareToRepair();

	/**
	 * 
	 */
	protected abstract void performRepair();
	
}
