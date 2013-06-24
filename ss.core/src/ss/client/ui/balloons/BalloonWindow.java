/*
 * Created on Feb 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui.balloons;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.SavePageWindow;
import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.viewers.NewMessage;
import ss.common.StringUtils;
import ss.rss.RSSParser;

/**
 * A Shell wrapper which creates balloon popup windows.
 * 
 * <p>
 * By default, a balloon window has no title bar or system controls. The
 * following styles are supported:
 * </p>
 * 
 * <ul>
 * <li>SWT.ON_TOP - Keep the window on top of other windows</li>
 * <li>SWT.CLOSE - Show a "close" control on the title bar (implies SWT.TITLE)</li>
 * <li>SWT.TITLE - Show a title bar</li>
 * </ul>
 * 
 * @author Stefan Zeiger (szeiger@novocode.com)
 * @since Jul 2, 2004
 * @version $Id: BalloonWindow.java,v 1.1 2004/07/09 15:36:17 szeiger Exp $
 */

class BalloonWindow {
    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BalloonWindow.class);

    private final Shell shell;

    private final Composite contents;

    private Label titleLabel;

    private Canvas titleImageLabel;

    private final int style;

    private int preferredAnchor = SWT.BOTTOM | SWT.RIGHT;

    private boolean autoAnchor = true;

    private int locX = Integer.MIN_VALUE, locY = Integer.MIN_VALUE;

    private int marginLeft = 12, marginRight = 12, marginTop = 5,
            marginBottom = 10;

    private int titleSpacing = 3, titleWidgetSpacing = 8;

    private ToolBar systemControlsBar;

    private ArrayList<Control> selectionControls = new ArrayList<Control>();

    private boolean addedGlobalListener;

    private ArrayList<Listener> selectionListeners = new ArrayList<Listener>();

    private Display display = null;

    private Hashtable session = null;

   //private SupraSphereFrame sF = null;

    private Document doc = null;

	private IBalloonListener listener;
	
	private boolean dragAndDropBalloon = false;

    BalloonWindow(Shell parent, int style) {
        this(null, parent, style);
    }

    BalloonWindow(Display display, int style) {

        this(display, new Shell(), style);
        this.display = display;

    }

    BalloonWindow(Display display, Document doc, Hashtable session, int style, IBalloonListener listener, boolean dragdrop) {

        this(display, new Shell(), style);
        this.session = session;
        this.doc = doc;
        this.listener = listener;
        this.dragAndDropBalloon = dragdrop;
        // startTimer();

    }

    private BalloonWindow(Display display, Shell parent, final int style) {
        this.style = style;
        this.display = display;
        
        int shellStyle = this.style & SWT.ON_TOP;
        this.shell = (this.display != null) ? new Shell(this.display, SWT.NO_TRIM
                | shellStyle) : new Shell(parent, SWT.NO_TRIM | shellStyle);
        
        this.contents = new Composite(this.shell, SWT.NONE);

        final Color c = new Color(this.shell.getDisplay(), 200, 200, 200);
        this.shell.setBackground(c);
        this.shell.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        // shell.setLocation(display.getClientArea().width-,display.getClientArea().y);
        this.contents.setBackground(this.shell.getBackground());
        this.contents.setForeground(this.shell.getForeground());

        this.selectionControls.add(this.shell);
        this.selectionControls.add(this.contents);
        
        this.shell.setFocus();

        addShellListeners(c);
    }

    /**
     * @param style
     * @param c
     */
    private void addShellListeners(final Color c) {
    	final Listener globalListener = new Listener() {
    		private BalloonWindow balloonWindow = BalloonWindow.this;
    		public void handleEvent(Event event) {
    			for (int i = this.balloonWindow.selectionControls.size() - 1; i >= 0; i--) {
    				if (this.balloonWindow.selectionControls.get(i) == event.widget) {
    					if ((this.balloonWindow.style & SWT.CLOSE) != 0) {
    						for (int j = this.balloonWindow.selectionListeners.size() - 1; j >= 0; j--)
    							((Listener) this.balloonWindow.selectionListeners.get(j))
    							.handleEvent(event);
    					} else {

    						// System.out.println("SESSION:
    							// "+(String)session.get("sphere_id"));
    						// MessagesPane mp =
    							// sF.getMessagesPaneFromSphereId((String)session.get("sphere_id"));
    						// session = mp.getSession();

    						// sF.client.voteDocument(session,doc.getRootElement().element("message_id").attributeValue("value"),doc);
    						// shell.close();
    					}
    					event.doit = false;
    				}
    			}
    		}
    	};

    	/*
    	 * shell.addListener(SWT.MouseDown, new Listener() {
    	 * 
    	 * public void handleEvent(Event event) {
    	 * 
    	 *  }
    	 * 
    	 * 
    	 * });
    	 */

    	this.shell.addListener(SWT.Show, new Listener() {
    		private BalloonWindow balloonWindow = BalloonWindow.this;
    		public void handleEvent(Event event) {

    			if (!this.balloonWindow.addedGlobalListener) {
    				this.balloonWindow.shell.getDisplay().addFilter(SWT.MouseDown, 
    						globalListener);
    				this.balloonWindow.addedGlobalListener = true;
    			}
    		}
    	});

    	this.shell.addListener(SWT.Hide, new Listener() {
    		private BalloonWindow balloonWindow = BalloonWindow.this;
    		public void handleEvent(Event event) {
    			if (this.balloonWindow.addedGlobalListener) {
    				this.balloonWindow.shell.getDisplay().removeFilter(SWT.MouseDown, 
    						globalListener);
    				this.balloonWindow.addedGlobalListener = false;
    			}
    		}
    	});

    	this.shell.addListener(SWT.Dispose, new Listener() {
    		private BalloonWindow balloonWindow = BalloonWindow.this;

    		public void handleEvent(Event event) {
    			if (this.balloonWindow.addedGlobalListener) {
    				this.balloonWindow.shell.getDisplay().removeFilter(SWT.MouseDown,
    						globalListener);
    				this.balloonWindow.addedGlobalListener = false;
    			}
    			c.dispose();
    		}
    	});
    }

    /**
     * Adds a control to the list of controls which close the balloon window.
     * The background, title image and title text are included by default.
     */

    void addSelectionControl(Control control) {
        control.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {

                Thread t = new Thread() {
                    private BalloonWindow balloonWindow = BalloonWindow.this;
                    public void run() {

                        if (this.balloonWindow.doc != null) {
                        	
                            SupraSphereFrame.INSTANCE.toFrontOnTopAndDoClick((Document) this.balloonWindow.doc
                                    .clone(), (Hashtable) this.balloonWindow.session.clone());

                        } else {
                        	
                        }

                    }
                };
                t.start();
                closeBalloon();
            }
        });
        this.selectionControls.add(control);
    }

    void closeBalloon() {
        this.shell.dispose();
        if (this.listener != null){
    		this.listener.closed();
    	}
    }

    /**
     * Set the location of the anchor. This must be one of the following values:
     * SWT.NONE, SWT.LEFT|SWT.TOP, SWT.RIGHT|SWT.TOP, SWT.LEFT|SWT.BOTTOM,
     * SWT.RIGHT|SWT.BOTTOM
     */
    void setAnchor(int anchor) {
        switch (anchor) {
        case SWT.NONE:
        case SWT.LEFT | SWT.TOP:
        case SWT.RIGHT | SWT.TOP:
        case SWT.LEFT | SWT.BOTTOM:
        case SWT.RIGHT | SWT.BOTTOM:
            break;
        default:
            throw new IllegalArgumentException("Illegal anchor value " + anchor);
        }
        this.preferredAnchor = anchor;
    }

    void setLocation(int x, int y) {
        this.locX = x;
        this.locY = y;
    }

    void setText(String title) {
        this.shell.setText(title);
    }

    void setImage(Image image) {
        this.shell.setImage(image);
    }

    private void setMargins(int marginLeft, int marginRight, int marginTop,
            int marginBottom) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    void setMargins(int marginX, int marginY) {
        setMargins(marginX, marginX, marginY, marginY);
    }

    void setMargins(int margin) {
        setMargins(margin, margin, margin, margin);
    }

    public void setTitleSpacing(int titleSpacing) {
        this.titleSpacing = titleSpacing;
    }

    public void setTitleWidgetSpacing(int titleImageSpacing) {
        this.titleWidgetSpacing = titleImageSpacing;
    }

    Shell getShell() {
        return this.shell;
    }

    Composite getContents() {
        return this.contents;
    }

    private void prepareForOpen() {

    	if (this.dragAndDropBalloon){
    		addDragDropListener();
    	}
        
        Point contentsSize = this.contents.getSize();
        Point titleSize = new Point(0, 0);

        boolean showTitle = ((this.style & (SWT.CLOSE | SWT.TITLE)) != 0);
        
        if (showTitle) {
            titleSize = createTitle(contentsSize);
        }

        Rectangle screen = this.shell.getDisplay().getPrimaryMonitor()
                .getClientArea();

        int anchor = this.preferredAnchor;
        if (anchor != SWT.NONE && this.autoAnchor && this.locX != Integer.MIN_VALUE) {
            anchor = getModifiedAnchor(contentsSize, screen, anchor);
        }

        final Point shellSize = calculateShellSize(contentsSize, anchor);

        this.shell.setSize(shellSize);
        this.shell.setLocation(this.shell.getDisplay().getPrimaryMonitor()
                .getClientArea().width
                - shellSize.x,
                this.display.getPrimaryMonitor().getClientArea().height
                        - shellSize.y);

        int titleLocationY = this.marginTop + (((anchor & SWT.TOP) != 0) ? 20 : 0);
        
        this.contents.setLocation(this.marginLeft, titleSize.y + titleLocationY);
        
        if (showTitle) {
            setTitleLocation(titleSize, shellSize, titleLocationY);
        }

        final Region region = new Region();
        region.add(createOutline(shellSize, anchor, true));

        this.shell.setRegion(region);
        this.shell.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event event) {
                region.dispose();
            }
        });

        final int[] outline = createOutline(shellSize, anchor, false);
        this.shell.addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event event) {
                event.gc.drawPolygon(outline);
            }
        });

        if (this.locX != Integer.MIN_VALUE) {
            relocateShell(screen, anchor, shellSize);
        }
    }

    /**
     * @param screen
     * @param anchor
     * @param shellSize
     */
    private void relocateShell(Rectangle screen, int anchor, final Point shellSize) {
        Point shellLoc = new Point(this.locX, this.locY);
        if ((anchor & SWT.BOTTOM) != 0)
            shellLoc.y = shellLoc.y - shellSize.y + 1;
        if ((anchor & SWT.LEFT) != 0)
            shellLoc.x -= 15;
        else if ((anchor & SWT.RIGHT) != 0)
            shellLoc.x = shellLoc.x - shellSize.x + 16;

        if (this.autoAnchor) {
            if (shellLoc.x < screen.x)
                shellLoc.x = screen.x;
            else if (shellLoc.x > screen.x + screen.width - shellSize.x)
                shellLoc.x = screen.x + screen.width - shellSize.x;

            if (anchor == SWT.NONE) {
                if (shellLoc.y < screen.y)
                    shellLoc.y = screen.y;
                else if (shellLoc.y > screen.y + screen.height
                        - shellSize.y)
                    shellLoc.y = screen.y + screen.height - shellSize.y;
            }
        }

        this.shell.setLocation(shellLoc);
    }

    /**
     * @param titleSize
     * @param shellSize
     * @param titleLocY
     */
    private void setTitleLocation(Point titleSize, final Point shellSize, int titleLocY) {
        int realTitleHeight = titleSize.y - this.titleSpacing;
        if (this.titleImageLabel != null) {
            this.titleImageLabel.setLocation(this.marginLeft, titleLocY
                    + (realTitleHeight - this.titleImageLabel.getSize().y) / 2);
            this.titleLabel.setLocation(this.marginLeft + this.titleImageLabel.getSize().x
                    + this.titleWidgetSpacing, titleLocY
                    + (realTitleHeight - this.titleLabel.getSize().y) / 2);
        } else
            this.titleLabel.setLocation(this.marginLeft, titleLocY
                    + (realTitleHeight - this.titleLabel.getSize().y) / 2);
        if (this.systemControlsBar != null)
            this.systemControlsBar
                    .setLocation(shellSize.x - this.marginRight
                            - this.systemControlsBar.getSize().x, titleLocY
                            + (realTitleHeight - this.systemControlsBar
                                    .getSize().y) / 2);
    }

    /**
     * @param contentsSize
     * @param anchor
     * @return
     */
    private Point calculateShellSize(Point contentsSize, int anchor) {
        final Point shellSize = (anchor == SWT.NONE) ? new Point(contentsSize.x
                + this.marginLeft + this.marginRight, contentsSize.y + this.marginTop
                + this.marginBottom) : new Point(contentsSize.x + this.marginLeft
                + this.marginRight, contentsSize.y + this.marginTop + this.marginBottom + 20);

        if (shellSize.x < 54 + this.marginLeft + this.marginRight)
            shellSize.x = 54 + this.marginLeft + this.marginRight;
        if (anchor == SWT.NONE) {
            if (shellSize.y < 10 + this.marginTop + this.marginBottom)
                shellSize.y = 10 + this.marginTop + this.marginBottom;
        } else {
            if (shellSize.y < 30 + this.marginTop + this.marginBottom)
                shellSize.y = 30 + this.marginTop + this.marginBottom;
        }
        return shellSize;
    }

    /**
     * @param contentsSize
     * @param screen
     * @param anchor
     * @return
     */
    private int getModifiedAnchor(Point contentsSize, Rectangle screen, int anchor) {
        if ((anchor & SWT.LEFT) != 0) {
            if (this.locX + contentsSize.x + this.marginLeft + this.marginRight - 16 >= screen.x
                    + screen.width)
                anchor = anchor - SWT.LEFT + SWT.RIGHT;
        } else // RIGHT
        {
            if (this.locX - contentsSize.x - this.marginLeft - this.marginRight + 16 < screen.x)
                anchor = anchor - SWT.RIGHT + SWT.LEFT;
        }
        if ((anchor & SWT.TOP) != 0) {
            if (this.locY + contentsSize.y + 20 + this.marginTop + this.marginBottom >= screen.y
                    + screen.height)
                anchor = anchor - SWT.TOP + SWT.BOTTOM;
        } else // BOTTOM
        {
            if (this.locY - contentsSize.y - 20 - this.marginTop - this.marginBottom < screen.y)
                anchor = anchor - SWT.BOTTOM + SWT.TOP;
        }
        return anchor;
    }

    /**
     * @param contentsSize
     * @return
     */
    private Point createTitle(Point contentsSize) {
        Point titleSize;
        
        if (this.titleLabel == null) {
            createTitleLabel();
        }
        
        String titleText = this.shell.getText();
        this.titleLabel.setText(titleText == null ? "" : titleText);
        this.titleLabel.pack();
        titleSize = this.titleLabel.getSize();

        if (this.titleImageLabel == null && this.shell.getImage() != null) {
            createTitleImageLabel();

            Point titleImageSize = this.titleImageLabel.getSize();
            titleSize.x += titleImageSize.x + this.titleWidgetSpacing;
            if (titleImageSize.y > titleSize.y)
                titleSize.y = titleImageSize.y;
        }

        if (this.systemControlsBar == null && (this.style & SWT.CLOSE) != 0) {
            createSystemControlsBar();
            
            Point closeSize = this.systemControlsBar.getSize();
            titleSize.x += closeSize.x + this.titleWidgetSpacing;
            if (closeSize.y > titleSize.y)
                titleSize.y = closeSize.y;
        }

        titleSize.y += this.titleSpacing;
        if (titleSize.x > contentsSize.x) {
            contentsSize.x = titleSize.x;
            this.contents.setSize(contentsSize.x, contentsSize.y);
        }
        contentsSize.y += titleSize.y;
        return titleSize;
    }

    /**
     * 
     */
    private void createSystemControlsBar() {
        // Color closeFG = shell.getForeground(), closeBG =
        // shell.getBackground();
        // Color closeFG =
        // shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY),
        // closeBG = shell.getBackground();
        Color closeFG = this.shell.getDisplay().getSystemColor(
                SWT.COLOR_WIDGET_FOREGROUND), closeBG = this.shell
                .getDisplay().getSystemColor(
                        SWT.COLOR_WIDGET_BACKGROUND);
        final Image closeImage = createCloseImage(this.shell.getDisplay(),
                closeBG, closeFG);
        this.shell.addListener(SWT.Dispose, new Listener() {

            public void handleEvent(Event event) {

                // MessagesPane mp =
                // sF.getMessagesPaneFromSphereId((String)session.get("sphere_id"),(String)session.get("unique_id"));
                String mp = null;
                if (mp != null) {
                    try {
                        // session = mp.getSession();
                        // sF.client.voteDocument(session,doc.getRootElement().element("message_id").attributeValue("value"),doc);
                    } catch (Exception e) {
                    	logger.error(e.getMessage(), e);
                    }
                }
                // System.out.println("DID THIS ANYWAY, in balloon");
                closeImage.dispose();
            }
        });
        this.systemControlsBar = new ToolBar(this.shell, SWT.FLAT);
        this.systemControlsBar.setBackground(closeBG);
        this.systemControlsBar.setForeground(closeFG);
        ToolItem closeItem = new ToolItem(this.systemControlsBar, SWT.PUSH);
        closeItem.setImage(closeImage);
        closeItem.addListener(SWT.Selection, new Listener() {
            private BalloonWindow balloonWindow = BalloonWindow.this;
            public void handleEvent(Event event) {
                logger.warn("NO, do it here actually");

                MessagesPane mp = SupraSphereFrame.INSTANCE.getMessagesPaneFromSphereId(
                        (String) this.balloonWindow.session.get("sphere_id"),
                        (String) this.balloonWindow.session.get("unique_id"));

                if (mp != null) {
                    try {
                        this.balloonWindow.session = mp.getRawSession();
                        SupraSphereFrame.INSTANCE.client.voteDocument(this.balloonWindow.session, this.balloonWindow.doc
                                .getRootElement().element("message_id")
                                .attributeValue("value"), this.balloonWindow.doc);
                    } catch (Exception e) {
                    	logger.error(e.getMessage(), e);
                    }
                }
                closeBalloon();
            }
        });
        this.systemControlsBar.pack();
    }

    /**
     * 
     */
    private void createTitleImageLabel() {
        final Image titleImage = this.shell.getImage();
        this.titleImageLabel = new Canvas(this.shell, SWT.NONE);
        this.titleImageLabel.setBackground(this.shell.getBackground());
        this.titleImageLabel.setBounds(titleImage.getBounds());
        this.titleImageLabel.addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event event) {
                event.gc.drawImage(titleImage, 0, 0);
            }
        });
        this.selectionControls.add(this.titleImageLabel);
    }

    /**
     * 
     */
    private void createTitleLabel() {
        this.titleLabel = new Label(this.shell, SWT.NONE);

        this.titleLabel.setBackground(this.shell.getBackground());
        this.titleLabel.setForeground(this.shell.getForeground());
        FontData[] fds = this.shell.getFont().getFontData();
        this.titleLabel.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {
                Thread t = new Thread() {
                    private BalloonWindow balloonWindow = BalloonWindow.this;
                    public void run() {

                        if (this.balloonWindow.doc != null) {
                          
                          SupraSphereFrame.INSTANCE.toFrontOnTopAndDoClick((Document) this.balloonWindow.doc
                              .clone(), (Hashtable) this.balloonWindow.session.clone());
                          
                          
                        } else {

                        }

                    }
                };
                t.start();

                closeBalloon();

            }
        });
        for (int i = 0; i < fds.length; i++) {
            fds[i].setStyle(fds[i].getStyle() | SWT.BOLD);
        }
        final Font font = new Font(this.shell.getDisplay(), fds);
        this.titleLabel.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event event) {
                font.dispose();
            }
        });
        this.titleLabel.setFont(font);
        this.selectionControls.add(this.titleLabel);
    }

    void close() {
        this.shell.close();
    }

    void showOrHide(boolean visible) {
        if (!this.shell.isDisposed()) {
            this.shell.setVisible(visible);
        }
    }

    void setVisible(boolean visible) {
        if (visible)
            prepareForOpen();
        this.shell.setVisible(visible);
    }

    private static int[] createOutline(Point size, int anchor, boolean outer) {
        int o = outer ? 1 : 0;
        int w = size.x + o;
        int h = size.y + o;

        switch (anchor) {
        case SWT.RIGHT | SWT.BOTTOM:
            return new int[] {
            // top and top right
                    5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2,
                    w - 3, 3, w - 2, 3, w - 2, 5, w - 1, 5,
                    // right and bottom right
                    w - 1, h - 26, w - 2, h - 26, w - 2, h - 24, w - 3, h - 24,
                    w - 3, h - 23, w - 4, h - 23, w - 4, h - 22, w - 6, h - 22,
                    w - 6, h - 21,
                    // bottom with anchor
                    w - 16, h - 21, w - 16, h - 1, w - 16 - o, h - 1,
                    w - 16 - o, h - 2, w - 17 - o, h - 2, w - 17 - o, h - 3,
                    w - 18 - o, h - 3, w - 18 - o, h - 4, w - 19 - o, h - 4,
                    w - 19 - o, h - 5, w - 20 - o, h - 5, w - 20 - o, h - 6,
                    w - 21 - o, h - 6, w - 21 - o, h - 7, w - 22 - o, h - 7,
                    w - 22 - o, h - 8, w - 23 - o, h - 8, w - 23 - o, h - 9,
                    w - 24 - o, h - 9, w - 24 - o, h - 10, w - 25 - o, h - 10,
                    w - 25 - o, h - 11, w - 26 - o, h - 11, w - 26 - o, h - 12,
                    w - 27 - o, h - 12, w - 27 - o, h - 13, w - 28 - o, h - 13,
                    w - 28 - o, h - 14, w - 29 - o, h - 14, w - 29 - o, h - 15,
                    w - 30 - o, h - 15, w - 30 - o, h - 16, w - 31 - o, h - 16,
                    w - 31 - o, h - 17, w - 32 - o, h - 17, w - 32 - o, h - 18,
                    w - 33 - o, h - 18, w - 33 - o, h - 19, w - 34 - o, h - 19,
                    w - 34 - o, h - 20, w - 35 - o, h - 20, w - 35 - o, h - 21,
                    // bottom left
                    5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2,
                    h - 24, 1, h - 24, 1, h - 26, 0, h - 26,
                    // left and top left
                    0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
        case SWT.LEFT | SWT.BOTTOM:
            return new int[] {
            // top and top right
                    5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2,
                    w - 3, 3, w - 2, 3, w - 2, 5, w - 1, 5,
                    // right and bottom right
                    w - 1, h - 26, w - 2, h - 26, w - 2, h - 24, w - 3, h - 24,
                    w - 3, h - 23, w - 4, h - 23, w - 4, h - 22, w - 6, h - 22,
                    w - 6, h - 21,
                    // bottom with anchor
                    34 + o, h - 21, 34 + o, h - 20, 33 + o, h - 20, 33 + o,
                    h - 19, 32 + o, h - 19, 32 + o, h - 18, 31 + o, h - 18,
                    31 + o, h - 17, 30 + o, h - 17, 30 + o, h - 16, 29 + o,
                    h - 16, 29 + o, h - 15, 28 + o, h - 15, 28 + o, h - 14,
                    27 + o, h - 14, 27 + o, h - 13, 26 + o, h - 13, 26 + o,
                    h - 12, 25 + o, h - 12, 25 + o, h - 11, 24 + o, h - 11,
                    24 + o, h - 10, 23 + o, h - 10, 23 + o, h - 9, 22 + o,
                    h - 9, 22 + o, h - 8, 21 + o, h - 8, 21 + o, h - 7, 20 + o,
                    h - 7, 20 + o, h - 6, 19 + o, h - 6, 19 + o, h - 5, 18 + o,
                    h - 5, 18 + o, h - 4, 17 + o, h - 4, 17 + o, h - 3, 16 + o,
                    h - 3, 16 + o, h - 2, 15 + o, h - 2, 15, h - 1, 15, h - 21,
                    // bottom left
                    5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2,
                    h - 24, 1, h - 24, 1, h - 26, 0, h - 26,
                    // left and top left
                    0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
        case SWT.RIGHT | SWT.TOP:
            return new int[] {
            // top with anchor
                    5, 20, w - 35 - o, 20, w - 35 - o, 19, w - 34 - o, 19,
                    w - 34 - o, 18, w - 33 - o, 18, w - 33 - o, 17, w - 32 - o,
                    17, w - 32 - o, 16, w - 31 - o, 16, w - 31 - o, 15,
                    w - 30 - o, 15, w - 30 - o, 14, w - 29 - o, 14, w - 29 - o,
                    13, w - 28 - o, 13, w - 28 - o, 12, w - 27 - o, 12,
                    w - 27 - o, 11, w - 26 - o, 11, w - 26 - o, 10, w - 25 - o,
                    10, w - 25 - o, 9, w - 24 - o, 9, w - 24 - o, 8,
                    w - 23 - o, 8, w - 23 - o, 7, w - 22 - o, 7, w - 22 - o, 6,
                    w - 21 - o, 6, w - 21 - o, 5, w - 20 - o, 5, w - 20 - o, 4,
                    w - 19 - o, 4, w - 19 - o, 3, w - 18 - o, 3, w - 18 - o, 2,
                    w - 17 - o, 2, w - 17 - o, 1, w - 16 - o, 1, w - 16 - o, 0,
                    w - 16, 0, w - 16, 20,
                    // top and top right
                    w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22,
                    w - 3, 23, w - 2, 23, w - 2, 25, w - 1, 25,
                    // right and bottom right
                    w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4,
                    w - 3, h - 3, w - 4, h - 3, w - 4, h - 2, w - 6, h - 2,
                    w - 6, h - 1,
                    // bottom and bottom left
                    5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4,
                    1, h - 4, 1, h - 6, 0, h - 6,
                    // left and top left
                    0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
        case SWT.LEFT | SWT.TOP:
            return new int[] {
            // top with anchor
                    5, 20, 15, 20, 15, 0, 15 + o, 0, 16 + o, 1, 16 + o, 2,
                    17 + o, 2, 17 + o, 3, 18 + o, 3, 18 + o, 4, 19 + o, 4,
                    19 + o, 5, 20 + o, 5, 20 + o, 6, 21 + o, 6, 21 + o, 7,
                    22 + o, 7, 22 + o, 8, 23 + o, 8, 23 + o, 9, 24 + o, 9,
                    24 + o, 10, 25 + o, 10, 25 + o, 11, 26 + o, 11, 26 + o, 12,
                    27 + o, 12, 27 + o, 13, 28 + o, 13, 28 + o, 14, 29 + o, 14,
                    29 + o, 15, 30 + o, 15, 30 + o, 16, 31 + o, 16, 31 + o, 17,
                    32 + o, 17, 32 + o, 18, 33 + o, 18, 33 + o, 19, 34 + o, 19,
                    34 + o, 20,
                    // top and top right
                    w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22,
                    w - 3, 23, w - 2, 23, w - 2, 25, w - 1, 25,
                    // right and bottom right
                    w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4,
                    w - 3, h - 3, w - 4, h - 3, w - 4, h - 2, w - 6, h - 2,
                    w - 6, h - 1,
                    // bottom and bottom left
                    5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4,
                    1, h - 4, 1, h - 6, 0, h - 6,
                    // left and top left
                    0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
        default:
            return new int[] {
            // top and top right
                    5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2,
                    w - 3, 3, w - 2, 3, w - 2, 5, w - 1, 5,
                    // right and bottom right
                    w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4,
                    w - 3, h - 3, w - 4, h - 3, w - 4, h - 2, w - 6, h - 2,
                    w - 6, h - 1,
                    // bottom and bottom left
                    5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4,
                    1, h - 4, 1, h - 6, 0, h - 6,
                    // left and top left
                    0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
        }
    }

    private static final Image createCloseImage(Display display, Color bg,
            Color fg) {
        int size = 11, off = 1;
        Image image = new Image(display, size, size);
        GC gc = new GC(image);
        gc.setBackground(bg);
        gc.fillRectangle(image.getBounds());
        gc.setForeground(fg);
        gc.drawLine(0 + off, 0 + off, size - 1 - off, size - 1 - off);
        gc.drawLine(1 + off, 0 + off, size - 1 - off, size - 2 - off);
        gc.drawLine(0 + off, 1 + off, size - 2 - off, size - 1 - off);
        gc.drawLine(size - 1 - off, 0 + off, 0 + off, size - 1 - off);
        gc.drawLine(size - 1 - off, 1 + off, 1 + off, size - 1 - off);
        gc.drawLine(size - 2 - off, 0 + off, 0 + off, size - 2 - off);
        /*
         * gc.drawLine(1, 0, size-2, 0); gc.drawLine(1, size-1, size-2, size-1);
         * gc.drawLine(0, 1, 0, size-2); gc.drawLine(size-1, 1, size-1, size-2);
         */
        gc.dispose();
        return image;
    }
    
    private void addDragDropListener() {
        // Allow data to be copied or moved to the drop target
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget target = new DropTarget(this.shell, operations);

        // Receive data in Text or File format
        final TextTransfer textTransfer = TextTransfer.getInstance();
        final FileTransfer fileTransfer = FileTransfer.getInstance();
        Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
        target.setTransfer(types);
        
        target.addDropListener(new DropTargetListener() {

            private BalloonWindow balloonWindow = BalloonWindow.this;
            public void dragEnter(DropTargetEvent event) {
            	if (event.detail == DND.DROP_DEFAULT) {
                    if ((event.operations & DND.DROP_COPY) != 0) {
                        event.detail = DND.DROP_COPY;
                    } else {
                        event.detail = DND.DROP_NONE;
                    }
                }
                // will accept text but prefer to have files dropped
                for (int i = 0; i < event.dataTypes.length; i++) {
                    if (fileTransfer.isSupportedType(event.dataTypes[i])) {
                        event.currentDataType = event.dataTypes[i];
                        logger.info("DATA TYPE!: " + event.currentDataType);
                        // files should only be copied
                        if (event.detail != DND.DROP_COPY) {
                            event.detail = DND.DROP_NONE;
                        }
                        break;
                    }
                }
            }

            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
                if (textTransfer.isSupportedType(event.currentDataType)) {
                    // NOTE: on unsupported platforms this will return null
                    if (((String) (textTransfer
                            .nativeToJava(event.currentDataType))) != null) {
                        // System.out.println(t);
                    }
                }
            }

            public void dragOperationChanged(DropTargetEvent event) {
                /*
                 * if (event.detail == DND.DROP_DEFAULT) { event.detail =
                 * (event.operations & DND.DROP_COPY) != 0) { event.detail =
                 * DND.DROP_COPY; } else { event.detail = DND.DROP_NONE; }
                 */

                // allow text to be moved but files should only be copied
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    if (event.detail != DND.DROP_COPY) {
                        event.detail = DND.DROP_NONE;
                    }
                }
            }

            public void dragLeave(DropTargetEvent event) {
            }

            public void dropAccept(DropTargetEvent event) {
            }

            public void drop(DropTargetEvent event) {
                MessagesPane selectedMessagesPane = SupraSphereFrame.INSTANCE.tabbedPane
				                                        .getSelectedMessagesPane();
                if (selectedMessagesPane == null) {
                	List<MessagesPane> panes = SupraSphereFrame.INSTANCE.tabbedPane.getMessagesPanes();
                	if ( (panes != null) && (!panes.isEmpty()) ) {
                		selectedMessagesPane = panes.get( 0 );
                	}
                }
                
				if (textTransfer.isSupportedType(event.currentDataType)) {
                    logger.info("Dropped text only " + event.currentDataType);
                    String text = (String) event.data;
                    if (StringUtils.isBlank(text)) {
                    	logger.error("Text is blank");
                    	return;
                    }
                    text = text.trim();
                    if (text.toLowerCase().startsWith("www.")) {
                    	text = "http://" + text;
                    }
                    if (text.toLowerCase().startsWith("http://")) {

                        String url = null;
                        String title = null;
                        try {
                            StringTokenizer strTokenizer = new StringTokenizer(text, "\n");
                            url = strTokenizer.nextToken();
                            title = strTokenizer.nextToken();

                        } catch (Exception e) {
                        }

                        if (title == null) {
                            title = RSSParser.getTitleFromURL(text);
                        }

                        if (logger.isDebugEnabled()) {
                        	logger.debug("url:" + url);
                        	logger.debug("title:" + title);
						}

                    	try {
            				SavePageWindow.showDialog(url, title, new SpheresCollectionByTypeObject(SupraSphereFrame.INSTANCE.client));
                    	} catch (Throwable ex) {
                    		logger.error( ex );
                    	}

                    } else {
                    	new NewMessage(createNewSession(), selectedMessagesPane, 
                    			(text.length() > 21) ? text.substring(0, 20) : text, text );
                    }

                } else {

                    logger.info("Do something else");

                }
                if (fileTransfer.isSupportedType(event.currentDataType)) {

                    logger.info("Can get the file too: "
                            + event.currentDataType.toString());
                    String[] files = (String[]) event.data;

                    for (int i = 0; i < files.length; i++) {

                        // System.out.println("FILES: "+(files[i]));

                        String fileName = (String) files[i];

                        
                        Hashtable newSession = createNewSession();
                        final NewBinarySWT binary = new NewBinarySWT(newSession,
                                selectedMessagesPane, null, true);
                        binary.setFileName(fileName);
                        binary.setSubjectOnFileName( new File(fileName).getName() );
                        binary.setFocusToSubjectField();
                        binary.addButtons();
                    }

                }
            }

            /**
             * @return
             */
            @SuppressWarnings("unchecked")
			private Hashtable createNewSession() {
                Hashtable newSession = (Hashtable) this.balloonWindow.session.clone();
                newSession.put("sphere_id", SupraSphereFrame.INSTANCE.client.getVerifyAuth()
                        .getSystemName(
                                (String) this.balloonWindow.session.get("real_name")));

                Hashtable temp = (Hashtable) SupraSphereFrame.INSTANCE.getRegisteredSession(
                        (String) this.balloonWindow.session.get("supra_sphere"),
                        "DialogsMainCli");
                String sessionId = (String) temp.get("session");
                newSession.put("session", sessionId);
                return newSession;
            }

        });

    }
    
   /* public void startTimer() {
        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(10000);
                    BalloonWindow.this.display.asyncExec(new Thread() {
                        public void run() {
                            // shell.dispose();
                        }
                    });

                } catch (InterruptedException e) {
                	logger.error(e.getMessage());
                }

            }

        };
        t.start();

    }*/

	/**
	 * @return
	 */
	boolean isDisposed() {
		return this.shell.isDisposed();
	}
}
