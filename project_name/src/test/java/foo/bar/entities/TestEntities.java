package foo.bar.entities;

import java.io.File;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import core.BaseRepository;
import core.HibernateUtil;
import foo.bar.repositories.EventRepository;
import junit.framework.TestCase;

public class TestEntities extends TestCase {

	private String database = "test.db12";
	private Session session = null;
	private SessionFactory sessionFactory = null;

	public void createSessionFactory(String filename) {
		Configuration configuration = HibernateUtil.createConfiguration("test.cfg.xml");
		configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:" + filename);
		sessionFactory = configuration.buildSessionFactory();
	}

	protected void setUp() {
		createSessionFactory(database);
		session = sessionFactory.openSession();
	}

	public Event createEvent(String title, Date date) {
		return new Event(title, date);
	}

	public void testMain() {
		// Test main method with generic repository
		main(BaseRepository.create(Event.class, session));

		// Test main method with event repository
		main(new EventRepository(session));
	}

	public void main(BaseRepository<Event> eventRepository) {

		assertTrue(new File(database).isFile());

		// Creating entities that will be saved to the sqlite database
		Event hello = createEvent("Hello", new Date());
		Event world = createEvent("World", new Date());

		session.beginTransaction();

		// Saving to the database
		eventRepository.save(hello);
		eventRepository.save(world);

		// Committing the change in the database.
		session.getTransaction().commit();

		List<Event> result = eventRepository.getAll();

		// Fetching saved data
		// List<Event> result = session.createQuery("from Event").list();

		assertTrue(result != null);
		assertTrue(result.size() == 2);
		assertTrue(result.get(0).getTitle() == "Hello");
		assertTrue(result.get(1).getTitle() == "World");

		session.beginTransaction();
		eventRepository.delete(result.get(0));
		eventRepository.delete(result.get(1));
		session.getTransaction().commit();

		// session.getTransaction().rollback();

	}

	protected void tearDown() {
		session.close();
		sessionFactory.close();
		new File(database).delete();
	}

}