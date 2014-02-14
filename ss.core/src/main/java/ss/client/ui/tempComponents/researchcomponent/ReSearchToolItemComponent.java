/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.browser.SupraBrowser;
import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public class ReSearchToolItemComponent {
	
	private ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_RESEARCHTOOLITEM);
	
	private static final String RESEARCH = "RESEARCHCOMPONENT.RESEARCH";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReSearchToolItemComponent.class);
	
	private ToolItem item;

	private ResearchGUIInfoProvider reseachState;
	
	private SupraBrowser mb;

	private ReSearchAction actionListener;
	
	public ReSearchToolItemComponent( final ToolBar toolBar ){
		createReSearchItem( toolBar );
		ResearchInfoController.INSTANCE.register( this );
	}

	private void createReSearchItem( final ToolBar toolBar ) {
		this.item = new ToolItem( toolBar, SWT.DROP_DOWN );
		this.item.setText(this.bundle.getString(RESEARCH));
		this.item.setEnabled(false);

		final Menu dropDownMenu = new Menu( toolBar.getShell(), SWT.POP_UP );
		this.item.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					final Rectangle rect = ReSearchToolItemComponent.this.item.getBounds();
					dropDownMenu.setLocation( 
							toolBar.toDisplay( new Point(rect.x, rect.y + rect.height) ) );
					dropDownMenu.setVisible(true);
				} else {
					if (ReSearchToolItemComponent.this.actionListener != null) {
						ReSearchToolItemComponent.this.actionListener.action();
					}
				}
			}

		});
		this.item.addDisposeListener(new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
				ResearchInfoController.INSTANCE.unregister( ReSearchToolItemComponent.this );
			}
			
		});
		
		final MenuItem lookInOwnMenuItem = new MenuItem( dropDownMenu, SWT.CHECK );
		lookInOwnMenuItem.setText( ReSearchCommonPreferencesComposite.LOOK_FOR_MY_TAGS );
		
		final MenuItem lookInOthersMenuItem = new MenuItem( dropDownMenu, SWT.CHECK );
		lookInOthersMenuItem.setText( ReSearchCommonPreferencesComposite.LOOK_FOR_OTHERS_TAGS );
		
		final MenuItem newFromLastResearch = new MenuItem( dropDownMenu, SWT.CHECK );
		newFromLastResearch.setText( ReSearchCommonPreferencesComposite.NEW_FROM_LAST_RE_SEARCH );

		new MenuItem( dropDownMenu, SWT.SEPARATOR );
		
		final MenuItem advancedSettingsMenuItem = new MenuItem( dropDownMenu, SWT.PUSH );
		advancedSettingsMenuItem.setText("Advanced properties...");
		advancedSettingsMenuItem.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			
			}

			public void widgetSelected(SelectionEvent e) {
				final ReSearchPreferencesWindow window = new ReSearchPreferencesWindow(
						ReSearchToolItemComponent.this.item.getParent().getShell(),
						ReSearchToolItemComponent.this);
				window.open();
			}
			
		});
		this.reseachState = new ResearchGUIInfoProvider( lookInOwnMenuItem, lookInOthersMenuItem, newFromLastResearch );
		ResearchInfoController.INSTANCE.fillInitialProvider( this.reseachState );
	}
	
	public ResearchGUIInfoProvider getReseachState() {
		return this.reseachState;
	}
	
	public void activate( final SupraBrowser mb ){
		this.mb = mb;
		this.actionListener = new ReSearchAction( this );
		this.item.setEnabled(true);
	}

	public SupraBrowser getBrowser() {
		return this.mb;
	}
	
	public void setEnabled( final boolean enabled ){
		this.item.setEnabled( enabled );
	}

	public void setToolTipText( final String str ) {
		if (StringUtils.isBlank( str )) {
			logger.error("Tool tip text is blank");
			return;
		}
		this.item.setToolTipText( str );
	}

	public void setImage( final Image researchIcon ) {
		if ( researchIcon == null) {
			logger.error("ResearchIcon is null");
			return;
		}
		this.item.setImage( researchIcon );
	}
	
	public boolean isDead(){
		if ((this.item == null) || (this.item.isDisposed())){
			return true;
		}
		return false;
	}
}
