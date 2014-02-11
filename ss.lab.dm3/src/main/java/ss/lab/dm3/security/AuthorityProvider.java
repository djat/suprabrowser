package ss.lab.dm3.security;


public class AuthorityProvider {

	// private ClassResolver<IAuthorityCalculator> classResolver;
	
	public OperationAuthorityList get(String objectName, String operationName  ) {
		//TODO find permissionDescriptor 
		ObjectPermissionDescriptor permissionDescriptor = getPermDesc();
		return permissionDescriptor.getOperationAuthorities( operationName );
	}

	/**
	 * @return
	 */
	private ObjectPermissionDescriptor getPermDesc() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
