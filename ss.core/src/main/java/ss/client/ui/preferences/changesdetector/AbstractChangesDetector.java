/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import java.util.ResourceBundle;

import org.eclipse.swt.widgets.Display;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningOkCancelNoDialogListener;

/**
 * @author roman
 *
 */
public abstract class AbstractChangesDetector implements IChangesDetector {

	protected static final String EMAIL_ALIASES = "ABSTRACTCHANGESDETECTOR.EMAIL_ALIASES";
	
	protected static final String FORWARDING_RULES = "ABSTRACTCHANGESDETECTOR.FORWARDING_RULES";
	
	protected static final String USER_PREFERENCES = "ABSTRACTCHANGESDETECTOR.USER_PREFERENCES";
	
	protected static final String SPHERE_PREFERENCES = "ABSTRACTCHANGESDETECTOR.SPHERE_PREFERENCES";
	
	protected static final String SPHERE_DELIVERY = "ABSTRACTCHANGESDETECTOR.SPHERE_DELIVERY";
	
	protected static final String MEMBER_ACCESS = "ABSTRACTCHANGESDETECTOR.MEMBER_ACCESS";
	
	private boolean isLocalTransit = true;
	
	private boolean hasChanges = false;
	
	protected final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_CHANGESDETECTOR_ABSTRACTCHANGESDETECTOR); 
	
	abstract public void collectChangesAndUpdate();
	
	abstract public void rollbackChanges();

	public boolean hasChanges() {
		return this.hasChanges;
	}

	public boolean isLocalTransit() {
		return this.isLocalTransit;
	}

	public void setChanged(boolean value) {
		this.hasChanges = value;
	}

	public void setIsLocalTransit(boolean value) {
		this.isLocalTransit = value;
	}

	public void showDialog(final IChangable changable) {
		WarningOkCancelNoDialogListener listener = new WarningOkCancelNoDialogListener() {
			public void performCancel() {
				processCancel(changable);
			}

			public void performNO() {
				processNo(changable);
			}

			public void performOK() {
				processYes(changable);
			}
		};
		UserMessageDialogCreator.warningManageSpheresAccess(getWarningString(), listener);
	}

	
	public void showDisposingDialog(final IChangable currentChangable) {
		WarningOkCancelNoDialogListener listener = new WarningOkCancelNoDialogListener() {
			public void performCancel() {
				processCancel(currentChangable);
			}

			public void performNO() {
				processNo(currentChangable);
				Display.getDefault().getActiveShell().dispose();
				Display.getDefault().getActiveShell().dispose();
			}

			public void performOK() {
				processYes(currentChangable);
				Display.getDefault().getActiveShell().dispose();
				Display.getDefault().getActiveShell().dispose();
			}
		};
		UserMessageDialogCreator.warningManageSpheresAccess( getWarningString(), listener);
	}

	/**
	 * @return
	 */
	abstract protected String getWarningString();

	private void processCancel(final IChangable changable) {
		if(!isLocalTransit()) {
			return;
		}
		changable.revertSelection();
	}

	private void processNo(final IChangable changable) {
		rollbackChanges();
		if(!isLocalTransit()) {
			changable.jumpToNextItem();
		} else {
			changable.performFinalAction();
		}
	}

	private void processYes(final IChangable changable) {
		collectChangesAndUpdate();
		if(isLocalTransit()) {
			changable.performFinalAction();
		} else {
			changable.jumpToNextItem();
		}
	}
}
