package ss.framework.install;

public class QualifiedVersion extends Version {

	/**
	 * 
	 */
	static final String OS_TO_VERSION_DELIMETER = "-";
	
	private String operationSystem;

	public static QualifiedVersion safeParse( String value ) {
		Version parsedValue = VersionFactory.safeParse(value);
		if ( parsedValue instanceof QualifiedVersion ) {
			return (QualifiedVersion) parsedValue;
		}
		else {
			return new QualifiedVersion( OperationSystemName.ANY, parsedValue.getParts() );
		}			
	}
	
	/**
	 * @param parts
	 * @param operationSystem
	 */
	public QualifiedVersion(OperationSystemName operationSystem, int ... parts) {
		super(parts);
		setOperationSystem( operationSystem );		
	}
	
	/**
	 * @param parts
	 * @param operationSystem
	 */
	public QualifiedVersion(String operationSystem, int ... parts) {
		this( new OperationSystemName( operationSystem ), parts );
	}

	public OperationSystemName getOperationSystem() {
		return new OperationSystemName( this.operationSystem );
	}

	public void setOperationSystem(OperationSystemName operationSystem) {
		this.operationSystem = operationSystem.toString();
	}

	@Override
	public String toString() {
		if ( this.operationSystem.equals( OperationSystemName.ANY ) ) {
			return super.toString();
		}
		else {
			return this.operationSystem + OS_TO_VERSION_DELIMETER + super.toString();
		} 
	}

	/**
	 * @param versionStr
	 * @return
	 * @throws InvalidVersionException 
	 */
	public static QualifiedVersion parseAndCheck(String versionStr) throws InvalidVersionException {
		if ( versionStr == null ) {
			throw new NullPointerException( "versionStr" );
		}
		versionStr = versionStr.trim();
		QualifiedVersion versionObj = safeParse(versionStr);
		if ( !versionObj.toString().equals(versionStr) ) {
			throw new InvalidVersionException( versionStr, versionObj );
		}
		return versionObj;
	}
	
	
	
}
