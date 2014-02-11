package ss.lab.dm3.testsupport;

import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.connection.configuration.ConfigurationProvider;
import ss.lab.dm3.connection.configuration.DomainDataClassList;
import ss.lab.dm3.testsupport.objects.Attachment;
import ss.lab.dm3.testsupport.objects.ForumSphereExtension;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SphereExtension;
import ss.lab.dm3.testsupport.objects.SupraSphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;
import ss.lab.dm3.testsupport.objects.WikiSphereExtension;

public class TestConfigurationProvider extends ConfigurationProvider {

	public final static TestConfigurationProvider INSTANCE = new TestConfigurationProvider();

	@Override
	protected void setUp(Configuration configuration) {
		super.setUp(configuration);
		configuration
		.setDbUrl("jdbc:mysql://127.0.0.1/dm3?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&sessionVariables=FOREIGN_KEY_CHECKS=0");
		configuration.setDbUser("root");
		configuration.setDbPassword("toor");
		DomainDataClassList dataDataClassez = configuration
		.getDomainDataClasses();
		// Data classes
		dataDataClassez.add(SupraSphere.class);
		dataDataClassez.add(Sphere.class);
		dataDataClassez.add(UserAccount.class);
		dataDataClassez.add(UserInSphere.class);
		dataDataClassez.add(SphereExtension.class);
		dataDataClassez.add(WikiSphereExtension.class);
		dataDataClassez.add(ForumSphereExtension.class);
		dataDataClassez.add(Attachment.class);
		// Scripts handlers
		configuration.getScriptHandlers().add(
				new StartupLoaderScriptHandler());
	}

}
