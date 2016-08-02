package core;

import org.hibernate.QueryException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import foo.bar.entities.Message;

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

	/**
	 * Build a standard session factory for the application based on settings of
	 * the default Hibernate configuration.
	 * 
	 * @return A standard session factory
	 */
	public static SessionFactory createEntityManagerFactory() {
		return createConfiguration("hibernate.cfg.xml").buildSessionFactory();
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
		configuration.addAnnotatedClass(Message.class);
	}

	/**
	 * Return the entity metadata
	 *
	 * @param entityClass
	 *            The entity class
	 * @return Entity metadata
	 */
	@SuppressWarnings("deprecation")
	public static ClassMetadata getEntityMetadata(Class<?> entityClass, SessionFactory entityManagerFactory) {
		return entityManagerFactory.getClassMetadata(entityClass);
	}

	/**
	 * Retorn the type of an entity property recursively
	 *
	 * @param entityClass
	 *            Entity class
	 * @param propertyName
	 *            Property name (ie.: 'id', 'user.id', 'user.profile.id')
	 * @return Property type
	 */
	public static Type getPropertyType(Class<?> entityClass, String propertyName, SessionFactory entityManagerFactory)
			throws QueryException {
		if (propertyName == null)
			throw new QueryException("Property name passed cannot be null or empty");

		// se for uma propriedade singular
		if (!propertyName.contains(".")) {
			return HibernateUtil.getEntityMetadata(entityClass, entityManagerFactory).getPropertyType(propertyName);
		} // se não ...

		// seta a propriedade principal (a primeira)
		Type mainProperty = HibernateUtil.getEntityMetadata(entityClass, entityManagerFactory)
				.getPropertyType(propertyName.split("\\.")[0]);

		// se realmente for uma propriedade e se for um tipo de entidade
		if (mainProperty.isEntityType()) {
			String subProperties = propertyName.substring(propertyName.indexOf(".") + 1, propertyName.length());

			// verifica se as subpropriedades sao validas pelo seu gerenciador
			return getPropertyType(mainProperty.getReturnedClass(), subProperties, entityManagerFactory);
		} else
			throw new QueryException("Property type " + propertyName.split("\\.")[0] + " is not a entity.");
	}

}
