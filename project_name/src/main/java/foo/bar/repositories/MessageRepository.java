package foo.bar.repositories;

import org.hibernate.Session;

import core.GenericRepository;
import foo.bar.entities.Message;

/**
 * Repository for messages.
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
public class MessageRepository extends GenericRepository<Message> {

	public MessageRepository(Session session) {
		super(session);
	}

}
