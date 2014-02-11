package ss.lab.dm3.orm.mapper.property.converter;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.utils.ReflectionHelper;

public class ConverterFactory {

	private static final String CUSTOM_TYPE_CONVERTER_SUFFIX = "TypeConverter";
	private static final String PRIMITIVE_TYPE_CONVERTER_SUFFIX = "Primitive" + CUSTOM_TYPE_CONVERTER_SUFFIX;
	private static final String CUSTOM_TYPE_CONVERTER_PACKAGE_NAME = ConverterFactory.class
			.getPackage().getName();

	public final static ConverterFactory INSTANCE = new ConverterFactory();

	private ConverterFactory() {
	}

	public <T> TypeConverter<T> create(Class<T> valueClazz) {
		if ( QualifiedObjectId.class == valueClazz ) {
			throw new IllegalArgumentException( "QualifiedObjectId is not supported" );
		}
		else if (hasCustomTypeConverter(valueClazz)) {
			Class<TypeConverter<T>> customConverterClazz = getCustomTypeConverterClass(valueClazz);
			return ReflectionHelper.create(customConverterClazz);
		}
		else {
			return new TypeConverter<T>(valueClazz);
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> Class<TypeConverter<T>> getCustomTypeConverterClass(
			Class<T> valueClazz) {
		final String className = getCustomTypeConverterName(valueClazz);
		try {
			return (Class<TypeConverter<T>>) Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new CantResolveTypeConverterClassException(className, ex);
		}
	}

	/**
	 * @param valueClazz
	 * @return
	 */
	private String getCustomTypeConverterName(Class<?> valueClazz) {
		final String suffix = valueClazz.isPrimitive() ? PRIMITIVE_TYPE_CONVERTER_SUFFIX : CUSTOM_TYPE_CONVERTER_SUFFIX;  
		final String simpleClassName = valueClazz.getSimpleName()
				+ suffix ;
		final String className = ReflectionHelper.combineClassName(
				CUSTOM_TYPE_CONVERTER_PACKAGE_NAME, simpleClassName);
		return className;
	}

	private boolean hasCustomTypeConverter(Class<?> valueClazz) {
		final String customTypeConverterName = getCustomTypeConverterName(valueClazz);
		return ReflectionHelper.isClassExists(customTypeConverterName);
	}

}
