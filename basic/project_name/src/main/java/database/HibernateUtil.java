package database;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import foo.bar.entities.Event;

/**
 * 
 * http://docs.jboss.org/hibernate/orm/5.1/userguide/html_single/
 * Hibernate_User_Guide.html#bootstrap
 * 
 * http://stackoverflow.com/questions/25684785/how-to-read-database-
 * configuration-parameter-using-properties-file-in-hibernate
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
public class HibernateUtil {

	// private static final SessionFactory sessionFactory;
	//
	// static {
	// try {
	// sessionFactory =
	// createSessionFactory(createConfiguration("hibernate.cfg.xml"));
	// } catch (Throwable ex) {
	// System.err.println("Initial SessionFactory creation failed." + ex);
	// throw new ExceptionInInitializerError(ex);
	// }
	// }
	//
	// public static SessionFactory getSessionFactory() {
	// return sessionFactory;
	// }

	public static SessionFactory createSessionFactory(String resourceName, Properties properties) {
		Configuration configuration = createConfiguration(resourceName, properties);
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		MetadataSources sources = new MetadataSources(registry);
		return sources.buildMetadata().buildSessionFactory();
	}

	public static SessionFactory createSessionFactory(Configuration configuration) {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		MetadataSources sources = new MetadataSources(registry);
		return sources.buildMetadata().buildSessionFactory();
	}

	public static void registerAnnoteatedClass(Configuration configuration) {
		configuration.addAnnotatedClass(Event.class);
	}

	public static Configuration createConfiguration(String resourceName) {
		return createConfiguration(resourceName, null);
	}

	/**
	 * Creates a Hibernate configuration based on a resource file. A properties
	 * object can be passed optionally as argument to add or overwrite resource
	 * settings.
	 * 
	 * @param resource
	 *            The resource name. If not given, Hibernate handles the default
	 *            configuration resource.
	 * @param properties
	 *            Optional properties
	 * @return A Hibernate configuration
	 */
	public static Configuration createConfiguration(String resource, Properties properties) {
		Configuration configuration = new Configuration();
		if (resource == null)
			configuration.configure();
		else
			configuration.configure(resource);
		if (properties != null)
			configuration.addProperties(properties);
		registerAnnoteatedClass(configuration);
		return configuration;
	}

}
