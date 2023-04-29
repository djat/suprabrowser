/*
 * Created on Mar 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui.widgets;
/*
 * Created on Feb 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import ss.client.localization.LocalizationLinks;
import ss.global.SSLogger;


import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.uif.component.PopupButton;



public class DropDownMenuButton {
	
  private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_WIDGETS_DROPDOWNMENUBUTTON);
  private static final String HELLO_DOUG = "DROPDOWNMENUBUTTON.HELLO_DOUG";
  private static final String CHANGE_BUTTON_TEXT = "DROPDOWNMENUBUTTON.CHANGE_BUTTON_TEXT";
  private static final String HELLO_STEVE = "DROPDOWNMENUBUTTON.HELLO_STEVE";
 
  JMenuItem menuItem = null;
  JButton button  =  null;
  
  private static final Logger logger = SSLogger.getLogger(DropDownMenuButton.class);

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
    	logger.error(e.getMessage(), e);
    }   
    
    DropDownMenuButton frame = new DropDownMenuButton();
    frame.createUI();
    
    
 
    
  }
  
  
  
  public void createUI() {
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    
    this.button = new JButton();
    this.button.setText(this.bundle.getString(HELLO_DOUG));
    
   
    
    
    JPopupMenu menu = new JPopupMenu();
    
    this.menuItem = new JMenuItem(this.bundle.getString(CHANGE_BUTTON_TEXT));  
    menu.add(this.menuItem);
    addMenuListener();
    addButtonListener();
    
    PopupButton popup = new PopupButton(this.button, menu);
    frame.setSize(500,500);
    panel.add(popup);
    frame.getContentPane().add(panel);
    panel.repaint();
    panel.revalidate();
    
    //frame.pack();
    frame.setVisible(true);
    
    
  }
  
  
  public void addButtonListener() {
    this.button.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        
        DropDownMenuButton.this.button.setText(DropDownMenuButton.this.bundle.getString(DropDownMenuButton.HELLO_DOUG));
        
      }
    });
    
  }
  
  public void addMenuListener() {
    this.menuItem.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        
        DropDownMenuButton.this.button.setText(DropDownMenuButton.this.bundle.getString(DropDownMenuButton.HELLO_STEVE));
        
      }
    });
    
  }
  
  
  
  
  
  

}
