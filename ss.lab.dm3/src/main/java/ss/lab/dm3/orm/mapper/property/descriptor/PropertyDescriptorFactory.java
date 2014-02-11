package ss.lab.dm3.orm.mapper.property.descriptor;

import java.lang.reflect.Field;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.utils.CantFindPropertyWithNameException;
import ss.lab.dm3.utils.ReflectionHelper;

/**
 * @author Dmitry Goncharov
 */
public class PropertyDescriptorFactory {

	public final static PropertyDescriptorFactory INSTANCE = new PropertyDescriptorFactory();

	private PropertyDescriptorFactory() {
	}

	public PropertyDescriptor<?> create(
			Class<? extends MappedObject> beanClazz, String propertyName) {
		Field field = ReflectionHelper.findPropertyDeclaration(beanClazz,
				propertyName);
		if ( field == null ) {
			throw new CantFindPropertyWithNameException( beanClazz, propertyName );
		}
		PropertyDescriptorBuilder builder = new PropertyDescriptorBuilder(
				beanClazz, field);
		return builder.buildDescriptor();
	}

}
