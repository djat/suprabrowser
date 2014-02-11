/**
 * 
 */
package ss.lab.dm3.persist.changeset;

import java.util.Hashtable;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov
 */
public class ObjectsDataBackup {

	final private Hashtable<QualifiedObjectId<? extends DomainObject>,Backup> idToBackup = new Hashtable<QualifiedObjectId<? extends DomainObject>,Backup>();
	
	/**
	 * @param mapperManager
	 */
	public ObjectsDataBackup() {
	}

	/**
	 * @param object
	 */
	public void backup(DomainObject object) {
		Backup backup = new Backup( object );
		addBackup(backup);
	}

	/**
	 * @param backup
	 */
	private void addBackup(Backup backup) {
		if ( this.idToBackup.containsKey( backup.getObjectId() ) ) {
			throw new IllegalArgumentException( "Object already backupped " + backup.getObject() );
		}
		this.idToBackup.put( backup.getObjectId(), backup );
	}


	/**
	 * @param domainObject
	 */
	public boolean backupIfNotBackupped(DomainObject object) {
		Backup backup = new Backup( object );
		if ( !this.idToBackup.containsKey( backup.getObjectId() ) ) {
			addBackup(backup);
			return false;
		}
		return true;
	}
	/**
	 * 
	 */
	public void restoreAll() {
		for( Backup backup : this.idToBackup.values() ) {
			backup.restore();
		}
	}

	/**
	 * 
	 */
	public void clear() {
		this.idToBackup.clear();
	}

	/**
	 * @param loadedDto
	 * @return
	 */
	public boolean hasDifferentData(DomainObject domainObject) {
		QualifiedObjectId<? extends DomainObject> domainObjectId = domainObject.getQualifiedId();
		Backup backup = this.idToBackup.get(domainObjectId);
		return backup != null ? !backup.equalsToObject( domainObject ) : false;
	}
	
	static class Backup {
		
		private final QualifiedObjectId<? extends DomainObject> objectId;
		
		private final DomainObject object;
		
		private final Entity backup;
		
		public Backup( DomainObject object ) {
			this.objectId = object.getQualifiedId();
			this.object = object;
			this.backup = object.toEntity();
		}
		
		/**
		 * @return
		 */
		public DomainObject getObject() {
			return this.object;
		}


		public QualifiedObjectId<? extends DomainObject> getObjectId() {
			return this.objectId;
		}


		public void restore() {
			this.object.from( this.backup, true );
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.objectId == null) ? 0 : this.objectId.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Backup other = (Backup) obj;
			if (this.objectId == null) {
				if (other.objectId != null)
					return false;
			} else if (!this.objectId.equals(other.objectId))
				return false;
			return true;
		}

		/**
		 * @param domainObject
		 * @return
		 */
		public boolean equalsToObject(DomainObject domainObject) {
			return this.backup.equalsDeep(domainObject.toEntity());
		}
		
	}

}
