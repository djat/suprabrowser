package ss.client.ui.tempComponents;

/**
 * @author zobo
 *
 */
public class MessagesPanePositionsInformation {
    
    public static final int CONTROL_PANEL = 1;
    
    public static final int MESSAGES_TREE = 2;
    
    public static final int MEMBER_LIST = 3;
    
    public static final int SUPRA_TABLE = 4;
    
    public static final int PREVIEW = 5;
    
    public static final int SEPARATOR_NEXT = 6;
    
    public static final int HORIZONTAL = 8;
    
    public static final int VERTICAL = 9;
   
    
    public static double DIV0 = 1639.1080375760794;//1618.019999999553;//0.45;
    
    public static double DIV1 = 36268.66966289282;//2639.1080375760794;//0.20;
    
    public static double DIV2 = 259.789566218853;//4368.840525329113;//0.45;
    
    public static double DIV3 = 418.29285714030266;//259.789566218853;//0.80;

    private double div0;
   
    private double div1;
    
    private double div2;
    
    private double div3;
    
    public int order0 = 1;
    
    public int order1 = 1;
    
    public int order2 = 1;
    
    public int order3 = 1;
    
    public int number = 0;
    
    public MessagesPanePositionsInformation(double div0, double div1, double div2, double div3) {
        super();
        this.div0 = div0;
        this.div1 = div1;
        this.div2 = div2;
        this.div3 = div3;
    }

    public MessagesPanePositionsInformation() {
        this(DIV0,DIV1,DIV2,DIV3);
    }

    public double getDiv0() {
        return this.div0;
    }

    public void setDiv0(double div0) {
        this.div0 = div0;
    }

    public double getDiv1() {
        return this.div1;
    }

    public void setDiv1(double div1) {
        this.div1 = div1;
    }

    public double getDiv2() {
        return this.div2;
    }

    public void setDiv2(double div2) {
        this.div2 = div2;
    }

    public double getDiv3() {
        return this.div3;
    }

    public void setDiv3(double div3) {
        this.div3 = div3;
    }

    @Override
    public String toString() {
        
        return new String(
                "DIVS: div0 = "+this.div0+"; div1 = "+this.div1+"; div2 = "+this.div2+"; div3 = "+this.div3);
    }
}
