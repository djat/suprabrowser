package ss.lab.dm3.testsupport.objects;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.lucene.document.Document;

import ss.lab.dm3.annotation.SearchableField;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.backend.search.SearchHelper;
import ss.lab.dm3.persist.search.ISearchable;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class SphereExtension extends DomainObject implements ISearchable {

	@OneToMany
	private final ChildrenDomainObjectList<Sphere> relatedSpheres = new ChildrenDomainObjectList<Sphere>();

	@Transient
	public ChildrenDomainObjectList<Sphere> getRelatedSpheres() {
		return this.relatedSpheres;
	}
	
	@SearchableField
	private String description;
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void collectFields(Document collector) {
		SearchHelper.collectByDefault(this, collector);
	}
	
}
