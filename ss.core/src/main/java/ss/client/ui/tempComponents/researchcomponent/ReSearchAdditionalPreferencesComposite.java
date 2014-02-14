/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import ss.client.ui.spheremanagement.LayoutUtils;

/**
 * @author zobo
 *
 */
public class ReSearchAdditionalPreferencesComposite extends
		ReSearchAbstractPreferencesComposite {

	private Button contactsAsKeywords;
	
	private Button useRecentData;

	private Composite recentComposite;

	private Spinner numberRecentTags;

	private Spinner numberRecentSpheres;

	/**
	 * @param parent
	 */
	public ReSearchAdditionalPreferencesComposite( final Composite parent ) {
		super( parent );
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents( final Composite parent ) {
		parent.setLayout( new GridLayout() );
		
		this.contactsAsKeywords = new Button( parent, SWT.CHECK );
		this.contactsAsKeywords.setText( "Consider contacts as Keywords?" );
		this.contactsAsKeywords.setLayoutData( LayoutUtils.createFillHorizontalGridData() );
		
		this.useRecentData = new Button( parent, SWT.CHECK );
		this.useRecentData.setText( "Use recent preferences" );
		this.useRecentData.setLayoutData( LayoutUtils.createFillHorizontalGridData() );
		this.useRecentData.addSelectionListener(new SelectionListener(){
			
			final ReSearchAdditionalPreferencesComposite research = ReSearchAdditionalPreferencesComposite.this;

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				this.research.recentComposite.setEnabled( 
						this.research.useRecentData.getSelection() );
			}
			
		});

		createRecentComposite( parent ).setLayoutData(LayoutUtils.createFullFillGridData());
	}

	/**
	 * @param parent
	 */
	private Control createRecentComposite(Composite parent) {
		this.recentComposite = new Composite( parent, SWT.BORDER );
		this.recentComposite.setLayout( new GridLayout(2, false) );
		
		Label label = new Label( this.recentComposite, SWT.LEFT );
		label.setText("Number of most recent individual tags: ");
		label.setLayoutData( LayoutUtils.createFillHorizontalGridData() );
		this.numberRecentTags = new Spinner( this.recentComposite, SWT.NONE );
		this.numberRecentTags.setMinimum( 0 );
		this.numberRecentTags.setMaximum(999);
		this.numberRecentTags.setIncrement( 1 );
		this.numberRecentTags.setSelection( 10 );
		this.numberRecentTags.setLayoutData( new GridData(SWT.FILL, SWT.FILL, false, false) );
		
		label = new Label( this.recentComposite, SWT.LEFT );
		label.setText("Number of most recently opened spheres: ");
		label.setLayoutData( LayoutUtils.createFillHorizontalGridData() );
		this.numberRecentSpheres = new Spinner( this.recentComposite, SWT.NONE );
		this.numberRecentSpheres.setMinimum( 0 );
		this.numberRecentSpheres.setMaximum(999);
		this.numberRecentSpheres.setIncrement( 1 );
		this.numberRecentSpheres.setSelection( 10 );
		this.numberRecentSpheres.setLayoutData( new GridData(SWT.FILL, SWT.FILL, false, false) );

		return this.recentComposite;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#fill(ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer)
	 */
	@Override
	public void fill( final ResearchComponentDataContainer container ) {
		if (container == null) {
			return;
		}
		container.setContactsAsKeywords( this.contactsAsKeywords.getSelection() );
		container.setUseRecent(this.useRecentData.getSelection());
		container.setNumberRecentTags(this.numberRecentTags.getSelection());
		container.setNumberRecentSpheres(this.numberRecentSpheres.getSelection());
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Additional";
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.researchcomponent.ReSearchAbstractPreferencesComposite#set(ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer)
	 */
	@Override
	public void set(ResearchComponentDataContainer container) {
		if (container == null) {
			return;
		}
		this.contactsAsKeywords.setSelection( container.isContactsAsKeywords() );
		this.numberRecentTags.setSelection( container.getNumberRecentTags() );
		this.numberRecentSpheres.setSelection( container.getNumberRecentSpheres() );
		this.useRecentData.setSelection( container.isUseRecent() );
		this.recentComposite.setEnabled( container.isUseRecent() );
	}
}
