/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class SphereHierarchyItemListener implements SelectionListener {
	
//	private static boolean loading = false;

//	private DownloadProgressBar bar;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereHierarchyItemListener.class);
	
	
	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent arg0) {
		/*if (SphereHierarchyDialog.INSTANCE == null){
			if (loading){
				return;
			}
			loading = true;
			if (logger.isDebugEnabled()) {
				logger.debug("Creating Download progress bar for sphere hierarchy");
			}
			this.bar = new DownloadProgressBar( "Loading Hierarchy" , SWT.TOP );
			Thread t = new Thread(){
				@Override
				public void run() {
					loadData();
				}
			};
			ThreadUtils.start(t, "loading sphere hierarchy");
		} else {
			SphereHierarchyDialog.INSTANCE.focus();
		}*/
		SupraSphereFrame.INSTANCE.initializeRootTab();
	}

//	private void loadData(){
//		final HierarchyDialogDefinitionProvider provider = new HierarchyDialogDefinitionProvider(SupraSphereFrame.INSTANCE.client);
//		provider.initialize();
//		if (logger.isDebugEnabled()) {
//			logger.debug("Destroying Download progress bar for sphere hierarchy");
//		}
//		SphereHierarchyItemListener.this.bar.destroyDownloadBar();
//		SDisplay.display.async(new Runnable() {
//			public void run() {
//				//new SphereHierarchyComposite(provider);	
//				loading = false;
//			}
//		});	
//	}
}
