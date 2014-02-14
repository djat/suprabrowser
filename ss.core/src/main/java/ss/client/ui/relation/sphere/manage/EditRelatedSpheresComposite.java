package ss.client.ui.relation.sphere.manage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereTreeContentProvider;
import ss.framework.arbitrary.change.ArbitraryChangeSet;

/**
 * 
 */
public class EditRelatedSpheresComposite extends Composite {

	private final CheckboxTreeViewer tv;
	
	private final SphereRelationModel model;
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditRelatedSpheresComposite(Composite parent, SphereRelationModel model ) {
		super(parent, SWT.NONE );
		this.model = model;
		setLayout( GridLayoutFactory.fillDefaults().create() );
		Label lblSphere = new Label( this, SWT.LEFT );
		lblSphere.setText( this.model.getSelectedShpere().getDisplayName() + Messages.getString("EditRelatedSpheresComposite.RelationSuffix") );		 //$NON-NLS-1$
		lblSphere.setLayoutData( GridDataFactory.swtDefaults().grab( true, false ).create() );		
		this.tv = new CheckboxTreeViewer( this );
		this.tv.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ManagedSphere) element).getDisplayName();
			}
		});
		this.tv.getControl().setLayoutData( GridDataFactory.fillDefaults().grab( true, true ).create() );
		this.tv.setContentProvider( new SphereTreeContentProvider() );
		this.tv.setInput( new Object[] { model.getRoot() } );
		this.tv.setCheckedElements(model.getRelatedSpheres().toArray());
		// Expand to see not root only
		this.tv.expandToLevel( 2 );
		// Expand to see all checked items
		for( ManagedSphere sphere : model.getRelatedSpheres() ) {
			final List<ManagedSphere> path = sphere.getPathAsList();
			final int pathDepth = path.size() - 1;
			for (int n = 0; n < pathDepth; n++) {
				ManagedSphere pathItem = path.get(n);
				this.tv.expandToLevel( pathItem, 1 );
			}
		}
	}
	/**
	 * 
	 */
	public ArbitraryChangeSet<String> flushEditResult() {
		Set<ManagedSphere> spheres = new HashSet<ManagedSphere>();
		for( Object obj : this.tv.getCheckedElements() ) {
			spheres.add( (ManagedSphere) obj );
		}
		return this.model.setRelatedSpheres( spheres );		
	}
	

}
