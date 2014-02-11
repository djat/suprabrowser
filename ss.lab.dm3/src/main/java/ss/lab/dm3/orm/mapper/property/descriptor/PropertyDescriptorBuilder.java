/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;


import ss.lab.dm3.annotation.CascadeFetch;
import ss.lab.dm3.annotation.SearchableField;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedReference;
import ss.lab.dm3.orm.mapper.property.descriptor.IReferenceDescriptor.Multiplicity;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.ManagedCollectionDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.NativeCollectionDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.NativeReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.PlainDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.QualifiedReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.TransientDescriptor;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.utils.ReflectionHelper;

/**
 * @author Dmitry Goncharov
 */
public class PropertyDescriptorBuilder {

	private final Class<? extends MappedObject> beanClazz; 
	
	private final Field field;

	private Annotation[] annotations;
		
	/**
	 * @param beanClazz
	 * @param field
	 */
	public PropertyDescriptorBuilder(Class<? extends MappedObject> beanClazz, Field field) {
		super();
		this.beanClazz = beanClazz;
		this.field = field;
	}
	
	private Annotation[] getAnnotations() {
		if ( this.annotations == null ) {
			this.annotations = collectAnnotations();
		}
		return this.annotations; 
	}

	private <T extends Annotation> T findAnnotation( Class<T> annotationClazz ) {
		for( Annotation annotation : getAnnotations() ) {
			if ( annotation.annotationType() == annotationClazz ) {
				return annotationClazz.cast( annotation );
			}
		}
		return null;		
	}
	

	/**
	 * @param class1
	 * @return
	 */
	private <T extends Annotation> T getAnnotation( Class<T> annotationClazz ) {
		T annotation = findAnnotation(annotationClazz);
		if ( annotation == null ) {
			throw new RuntimeException( "Can't find annotation " + annotationClazz + " for " + this );
		}
		return annotation;
	}
	
	private boolean hasAnnotation( Class<? extends Annotation> annotationClazz ) {
		return findAnnotation(annotationClazz) != null;
	}
	
	/**
	 * @return
	 */
	private Annotation[] collectAnnotations() {
		Annotation[] fieldAnnotations = this.field.getAnnotations();
		Method method = findGetterMethod();
		if ( method != null ) {
			Annotation[] methodAnnotations = method.getAnnotations();
			Annotation[] mergedAnnotations = new Annotation[ fieldAnnotations.length + methodAnnotations.length ];
			System.arraycopy( fieldAnnotations, 0, mergedAnnotations, 0, fieldAnnotations.length );
			System.arraycopy( methodAnnotations, 0, mergedAnnotations, fieldAnnotations.length, methodAnnotations.length );
			return mergedAnnotations;
		}
		else {
			return fieldAnnotations;
		}
	}

	/**
	 * @return
	 */
	private Method findGetterMethod() {
		return ReflectionHelper.findGetter( this.beanClazz, getPropertyName() );
	}

	public PropertyDescriptor<?> buildDescriptor() {
		final PropertyDescriptor<?> descriptorBase = createDescriptorBase();
		final CascadeFetch cascadeFetch = findAnnotation(CascadeFetch.class);
		final SearchableField searchableField = findAnnotation(SearchableField.class);
		descriptorBase.setCascadeFetch( cascadeFetch != null );
		descriptorBase.setSearchableField( searchableField != null );
		return descriptorBase;
	}

	@SuppressWarnings("unchecked")
	private PropertyDescriptor<?> createDescriptorBase() {
		if ( isNativeReference() ) {
			return new NativeReferenceDescriptor( this.beanClazz, getPropertyName(), (Class)getPropertyValueType(), getMultiplicity() );
		}
		else if ( isManagedCollection() ) {
			ManagedCollectionDescriptor managedCollectionDescriptor = new ManagedCollectionDescriptor( this.beanClazz, getPropertyName(), (Class)getPropertyValueType(), getMappedByName() );
			managedCollectionDescriptor.setItemType( getTargetEntityType() );
			return managedCollectionDescriptor;
		}
		else if ( isQualifiedReference() ) {
			return new QualifiedReferenceDescriptor( this.beanClazz, getPropertyName(), getTargetEntityTypeByGeneric(), getMultiplicity() );
		}
		else if ( isNativeCollection() ) {
			NativeCollectionDescriptor nativeCollectionDescriptor = new NativeCollectionDescriptor( this.beanClazz, getPropertyName(), (Class)getPropertyValueType(), getMappedByName() );
			nativeCollectionDescriptor.setItemType( getTargetEntityType() );
			return nativeCollectionDescriptor;
		}
		else if ( isTransient() ) {
			return new TransientDescriptor( this.beanClazz, getPropertyName(), getPropertyValueType() );
		}
		else {
			return new PlainDescriptor( this.beanClazz, getPropertyName(), getPropertyValueType() );
		}
	}

	/**
	 * @return
	 */
	private boolean isQualifiedReference() {
		return QualifiedReference.class.isAssignableFrom( getPropertyValueType() );
	}

	/**
	 * @return
	 */
	private Class<?> getTargetEntityType() {
		if ( hasAnnotation( OneToMany.class ) ) {
			Class<?> targetEntity = getAnnotation( OneToMany.class ).targetEntity();
			return targetEntity != void.class ? targetEntity : getTargetEntityTypeByGeneric();
		}
		else {
			return getTargetEntityTypeByGeneric();
		}
	}
	
	

	/**
	 * @return
	 */
	private Class<?> getTargetEntityTypeByGeneric() {
		final Type genericType = this.field.getGenericType();
		if ( genericType instanceof ParameterizedType ) {
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if ( actualTypeArguments.length == 1 ) {
				final Type type = actualTypeArguments[ 0 ];
				if ( type instanceof Class<?> ) {
					return (Class<?>) type;
				}
			}
			throw new IllegalStateException( "Can't determice collection item type by actual type arguments " + actualTypeArguments + " in " + this );
		}
		else {
			return null;
		}
	}

	/**
	 * By default Multiplicity is one to many
	 * @return
	 */
	private Multiplicity getMultiplicity() {
		if ( hasAnnotation( OneToOne.class ) ) {
			return Multiplicity.OneToOne;
		}
		else if ( hasAnnotation( ManyToOne.class ) ) {
			return Multiplicity.OneToMany;
		}
		//TODG think about this 
		return Multiplicity.OneToMany;
	}

	/**
	 * @return
	 */
	private Class<?> getPropertyValueType() {
		return this.field.getType();
	}

	/**
	 * @return
	 */
	private String getPropertyName() {
		return this.field.getName();
	}

	/**
	 * @return
	 */
	private boolean isTransient() {
		return hasAnnotation( Transient.class );
	}

	/**
	 * @return
	 */
	private String getMappedByName() {
		OneToMany oneToMany = getAnnotation( OneToMany.class );
		return oneToMany.mappedBy();
	}

	/**
	 * @return
	 */
	private boolean isNativeCollection() {
		return Collection.class.isAssignableFrom( getPropertyValueType() );
	}

	/**
	 * @return
	 */
	private boolean isManagedCollection() {
		return ChildrenDomainObjectList.class.isAssignableFrom( getPropertyValueType() );
	}
	
	/**
	 * @return
	 */
	private boolean isNativeReference() {
		return MappedObject.class.isAssignableFrom( getPropertyValueType() );
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append( "field", this.field );
		return tsb.toString();		
	}


}
