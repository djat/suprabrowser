package ss.lab.dm3.security;

public interface IAuthentication {

	/**
	 * @param staticAuthority
	 */
	public abstract boolean hasAuthority(String authorityName);

	public abstract Long getUserAccountId();

}