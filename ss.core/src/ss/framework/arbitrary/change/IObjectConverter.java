package ss.framework.arbitrary.change;

public interface IObjectConverter<T,F> {
	
	T convert(F obj);

}
