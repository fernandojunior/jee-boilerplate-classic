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

import database.Manager;
import junit.framework.TestCase;

public class TestEntities extends TestCase {

	private String database = "test.db12";
	private Session session = null;
	private SessionFactory sessionFactory = null;

	@SuppressWarnings("unused")
	private void createSessionFactory() {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
	}

	public void createSessionFactory(String filename) {
		Configuration configuration = new Configuration();
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

		Manager<Event> manager = Manager.create(Event.class, session);

		// Creating entities that will be saved to the sqlite database
		Event hello = createEvent("Hello", new Date());
		Event world = createEvent("World", new Date());

		// Saving to the database
		manager.save(hello);
		manager.save(world);

		// Committing the change in the database.
		session.getTransaction().commit();

		List<Event> result = manager.getAll();

		// Fetching saved data
		// List<Event> result = session.createQuery("from Event").list();

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