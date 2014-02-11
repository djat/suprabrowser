package ss.lab.dm3.orm.query.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.Property;

public class Index {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Property<?> property;

	private final Map<Object,Object> valueToObject = new HashMap<Object,Object>();
	
	private final Map<MappedObject,Object> beanToIndexedValue = new HashMap<MappedObject,Object>();

	public Index(Property<?> property) {
		super();
		this.property = property;
	}

	@SuppressWarnings("unchecked")
	public void add(MappedObject bean) {
		Object value = MatchUtils.getNotNullMatchable( this.property.getValue(bean), this.property );
		final Object object = this.valueToObject.get( value );
		if ( object instanceof MappedObject ) {
			MappedObject existedBean = (MappedObject) object;
			List<MappedObject> items = new ArrayList<MappedObject>();
			items.add( existedBean );
			items.add(bean);
			this.valueToObject.put(value, items);			
		}
		else if ( object instanceof List ) {
			List<MappedObject> items = (List<MappedObject>) object;
			items.add(bean);
		}
		else {
			this.valueToObject.put( value, bean );
		}
		this.beanToIndexedValue.put(bean,value);
	}

	public void remove(MappedObject bean) {
		Object value = this.beanToIndexedValue.get( bean );
		if ( value != null ) {
			this.valueToObject.remove(value);
			this.beanToIndexedValue.remove(bean);
		}
	}
	
	public void update(MappedObject bean) {
		remove( bean );
		add( bean );
	}

	@SuppressWarnings("unchecked")
	public void eq(ICollector<MappedObject> collector, Object value) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Collect Eq " + value + " in " + this );
		}
		value = MatchUtils.getNotNullMatchable( value, this.property );
		final Object object = this.valueToObject.get( value );
		if ( object instanceof MappedObject ) {
			collector.add( (MappedObject) object);			
		}
		else if ( object instanceof List ) {
			List<MappedObject> items = (List<MappedObject>) object;
			for (MappedObject item : items ) {
				collector.add(item);
			}
		}
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "property", this.property );
		tsb.append("valueToObject size", this.valueToObject.size());
		tsb.append("beanToIndexedValue size", this.beanToIndexedValue.size());
		return tsb.toString();
	}


}
