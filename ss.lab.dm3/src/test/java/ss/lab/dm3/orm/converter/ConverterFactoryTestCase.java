package ss.lab.dm3.orm.converter;

import java.io.Serializable;

import junit.framework.TestCase;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.converter.BooleanTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.ConverterFactory;
import ss.lab.dm3.orm.mapper.property.converter.DoubleTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.IntegerTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.LongTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.StringTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.TypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.booleanPrimitiveTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.doublePrimitiveTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.intPrimitiveTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.longPrimitiveTypeConverter;

public class ConverterFactoryTestCase extends TestCase {

	private final ConverterFactory factory = ConverterFactory.INSTANCE;
	
	public void test() {
		TypeConverter<Long> longConverter = this.factory.create( Long.class );
		check( longConverter, LongTypeConverter.class, 1L, 1L );
		
		TypeConverter<Long> primitiveLongConv = this.factory.create( long.class );
		check( primitiveLongConv, longPrimitiveTypeConverter.class, 1L, 1L );
		
		TypeConverter<Integer> intConverter = this.factory.create( Integer.class );
		check( intConverter, IntegerTypeConverter.class, 20, 20 );
		
		TypeConverter<Integer> primitiveIntConverter = this.factory.create( int.class );
		check( primitiveIntConverter, intPrimitiveTypeConverter.class, 25, 25 );
		
		TypeConverter<Double> doubleConverter = this.factory.create( Double.class );
		check( doubleConverter, DoubleTypeConverter.class, 20.6, 20.6 );
		
		TypeConverter<Double> primitiveDoubleConverter = this.factory.create( double.class );
		check( primitiveDoubleConverter, doublePrimitiveTypeConverter.class, 25.17, 25.17 );
		
		TypeConverter<Boolean> booleanConverter = this.factory.create( Boolean.class );
		check( booleanConverter, BooleanTypeConverter.class, true, true );
		
		TypeConverter<Boolean> primitiveBooleanConverter = this.factory.create( boolean.class );
		check( primitiveBooleanConverter, booleanPrimitiveTypeConverter.class, false, false );
		
		TypeConverter<String> stringConverter = this.factory.create( String.class );
		check( stringConverter, StringTypeConverter.class, "true", "true" );
		
	}

	private void check(TypeConverter<?> converter,
			Class<?> converterClazz, Serializable serializableValue, Object plainValue ) {
		assertEquals( converterClazz, converter.getClass() );
		assertEquals( plainValue, converter.fromSerializable(serializableValue) );
		assertEquals( serializableValue, converter.toSerializable(plainValue) );		
	}
	
	public static class MyItem implements MappedObject {

		/* (non-Javadoc)
		 * @see ss.lab.dm3.orm.MappedObject#getId()
		 */
		public Long getId() {
			return null;
		}

	}

}
