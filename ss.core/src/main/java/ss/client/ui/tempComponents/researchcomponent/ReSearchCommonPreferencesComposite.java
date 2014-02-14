/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author zobo
 *
 */
public class ReSearchCommonPreferencesComposite extends ReSearchAbstractPreferencesComposite {

	public static final String NEW_FROM_LAST_RE_SEARCH = "New from last Re-Search";

	public static final String LOOK_FOR_OTHERS_TAGS = "Look for others tags";

	public static final String LOOK_FOR_MY_TAGS = "Look for my tags";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReSearchCommonPreferencesComposite.class);
	
	private Button newFromLastResearch;

	private Button lookInOwn;

	private Button lookInOthers;
	
	private Spinner sameKeywordsMaxCount; 
	
	public ReSearchCommonPreferencesComposite( final Composite parent ) {
		super( parent );
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents( final Composite parent ) {
		parent.setLayout( new GridLayout( 2, false ) );
		
		this.newFromLastResearch = new Button( parent, SWT.CHECK );
		this.newFromLastResearch.setText( NEW_FROM_LAST_RE_SEARCH );
		this.newFromLastResearch.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2 , 1) );

		this.lookInOwn = new Button( parent, SWT.CHECK );
		this.lookInOwn.setText( LOOK_FOR_MY_TAGS );
		this.lookInOwn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2 , 1) );
		
		this.lookInOthers = new Button( parent, SWT.CHECK );
		this.lookInOthers.setText( LOOK_FOR_OTHERS_TAGS );
		this.lookInOthers.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2 , 1) );
		
		Label label = new Label( parent, SWT.LEFT );
		label.setText("Number of most recent individual tags: ");
		label.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false) );
		this.sameKeywordsMaxCount = new Spinner( parent, SWT.NONE);
		this.sameKeywordsMaxCount.setMinimum( 0 );
		this.sameKeywordsMaxCount.setMaximum(999);
		this.sameKeywordsMaxCount.setIncrement( 1 );
		this.sameKeywordsMaxCount.setSelection( 3 );
		this.sameKeywordsMaxCount.setLayoutData( new GridData(SWT.FILL, SWT.FILL, false, false) );
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#getTitle()
	 */
	@Override
	public String getTitle() {
		return "common";
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#set(ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer)
	 */
	@Override
	public void set( final ResearchComponentDataContainer container ) {
		if ( container == null ) {
			logger.error("ResearchComponentDataContainer is null");
			return;
		}
		this.lookInOthers.setSelection( container.isLookInOthers() );
		this.lookInOwn.setSelection( container.isLookInOwn() );
		this.newFromLastResearch.setSelection( container.isNewFromLastResearch() );
		this.sameKeywordsMaxCount.setSelection( container.getSameKeywordsMaxCount() );
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#fill(ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer)
	 */
	@Override
	public void fill(ResearchComponentDataContainer container) {
		if ( container == null ) {
			logger.error("ResearchComponentDataContainer is null");
			return;
		}
		container.setLookInOwn( this.lookInOwn.getSelection() );
		container.setLookInOthers( this.lookInOthers.getSelection() );
		container.setNewFromLastResearch( this.newFromLastResearch.getSelection() );
		container.setSameKeywordsMaxCount( this.sameKeywordsMaxCount.getSelection() );
	}
}
