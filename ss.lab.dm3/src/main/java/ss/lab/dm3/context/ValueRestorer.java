package ss.lab.dm3.context;

import ss.lab.dm3.orm.mapper.property.IAccessor;

public class ValueRestorer {

	private final Object bean;
	
	private final IAccessor propertyAccessor;
	
	private final Object originalValue;
	
	/**
	 * @param bean
	 * @param propertyAccessor
	 * @param originalValue
	 */
	public ValueRestorer(Object bean, IAccessor propertyAccessor, Object originalValue) {
		super();
		if ( bean == null ) {
			throw new NullPointerException( "bean" );
		}
		if ( propertyAccessor == null ) {
			throw new NullPointerException( "propertyAccessor" );
		}
		this.bean = bean;
		this.propertyAccessor = propertyAccessor;
		this.originalValue = originalValue;
	}

	public void restore() {
		this.propertyAccessor.setValue(this.bean, this.originalValue );
	}
	
}
