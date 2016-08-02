package foo.bar;

import java.io.File;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import junit.framework.TestCase;

import core.GenericRepository;
import core.HibernateUtil;
import foo.bar.entities.Message;
import foo.bar.repositories.MessageRepository;

public class TestMassage extends TestCase {

	private String database = "test.db12";
	private Session entityManager = null;
	private SessionFactory entityManagerFactory = null;

	public void createEntityManagerFactory(String filename) {
		Configuration configuration = HibernateUtil.createConfiguration("test.cfg.xml");
		configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:" + filename);
		entityManagerFactory = configuration.buildSessionFactory();
	}

	protected void setUp() {
		createEntityManagerFactory(database);
		entityManager = entityManagerFactory.openSession();
	}

	public Message createMessage(String message) {
		return new Message(message);
	}

	public void testMain() {
		// Test main method with generic repository
		main(GenericRepository.create(Message.class, entityManager));

		// Test main method with message repository
		main(new MessageRepository(entityManager));
	}

	public void main(GenericRepository<Message> messageRepository) {

		assertTrue(new File(database).isFile());

		// Creating entities that will be saved to the sqlite database
		Message hello = createMessage("Hello");
		Message world = createMessage("World");

		messageRepository.beginTransaction();

		// Saving to the database
		messageRepository.save(hello);
		messageRepository.save(world);

		// Committing the change in the database.
		messageRepository.commit();

		// Fetching saved data
		List<Message> result = messageRepository.findAll();

		assertTrue(result != null);
		assertTrue(result.size() == 2);
		assertTrue(result.get(0).getContent() == "Hello");
		assertTrue(result.get(1).getContent() == "World");

		messageRepository.beginTransaction();
		messageRepository.remove(result.get(0));
		messageRepository.remove(result.get(1));
		messageRepository.commit();

		// entityManager.getTransaction().rollback();

	}

	protected void tearDown() {
		entityManager.close();
		entityManagerFactory.close();
		new File(database).delete();
	}

}