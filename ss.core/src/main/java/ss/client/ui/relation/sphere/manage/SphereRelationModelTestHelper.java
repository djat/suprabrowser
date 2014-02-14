package ss.client.ui.relation.sphere.manage;

import java.util.ArrayList;
import java.util.Collection;

import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.domainmodel.SphereStatement;

public class SphereRelationModelTestHelper {

	public static SphereRelationModel createModel() {
		return SphereRelationModel.create( new ISphereDefinitionProvider() {

			public void checkOutOfDate() {}

			public Collection<SphereStatement> getAllSpheres() {
				Collection<SphereStatement> spheres = new ArrayList<SphereStatement>();
				spheres.add( create( "root", null, "Second", "3.1.1" ) );
				spheres.add( create( "First", "root" ) );
				spheres.add( create( "1.1", "First" ) );
				spheres.add( create( "1.2", "First" ) );
				spheres.add( create( "Second", "root" ) );
				spheres.add( create( "2.1", "Second" ) );
				spheres.add( create( "Third", "root" ) );
				spheres.add( create( "3.1", "Third" ) );
				spheres.add( create( "3.1.1", "3.1" ) );
				
				return spheres;
			}

			private SphereStatement create(String id, String parentId, String ... relationIds ) {
				SphereStatement sphere = new SphereStatement(); 
				sphere.setSystemName( id );
				sphere.setDisplayName( id );
				sphere.setSphereCoreId( parentId );
				for( String relationId : relationIds ) {
					sphere.getRelations().add( relationId, relationId );	
				}
				return sphere;
			}

			public String getRootId() {
				return "root";
			}

			public boolean isSphereVisible(SphereStatement sphere) {
				return true;
			}

			public void outOfDate() {}
			
		}, "root" );
	}
}
