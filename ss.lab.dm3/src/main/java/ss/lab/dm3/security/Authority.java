package ss.lab.dm3.security;

public abstract class Authority {

	private String name;
	
	
	public String getName() {
		return this.name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public abstract boolean isProvidedFor(Object object, IAuthentication authentication);

}
