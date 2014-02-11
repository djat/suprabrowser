package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

import ss.lab.dm3.utils.ConverterUtils;

/**
 * @author Dmitry Goncharov
 *
 */
public class StringTypeConverter extends TypeConverter<String> {

	/**
	 */
	public StringTypeConverter() {
		super(String.class);
	}

	@Override
	public String fromSerializable(Serializable loadValue) {
		return ConverterUtils.getStringValue(loadValue);
	}
	
}
