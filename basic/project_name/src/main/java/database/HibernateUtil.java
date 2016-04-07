package database;

import org.hibernate.SessionFactory;
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

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void setSessionFactory(SessionFactory sessionFactory) {
		HibernateUtil.sessionFactory = sessionFactory;
	}

	/**
	 * Build a standard session factory for the application based on settings of
	 * the default Hibernate configuration.
	 * 
	 * @return A standard session factory
	 */
	public static SessionFactory buildSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = createConfiguration("hibernate.cfg.xml").buildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Creates a Hibernate configuration based on a resource file wit Annotated
	 * classes registered dynamically.
	 * 
	 * @param resource
	 *            The resource name. If none given, Hibernate handles the
	 *            default configuration resource.
	 * @see HibernateUtil#registerAnnoteatedClass(Configuration)
	 * 
	 * @return A Hibernate configuration
	 */
	public static Configuration createConfiguration(String resource) {
		Configuration configuration = new Configuration();
		if (resource == null)
			configuration.configure();
		else
			configuration.configure(resource);
		registerAnnoteatedClass(configuration);
		return configuration;
	}

	/**
	 * Adds the entity model classes into a configuration file that will be used
	 * by Hibernate to map.
	 * 
	 * @param configuration
	 */
	public static void registerAnnoteatedClass(Configuration configuration) {
		configuration.addAnnotatedClass(Event.class);
	}

}
