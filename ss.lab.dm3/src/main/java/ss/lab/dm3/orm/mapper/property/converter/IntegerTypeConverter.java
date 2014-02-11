package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

import ss.lab.dm3.utils.ConverterUtils;

/**
 * @author Dmitry Goncharov
 *
 */
public class IntegerTypeConverter extends TypeConverter<Integer> {
	
	public IntegerTypeConverter() {
		super( Integer.class );
	}

	@Override
	public Integer fromSerializable(Serializable loadValue) {
		return ConverterUtils.getIntegerValue(loadValue);
	}
	
	@Override
	public Integer fromString(String value) {
		return new Integer(value);
	}
}
