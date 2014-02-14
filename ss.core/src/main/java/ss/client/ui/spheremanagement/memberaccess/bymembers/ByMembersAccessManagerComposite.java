/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.bymembers;

import java.util.Hashtable;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.memberaccess.AbstractAccessManagerComposite;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.client.ui.spheremanagement.memberaccess.MemberAccessManager;
import ss.domainmodel.MemberReference;

/**
 * 
 */
public class ByMembersAccessManagerComposite extends
		AbstractAccessManagerComposite implements IChangable {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ByMembersAccessManagerComposite.class);

	private CheckedSpheresHieararchyComposite selectedMemberSpheres;

	private final Composite hierarchyPlace;

	private PeopleListComposite peopleComp;

	private MemberReference memberToRevert;

	private MemberReference currentlySelected;
	
	private IChangesDetector detector;

	/**
	 * @param parent
	 * @param manager 
	 * @param style
	 */
	public ByMembersAccessManagerComposite(Composite parent, MemberAccessManager manager, IChangesDetector detector) {
		super(parent,manager);
		this.detector = detector;
		setLayout( new GridLayout( 2, false ) );
		if (logger.isDebugEnabled()) {
			logger.debug("Creating PeopleListComposite");
		}
		final Hashtable<MemberReference, Boolean> membersOnlineState = getManager()
				.getMembersOnlineState();
		this.peopleComp = new PeopleListComposite(
				this, membersOnlineState);
		this.peopleComp.addSelectionListener(new MemberSelectionChangedListener());
		if (this.selectedMemberSpheres != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Move it above");
			}
			this.peopleComp.moveAbove(this.selectedMemberSpheres);
		}
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		this.peopleComp.setLayoutData(data);
		this.hierarchyPlace = LayoutUtils.createPlaceHolderComosite( this );
	}

	
	/**
	 * 
	 */
	private void disposeSelectedMemberSpheres() {
		rollbackChanges();
		if (this.selectedMemberSpheres != null) {
			this.selectedMemberSpheres.dispose();
			this.selectedMemberSpheres = null;
		}
	}

	private void createSelectedMembersSpheres(MemberReference member) {
		disposeSelectedMemberSpheres();
		final Boolean isOnline = getManager().getMembersOnlineState().get(
				member);
		this.selectedMemberSpheres = new CheckedSpheresHieararchyComposite(this.hierarchyPlace, this, member,
				isOnline != null ? isOnline.booleanValue() : false, getDetector() );
		this.selectedMemberSpheres.setLayoutData(LayoutUtils.createFullFillGridData());
		this.hierarchyPlace.layout();
	}

	class MemberSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			
			StructuredSelection selection = (StructuredSelection) event
					.getSelection();
			if ( selection != null && !selection.isEmpty() ) {
				final MemberReference selectedMember = (MemberReference) selection
						.getFirstElement();
				
				setCurrentlySelectedMember(selectedMember);
				
				MemberReference previousMember = null;
				try {
					previousMember = ByMembersAccessManagerComposite.this.selectedMemberSpheres.getMember();
				} catch (Exception ex) {
					logger.warn("hasn't previously selected member");
				}
				
				if(previousMember!=null && selectedMember.getLoginName().equals(previousMember.getLoginName())) {
					return;
				}
				
				if (getDetector().hasChanges()) {
					setMemberToRevert(previousMember);
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(ByMembersAccessManagerComposite.this);
				} else {
					performFinalAction();
				}
			}	
		}
	}

	public void performFinalAction() {
		createSelectedMembersSpheres(this.currentlySelected);	
	}

	public void setCurrentlySelectedMember(MemberReference selectedMember) {
		this.currentlySelected = selectedMember;	
	}

	public void setMemberToRevert(MemberReference previousMember) {
		this.memberToRevert = previousMember;
	}

	public void revertSelection() {
		ByMembersAccessManagerComposite.this.peopleComp.selectMember(this.memberToRevert);
		this.memberToRevert = null;
	}

	public IChangesDetector getDetector() {
		return this.detector;
	}


	public void jumpToNextItem() {
		
	}
}
