/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.spheremanagement.LayoutUtils;

/**
 * @author zobo
 *
 */
public class ReSearchPreferencesWindow {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReSearchPreferencesWindow.class);
	
	private class ReSearchPreferencesInternalWindow extends Window {

		private static final String RE_SEARCH_PREFERENCES = "Re-Search preferences";
		
		private List<ReSearchAbstractPreferencesComposite> items;

		private ResearchComponentDataContainer container;

		public ReSearchPreferencesInternalWindow( final Shell shell, final ResearchComponentDataContainer container ) {
			super( shell );
			this.container = container;
		}

		private void organizeControls( final ResearchComponentDataContainer container ) {
			if ( container == null ) {
				logger.error("Items have not been organized, DataContainer is null");
				return;
			}
			for ( ReSearchAbstractPreferencesComposite component : this.items ) {
				component.set( container );
			}
		}

		@Override
		protected void configureShell( final Shell shell ) {
			super.configureShell( shell );
			shell.setText(RE_SEARCH_PREFERENCES);
		}

		@Override
		protected Control createContents( Composite parent ) {
			parent.setLayout( new GridLayout() );
			
			createTabFolder( parent ).setLayoutData( LayoutUtils.createFullFillGridData() );
			
			createButtonsPanel( parent ).setLayoutData( LayoutUtils.createFillHorizontalGridData() );
			
			organizeControls( this.container );
			
			return parent;
		}

		private Control createButtonsPanel( final Composite parent ) {
			final Composite buttonsComposite = new Composite( parent, SWT.NONE );
			buttonsComposite.setLayout( new GridLayout(2, false) );
			final Button apply = new Button( buttonsComposite, SWT.PUSH );
			apply.setText( "Apply" );
			apply.setLayoutData( new GridData(SWT.END, SWT.BEGINNING, true, true ) );
			apply.addSelectionListener( new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					applyPerformed();
				}
				
			});
			
			final Button cancel = new Button( buttonsComposite, SWT.PUSH );
			cancel.setText( "Cancel" );
			cancel.setLayoutData( new GridData(SWT.END, SWT.BEGINNING, false, true ) );
			cancel.addSelectionListener( new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					ReSearchPreferencesInternalWindow.this.close();
				}
				
			});
			
			return buttonsComposite;
		}
		
		private void applyPerformed() {
			if (logger.isDebugEnabled()) {
				logger.debug("Apply performed");
			}
			final ResearchComponentDataContainer container = new ResearchComponentDataContainer();
			for (ReSearchAbstractPreferencesComposite item : this.items) {
				item.fill( container );
			}
			if (logger.isDebugEnabled()) {
				logger.debug("New Data: " + container.toString());
			}
			ResearchInfoController.INSTANCE.setDataProvider( container );
			close();
		}

		private Control createTabFolder( final Composite parent ) {
			final CTabFolder folder = new CTabFolder( parent , SWT.NONE );
			folder.setLayout( LayoutUtils.createFullFillGridLayout() );
			
			this.items = new ArrayList<ReSearchAbstractPreferencesComposite>();
			this.items.add( new ReSearchCommonPreferencesComposite( folder ) );
			this.items.add( new ReSearchUsersPreferencesComposite( folder ) );
			this.items.add( new ReSearchAdditionalPreferencesComposite( folder ) );
			
			for ( ReSearchAbstractPreferencesComposite component : this.items ) {
				createItem( folder, component );
			}
			folder.setSelection( 0 );
			
			return folder;
		}
		
		private void createItem( final CTabFolder folder, final ReSearchAbstractPreferencesComposite component ){
			final CTabItem item = new CTabItem( folder, SWT.CENTER );
			item.setText( component.getTitle() );
			item.setControl( component );
		}

		@Override
		protected Point getInitialLocation( Point point ) {
			return super.getInitialLocation( point );
		}

		@Override
		protected Point getInitialSize() {
			return new Point( 400, 300 );
		}

		@Override
		protected void setShellStyle( int style ) {
			super.setShellStyle( style | SWT.CLOSE | SWT.MAX | SWT.MIN);
		}
	}

	private final ReSearchPreferencesInternalWindow window;
	
	private final ReSearchToolItemComponent component;
	
	public ReSearchPreferencesWindow( final Shell shell, final ReSearchToolItemComponent component ){
		this.component = component;
		this.window = new ReSearchPreferencesInternalWindow( shell, ResearchInfoController.INSTANCE.getDataProvider() );
	}
	
	public int open(){
		return this.window.open();
	}
}
