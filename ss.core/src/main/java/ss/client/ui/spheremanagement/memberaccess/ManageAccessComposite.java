/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.ui.preferences.ManagePreferencesCommonShell;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.preferences.changesdetector.MemberAccessChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.memberaccess.bymembers.ByMembersAccessManagerComposite;
import ss.client.ui.spheremanagement.memberaccess.byspheres.BySpheresAccessManagerComposite;

/**
 *
 */
public class ManageAccessComposite extends Composite implements IChangable {

	private static final String SHOW_MEMBER_ACCESS_BY = "Show member acccess by";
	
	private static final String MEMBERS = "members";
	
	private static final String SPHERES = "spheres";
	
	private AccessView viewDeterminator = null;
	
	private IChangesDetector detector;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ManageAccessComposite.class);
	
	enum AccessView {
		BY_SPHERES,
		BY_MEMBERS,
		NONE
	}
	private final MemberAccessManager manager;
	private Button byMembers;
	private AbstractAccessManagerComposite accessComposite;
	private final SelectionListener showAccessByListener = new ShowAccessByListener();

	private Button bySpheres;
	
	private ManagePreferencesCommonShell commonShell;

	/**
	 * 
	 */
	public ManageAccessComposite(ManagePreferencesCommonShell commonShell) {
		this( commonShell.getTabFolder(), commonShell.getController().getMemberAccessManager(), commonShell );
	}
	
	/**
	 * 
	 */
	public ManageAccessComposite(Composite parent, MemberAccessManager manager, ManagePreferencesCommonShell commonShell ) {
		super(parent, SWT.BORDER);
		this.commonShell = commonShell;
		this.manager = manager;
		this.detector = new MemberAccessChangesDetector(this.manager);
		createContent();
	}

	/**
	 * 
	 */
	private void createContent() {
		setLayout( LayoutUtils.createNoMarginGridLayout( 1 ) );
		Composite viewSelectorPane = new Composite( this, SWT.NONE );
		viewSelectorPane.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ));
		viewSelectorPane.setLayout( new GridLayout(3,false) );
		Label lblShowAccess = new Label(viewSelectorPane, SWT.LEFT );
		lblShowAccess.setText( SHOW_MEMBER_ACCESS_BY ); 
		this.byMembers = new Button( viewSelectorPane, SWT.RADIO );
		this.byMembers.setText( MEMBERS);
		this.byMembers.addSelectionListener( this.showAccessByListener );
		
		this.bySpheres = new Button( viewSelectorPane, SWT.RADIO );
		this.bySpheres.addSelectionListener( this.showAccessByListener );
		this.bySpheres.setText( SPHERES );
		
		this.byMembers.setSelection( true );
		ensureMemberAccessIs( AccessView.BY_MEMBERS );
	}

	/**
	 * @param memberAccessView 
	 * 
	 */
	private void ensureMemberAccessIs(final AccessView memberAccessView) {
		if ( getActualView() != memberAccessView ) {
			setViewDeterminator(memberAccessView);
			//Reset selection to avoid problems with selected sphere
			if(this.detector.hasChanges()) {
				this.detector.showDialog(this);
			} else {
				performFinalAction();
			}
		}
	}

	private void createNewAccessComposite(final AccessView memberAccessView) {
		this.manager.setSelectedSphere( null );
		if ( this.accessComposite != null ) {
			this.accessComposite.dispose();
			this.accessComposite = null;
		}
		this.accessComposite = createMemberAccessComposite( memberAccessView );
		GridData gridData = LayoutUtils.createFullFillGridData();
		this.accessComposite.setLayoutData( gridData );
		layout();
	}

	
	/**
	 * @param memberAccessView 
	 * @return
	 */
	private AbstractAccessManagerComposite createMemberAccessComposite(AccessView memberAccessView) {
		AbstractAccessManagerComposite comp = null;
		if ( memberAccessView == AccessView.BY_MEMBERS ) {
			this.manager.clearListenersList();
			comp = new ByMembersAccessManagerComposite( this, this.manager, getDetector() );
		}
		else {
			comp = new BySpheresAccessManagerComposite( this, this.manager, getDetector() );
		}
		return comp;
	}
	
	public AccessView getActualView() {
		if ( this.accessComposite != null ) {
			return this.accessComposite instanceof ByMembersAccessManagerComposite ? AccessView.BY_MEMBERS : AccessView.BY_SPHERES; 
		}
		else {
			return AccessView.NONE;
		}
	}
	
	
	/**
	 *
	 */
	public class ShowAccessByListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			ensureMemberAccessIs( ManageAccessComposite.this.byMembers == e.getSource() ? AccessView.BY_MEMBERS : AccessView.BY_SPHERES );
		}
	}

	public void performFinalAction() {
		createNewAccessComposite(this.viewDeterminator);
		this.viewDeterminator = null;
	}
	
	public void revertSelection() {
		boolean byMembersSelection = ManageAccessComposite.this.byMembers.getSelection(); 
		ManageAccessComposite.this.byMembers.setSelection(!byMembersSelection);
		
		boolean bySpheresSelection = ManageAccessComposite.this.bySpheres.getSelection();
		ManageAccessComposite.this.bySpheres.setSelection(!bySpheresSelection);
	}

	public void setViewDeterminator(AccessView determinator) {
		this.viewDeterminator = determinator;
	}
	
	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void jumpToNextItem() {
		if ( this.commonShell != null ) {
			this.commonShell.jumpToNextItem();
		}
	}
}
