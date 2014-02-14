package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;

public class WindowPosition extends Order {

	private final ISimpleEntityProperty div0 = super
		.createAttributeProperty( "@div0" );
	
	private final ISimpleEntityProperty div1 = super
		.createAttributeProperty( "@div1" );
	
	private final ISimpleEntityProperty div2 = super
		.createAttributeProperty( "@div2" );
	
	private final ISimpleEntityProperty div3 = super
		.createAttributeProperty( "@div3" );
	
	public WindowPosition() {
		super();
	}
	
	public String getDiv0() {
		return this.div0.getValue();
	}
	
	public void setDiv0(String value) {
		this.div0.setValue(value);
	}
	
	public String getDiv1() {
		return this.div1.getValue();
	}
	
	public void setDiv1(String value) {
		this.div1.setValue(value);
	}
	
	public String getDiv2() {
		return this.div2.getValue();
	}
	
	public void setDiv2(String value) {
		this.div2.setValue(value);
	}
	
	public String getDiv3() {
		return this.div3.getValue();
	}
	
	public void setDiv3(String value) {
		this.div3.setValue(value);
	}
}
