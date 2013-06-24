/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author zobo
 *
 */
public abstract class ReSearchAbstractPreferencesComposite extends Composite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReSearchAbstractPreferencesComposite.class);
	
	public ReSearchAbstractPreferencesComposite( final Composite parent ) {
		super(parent, SWT.NONE);
		createContents( this );
	}
	
	protected abstract void createContents( final Composite parent);
	
	public abstract String getTitle();

	public abstract void set(ResearchComponentDataContainer container);
	
	public abstract void fill(ResearchComponentDataContainer container);
}
