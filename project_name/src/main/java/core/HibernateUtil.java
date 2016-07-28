package core;

import java.text.ParseException;

import org.hibernate.QueryException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import util.Parser;

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

	/**
	 * Retorna o tipo da propriedade de uma entidade recursivamente
	 * 
	 * @param c
	 *            A classe da entidade
	 * @param propertyName
	 *            O nome da propriedade (ex.: veiculo.modelo.codigo))
	 * @return O tipo da propriedade
	 * @throws QueryException
	 */
	public static Type getPropertyType(Class<?> c, String propertyName) throws QueryException {

		if (propertyName == null)
			throw new QueryException("Property name passed cannot be null or empty");

		// se for uma propriedade singular
		if (!propertyName.contains(".")) {
			return getClassMetadata(c).getPropertyType(propertyName);
		} // se não ...

		// seta a propriedade principal (a primeira)
		Type mainProperty = getClassMetadata(c).getPropertyType(propertyName.split("\\.")[0]);

		// se realmente for uma propriedade e se for um tipo de entidade
		if (mainProperty.isEntityType()) {

			String subProperties = propertyName.substring(propertyName.indexOf(".") + 1, propertyName.length());

			// verifica se as subpropriedades sao validas pelo seu
			// gerenciador
			return getPropertyType(mainProperty.getReturnedClass(), subProperties);
		} else
			throw new QueryException("Property " + propertyName.split("\\.")[0] + " is not a entity.");
	}

	/**
	 * Retorna os metadados de uma entidade
	 * 
	 * @param c
	 *            A entidade
	 * @return Se nao for um entidade, retornara null
	 */
	@SuppressWarnings("deprecation")
	public static ClassMetadata getClassMetadata(Class<?> c) {
		return sessionFactory.getClassMetadata(c);
	}

	/**
	 * Converte um valor conforme a classe retornada do tipo de uma propriedade
	 * de uma determinada entidade
	 * 
	 * @param c
	 *            A entidade
	 * @param propertyName
	 *            O nome da propriedade
	 * @param value
	 *            O valor a ser convertido
	 * @return O valor convertido
	 * @throws ParseException
	 */
	public static Object parseValue(Class<?> c, String propertyName, Object value) throws ParseException {

		Type type = getPropertyType(c, propertyName);

		if (value.getClass().equals(type.getReturnedClass()))
			return value;
		else
			return Parser.parseValue(type.getReturnedClass(), value);
	}

	/**
	 * Coverte um valor conforme a classe retornada de um tipo de propriedade
	 * 
	 * @param type
	 *            O tipo de propriedade
	 * @param value
	 *            O valor a ser convertido
	 * @return O valor convertido
	 * @throws ParseException
	 */
	public static Object parseValue(Type type, Object value) throws ParseException {
		return Parser.parseValue(type.getReturnedClass(), value);
	}

}
