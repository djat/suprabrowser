package ss.client.ui.relation.sphere.manage;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ss.client.networking.protocol.actions.SaveSphereRelationsCommand;
import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.SphereStatement;
import ss.framework.arbitrary.change.ArbitraryChangeSet;

/**  
 *
 */
public class EditRelatedSpheresDialog extends Dialog {
	
	private final SphereRelationModel model;
	
	private EditRelatedSpheresComposite cmpEditRelatedSpheres;
	
	/**
	 * @param parentShell
	 */
	public EditRelatedSpheresDialog(Shell parent, SphereRelationModel model) {
		super(parent );
		this.model = model;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText( Messages.getString("EditRelatedSpheresDialog.Title") ); //$NON-NLS-1$
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);		
		this.cmpEditRelatedSpheres = new EditRelatedSpheresComposite( area, this.model );
		this.cmpEditRelatedSpheres.setLayoutData( GridDataFactory.fillDefaults().grab( true, true ).create() );
		return area;
	}

	@Override
	protected Point getInitialSize() {
		return new Point( 640, 480 );
	}

	@Override
	protected void okPressed() {
		// Update sphere statement
		ArbitraryChangeSet<String> changeSet = this.cmpEditRelatedSpheres.flushEditResult();
		// Call server to save update
		final SphereStatement sphere = this.model.getSelectedShpere().getStatement();
		SaveSphereRelationsCommand command = new SaveSphereRelationsCommand( sphere.getSystemName(), sphere.getRelations(), changeSet );
		command.beginExecute( SupraSphereFrame.INSTANCE.client );
		super.okPressed();
	}
	
}