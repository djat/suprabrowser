package ss.lab.dm3.security2.backend.configuration;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.SecurityId;
import ss.lab.dm3.security2.backend.vote.Voter;
import ss.lab.dm3.security2.backend.vote.authoritydrivenmodel.AuthorityVoter;
import ss.lab.dm3.security2.backend.vote.authoritydrivenmodel.AuthoryPemissions;

public class SecurityConfiguration {
	
	private List<Voter> voters = new ArrayList<Voter>();
	
	private String dbUrl;
	
	private String dbUser;
	
	private String dbPassword;
	
	public void addAuthorityVoter() {
		final AuthorityVoter authorityVoter = new AuthorityVoter();
		// Add administrator that can anything
		{
			AuthoryPemissions permissions = new AuthoryPemissions();
			permissions.put( new SecurityId( "ss" ), Permission.ALL );
			authorityVoter.getModel().put( Authority.ADMINISTRATOR, permissions );
		}
		// Add user that can only read
		{
			AuthoryPemissions permissions = new AuthoryPemissions();
			permissions.put( new SecurityId( "ss" ), Permission.READ );
			authorityVoter.getModel().put( Authority.USER, permissions );
		}
		this.voters.add( authorityVoter );
	}
	
	public List<Voter> getVoters() {
		return this.voters;
	}

	public String getDbUrl() {
		return this.dbUrl;
	}

	public String getDbUser() {
		return this.dbUser;
	}

	public String getDbPassword() {
		return this.dbPassword;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}


	
}
