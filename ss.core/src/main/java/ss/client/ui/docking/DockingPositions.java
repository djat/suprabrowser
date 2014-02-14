/**
 * 
 */
package ss.client.ui.docking;

/**
 * @author zobo
 *
 */
public class DockingPositions {

    public static int TOP = 2;
    
    public static int BOTTOM = 4;
    
    public static int LEFT = 8;
    
    public static int RIGHT = 16;
    
    public static int INSIDE = 32;
    
    public static int ENCLOSED = 64;
    
    private int location = 0;
    /**
     * 
     */
    public DockingPositions(int loc) {
        super();
        this.location = loc;
    }
    /**
     * @return Returns the location.
     */
    public int getLocation() {
        return this.location;
    }
    /**
     * @param location The location to set.
     */
    public void setLocation(int location) {
        this.location = location;
    }

}
