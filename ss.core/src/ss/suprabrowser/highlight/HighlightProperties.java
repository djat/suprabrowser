package ss.suprabrowser.highlight;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * User: krishnm
 * Date: Jul 26, 2006
 * Time: 8:44:26 PM
  */

/**
 * The properties are applied on the keywords in the HTML DOM text nodes.
 */

public class HighlightProperties
{
    // make the font color darker than background
    private static final int SEED1 = 255;
    private static final int SEED2 = 128;

    private Color color = Color.red;
    private Color bg_color = Color.darkGray;
    private String onmouseover;
    private String onclick;
    private String onmouseout;
    private String className;
    private String id;
    
    Map<String, String> infoMap = new HashMap<String, String>();

    public HighlightProperties()
    {
        setRandomColorStyleProperties();
    }

    public String assembleStyle()
    {
        StringBuffer buf1 = new StringBuffer();
        buf1.append("color: rgb(" + this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue() + "); ");
        buf1.append("background-color: rgb("  + this.bg_color.getRed() + "," + this.bg_color.getGreen() + "," + this.bg_color.getBlue() + ");");
        buf1.append("cursor:pointer;");
        buf1.append("display:inline;");
        return buf1.toString();
        
    }

    /**
     * Default properties for keyword font and background are set.
     */
    private void setRandomColorStyleProperties()
    {
        setColor(new Color(Math.round((float)Math.random() * SEED1), Math.round((float)Math.random() * SEED1), Math.round((float)Math.random() * SEED1)));
        setBg_color(new Color(Math.round((float)Math.random() * SEED2), Math.round((float)Math.random() * SEED2), Math.round((float)Math.random() * SEED2)));
    }

    public String getOnclick()
    {
        return this.onclick;
    }


    public void setOnclick(String onclick)
    {
        this.onclick = onclick;
    }


    public String getOnmouseout()
    {
        return this.onmouseout;
    }
    
    public void setClass(String className) {
    	this.className = className;
    }
    
    public String getClassName() {
    	return this.className;
    }
    
    public void setId(String id) {
    	
    	this.id = id;
    }
    
    public String getId() {
    	
    	return this.id;
    	
    }


    public void setOnmouseout(String onmouseout)
    {
        this.onmouseout = onmouseout;
    }


    public String getOnmouseover()
    {
        return this.onmouseover;
    }


    public void setOnmouseover(String onmouseover)
    {
        this.onmouseover = onmouseover;
    }


    public Color getBg_color()
    {
        return this.bg_color;
    }

    public void setBg_color(Color bg_color)
    {
        this.bg_color = bg_color;
    }

    public Color getColor()
    {
        return this.color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setInfo(String key, String value)
    {
    	this.infoMap.put(key, value);
    }

    /**
     * This data is injected into the head node as javascript data which is shown in the js pop up window.
     * @return js variable. Var as the keyword and the value of it to be shown in the js pop up window.
     */
    public String getInfo()
    {
        StringBuffer buf = new StringBuffer();

        for (Iterator i = this.infoMap.entrySet().iterator(); i.hasNext(); )
        {
            String key = (String) ((Map.Entry)i.next()).getKey();
            buf.append("var " + key + "=" + this.infoMap.get(key) + ";");
            if (i.hasNext())
                buf.append("\n");
        }

        return buf.append("\n").toString();
    }
}