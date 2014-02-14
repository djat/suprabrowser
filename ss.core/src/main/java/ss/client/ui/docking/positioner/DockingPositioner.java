/**
 * 
 */
package ss.client.ui.docking.positioner;

import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.MessagesPane;
import ss.client.ui.PeopleTable;
import ss.client.ui.PeopleTableOwner;
import ss.client.ui.docking.ControlPanelDocking;
import ss.client.ui.docking.MemberListDocking;
import ss.client.ui.docking.MessagesTreeDocking;
import ss.client.ui.docking.PreviewAreaDocking;
import ss.client.ui.docking.SupraDockingManager;
import ss.client.ui.docking.SupraTableDocking;
import ss.client.ui.peoplelist.IPeopleList;
import ss.client.ui.tempComponents.MessagesPanePositionsInformation;
import ss.client.ui.tree.MessagesTree;
import ss.common.UiUtils;
import swtdock.ILayoutPart;
import swtdock.LayoutTree;
import swtdock.LayoutTreeNode;
import swtdock.PartDragDrop;
import swtdock.PartSashContainer;
import swtdock.PartTabFolder;
import swtdock.RootLayoutContainer;

/**
 * @author zobo
 *
 */
public class DockingPositioner {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DockingPositioner.class);
	
	private final MessagesPane messagesPane;
	
    private double div0 = MessagesPanePositionsInformation.DIV0;

    private double div1 = MessagesPanePositionsInformation.DIV1;

    private double div2 = MessagesPanePositionsInformation.DIV2;

    private double div3 = MessagesPanePositionsInformation.DIV3;
    
    private boolean savedPositions = false; 

    private SupraDockingManager dockingManager;

    private MemberListDocking memberDocking = null;

    private ControlPanelDocking controlPanelDocking = null;

    private PreviewAreaDocking previewDocking = null;

    private MessagesTreeDocking messagesTreeDocking = null;

    private SupraTableDocking tableDocking = null;
    
    private IPeopleList peoples;
    
    private boolean deployed = false;
    
    public DockingPositioner(final MessagesPane pane){
    	this.messagesPane = pane;
    }
    
    public DockingPositioner(final MessagesPane pane,final double div0, final double div1, final double div2,
    		final double div3){
    	this.messagesPane = pane;
        this.div0 = div0;
        this.div1 = div1;
        this.div2 = div2;
        this.div3 = div3;
    }
    
    public void deploy(MessagesTree messagesTree){
    	createAndShowUI(this.messagesPane, this.messagesPane.getRawSession(), messagesTree);
    }
    
    public boolean isDeployed(){
    	return this.deployed;
    }
	
    @SuppressWarnings("unchecked")
	private void createAndShowUI(final MessagesPane pane, final Hashtable session, final MessagesTree messagesTree) {
    	session.put("sphere_id", pane.getSystemName());
        try {
            this.dockingManager = new SupraDockingManager(pane,SWT.CLOSE);

//            String sess = (String) session.get("sphere_id");
//            String sphereCoreId = null;

//            try {
//	            // TODO: resolve what is this
//                pane.sF.client.getVerifyAuth().getSphereCoreId();
//            } catch (Exception e) {
//                logger.error("", e);
//            }
			this.controlPanelDocking = new ControlPanelDocking(
					this.dockingManager, pane.sF, pane);
            this.dockingManager.addPart(this.controlPanelDocking);

			this.memberDocking = new MemberListDocking(this.dockingManager,pane);
            this.peoples = new PeopleTable(new PeopleTableOwner(pane));
            this.dockingManager.addPart(this.memberDocking);
            int mode = 0;
            
			this.previewDocking = new PreviewAreaDocking(
					this.dockingManager, pane);
            this.dockingManager.addPart(this.previewDocking);

			this.messagesTreeDocking = new MessagesTreeDocking(
					this.dockingManager, session, pane);
            this.messagesTreeDocking.setTree(messagesTree);
            this.dockingManager.addPart(this.messagesTreeDocking);

			this.tableDocking = new SupraTableDocking(
					this.dockingManager, pane);
                  
            this.dockingManager.addPart(this.tableDocking);
            mode = 1;

            /*if (!sess.equals(sphereCoreId)) {
                if (!sess.equals((String) session.get("supra_sphere"))) {

                    if (pane.sF.client.getVerifyAuth().getMiddleChat() == true) {

						this.previewDocking = new PreviewAreaDocking(
								this.dockingManager, pane);
                        this.dockingManager.addPart(this.previewDocking);

						this.messagesTreeDocking = new MessagesTreeDocking(
								this.dockingManager, session, pane);
                        this.messagesTreeDocking.setTree(this.messagesTreePane);
                        this.dockingManager.addPart(this.messagesTreeDocking);

						this.tableDocking = new SupraTableDocking(
								this.dockingManager, pane);
			                  
                        this.dockingManager.addPart(this.tableDocking);
                        mode = 1;

                    } else {

						this.previewDocking = new PreviewAreaDocking(
								this.dockingManager, pane);
                        this.dockingManager.addPart(this.previewDocking);

						this.messagesTreeDocking = new MessagesTreeDocking(
								this.dockingManager, session, pane);
                        this.messagesTreeDocking.setTree(this.messagesTreePane);
                        this.dockingManager.addPart(this.messagesTreeDocking);

						this.tableDocking = new SupraTableDocking(
								this.dockingManager, pane);
                        
                        this.dockingManager.addPart(this.tableDocking);
                        mode = 2;
                    }
                } else {

					this.messagesTreeDocking = new MessagesTreeDocking(
							this.dockingManager, session, pane);
                    this.messagesTreeDocking.setTree(this.messagesTreePane);
                    this.dockingManager.addPart(this.messagesTreeDocking);
                    mode = 3;
                }
            } else {
                try {

					this.messagesTreeDocking = new MessagesTreeDocking(
							this.dockingManager, session, pane);
                    this.messagesTreeDocking.setTree(this.messagesTreePane);
                    this.dockingManager.addPart(this.messagesTreeDocking);

					this.tableDocking = new SupraTableDocking(
							this.dockingManager, pane);
                   
                    this.dockingManager.addPart(this.tableDocking);
                    mode = 4;

                } catch (NullPointerException npe) {
                    logger.error("", npe);
                }
            }*/
            
//            MessagesTable messagesTable = new MessagesTable(this.tableDocking, pane);
//            pane.setMessagesTable(messagesTable);
            

            layoutDockingComponents(mode);            

            pane.layout();
            this.deployed = true;
            pane.notifyDeployed();
        } catch (Exception e) {
            logger.error("createAndShowUI failed", e);
        }
    }

    public MessagesPanePositionsInformation calculateDivs(){
        MessagesPanePositionsInformation pos = new MessagesPanePositionsInformation();

		RootLayoutContainer root = this.controlPanelDocking.getContainer()
				.getRootContainer();


        LayoutTreeNode tree = (LayoutTreeNode)(root.getLayoutTree());
        deeper(tree, pos);
        logger.error(pos.toString());
        return pos;
    }

	private void setElement(LayoutTree tree,
			MessagesPanePositionsInformation pos, int num) {
        ILayoutPart part = tree.getPart();
        if (part instanceof PartTabFolder)
            part = ((PartTabFolder)part).getVisiblePart();
        if (part instanceof ControlPanelDocking)
            setDigit(MessagesPanePositionsInformation.CONTROL_PANEL, pos, num);
        else if (part instanceof MessagesTreeDocking)
            setDigit(MessagesPanePositionsInformation.MESSAGES_TREE, pos, num);
        else if (part instanceof MemberListDocking)
            setDigit(MessagesPanePositionsInformation.MEMBER_LIST, pos, num);
        else if (part instanceof SupraTableDocking)
            setDigit(MessagesPanePositionsInformation.SUPRA_TABLE, pos, num);
        else
            setDigit(MessagesPanePositionsInformation.PREVIEW, pos, num);
    }

	private void deeper(LayoutTreeNode tree,
			MessagesPanePositionsInformation pos) {
            int num = pos.number;
            LayoutTreeNode node = (LayoutTreeNode)tree;
            switch (num) {
            case 0:
                pos.setDiv0(node.getSash().getRatio());
                break;
            case 1:
                pos.setDiv1(node.getSash().getRatio());
                break;
            case 2:
                pos.setDiv2(node.getSash().getRatio());
                break;
            case 3:
                pos.setDiv3(node.getSash().getRatio());
                break;
            default:
                break;
            }
            pos.number++;
            if (node.getSash().isVertical())
                setDigit(MessagesPanePositionsInformation.VERTICAL, pos, num);
            else
                setDigit(MessagesPanePositionsInformation.HORIZONTAL, pos, num);

            LayoutTree tempTree = node.getLeftChild();
            if (tempTree instanceof LayoutTreeNode){
                setDigit(MessagesPanePositionsInformation.SEPARATOR_NEXT, pos, num);
                setDigit(pos.number, pos, num);
                deeper((LayoutTreeNode)tempTree, pos);
            } else
                setElement(tempTree, pos, num);

            tempTree = node.getRightChild();
            if (tempTree instanceof LayoutTreeNode) {
                setDigit(MessagesPanePositionsInformation.SEPARATOR_NEXT, pos, num);
                setDigit(pos.number, pos, num);
                deeper((LayoutTreeNode)tempTree, pos);
            }else
                setElement(tempTree, pos, num);
    }

	private void setDigit(int digit, MessagesPanePositionsInformation pos,
			int number) {
        double div;
        switch (number) {
        case 0:
            div = pos.getDiv0();
            div += digit*pos.order0;
            pos.setDiv0(div);
            pos.order0 *= 10;
            break;
        case 1:
            div = pos.getDiv1();
            div += digit*pos.order1;
            pos.setDiv1(div);
            pos.order1 *= 10;
            break;
        case 2:
            div = pos.getDiv2();
            div += digit*pos.order2;
            pos.setDiv2(div);
            pos.order2 *= 10;
            break;
        case 3:
            div = pos.getDiv3();
            div += digit*pos.order3;
            pos.setDiv3(div);
            pos.order3 *= 10;
            break;
        default:
            break;
        }
    }

    private int getDigit(int number, int order){
        return (number / order - (number / (order*10))*10);
    }

    private ILayoutPart getILayoutPart(int index){
        switch (index) {
        case MessagesPanePositionsInformation.CONTROL_PANEL:
            return this.controlPanelDocking;
        case MessagesPanePositionsInformation.MESSAGES_TREE:
            return this.messagesTreeDocking;
        case MessagesPanePositionsInformation.MEMBER_LIST:
            return this.memberDocking;
        case MessagesPanePositionsInformation.SUPRA_TABLE:
            return this.tableDocking;
        case MessagesPanePositionsInformation.PREVIEW:
            return this.previewDocking;
        }
        return null;
    }

    private void reorder(){
        if (Math.floor(this.div2) == 0){
            reoderThree();
        } else if (Math.floor(this.div3) == 0) {
            reoderFour();
        } else {
            reoderFive();
        }

    }

    private void reoderFour(){
        double div0 = .45;
        double div1 = 0.20;

        this.dockingManager.movePart(this.controlPanelDocking,
                PartDragDrop.TOP,this.messagesTreeDocking, (float)0.02);
		this.dockingManager.movePart(this.memberDocking, PartDragDrop.LEFT,
				this.messagesTreeDocking, (float) div1);
		this.dockingManager.movePart(this.tableDocking, PartDragDrop.BOTTOM,
				this.messagesTreeDocking, (float) div0);
    }

    private void reoderFive(){

        int info = (int)Math.floor(this.div0);
        int direction = getDigit(info,1);

        int digit = getDigit(info, 10);
        int nextDivLeft = 0;
        int nextDivRight = 0;
        int type = 0;
        if (digit != MessagesPanePositionsInformation.SEPARATOR_NEXT){
            //part = getILayoutPart(digit);
            nextDivRight = getDigit(info, 1000);
            type = -1;
        } else {
            nextDivLeft = getDigit(info, 100);
            digit = getDigit(info, 1000);
            if (digit != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                //part = getILayoutPart(digit);
                type = 1;
            } else {
                nextDivRight = getDigit(info, 10000);
            }
        }


        if (type != 0){
            variantFiveNonZero(direction);
        } else {
			variantFiveZero(direction, this.div0 - Math.floor(this.div0),
					nextDivLeft, nextDivRight);
        }
    }

    private void variantFiveNonZero(int direct){
        int info0 = (int)Math.floor(this.div0);
        int info1 = (int)Math.floor(this.div1);
        int info2 = (int)Math.floor(this.div2);
        int info3 = (int)Math.floor(this.div3);

        if (this.div1 < 10000){
            int direction3 = getDigit(info3,1);
            ILayoutPart left3 = getILayoutPart(getDigit(info3,10));
            ILayoutPart right3 = getILayoutPart(getDigit(info3,100));

            int direction0 = getDigit(info0,1);
            int temp = getDigit(info0,10);
            ILayoutPart part0;
            if (temp != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                part0 = getILayoutPart(temp);
                movePart(part0,right3, direction0, this.div0, true);
            } else {
                part0 = getILayoutPart(getDigit(info0,1000));
                movePart(part0,right3, direction0, this.div0, false);
            }

            int direction1 = getDigit(info1,1);
            temp = getDigit(info1,10);
            ILayoutPart part1;
            if (temp != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                part1 = getILayoutPart(temp);
                movePart(part1,right3, direction1, this.div1, true);
            } else {
                part1 = getILayoutPart(getDigit(info1,1000));
                movePart(part1,right3, direction1, this.div1, false);
            }

            int direction2 = getDigit(info2,1);
            temp = getDigit(info2,10);
            ILayoutPart part2;
            if (temp != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                part2 = getILayoutPart(temp);
                movePart(part2,right3, direction2, this.div2, true);
            } else {
                part2 = getILayoutPart(getDigit(info2,1000));
                movePart(part2,right3, direction2, this.div2, false);
            }
            movePart(left3,right3, direction3, this.div3, true);
        } else {

            int direction3 = getDigit(info3,1);
            ILayoutPart left3 = getILayoutPart(getDigit(info3,10));
            ILayoutPart right3 = getILayoutPart(getDigit(info3,100));

            int direction0 = getDigit(info0,1);
            int temp = getDigit(info0,10);
            ILayoutPart part0;
            if (temp != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                part0 = getILayoutPart(temp);
                movePart(part0,right3, direction0, this.div0, true);
            } else {
                part0 = getILayoutPart(getDigit(info0,1000));
                movePart(part0,right3, direction0, this.div0, false);
            }

            int direction1 = getDigit(info1,1);

            int direction2 = getDigit(info2,1);
            ILayoutPart left2 = getILayoutPart(getDigit(info2,10));
            ILayoutPart right2 = getILayoutPart(getDigit(info2,100));
            movePart(right2,right3, direction1, this.div1, true);
            movePart(left2,right2, direction2, this.div2, true);

            movePart(left3,right3, direction3, this.div3, true);
        }
    }

	private void variantFiveZero(int direction0, double div, int nextLeft,
			int nextRight) {
        int info1 = (int)Math.floor(this.div1);
        int info2 = (int)Math.floor(this.div2);
        int info3 = (int)Math.floor(this.div3);

        if (this.div1 < 1000){
            int direction3 = getDigit(info3,1);
            ILayoutPart left3 = getILayoutPart(getDigit(info3,10));
            ILayoutPart right3 = getILayoutPart(getDigit(info3,100));

            int direction1 = getDigit(info1,1);
            ILayoutPart left1 = getILayoutPart(getDigit(info1,10));
            ILayoutPart right1 = getILayoutPart(getDigit(info1,100));

            movePart(right1,right3, direction0, this.div0, true);
            movePart(left1,right1, direction1, this.div1, true);

            int direction2 = getDigit(info2,1);
            int temp = getDigit(info2,10);
            ILayoutPart part2;
            if (temp != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                part2 = getILayoutPart(temp);
                movePart(part2,right3, direction2, this.div2, true);
            } else {
                part2 = getILayoutPart(getDigit(info2,1000));
                movePart(part2,right3, direction2, this.div2, false);
            }
            movePart(left3,right3, direction3, this.div3, true);
        } else {
            int direction2 = getDigit(info2,1);
            ILayoutPart left2 = getILayoutPart(getDigit(info2,10));
            ILayoutPart right2 = getILayoutPart(getDigit(info2,100));

            int direction3 = getDigit(info3,1);
            ILayoutPart left3 = getILayoutPart(getDigit(info3,10));
            ILayoutPart right3 = getILayoutPart(getDigit(info3,100));

            movePart(right3,right2, direction0, this.div0, false);
            movePart(left3,right3, direction3, this.div3, true);

            int direction1 = getDigit(info1,1);
            int temp = getDigit(info1,10);
            ILayoutPart part1;
            if (temp != MessagesPanePositionsInformation.SEPARATOR_NEXT){
                part1 = getILayoutPart(temp);
                movePart(part1,right2, direction1, this.div1, true);
            } else {
                part1 = getILayoutPart(getDigit(info1,1000));
                movePart(part1,right2, direction1, this.div1, false);
            }
            movePart(left2,right2, direction2, this.div2, true);
        }
    }

	private void movePart(ILayoutPart one, ILayoutPart reciever, int direction,
			double divD, boolean left) {
        double div = divD - Math.floor(divD);
        if (left){
        if  (direction == MessagesPanePositionsInformation.HORIZONTAL)
				this.dockingManager.movePart(one, PartDragDrop.TOP, reciever,
						(float) div);
        else
				this.dockingManager.movePart(one, PartDragDrop.LEFT, reciever,
						(float) div);
        } else {
            if  (direction == MessagesPanePositionsInformation.HORIZONTAL)
				this.dockingManager.movePart(one, PartDragDrop.BOTTOM,
						reciever, (float) (div));
            else
				this.dockingManager.movePart(one, PartDragDrop.RIGHT, reciever,
						(float) (div));
        }
    }

    private void reoderThree(){
        int i0 = (int)Math.floor(this.div0);
        int i1 = (int)Math.floor(this.div1);
        int order = 1;
        int direction0 = getDigit(i0, order);
        int direction1 = getDigit(i1, order);
        order *= 10;
        ILayoutPart left = null;
        ILayoutPart right = null;
        left = getILayoutPart(getDigit(i1,10));
        right = getILayoutPart(getDigit(i1,100));

        ILayoutPart top = getILayoutPart(getDigit(i0,10));
        double div = this.div0 - Math.floor(this.div0);
        if (top == null){
            top = getILayoutPart(getDigit(i0,1000));
            if  (direction0 == MessagesPanePositionsInformation.HORIZONTAL)
				this.dockingManager.movePart(top, PartDragDrop.BOTTOM, right,
						(float) (div));
            else
				this.dockingManager.movePart(top, PartDragDrop.RIGHT, right,
						(float) (div));
        } else {
            if  (direction0 == MessagesPanePositionsInformation.HORIZONTAL)
				this.dockingManager.movePart(top, PartDragDrop.TOP, right,
						(float) div);
            else
				this.dockingManager.movePart(top, PartDragDrop.LEFT, right,
						(float) div);
        }

        div = this.div1 - Math.floor(this.div1);
        if  (direction1 == MessagesPanePositionsInformation.HORIZONTAL)
			this.dockingManager.movePart(left, PartDragDrop.TOP, right,
					(float) div);
        else
			this.dockingManager.movePart(left, PartDragDrop.LEFT, right,
					(float) div);
    }

    @SuppressWarnings("unused")
	private ILayoutPart reoderNext(double div){
        int info = (int)Math.floor(div);
        div -= Math.floor(div);
        int order = 1;
        int direction = getDigit(info, order);
        order *= 10;
        int temp;
        ILayoutPart left = null;
        ILayoutPart right = null;
        temp = getDigit(info,order);
        if (temp == MessagesPanePositionsInformation.SEPARATOR_NEXT){
            order *= 10;
            temp = getDigit(info,order);
            switch (temp) {
            case 1:
                left = reoderNext(this.div1);
                break;
            case 2:
                left = reoderNext(this.div2);
                break;
            case 3:
                left = reoderNext(this.div3);
                break;
            }
        } else {
            switch (temp) {
            case MessagesPanePositionsInformation.CONTROL_PANEL:
                left = this.controlPanelDocking;
                break;
            case MessagesPanePositionsInformation.MESSAGES_TREE:
                left = this.messagesTreeDocking;
                break;
            case MessagesPanePositionsInformation.MEMBER_LIST:
                left = this.memberDocking;
                break;
            case MessagesPanePositionsInformation.SUPRA_TABLE:
                left = this.tableDocking;
                break;
            case MessagesPanePositionsInformation.PREVIEW:
                left = this.previewDocking;
                break;
            }
        }
        order *= 10;

        temp = getDigit(info,order);
        if (temp == MessagesPanePositionsInformation.SEPARATOR_NEXT){
            order *= 10;
            temp = getDigit(info,order);
            switch (temp) {
            case 1:
                right = reoderNext(this.div1);
                break;
            case 2:
                right = reoderNext(this.div2);
                break;
            case 3:
                right = reoderNext(this.div3);
                break;
            }
        } else {
            switch (temp) {
            case MessagesPanePositionsInformation.CONTROL_PANEL:
                right = this.controlPanelDocking;
                break;
            case MessagesPanePositionsInformation.MESSAGES_TREE:
                right = this.messagesTreeDocking;
                break;
            case MessagesPanePositionsInformation.MEMBER_LIST:
                right = this.memberDocking;
                break;
            case MessagesPanePositionsInformation.SUPRA_TABLE:
                right = this.tableDocking;
                break;
            case MessagesPanePositionsInformation.PREVIEW:
                right = this.previewDocking;
                break;
            }
        }
        if  (direction == MessagesPanePositionsInformation.HORIZONTAL)
			this.dockingManager.movePart(left, PartDragDrop.TOP, right,
					(float) 0.5);
        else
			this.dockingManager.movePart(left, PartDragDrop.LEFT, right,
					(float) 0.5);

        if ((left.getContainer()) instanceof PartTabFolder){
            return (PartSashContainer) (left.getContainer().getContainer());
        } else {
            return (PartSashContainer) (left.getContainer());
        }
    }

    private void layoutDockingComponents(int mode){

        if (this.savedPositions){
            reorder();
            return;
		} else if ((mode == 1)||(mode == 2)){
            reoderFive();
            return;
        }

        double div0 = .45;
        double div1 = 0.20;
        double div2 = .45;
        //@SuppressWarnings("unused")
		double div3 = 0.80;

        this.dockingManager.movePart(this.controlPanelDocking,
                PartDragDrop.TOP,this.messagesTreeDocking, (float)0.02);
        switch (mode) {
        case 1:
			this.dockingManager.movePart(this.memberDocking, PartDragDrop.LEFT,
					this.messagesTreeDocking, (float) div1);
			this.dockingManager
					.movePart(this.tableDocking, PartDragDrop.BOTTOM,
							this.messagesTreeDocking, (float) div0);
            this.dockingManager.movePart(this.previewDocking,
                    PartDragDrop.LEFT, this.messagesTreeDocking, (float)div2);
            break;
        case 2:
			this.dockingManager.movePart(this.memberDocking, PartDragDrop.LEFT,
					this.messagesTreeDocking, (float) div1);
			this.dockingManager
					.movePart(this.tableDocking, PartDragDrop.BOTTOM,
							this.messagesTreeDocking, (float) div0);
            this.dockingManager.movePart(this.previewDocking,
                    PartDragDrop.LEFT, this.messagesTreeDocking, (float)div2);
            break;
        case 3:
			this.dockingManager.movePart(this.memberDocking, PartDragDrop.LEFT,
					this.messagesTreeDocking, (float) div1);
            break;
        case 4:
			this.dockingManager.movePart(this.memberDocking, PartDragDrop.LEFT,
					this.messagesTreeDocking, (float) div1);
			this.dockingManager
					.movePart(this.tableDocking, PartDragDrop.BOTTOM,
							this.messagesTreeDocking, (float) div0);
            break;
        default:
            break;
        }
    }
    
    public void repaintAll() {
    	UiUtils.swtBeginInvoke(new Runnable() {
			
			private DockingPositioner dp = DockingPositioner.this;
			
			public void run() {
				if (this.dp.previewDocking != null) {
					Composite mainControl = this.dp.previewDocking.getMainControl();
					if((mainControl != null)&& (!mainControl.isDisposed())){
						if (logger.isDebugEnabled()) {
							logger.debug("resizing preview docking");
						}
						Point p = mainControl.getSize();
						Point p1 = new Point(p.x+3,p.y+3);
						mainControl.setSize(p1);
						mainControl.setSize(p);
						mainControl.redraw();
					}
				}
			}
		});
		this.messagesPane.repaint();
	}

	public ControlPanelDocking getControlPanelDocking() {
		return this.controlPanelDocking;
	}

	public MemberListDocking getMemberDocking() {
		return this.memberDocking;
	}

	public MessagesTreeDocking getMessagesTreeDocking() {
		return this.messagesTreeDocking;
	}

	public PreviewAreaDocking getPreviewDocking() {
		return this.previewDocking;
	}

	public SupraTableDocking getTableDocking() {
		return this.tableDocking;
	}

	public void setSavedPositions(boolean savedPositions) {
		this.savedPositions = savedPositions;
	}

	public IPeopleList getPeoples() {
		return this.peoples;
	}
}
