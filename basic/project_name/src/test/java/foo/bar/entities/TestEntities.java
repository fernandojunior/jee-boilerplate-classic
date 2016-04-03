package foo.bar.entities;

import java.io.File;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import junit.framework.TestCase;

public class TestEntities extends TestCase {

	private String database = "test.db12";

	private SessionFactory sessionFactory = null;
	private Session session = null;

	@SuppressWarnings("unused")
	private void createSessionFactory() {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
	}

	public void createSessionFactory(String filename) {
		// http://stackoverflow.com/questions/22200773/hibernate-cfg-xml-modification-on-runtime
		// http://stackoverflow.com/questions/6437153/hibernate-changing-cfg-properties-at-runtime
		Configuration configuration = new Configuration();

		// <!-- Database connection settings -->
		configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
		configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:" + filename);
		configuration.setProperty("hibernate.show_sql", "true");
		configuration.setProperty("hibernate.format_sql", "true");
		configuration.setProperty("hibernate.hbm2ddl.auto", "update");
		configuration.addAnnotatedClass(Event.class);
		sessionFactory = configuration.buildSessionFactory();
	}

	protected void setUp() {
		createSessionFactory(database);
		session = sessionFactory.openSession();
		session.beginTransaction();
	}

	public Event createEvent(String title, Date date) {
		return new Event(title, date);
	}

	public void testMain() {

		// Creating entities that will be saved to the sqlite database
		Event hello = createEvent("Hello", new Date());
		Event world = createEvent("World", new Date());

		// Saving to the database
		session.save(hello);
		session.save(world);

		// Committing the change in the database.
		session.getTransaction().commit();

		// Fetching saved data
		@SuppressWarnings("unchecked")
		List<Event> result = session.createQuery("from Event").list();

		assertTrue(result != null);
		assertTrue(result.size() == 2);
		assertTrue(result.get(0).getTitle() == "Hello");
		assertTrue(result.get(1).getTitle() == "World");

		// session.getTransaction().rollback();

	}

	protected void tearDown() {
		session.close();
		sessionFactory.close();
		new File(database).delete();
	}

}