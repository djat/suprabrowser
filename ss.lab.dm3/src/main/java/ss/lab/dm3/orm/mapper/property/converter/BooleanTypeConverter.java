package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

import ss.lab.dm3.utils.ConverterUtils;

public class BooleanTypeConverter extends TypeConverter<Boolean> {

	public BooleanTypeConverter() {
		super(Boolean.class);
	}

	@Override
	public Boolean fromSerializable(Serializable loadValue) {
		return ConverterUtils.getBooleanValue(loadValue);
	}

	@Override
	public Boolean fromString(String value) {
		return new Boolean(value);
	}
}
