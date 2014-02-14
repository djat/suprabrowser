/**
 * 
 */
package ss.domainmodel.spherehierarchy;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import ss.domainmodel.SphereStatement;

/**
 * 
 */
public abstract class AbstractGroupSphereHierarchyBuilder {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractGroupSphereHierarchyBuilder.class);

	private final SphereCollection spheresToProcess = new SphereCollection();
	
	public final void addSphere(final Document sphereDocument) {
		final SphereStatement sphereStatement = SphereStatement.wrap(sphereDocument);
		if (sphereStatement.hasValidType()) {
			this.spheresToProcess.add(sphereStatement);
		}
	}

	/**
	 * @param sphereStatement
	 * @return
	 */
	protected abstract boolean shouldAddToResult(SphereStatement sphere);
	
	/**
	 * 
	 */
	protected void prepareToBuildHierarchy() {
		// NOOP
	}

	public final SphereHierarchyBuildResult buildHiearchy() {
		if (this.spheresToProcess.getCount() > 0) {
			prepareToBuildHierarchy();
			checkBuildConditions();
			return processSpheres();
		} else {
			logger.debug("nothing to build");
			return new SphereHierarchyBuildResult();
		}		
	}

	/**
	 * 
	 */
	protected void checkBuildConditions() {	
	}

	private SphereHierarchyBuildResult processSpheres() {
		final List<SphereStatement> processedSpheres = new ArrayList<SphereStatement>(); 
		for (SphereStatement sphere : this.spheresToProcess) {
			if ( shouldAddToResult( sphere ) ) {
				processedSpheres.add( sphere );
				final String sphereCoreId = sphere.getSphereCoreId();
				final SphereStatement sphereCore = findCoreSphere( sphere );
				sphere.setParent( sphereCore );
				if (sphereCore == null && 
						logger.isDebugEnabled()) {
						logger.debug("cannot bind sphere to core by sphereCoreId "
									+ sphereCoreId);
				}
			}
		}
		return new SphereHierarchyBuildResult( processedSpheres );
	}

	/**
	 * @param sphereCoreId
	 * @return
	 */
	protected SphereStatement findCoreSphere(SphereStatement sphere ) {
		final String sphereCoreId = sphere.getSphereCoreId(); 
		if (sphereCoreId != null) {
			return this.spheresToProcess.get(sphereCoreId);								
		}
		return null;
	}

	/**
	 * @return the spheres to process
	 */
	protected final List<SphereStatement> getSpheresToProcess() {
		return this.spheresToProcess.asList();
	}

	/**
	 * @param spheresDocuments
	 */
	public final void addAllSpheres(Iterable<Document> spheresDocuments) {
		for (Document sphereDocument : spheresDocuments) {
			addSphere(sphereDocument);
		}
	}
}
