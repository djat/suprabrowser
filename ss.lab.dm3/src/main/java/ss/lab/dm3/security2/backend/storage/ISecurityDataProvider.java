package ss.lab.dm3.security2.backend.storage;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;

public interface ISecurityDataProvider {

	Authentication getAuthentication(String accountName);

	void deleteAccount(String accountName);

	void addAuthority(Authentication authentication, Authority authority);

	void removeAuthority(Authentication authentication, Authority authority);

	Authentication createAccount(String accountName);

	void dispose();

}
