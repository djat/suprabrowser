/**
 * 
 */
package ss.lab.dm3.orm.mapper.map.linker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.descriptor.IReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.IReferenceDescriptor.Multiplicity;

/**
 *
 */
public class ReferenceFromList {
		
	private final List<IReferenceDescriptor> references = new ArrayList<IReferenceDescriptor>();

	/**
	 * @param targetEntityClazz
	 */
	public ReferenceFromList() {
		super();
	}

	/**
	 * @param mappedByName
	 * @return
	 */
	public Class<? extends MappedObject> resolve(String mappedByName) {
		final AccepableList accepable = new AccepableList();
		for( IReferenceDescriptor reference : this.references ) {
			final Multiplicity multiplicity = reference.getMultiplicity();
			if ( reference.getName().equals( mappedByName ) && 
			     multiplicity == Multiplicity.OneToMany ) {
				accepable.add( reference );
			}
		}
		if (accepable.isValid()) {
			return accepable.getBeanClazz();
		} else {
			throw new CantResolveCollectionItemTypeException( "Can't find appropriate reference. Total reference count " +
				this.references.size() +  ". Possible references " + accepable );
		}		
	}

	/**
	 * @param itemType
	 * @return
	 */
	public String resolve(Class<? extends MappedObject> itemType) {
		final AccepableList accepable = new AccepableList();
		for( IReferenceDescriptor reference : this.references ) {
			final Multiplicity multiplicity = reference.getMultiplicity();
			if ( reference.getBeanClazz() == itemType && 
			     multiplicity == Multiplicity.OneToMany ) {
				accepable.add( reference );
			}
		}
		if (accepable.isValid()) {
			return accepable.getName();
		} else {
			throw new CantResolveCollectionItemTypeException( "Can't find appropriate reference. Total reference count " +
				this.references.size() +  ". Possible references " + accepable );
		}
	}
	
	/**
	 * @param referenceProperty
	 */
	public void add(IReferenceDescriptor referenceProperty) {
		this.references.add(referenceProperty);
	}
	
	
	
	private static class AccepableList {
		
		final List<IReferenceDescriptor> items = new ArrayList<IReferenceDescriptor>();

		/**
		 * @return
		 */
		public Class<? extends MappedObject> getBeanClazz() {
			return this.items.get( 0 ).getBeanClazz();
		}

		/**
		 * @return
		 */
		public String getName() {
			return this.items.get( 0 ).getName();
		}

		/**
		 * @return
		 */
		public boolean isValid() {
			return this.items.size() == 1;
		}

		/**
		 * @param reference
		 */
		public void add(IReferenceDescriptor reference) {
			this.items.add(reference);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			ToStringBuilder tsb = new ToStringBuilder( this );
			tsb.append( "size", this.items.size() );
			for( IReferenceDescriptor reference : this.items ) {
				tsb.append( reference );
			}
			return tsb.toString();
		}
		
	}

}
