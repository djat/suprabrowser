/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.ArrayList;

import ss.common.StringUtils;

/**
 *
 */
public final class FieldMap {

	private final DomainObject objectOwner;
	
	private final ArrayList<Field> fields = new ArrayList<Field>(); 
	
	/**
	 * @param objectOwner
	 */
	public FieldMap(final DomainObject objectOwner) {
		super();
		this.objectOwner = objectOwner;
	}
	
	synchronized <FD extends FieldDescriptor<F, ?>, F extends Field> F add( FD fieldDescriptor ) {
		F field = getField(fieldDescriptor);
		if ( field != null ) {
			throw new FieldAlreadyExistsException( fieldDescriptor );
		}
		field = fieldDescriptor.createField(this);
		this.fields.add( field );
		return field; 
	}

	/**
	 * 
	 */
	public void markDirty() {
		this.objectOwner.markDirty();
	}

	/**
	 * @param record
	 */
	@SuppressWarnings("unchecked")
	public void load(Record record) {
		for( Field field : this.fields ) {
			field.descriptor.load(field, record);
		}
	}

	/**
	 * @param record
	 */
	@SuppressWarnings("unchecked")
	public void save(Record record) {
		for( Field field : this.fields ) {
			field.descriptor.save(field, record);
		}
	}
	
	
	/**
	 * @return
	 */
	public AbstractDomainSpace getSpaceOwner() {
		return this.objectOwner.getSpaceOwner();
	}

	/**
	 * @param descriptor
	 * @return
	 */
	public <F extends Field> F requireField(FieldDescriptor<F,?> descriptor) {
		F field = getField(descriptor);
		if ( field == null ) {
			throw new FieldNotFoundException( descriptor );
		}
		return field;
	}

	/**
	 * @param descriptor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <F extends Field> F getField(FieldDescriptor<F,?> descriptor) {
		//TODO:#optimize usage!
		for( Field field : this.fields ) {
			if ( field.descriptor == descriptor ) {
				return (F)field;
			}
		}
		return null;
	}

	
	/**
	 * @return
	 */
	public DomainObject getObjectOwner() {
		return this.objectOwner;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String allFieldsToString() {
		StringBuilder sb = new StringBuilder();
		for( Field field : this.fields ) {
			final String name = field.descriptor.getName();
			final String descriptorName = field.descriptor.getClass().getSimpleName();
			final String strValue = field.descriptor.getValueToSave(field);
			sb.append( descriptorName );
			sb.append( '\t' );
			sb.append( name );
			sb.append( ':'  );
			sb.append( ' ' );
			sb.append( strValue );
			sb.append( ';' );
			sb.append( StringUtils.getLineSeparator() );
		}
		return sb.toString();
	}

	/**
	 *
	 */
	public class FieldNotFoundException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4126678074793211687L;

		/**
		 * @param fieldDescriptor
		 */
		public FieldNotFoundException(FieldDescriptor fieldDescriptor) {
			super( "Field not found " + fieldDescriptor );
		}
	}


	/**
	 *
	 */
	public static class FieldAlreadyExistsException extends
		RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5014863946850140072L;

		/**
		 * @param fieldDescriptor
		 */
		public FieldAlreadyExistsException(FieldDescriptor fieldDescriptor) {
			super( "Field already exists " + fieldDescriptor );
		}

	}

	
}
