/**
 * 
 */
package ss.client.ui.docking;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.PeopleTable;
import ss.client.ui.peoplelist.IPeopleList;
import ss.global.SSLogger;
import swtdock.ILayoutPart;
import swtdock.PartDragDrop;

/**
 * @author zobo
 *
 */
public class MemberListDocking extends AbstractDockingComponent{

    private static final String TITLE_LIST = "MEMBERLISTDOCKING.TITLE_LIST";
    
    private ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_DOCKING_MEMBERLISTDOCKING);
    
    private IPeopleList memberList;
    
    private MessagesPane mp;
    
    @SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(MemberListDocking.class);
    
    /**
     * 
     */
    public MemberListDocking(SupraDockingManager dm, MessagesPane mP) {
        super(dm);
        this.mp = mP;
    }
    
    @Override
    public String getName() {
        return this.bundle.getString(TITLE_LIST);
    }

    @Override
    public void createContent(Composite parent) {
        parent.setLayout(new FillLayout());
        
        this.memberList = this.mp.getPeopleTable(); 
        ((PeopleTable)this.memberList).createUi(parent);
        
       addMouseListener();
    }


    /**
	 * 
	 */
	private void addMouseListener() {
		this.memberList.addMouseListener();
	}

	@Override
    public IPeopleList getContent() {
        return this.memberList;
    }

    @Override
    public int getMinimumWidth() {
        return 0;
    }

    @Override
    public int getMinimumHeight() {
        return 0;
    }

    @Override
    public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
        if (target == null){
            if ((direction == PartDragDrop.SHELL_TOP)||(direction == PartDragDrop.SHELL_BOTTOM))
                return false;
        } else if (!super.checkPossibilityOfDocking(direction,target))
            return false;
        if ((direction == PartDragDrop.TOP)||(direction == PartDragDrop.BOTTOM))
            return false;
        return true;
    }

    @Override
    public boolean checkIfCanDockOn(int direction) {
        if ((direction == PartDragDrop.TOP)||(direction == PartDragDrop.BOTTOM))
            return false;
        return true;
    }
    
	@Override
	protected void createToolBar(Composite parent) {
		// DO NOTHING
	}

	public MessagesPane getMessagesPane() {
		return this.mp;
	}
}
