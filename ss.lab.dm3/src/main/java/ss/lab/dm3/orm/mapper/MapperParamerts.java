/**
 * 
 */
package ss.lab.dm3.orm.mapper;

import java.io.Serializable;

import ss.lab.dm3.orm.mapper.map.BeanSpace;

/**
 * @author Dmitry Goncharov
 */
public class MapperParamerts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -728437276208235589L;

	private final BeanSpace beanSpace;

	
	/**
	 * @param baseObjectClazz
	 * @param packages
	 * @param maps
	 */
	public MapperParamerts(BeanSpace beanSpace) {
		super();
		this.beanSpace = beanSpace;
	}

	/**
	 * @return the maps
	 */
	public BeanSpace getBeanSpace() {
		return this.beanSpace;
	}
	
}
