package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

import ss.lab.dm3.utils.ConverterUtils;

/**
 * @author Dmitry Goncharov
 * 
 */
public class LongTypeConverter extends TypeConverter<Long> {

	/**
	 */
	public LongTypeConverter() {
		super(Long.class);
	}

	@Override
	public Long fromSerializable(Serializable loadValue) {
		return ConverterUtils.getLongValue(loadValue);
	}

	@Override
	public Long fromString(String value) {
		return new Long(value);
	}

}
