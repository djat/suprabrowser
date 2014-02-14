/**
 * 
 */
package ss.client.ui.docking;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import ss.client.event.TableSWTMouseListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.MessagesTable;
import ss.common.UiUtils;
import ss.global.SSLogger;
import swtdock.ILayoutPart;


/**
 * @author zobo
 *
 */
public class SupraTableDocking extends AbstractDockingComponent{
  
	private MessagesPane mP;

	private MessagesTable table;
    
    @SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(SupraTableDocking.class);
    
    private static final ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_DOCKING_SUPRATABLEDOCKING);
    
    
	public static final String ENTRY = "SUPRATABLEDOCKING.ENTRY";
	public static final String ENTRIES = "SUPRATABLEDOCKING.ENTRIES";
	private static final String MESSAGES_TABLE = "SUPRATABLEDOCKING.MESSAGES_TABLE";
	
	
    
    public SupraTableDocking(SupraDockingManager dm, MessagesPane mP) {
        super(dm);
        this.mP = mP;
    }

    @Override
    public String getName() {
        return bundle.getString(MESSAGES_TABLE);
    }

    @Override
    public void createContent(Composite parent) {
        parent.setLayout(new GridLayout());
        
        addTable(parent);
    }

    /**
	 * 
	 */
	private void addTable(Composite parent) {
		this.table = new MessagesTable(parent, this.mP);
	
		this.table.addMouseListener(new TableSWTMouseListener(this.mP));
		this.table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDown(MouseEvent arg0) {
				SupraTableDocking.this.mP.getPeopleTable().update(true);
			}});
		
		this.table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent ce) {
				((Table)ce.widget).getColumn(0).setWidth(((Table)ce.widget).getSize().x-300);
				((Table)ce.widget).getColumn(1).setWidth(150);
				((Table)ce.widget).getColumn(2).setWidth(150);
				((Table)ce.widget).redraw();
			}
		});
	}

	@Override
    public Table getContent() {
        return this.table.asComponent();
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
        } else 
        if (!super.checkPossibilityOfDocking(direction,target))
            return false;
        return true;
    }

    @Override
    public boolean checkIfCanDockOn(int direction) {
        return true;
    }
    
    public void setInfoToLabel() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				try {
					int r = SupraTableDocking.this.mP.getTableStatements() != null ? SupraTableDocking.this.mP
							.getTableStatements().size()
							: 0;
					SupraTableDocking.this.label.setText(bundle
							.getString(MESSAGES_TABLE)
							+ ": "
							+ r
							+ " "
							+ (r == 1 ? bundle.getString(ENTRY) : bundle
									.getString(ENTRIES)));
				} catch (NullPointerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		});
	}
        
    
	@Override
	protected void createToolBar(Composite parent) {
		// DO NOTHING
	}
	
	public MessagesPane getMessagesPane() {
		return this.mP;
	}
}
