/**
 * 
 */
package ss.client.ui.root;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.actions.MoveSphereCommand;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereTreeComposite;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.UiUtils;
import ss.framework.networking2.Command;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.ReplyHandler;
import ss.framework.networking2.SuccessReply;

/**
 * @author zobo
 *
 */
public class SelectDestinationSphereDialog {
	
	public interface SelectDestinationSphereDialogListener {
		
		public void sphereMoved( String targedSphereId );
		
		public void operationCanceled();
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SelectDestinationSphereDialog.class);
	
	private Shell shell;
	
	private List<SelectDestinationSphereDialogListener> listeners = new ArrayList<SelectDestinationSphereDialogListener>();
	
	private final ManagedSphere source;
	
	private final ISphereDefinitionProvider provider;

	private SphereTreeComposite treeComposite;
	
	private final DialogsMainCli cli;
	
	public SelectDestinationSphereDialog( final ManagedSphere source, final ISphereDefinitionProvider provider, final DialogsMainCli cli ){
		if ( source == null ) {
			throw new NullPointerException("Source ManagedSphere can not be null");
		}		
		if ( provider == null ) {
			throw new NullPointerException("SphereDefinitionProvider can not be null");
		}
		if ( cli == null ) {
			throw new NullPointerException("DialogsMainCli can not be null");
		}
		this.cli = cli;
		this.source = source;
		this.provider = provider;
	}
	
	public void open( final Shell parent ){
		if ( parent == null ) {
			throw new NullPointerException("Parent shell can not be null");
		}
		create( parent );
		this.shell.open();
	}
	
	private void create( final Shell parent ){
		this.shell = new Shell( parent );
		
		this.shell.setSize(400, 400);
		
		this.shell.setText("Select Target sphere");
		
		this.shell.setLayout(new GridLayout());
		createContents( this.shell );
		
		this.shell.layout();
		centerComponent( this.shell, parent );
	}
	
	private void createContents( final Composite parent ){
		RootSphereManager manager = new RootSphereManager(this.provider);
		this.treeComposite = new SphereTreeComposite(parent,
				manager, SWT.NONE);
		this.treeComposite.addHeader();
		this.treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Composite buttons = new Composite( parent, SWT.NONE );
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttons.setLayout(new GridLayout(3, false));
		
		LayoutUtils.addSpacer(buttons);
		final Button ok = new Button(buttons, SWT.PUSH);
		ok.setText("OK");
		ok.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		ok.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				performedOK();
			}
			
		});
		final Button cancel = new Button(buttons, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		cancel.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				performedCancel();
			}
			
		});
	}
	
	private void performedOK(){
		final ManagedSphere sphere = this.treeComposite.getSelected();
		if ( sphere == null ) {
			UserMessageDialogCreator.warning("Select Targed sphere please", "Sphere not selected");
			return;
		}
		if ( !checkAllowed( this.source,sphere ) ) {
			UserMessageDialogCreator.warning("Selected Targed sphere is not allowed", "Target not allowed");
			return;
		}
		final MoveSphereCommand command = new MoveSphereCommand();
		command.setSourceSphereId(this.source.getId());
		command.setTargetSphereId(sphere.getId());
		command.beginExecute(this.cli, new ReplyHandler(){

			@Override
			protected void commandSuccessfullyExecuted(Command command,
					SuccessReply successReply) {
				super.commandSuccessfullyExecuted(command, successReply);
				UiUtils.swtBeginInvoke(new Runnable(){

					public void run() {
						SelectDestinationSphereDialog.this.shell.close();
						for ( SelectDestinationSphereDialogListener listener : SelectDestinationSphereDialog.this.listeners ) {
							listener.sphereMoved( sphere.getId() );
						}										
					}
					
				});
			}

			@Override
			protected void exeptionOccured(CommandExecuteException exception)
					throws CommandHandleException {
				super.exeptionOccured(exception);
				UserMessageDialogCreator.error("Error on server, operation canceled", "Server error");
				performedCancel();
			}
			
		});
	}
	
	/**
	 * @param source2
	 * @param sphere
	 * @return
	 */
	private boolean checkAllowed( final ManagedSphere source, final ManagedSphere target) {
		ManagedSphere localTarget = target;
		while ( !localTarget.getId().equals(source.getId()) ) {
			localTarget = localTarget.getParent();
			if ( (localTarget == null) || (localTarget == target) ) {
				return true;
			}
		}
		return false;
	}

	private void performedCancel(){
		this.shell.close();
		for ( SelectDestinationSphereDialogListener listener : this.listeners ) {
			listener.operationCanceled();
		}
	}
	
	private void centerComponent( final Shell comp, final Shell parent ) {

		final Monitor primary = parent.getDisplay().getPrimaryMonitor();
		final Rectangle bounds = primary.getBounds();

		final Rectangle rect = comp.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		comp.setLocation(x, y);
	}
	
	public void addListener( final SelectDestinationSphereDialogListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("listener can not be null");
		}
		synchronized (this.listeners) {
			this.listeners.add( listener );
		}
	}
	
	public void removeListener( final SelectDestinationSphereDialogListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("listener can not be null");
		}
		synchronized (this.listeners) {
			this.listeners.remove( listener );
		}
	}
}
