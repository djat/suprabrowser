/**
 * 
 */
package ss.client.ui.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.custom.CTabItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.ListUtils;
import ss.common.UiUtils;
import ss.common.VerifyAuth;

/**
 * @author zobo
 *
 */
public class ForcedSpheresClosingProcessor {

	private static final String SPHERES_ACCESS_DENIED_INFORMATION = "FORCESPHERESCLOSINGPROCESSOR.SPHERES_ACCESS_DENIED_INFORMATION";

	private static final String THE_FOLLOWING_SPHERES_IS_NOW_DENIED_TO_ACCESS = "FORCESPHERESCLOSINGPROCESSOR.THE_FOLLOWING_SPHERES_IS_NOW_DENIED_TO_ACCESS";

	private static final String KICKED_IDENTIFIER = "FORCESPHERESCLOSINGPROCESSOR.KICKED_IDENTIFIER";
	
	private static final String COMMA = ", ";
	
    private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_PROCESSING_FORCESPHERESCLOSINGPROCESSOR);

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForcedSpheresClosingProcessor.class);
	
	public static final ForcedSpheresClosingProcessor INSTANCE = new ForcedSpheresClosingProcessor();
	
	private ForcedSpheresClosingProcessor(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void close(final List<String> spheresIds){
		if ((spheresIds == null)||(spheresIds.isEmpty())){
			if (logger.isDebugEnabled()){
				logger.debug("Close denied spheres performed with empty sphereIds list");
			}
			return;
		}
		if (logger.isDebugEnabled()){
			logger.debug("Close denied spheres performed with sphereIds list: " + ListUtils.valuesToString(spheresIds));
		}
		SupraSphereFrame sF = SupraSphereFrame.INSTANCE;
		List<String> kicked = new ArrayList<String>();
		CTabItem[] items = sF.tabbedPane.getItems();
		List<SupraCTabItem> panes = new ArrayList<SupraCTabItem>();
		List<SupraCTabItem> browsers = new ArrayList<SupraCTabItem>();
		for (CTabItem item : items){
			SupraCTabItem supraItem = (SupraCTabItem) item;
			MessagesPane pane = supraItem.getMessagesPane();
			if (pane != null){
				panes.add(supraItem);
			} else if (supraItem.getBrowserPane() != null){
				browsers.add(supraItem);
			}
		}
		for (SupraCTabItem item : browsers){
			MessagesPane pane = item.getBrowserPane().getMessagesPane();
			if ( pane != null ){
				if (spheresIds.contains(pane.getSystemName())){
					item.safeClose();
				}
			}
		}
		for (SupraCTabItem item : panes){
			String systemName = item.getMessagesPane().getSystemName();
			if (spheresIds.contains(systemName)){
				kicked.add(systemName);
				item.safeClose();
			}
		}
		if (logger.isDebugEnabled()){
			logger.debug("Was force kicked from following: " + ListUtils.valuesToString(spheresIds));
		}
		showMessage(spheresIds, kicked, sF.client.getVerifyAuth());
	}
	
	private void showMessage(List<String> allDeniedSpheres, List<String> allKickedSpheres, VerifyAuth auth){
		String message = bundle.getString(THE_FOLLOWING_SPHERES_IS_NOW_DENIED_TO_ACCESS) + "\n";
		String displayName;
		boolean commaNotPlaced = true;
		for (String system_name : allDeniedSpheres){
			displayName = auth.getDisplayName(system_name);
			if (displayName == null) {
				displayName = system_name;
			}
			if (commaNotPlaced){
				commaNotPlaced = false;
			} else {
				message += COMMA;
			}
			message += displayName;
			if (allKickedSpheres.contains(system_name)){
				message += bundle.getString(KICKED_IDENTIFIER);
			}
		}
		if (logger.isDebugEnabled()){
			logger.debug("Showing message info on denied spheres: " + message);
		}
		final String message1 = message;
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				UserMessageDialogCreator.info(message1, bundle.getString(SPHERES_ACCESS_DENIED_INFORMATION));
			}
		});
		
	}
}