package ss.lab.dm3.security2.backend.storage.jdbc;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.backend.configuration.SecurityConfiguration;
import ss.lab.dm3.security2.backend.storage.ISecurityDataProvider;

public class Test {

	public static void main(String[] args) {
		SecurityConfiguration cfg = new SecurityConfiguration();
		cfg.setDbUrl("jdbc:mysql://127.0.0.1/dm3use?useUnicode=true&characterEncoding=utf-8");
		cfg.setDbUser("root");
		cfg.setDbPassword("");
		ISecurityDataProvider securityDataProvider = new JdbcSecurityDataProvider( cfg );
		try {
			Authentication auth = securityDataProvider.getAuthentication( "test" );
			System.out.println( auth );
		}
		finally {
			securityDataProvider.dispose();
		}

	}
}
