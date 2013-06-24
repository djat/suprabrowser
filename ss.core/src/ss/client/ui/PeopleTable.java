/**
 * 
 */
package ss.client.ui;

import java.util.Vector;

import org.dom4j.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import ss.client.event.MemberPresenceListenerSWT;
import ss.client.event.RefreshPeopleListListener;
import ss.client.ui.peoplelist.IPeopleList;
import ss.client.ui.peoplelist.IPeopleListOwner;
import ss.client.ui.peoplelist.LastUserActivityToolTipProviderSWT;
import ss.client.ui.peoplelist.PeopleListContentProvider;
import ss.client.ui.peoplelist.PeopleListLabelProvider;
import ss.client.ui.peoplelist.SphereMember;
import ss.client.ui.peoplelist.SphereMembersTableModel;
import ss.common.UiUtils;
import ss.util.VariousUtils;

/**
 * @author roman
 * 
 */
public class PeopleTable implements IPeopleList {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PeopleTable.class);

	private TableViewer tv;
	
	private IPeopleListOwner owner;

	//private MessagesPane messagesPaneOwner;

	private SphereMembersTableModel model;

	private boolean refresh = false;

	/**
	 * @param mp
	 * @param parent
	 */
	public PeopleTable(IPeopleListOwner owner) {
		this.model = new SphereMembersTableModel(this);
		this.owner = owner;
	}

	/**
	 * 
	 */
	public void createUi(Composite parent) {
		this.tv = new TableViewer(parent, SWT.BORDER);
		this.tv.setLabelProvider(new PeopleListLabelProvider(
				this.owner.getFont(), this));
		this.tv.setContentProvider(new PeopleListContentProvider());
		this.tv.setInput(this.model);

		this.tv.getTable().setHeaderVisible(false);
		this.tv.getTable().setLinesVisible(false);

		this.tv.getTable().addMouseListener(
				new MemberPresenceListenerSWT(this.owner, this));
						
		new LastUserActivityToolTipProviderSWT(this.tv.getTable(),
				this.owner);
	}

	public void refreshMemberPresence(String memberName, boolean isOnline) {
		synchronized (this.model.getSyncRoot()) {
			final boolean isContactEnabled = this.owner.isContactEnabled(memberName);
			if (logger.isDebugEnabled()) {
				logger.debug("Refresh member presence " + memberName + " ["
						+ (isContactEnabled ? "add" : "remove") + "] online: "
						+ isOnline);
			}
			if (isContactEnabled) {
				addOrUpdate(memberName, isOnline);
			} else {
				remove(memberName);
			}
		}
	}

	/**
	 * @param memberName
	 * @param isOnline
	 */
	private void addOrUpdate(String memberName, boolean isOnline) {
		SphereMember member = this.model.findMember(memberName);
		if (member != null) {
			member.updatePresence(isOnline);
		} else {
			member = this.model.addMemberAndNotify(memberName);
			member.updatePresence(isOnline);
		}
	}

	/**
	 * @param memberName
	 */
	private void remove(String memberName) {
		this.model.removeMemberAndNotify(memberName);
		update();
	}

	public SphereMember getSelectedMember() {
		int index = this.tv.getTable().getSelectionIndex();
		return index >= 0 ? (SphereMember) this.model.getElementAt(index) : null;
	}

	public String getSelectedMemberName() {
		SphereMember member = getSelectedMember();
		return member != null ? member.getName() : null;
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getSelectedMembersNames() {
		final Vector<String> memberNames = new Vector<String>();
		final TableItem[] items = this.tv.getTable().getSelection();
		if (items != null) {
			for (TableItem item : items) {
				memberNames.add(item.getText());
			}
		}
		return memberNames;
	}

	public void extractNotExistedMembers(Vector<String> targetList) {
		for (String memberName : getMembers()) {
			if (!VariousUtils.vectorContains(memberName, targetList)) {
				targetList.add(memberName);
			}
		}
	}

	public Vector<String> getMembers() {
		return this.model.getMembersNames();
	}

	/**
	 * @param members
	 */
	public void setMembers(Vector<String> members) {
		this.model.setMembers(members);
	}

	public void update(final boolean clearSelection) {
		if(PeopleTable.this.tv.getTable().isDisposed()) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (logger.isDebugEnabled()){
					logger.debug("Update performed");
				}
				if (PeopleTable.this.tv != null) {
					if (clearSelection) {
						doClearSelection();
					}
					PeopleTable.this.tv.refresh();
					getOwner().getVotingEngine().notifyEndUpdate();
				}
			}
		});
	}

	public void update() {
		update(false);
	}

	public void updateRefresh(final boolean clearSelection) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (PeopleTable.this.tv != null) {
					PeopleTable.this.refresh = true;
					if (clearSelection) {
						doClearSelection();
					}
					PeopleTable.this.tv.refresh();
					PeopleTable.this.refresh = false;
				}
			}
		});
	}

	public Document getSelectedDocument() {
		return this.refresh ? null : this.owner
				.getLastSelectedDoc();
	}

	public void updateMember(final SphereMember member) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (PeopleTable.this.tv != null) {
					Document doc = getSelectedDocument();
					if (logger.isDebugEnabled()) {
						if (doc == null) {
							logger.debug("NULL DOC");
						} else {
							logger.debug("DOC " + doc.asXML());
						}
					}
					PeopleTable.this.tv.refresh(member);
				}
			}
		});
	}

	public boolean isMemberOnline(String memberName) {
		return false;
	}

	/**
	 * @return
	 */
	public boolean hasModel() {
		return this.model != null;
	}

	public boolean setAsTyping(String typingUser, String replyId) {
		boolean setted = this.model.setAsTyping(typingUser, replyId);
		updateMember(this.model.findMember(typingUser));
		return setted;
	}

	public boolean setAsNotTyping(String typingUser) {
		boolean setted = this.model.setAsNotTyping(typingUser);
		updateMember(this.model.findMember(typingUser));
		return setted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.IPeopleList#addMouseListener()
	 */
	public void addMouseListener() {
		final MessagesPane messagesPaneOwner = this.owner.getMessagesPane();
		if ( messagesPaneOwner != null ) {
			this.tv.getTable().addMouseListener(
						new RefreshPeopleListListener(messagesPaneOwner, false));
		}
	}

	private void doClearSelection() {
		PeopleTable.this.tv.setSelection(new ISelection() {
			public boolean isEmpty() {
				return true;
			}
		});
	}
	
	public SphereMember findMember(String memberName) {
		return this.model.findMember(memberName);
	}

	public IPeopleListOwner getOwner() {
		return this.owner;
	}
	
	public void setLayoutData(Object o) {
		this.tv.getTable().setLayoutData(o);
	}
}
