package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

import ss.lab.dm3.utils.ConverterUtils;

public class DoubleTypeConverter extends TypeConverter<Double> {

	public DoubleTypeConverter() {
		super(Double.class);
	}

	@Override
	public Double fromSerializable(Serializable loadValue) {
		return ConverterUtils.getDoubleValue(loadValue);
	}

	@Override
	public Double fromString(String value) {
		return new Double(value);
	}
}
